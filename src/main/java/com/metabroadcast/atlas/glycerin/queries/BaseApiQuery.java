package com.metabroadcast.atlas.glycerin.queries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.metabroadcast.atlas.glycerin.GlycerinQuery;
import com.metabroadcast.atlas.glycerin.GlycerinResponse;
import com.metabroadcast.atlas.glycerin.model.Nitro;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


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
    
    @Override
    protected final GlycerinResponse<TRANSFORMED> toResponse(Nitro raw) {
        List<Object> results = raw.getResults().getBrandOrSeriesOrEpisode();
        return new GlycerinResponse<TRANSFORMED>(Lists.transform(results, toTransformed));
    }

}
