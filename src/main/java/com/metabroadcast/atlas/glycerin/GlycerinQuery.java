package com.metabroadcast.atlas.glycerin;

import java.util.List;
import java.util.Map;

public abstract class GlycerinQuery<RAW, TRANSFORMED> {

    protected abstract String resourcePath();
    
    protected abstract Map<String, Object> queryParameters();
    
    protected abstract Class<RAW> type();
    
    protected abstract List<TRANSFORMED> transform(RAW raw);

    protected abstract GlycerinQuery<RAW, TRANSFORMED> responseNext(RAW raw);
    
}
