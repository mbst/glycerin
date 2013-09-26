package com.metabroadcast.atlas.glycerin;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpResponseException;
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

    private static final String DEVELOPER_OVER_RATE_MSG = "Developer Over Rate";
    private static final String DEVELOPER_INACTIVE_MSG = "Developer Inactive";
    
    private static final String DEFAULT_HOST = "d.bbc.co.uk";
    
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }
    
    public static final class Builder {

        private final String apiKey;
        
        private HostSpecifier host = HostSpecifier.fromValid(DEFAULT_HOST);
        private Optional<RateLimiter> limiter = Optional.absent();

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
        
        public XmlGlycerin build() {
            return new XmlGlycerin(host, apiKey, limiter);
        }
        
    }
    
    private final GlycerinHttpClient client;

    public XmlGlycerin(HostSpecifier host, String apiKey, Optional<RateLimiter> limiter) {
        this.client = new GlycerinHttpClient(host, apiKey, limiter);
    }

    private <T> GlycerinResponse<T> executeQuery(GlycerinQuery<?, T> query) throws GlycerinException {
        try {
            return client.get(query);
        } catch (HttpResponseException hre) {
            throw asGlycerinExcetpion(query.toString(), hre);
        } catch (IOException e) {
            throw new GlycerinException(query.toString(), e);
        }
    }

    private GlycerinException asGlycerinExcetpion(String url, HttpResponseException hre) {
        String content = hre.getContent();
        Pattern responsePattern = Pattern.compile("<h1>([^<]+)</h1>");
        Matcher matcher = responsePattern.matcher(content);
        if (matcher.matches()) {
            String msg = matcher.group(1);
            if (msg.equals(DEVELOPER_INACTIVE_MSG)) {
                return new GlycerinUnauthorizedException();
            }
            if (msg.equals(DEVELOPER_OVER_RATE_MSG)) {
                return new GlycerinOverRateException();
            }
        }
        return new GlycerinException(url, hre);
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
