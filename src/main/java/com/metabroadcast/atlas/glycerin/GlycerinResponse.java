package com.metabroadcast.atlas.glycerin;

import com.google.common.collect.ImmutableList;

public interface GlycerinResponse<T> {

    ImmutableList<T> getResults();

    boolean hasNext();

    GlycerinResponse<T> getNext() throws GlycerinException;

}