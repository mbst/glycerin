package com.metabroadcast.atlas.glycerin.queries;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.metabroadcast.atlas.glycerin.GlycerinQuery;
import com.metabroadcast.atlas.glycerin.model.Next;
import com.metabroadcast.atlas.glycerin.model.Nitro;
import com.metabroadcast.atlas.glycerin.model.Pagination;


public abstract class BaseApiQuery<TRANSFORMED> extends GlycerinQuery<Nitro, TRANSFORMED> {

    private final Map<String, Object> params;

    protected BaseApiQuery(Map<String,Object> params) {
        this.params = ImmutableMap.copyOf(params);
    }

    @Override
    protected final Map<String, Object> queryParameters() {
        return params;
    }

    @Override
    protected final Class<Nitro> type() {
        return Nitro.class;
    }
    
    protected abstract Class<TRANSFORMED> resultsType();

    private final Function<Object, TRANSFORMED> toTransformed =
        new Function<Object, TRANSFORMED>() {
            @Override
            public TRANSFORMED apply(Object input) {
                Object res = input;
                if (!resultsType().isAssignableFrom(input.getClass())) {
                    Method method;
                    try {
                        method = resultsType().getMethod("valueOf", Object.class);
                        res = method.invoke(null, input);
                    } catch (Exception e) {
                        throw new RuntimeException(String.format(
                            "Failed to transform %s to %s", 
                            input.getClass().getName(), 
                            resultsType().getName()
                        ), e);
                    }
                }
                return resultsType().cast(res);
            }
        };
    private static final MapSplitter paramSplitter = Splitter.on("&")
            .withKeyValueSeparator("=");
    
    @Override
    protected final List<TRANSFORMED> transform(Nitro raw) {
        List<Object> results = raw.getResults().getBrandOrSeriesOrEpisode();
        return ImmutableList.copyOf(Lists.transform(results, toTransformed));
    }
    
    @Override
    protected BaseApiQuery<TRANSFORMED> responseNext(Nitro raw) {
        String nextHref = nextHref(raw);
        return nextHref == null ? null : copy(toParameters(nextHref));
    }

    private ImmutableMap<String, Object> toParameters(String nextHref) {
        int paramStart = nextHref.indexOf("?")+1;
        String params = nextHref.substring(paramStart);
        return ImmutableMap.<String, Object>copyOf(paramSplitter.split(params));
    }

    protected abstract BaseApiQuery<TRANSFORMED> copy(ImmutableMap<String, Object> params);

    private String nextHref(Nitro raw) {
        String nextHref = null;
        Pagination pagination = raw.getPagination();
        if (pagination != null) {
            Next next = pagination.getNext();
            if (next != null) {
                nextHref = Strings.emptyToNull(next.getHref());
            }
        }
        return nextHref;
    }

    @Override
    public String toString() {
        GenericUrl url = new GenericUrl();
        url.setRawPath(resourcePath());
        url.putAll(params);
        return url.buildRelativeUrl();
    }
    
}
