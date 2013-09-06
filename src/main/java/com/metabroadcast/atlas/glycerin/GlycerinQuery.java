package com.metabroadcast.atlas.glycerin;

import java.util.Map;

public abstract class GlycerinQuery<RAW, TRANSFORMED> {

    protected abstract String resourcePath();
    
    protected abstract Map<String, Object> queryParameters();
    
    protected abstract Class<RAW> type();
    
    protected abstract GlycerinResponse<TRANSFORMED> toResponse(RAW raw);
    
}
