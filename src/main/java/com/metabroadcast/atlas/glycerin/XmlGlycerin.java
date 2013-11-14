package com.metabroadcast.atlas.glycerin;

import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.google.common.util.concurrent.RateLimiter;
import com.metabroadcast.atlas.glycerin.model.Availability;
import com.metabroadcast.atlas.glycerin.model.Broadcast;
import com.metabroadcast.atlas.glycerin.model.Programme;
import com.metabroadcast.atlas.glycerin.model.Service;
import com.metabroadcast.atlas.glycerin.model.Version;
import com.metabroadcast.atlas.glycerin.queries.AvailabilityQuery;
import com.metabroadcast.atlas.glycerin.queries.BroadcastsQuery;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesQuery;
import com.metabroadcast.atlas.glycerin.queries.ServicesQuery;
import com.metabroadcast.atlas.glycerin.queries.VersionsQuery;

public class XmlGlycerin implements Glycerin {
    
    private static final String DEFAULT_HOST = "d.bbc.co.uk";
    
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }
    
    public static final class Builder {

        private final String apiKey;
        
        private HostSpecifier host = HostSpecifier.fromValid(DEFAULT_HOST);
        private Optional<RateLimiter> limiter = Optional.absent();
        private Optional<String> root = Optional.absent();

        public Builder(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public Builder withHost(HostSpecifier host) {
            this.host = host;
            return this;
        }
        
        public Builder withLimiter(RateLimiter limiter) {
            this.limiter = Optional.of(limiter);
            return this;
        }
        
        public Builder withRootResource(String root) {
            this.root = Optional.of(root);
            return this;
        }
        
        public XmlGlycerin build() {
            return new XmlGlycerin(host, apiKey, limiter, root);
        }
        
    }
    
    private final GlycerinHttpClient client;

    public XmlGlycerin(HostSpecifier host, String apiKey, Optional<RateLimiter> limiter, Optional<String> root) {
        this.client = new GlycerinHttpClient(host, apiKey, limiter, root);
    }

    private <T> GlycerinResponse<T> executeQuery(GlycerinQuery<?, T> query) throws GlycerinException {
        return client.get(query);
    }

    @Override
    public GlycerinResponse<Programme> execute(ProgrammesQuery query) throws GlycerinException {
        return executeQuery(query);
    }

    @Override
    public GlycerinResponse<Availability> execute(AvailabilityQuery query) throws GlycerinException {
        return executeQuery(query);
    }

    @Override
    public GlycerinResponse<Broadcast> execute(BroadcastsQuery query) throws GlycerinException {
        return executeQuery(query);
    }

    @Override
    public GlycerinResponse<Service> execute(ServicesQuery query) throws GlycerinException {
        return executeQuery(query);
    }

    @Override
    public GlycerinResponse<Version> execute(VersionsQuery query) throws GlycerinException {
        return executeQuery(query);
    }
    
}
