package com.metabroadcast.atlas.glycerin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.google.common.util.concurrent.RateLimiter;

class GlycerinHttpClient {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final HostSpecifier host;
    private final String apiKey;
    private final Optional<RateLimiter> limiter;
    
    private final HttpRequestFactory requestFactory;

    public GlycerinHttpClient(HostSpecifier host, String apiKey, Optional<RateLimiter> limiter) {
        this.host = checkNotNull(host);
        this.apiKey = checkNotNull(apiKey);
        this.limiter = checkNotNull(limiter);
        this.requestFactory = initRequestFactory();
    }

    private HttpRequestFactory initRequestFactory() {
        return new ApacheHttpTransport()
            .createRequestFactory(new HttpRequestInitializer() {
                
                private final ExponentialBackOff.Builder BACK_OFF = new ExponentialBackOff.Builder()
                    .setInitialIntervalMillis(500);
                
                @Override
                public void initialize(HttpRequest request) {
                    request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(backOff()));
                    request.setIOExceptionHandler(new HttpBackOffIOExceptionHandler(backOff()));
                    request.setParser(new JaxbObjectParser(new NitroJaxbContext().getContext()));
                    request.setInterceptor(new HttpExecuteInterceptor() {
                        @Override
                        public void intercept(HttpRequest request) throws IOException {
                            if (limiter.isPresent()) {
                                limiter.get().acquire();
                            }
                        }
                    });
                }

                private BackOff backOff() {
                    return BACK_OFF.build();
                }
            });
    }
    
    public <R, T> GlycerinResponse<T> get(GlycerinQuery<R, T> q) throws IOException {
        HttpRequest getRequest = requestFactory.buildGetRequest(urlFor(q));
        log.debug("{}", getRequest.getUrl());
        return q.toResponse(getRequest.execute().parseAs(q.type()));
    }
    
    private GenericUrl urlFor(GlycerinQuery<?, ?> q) {
        GenericUrl url = new GenericUrl();
        url.setScheme("http");
        url.setHost(host.toString());
        url.set("api_key", apiKey);
        url.setRawPath(q.resourcePath());
        url.putAll(q.queryParameters());
        return url;
    }

}
