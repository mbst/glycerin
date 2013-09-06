package com.metabroadcast.atlas.glycerin.queries;

import java.util.Map;

import com.metabroadcast.atlas.glycerin.GlycerinQuery;
import com.metabroadcast.atlas.glycerin.GlycerinResponse;
import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Feeds;
import com.google.common.collect.ImmutableMap;


public class FeedsQuery extends GlycerinQuery<Feeds, Feed> {

    @Override
    protected String resourcePath() {
        return "/nitro/api";
    }

    @Override
    protected Map<String, Object> queryParameters() {
        return ImmutableMap.of();
    }

    @Override
    protected Class<Feeds> type() {
        return Feeds.class;
    }

    @Override
    protected GlycerinResponse<Feed> toResponse(Feeds raw) {
        return new GlycerinResponse<Feed>(raw.getFeed());
    }
}
