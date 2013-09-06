package com.metabroadcast.atlas.glycerin;

import com.google.common.collect.ImmutableList;


public class GlycerinResponse<T> {

    private final ImmutableList<T> results;

    public GlycerinResponse(Iterable<T> results) {
        this.results = ImmutableList.copyOf(results);
    }

    public ImmutableList<T> getResults() {
        return results;
    }
    
}
