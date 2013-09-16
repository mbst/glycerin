package com.metabroadcast.atlas.glycerin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.common.net.HostSpecifier;

class GlycerinHttpClient {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final HostSpecifier host;
    private final String apiKey;
    
    private final HttpRequestFactory requestFactory;

    public GlycerinHttpClient(HostSpecifier host, String apiKey) {
        this.host = checkNotNull(host);
        this.apiKey = checkNotNull(apiKey);
        this.requestFactory = new ApacheHttpTransport()
            .createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JaxbObjectParser(new NitroJaxbContext().getContext()));
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
