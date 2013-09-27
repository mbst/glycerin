package com.metabroadcast.atlas.glycerin.queries;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.metabroadcast.atlas.glycerin.GlycerinQuery;
import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Feeds;


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
    protected List<Feed> transform(Feeds raw) {
        return raw.getFeed();
    }
    
    @Override
    protected GlycerinQuery<Feeds, Feed> responseNext(Feeds raw) {
        return null;
    }
    
}
