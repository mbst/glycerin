package com.metabroadcast.atlas.glycerin.generator;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Filter;
import com.metabroadcast.atlas.glycerin.model.GroupBody;
import com.metabroadcast.atlas.glycerin.model.Mixin;
import com.metabroadcast.atlas.glycerin.model.Option;
import com.metabroadcast.atlas.glycerin.model.Sort;
import com.metabroadcast.atlas.glycerin.model.SortDirection;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.metabroadcast.atlas.glycerin.model.Version;
import com.metabroadcast.atlas.glycerin.queries.BaseApiQuery;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;


public class FeedQueryGenerator {
    
    private static final int privateStaticFinal = JMod.PRIVATE|JMod.STATIC|JMod.FINAL;
    private static final String MODEL_PKG = "com.metabroadcast.atlas.glycerin.model.";
    private final JCodeModel model;
    private final JPackage pkg;
    
    private final JClass parent;
    private final JClass precs;
    private final JClass immutableList;
    private final JClass immutableMap;
    private final JClass immutableMapBldr;

    private static final ImmutableMap<String, Class<?>> typeMap = ImmutableMap.<String, Class<?>>builder()
        .put("integer", Integer.class)
        .put("datetime", DateTime.class)
        .put("string", String.class)
        .put("ID", String.class)
        .put("PID", String.class)
        .put("character", Character.class)
        .put("boolean", Boolean.class)
        .put("date", LocalDate.class)
        .build();


    public FeedQueryGenerator(JCodeModel model, JPackage pkg) {
        this.model = model;
        this.pkg = pkg;
        this.parent = model.directClass(BaseApiQuery.class.getCanonicalName());
        this.precs = model.directClass(Preconditions.class.getCanonicalName());
        this.immutableList = model.directClass(ImmutableList.class.getCanonicalName());
        this.immutableMap = model.directClass(ImmutableMap.class.getCanonicalName());
        this.immutableMapBldr = model.directClass(ImmutableMap.Builder.class.getCanonicalName())
                .narrow(String.class, Object.class);
    }

    public void generateQuery(Feed feed) {
        try {
            JClass transformedType = getTransformedType(feed);
            if (transformedType == null) {
                return;
            }
            
            JDefinedClass cls = pkg._class(JMod.PUBLIC|JMod.FINAL, String.format("%sQuery", feed.getName()));
            cls._extends(parent.narrow(transformedType));
            
            if (feed.getTitle()!=null) {
                cls.javadoc().add(String.format("<p>%s</p>",feed.getTitle()));
            }
            
            addResourcePath(cls, feed);
            addResultTypeMethod(model, cls, transformedType);
            addConstructor(cls);
            JDefinedClass bldrCls = addBuilderCls(feed, cls);
            
            cls.method(JMod.PUBLIC|JMod.STATIC|JMod.FINAL, bldrCls, "builder")
                .body()._return(JExpr._new(bldrCls));
            
            JMethod cpyMthd = cls.method(JMod.PROTECTED|JMod.FINAL, cls, "copy");
            JVar cpyParam = cpyMthd.param(immutableMap.narrow(String.class, Object.class), "params");
            cpyMthd.body()._return(JExpr._new(cls).arg(cpyParam));
            
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private JDefinedClass addBuilderCls(Feed feed, JDefinedClass cls)
            throws JClassAlreadyExistsException, ClassNotFoundException {
        JDefinedClass bldrCls = cls._class(JMod.PUBLIC|JMod.STATIC|JMod.FINAL, "Builder");
        JVar paramBuilder = bldrCls.field(JMod.PRIVATE|JMod.FINAL, immutableMapBldr, "params")
            .init(immutableMap.staticInvoke("builder"));

        addUnsafeUrlArg(bldrCls, paramBuilder);

        addFilters(feed, bldrCls, paramBuilder);

        addMixins(feed, cls, bldrCls, paramBuilder);

        addSorts(feed, cls, bldrCls, paramBuilder);

        JMethod bldMthd = bldrCls.method(JMod.PUBLIC, cls, "build");
        bldMthd.body()._return(JExpr._new(cls).arg(paramBuilder.invoke("build")));

        return bldrCls;
    }

    private void addFilters(Feed feed, JDefinedClass bldrCls, JVar paramBuilder) throws ClassNotFoundException, JClassAlreadyExistsException {
        for (Filter filter : feed.getFilters().getFilter()) {
            if (!Boolean.TRUE.equals(filter.isDeprecated())) {
                addWithersFor(filter, bldrCls, paramBuilder);
            }
        }
    }

    private void addMixins(Feed feed, JDefinedClass cls, JDefinedClass bldrCls, JVar paramBuilder) throws JClassAlreadyExistsException {
        if (feed.getMixins() != null && feed.getMixins().getMixin() != null) {

            List<String> mixinValues = new ArrayList<>();
            for (Mixin mixin : feed.getMixins().getMixin()) {
                mixinValues.add(mixin.getName());
            }
            String mixinEnumName = camel(feed.getName(), true) + "Mixin";
            JDefinedClass mixinEnum = buildEnumClass(mixinEnumName, mixinValues);

            JFieldVar field = cls.field(privateStaticFinal, String.class, "MIXIN");
            field.init(JExpr.lit("mixin"));
            JMethod iterWither = bldrCls.method(JMod.PUBLIC, bldrCls, "withMixins");
            JVar param = iterWither.param(iterable(mixinEnum), "mixins");
            JBlock mthdBody = iterWither.body();
            mthdBody.add(paramBuilder.invoke("put")
                    .arg(field)
                    .arg(immutableList.staticInvoke("copyOf").arg(param)));
            mthdBody._return(JExpr._this());
            JMethod varArgWither = bldrCls.method(JMod.PUBLIC, bldrCls, "withMixins");
            param = varArgWither.varParam(mixinEnum, "mixins");
            varArgWither.body()._return(JExpr.invoke(iterWither)
                    .arg(immutableList.staticInvoke("copyOf").arg(param)));
        }
    }

    private void addSorts(Feed feed, JDefinedClass cls, JDefinedClass bldrCls, JVar paramBuilder) throws JClassAlreadyExistsException {

        if (feed.getSorts() == null || feed.getSorts().getSort() == null) {
            return;
        }

        List<String> sortEnumValues = new ArrayList<>();
        List<String> sortDirectionValues = new ArrayList<>();
        for (Sort sort : feed.getSorts().getSort()) {
            sortEnumValues.add(sort.getName());
            if (sort.getSortDirection() != null) {
                for (SortDirection sortDirection : sort.getSortDirection()) {
                    if (!sortDirectionValues.contains(sortDirection.getValue())) {
                        sortDirectionValues.add(sortDirection.getValue());
                    }
                }
            }
        }
        String sortEnumName = camel(feed.getName(), true) + "Sort";
        JDefinedClass sortEnum = buildEnumClass(sortEnumName, sortEnumValues);
        JFieldVar sortField = cls.field(privateStaticFinal, String.class, "SORT");
        sortField.init(JExpr.lit("sort"));

        JMethod sortBy = bldrCls.method(JMod.PUBLIC, bldrCls, "sortBy");
        JVar sortParam = sortBy.param(sortEnum, "sortField");
        JBlock mthdBody = sortBy.body();
        mthdBody.add(paramBuilder.invoke("put")
                .arg(sortField)
                .arg(precs.staticInvoke("checkNotNull").arg(sortParam)));
        mthdBody._return(JExpr._this());

        //add sort by with direction
        JDefinedClass sortDirectionEnum = null;

        if (sortDirectionValues.size() > 0) {
            String sortDirectionEnumName = camel(feed.getName(), true) + "SortDirection";
            sortDirectionEnum = buildEnumClass(sortDirectionEnumName, sortDirectionValues);
            JFieldVar sortDirectionField = cls.field(privateStaticFinal, String.class, "SORT_DIRECTION");
            sortDirectionField.init(JExpr.lit("sort_direction"));

            JMethod sortByWithDirection = bldrCls.method(JMod.PUBLIC, bldrCls, "sortBy");
            sortParam = sortByWithDirection.param(sortEnum, "sortField");
            JVar sortDirectionParam = sortByWithDirection.param(sortDirectionEnum, "sortDirection");
            mthdBody = sortByWithDirection.body();
            mthdBody.add(paramBuilder.invoke("put")
                    .arg(sortField)
                    .arg(precs.staticInvoke("checkNotNull").arg(sortParam)));
            mthdBody.add(paramBuilder.invoke("put")
                    .arg(sortDirectionField)
                    .arg(precs.staticInvoke("checkNotNull").arg(sortDirectionParam)));

            mthdBody._return(JExpr._this());
        }
    }

    // TODO: MBST-15453.
    /* This is a hack to work around us not supporting overloaded availability queries. Nitro
    supports both enums and ISO8601 durations, we only support enums. Adding enums is hard, adding
    this is simple.
     */
    private void addUnsafeUrlArg(JDefinedClass bldrCls, JVar paramBuilder) {
        JMethod method = bldrCls.method(JMod.PUBLIC, bldrCls, "withUnsafeArbitrary");
        JVar nameParam = method.param(String.class, "name");
        JVar valueParam = method.varParam(String.class, "values");

        JBlock mthdBody = method.body();

        mthdBody.add(
                paramBuilder.invoke("put")
                        .arg(nameParam)
                        .arg(immutableList.staticInvoke("copyOf").arg(valueParam))
        );

        mthdBody._return(JExpr._this());
    }

    private JDefinedClass buildEnumClass(String enumName, List<String> values) throws JClassAlreadyExistsException {
        if (pkg.isDefined(enumName)) {
            return pkg._getClass(enumName);
        }
        JDefinedClass valueEnum = pkg._enum(enumName);

        JFieldVar valField = valueEnum.field(JMod.PRIVATE | JMod.FINAL, String.class, "value");
        JMethod ctor = valueEnum.constructor(JMod.PRIVATE);
        JVar param = ctor.param(String.class, "val");
        ctor.body().assign(valField, param);

        JMethod toString = valueEnum.method(JMod.PUBLIC, String.class, "toString");
        toString.annotate(Override.class);
        toString.body()._return(valField);

        for (String value : values) {
            JEnumConstant sortCnst = valueEnum.enumConstant(value.toUpperCase().replace(' ', '_'));
            sortCnst.arg(JExpr.lit(value));
        }
        return valueEnum;
    }

    private JMethod addConstructor(JDefinedClass cls) {
        JMethod constructor = cls.constructor(JMod.PRIVATE);
        constructor.param(stringObjectMap(model), "params");
        constructor.body().invoke("super")
            .arg(constructor.listParams()[0]);
        return constructor;
    }

    private JClass getTransformedType(Feed feed) {
        Class<?> cls = null;
        String name = feed.getName();
        if(name.equals("Versions")) {
            cls = Version.class;
            return model.ref(cls);
        }
        if(name.equals("Groups")) {
            cls = GroupBody.class;
            return model.ref(cls);
        }
        cls = tryLoadClass(MODEL_PKG + name);
        if (cls == null) {//attempt in possible naive singular.
            cls = tryLoadClass(MODEL_PKG + name.substring(0, name.length()-1));
        }
        if (cls == null) {
            cls = Object.class;
        }
        return model.ref(cls);
    }

    private Class<?> tryLoadClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private JClass stringObjectMap(JCodeModel model) {
        return model.ref(Map.class).narrow(String.class, Object.class);
    }

    private void addWithersFor(Filter filter, JDefinedClass bldrCls, JVar paramBuilder) throws ClassNotFoundException, JClassAlreadyExistsException {
        JDefinedClass cls = (JDefinedClass) bldrCls.parentContainer();
        
        JFieldVar field = cls.field(privateStaticFinal, String.class, sanitize(filter.getName().toUpperCase()));
        field.init(JExpr.lit(filter.getName()));
        
        JClass paramType = mapType(filter);
        if (Boolean.TRUE.equals(filter.isMultipleValues())) {
            JMethod iterableWither = addIterableWither(filter, bldrCls, paramBuilder, field, paramType);
            addVarArgsWither(filter, iterableWither, bldrCls, paramType);
        } else {
            addWither(filter, bldrCls, paramBuilder, field, paramType);
        }
    }

    private String sanitize(String input) {
        return input.replaceAll("-", "_");
    }

    private void addVarArgsWither(Filter filter, JMethod wither, JDefinedClass bldrCls, JClass paramType) throws JClassAlreadyExistsException {
        JMethod method = bldrCls.method(wither.mods().getValue(),
                wither.type(), wither.name());
        if (filter.getTitle()!=null) {
            method.javadoc().add(String.format("<p>%s</p>",filter.getTitle()));
        }
        JVar param = method.varParam(paramType, wither.listParams()[0].name());
        method.body()._return(JExpr.invoke(wither).arg(immutableList.staticInvoke("copyOf").arg(param)));
    }

    private void addWither(Filter filter, JDefinedClass bldrCls, JVar paramBuilder, JFieldVar field, JClass paramType) throws JClassAlreadyExistsException {
        JMethod method = createWitherMethod(filter, bldrCls);
        if (filter.getTitle()!=null) {
            method.javadoc().add(String.format("<p>%s</p>",filter.getTitle()));
        }
        JVar param = addParam(filter, method, paramType);
        JBlock mthdBody = method.body();
        boolean needsNullCheck = true;
        if (filter.getMinValue() != null) {
            mthdBody.add(precs.staticInvoke("checkNotNull").arg(param));
            needsNullCheck = false;
            int min = filter.getMinValue().intValue();
            mthdBody.add(precs.staticInvoke("checkArgument")
                .arg(JExpr.lit(min).lte(param))
                .arg(JExpr.lit(param.name() + ": %s < " + min))
                .arg(param)
            );
        }
        if (filter.getMaxValue() != null) {
            if (needsNullCheck) {
                mthdBody.add(precs.staticInvoke("checkNotNull").arg(param));
                needsNullCheck = false;
            }
            int max = filter.getMaxValue().intValue();
            mthdBody.add(precs.staticInvoke("checkArgument")
                .arg(JExpr.lit(max).gte(param))
                .arg(JExpr.lit(param.name() + ": %s > " + max))
                .arg(param)
            );
        }
        JInvocation putIntoMap = paramBuilder.invoke("put")
                .arg(field);
        if (needsNullCheck) {
            putIntoMap.arg(precs.staticInvoke("checkNotNull").arg(param));
        } else {
            putIntoMap.arg(param);
        }
        mthdBody.add(putIntoMap);
        mthdBody._return(JExpr._this());
    }

    private JMethod addIterableWither(Filter filter, JDefinedClass bldrCls, JVar paramBuilder, JFieldVar field, JClass paramType) throws JClassAlreadyExistsException {
        JMethod method = createWitherMethod(filter, bldrCls);
        if (filter.getTitle()!=null) {
            method.javadoc().add(String.format("<p>%s</p>",filter.getTitle()));
        }
        JVar param = addParam(filter, method, iterable(paramType));
        JBlock mthdBody = method.body();
        mthdBody.add(paramBuilder.invoke("put")
                .arg(field)
                .arg(immutableList.staticInvoke("copyOf").arg(param)));
        mthdBody._return(JExpr._this());
        return method;
    }

    private JVar addParam(Filter filter, JMethod method, JType paramType) {
        return method.param(paramType, camel(sanitize(filter.getName()), false));
    }

    private JMethod createWitherMethod(Filter filter, JDefinedClass bldrCls) {
        String filterMethodName = "with" + camel(sanitize(filter.getName()), true);
        return bldrCls.method(JMod.PUBLIC, bldrCls, filterMethodName);
    }
    
    private JType iterable(JClass mapType) {
        return model.directClass("Iterable").narrow(mapType);
    }
            
    private JClass mapType(Filter filter) throws JClassAlreadyExistsException {
        if (!(filter.getOption() == null || filter.getOption().isEmpty())){
            return getParamTypeEnum(filter);
        }
        String type = filter.getType();
        Class<?> typeCls = typeMap.get(type);
        checkState(typeCls != null, "Unexpected type: %s", type);
        return model.ref(typeCls);
    }

    private JClass getParamTypeEnum(Filter filter) throws JClassAlreadyExistsException {
        String enumName = camel(filter.getName(), true) + "Option";
        if (pkg.isDefined(enumName)) {
            return pkg._getClass(enumName);
        }
        List<Option> options = filter.getOption();
        if (options.size() == 1) {/*turn into '*Only' method?*/}
        if (options.size() == 2) {
            if (ImmutableSet.of("true","false").equals(
                    ImmutableSet.of(options.get(0).getValue().toLowerCase(),
                        options.get(1).getValue().toLowerCase()
                    )
                )) {
                return model.ref(Boolean.class);
            }
        }
        
        
        JDefinedClass valueEnum = pkg._enum(enumName);
        
        JFieldVar valField = valueEnum.field(JMod.PRIVATE|JMod.FINAL, String.class, "value");
        JMethod ctor = valueEnum.constructor(JMod.PRIVATE);
        JVar param = ctor.param(String.class, "val");
        ctor.body().assign(valField, param);
        
        JMethod toString = valueEnum.method(JMod.PUBLIC, String.class, "toString");
        toString.annotate(Override.class);
        toString.body()._return(valField);
        
        for (Option option : options) {
            String optionName = option.getValue().toUpperCase().replace(' ', '_');
            JEnumConstant optionCst = valueEnum.enumConstant(optionName);
            optionCst.arg(JExpr.lit(option.getValue()));
        }
        return valueEnum;
    }

    private String camel(String name, boolean upperInitial) {
        char initial = name.charAt(0);
        StringBuilder converted = new StringBuilder()
            .append(upperInitial ? Character.toUpperCase(initial) : initial);
        
        for (int i = 1; i < name.length(); i++) {
            char chr = name.charAt(i);
            if (chr == '_' && i+1 < name.length()) {
                converted.append(Character.toUpperCase(name.charAt(++i)));
            } else {
                converted.append(chr);
            }
        }
        return converted.toString();
    }

    private void addResultTypeMethod(JCodeModel model, JDefinedClass cls, JClass transformedType) {
        JClass classCls = model.ref(Class.class);
        JMethod method = cls.method(JMod.PROTECTED|JMod.FINAL, classCls.narrow(transformedType), "resultsType");
        method.body()._return(JExpr.dotclass(transformedType));
        method.annotate(Override.class);
    }

    private void addResourcePath(JDefinedClass cls, Feed feed) {
        JFieldVar field = cls.field(privateStaticFinal, String.class, "RESOURCE_PATH");
        field.init(JExpr.lit(feed.getHref()));
        
        JMethod method = cls.method(JMod.PROTECTED|JMod.FINAL, String.class, "resourcePath");
        method.annotate(Override.class);
        method.body()._return(field);
    }

}
