package com.metabroadcast.atlas.glycerin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffIOExceptionHandler;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.google.common.util.concurrent.RateLimiter;

class GlycerinHttpClient {

    private static final String DEVELOPER_OVER_RATE_MSG = "Developer Over Rate";
    private static final String DEVELOPER_INACTIVE_MSG = "Developer Inactive";
    private static final NitroJaxbContext NITRO_JAXB_CONTEXT = new NitroJaxbContext();

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final HostSpecifier host;
    private final String apiKey;
    private final Optional<RateLimiter> limiter;
    private final Optional<String> root;
    
    private final HttpRequestFactory requestFactory;

    public GlycerinHttpClient(HostSpecifier host, String apiKey, Optional<RateLimiter> limiter, Optional<String> root) {
        this.host = checkNotNull(host);
        this.apiKey = checkNotNull(apiKey);
        this.limiter = checkNotNull(limiter);
        this.root = checkNotNull(root);
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
                    request.setParser(new JaxbObjectParser(NITRO_JAXB_CONTEXT.getContext()));
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
    
    public <R, T> GlycerinResponse<T> get(GlycerinQuery<R, T> q) throws GlycerinException {
        try {
            return doGet(q);
        } catch (HttpResponseException hre) {
            throw asGlycerinExcetpion(q.toString(), hre);
        } catch (IOException e) {
            throw new GlycerinException(q.toString(), e);
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

    private <T, R> GlycerinResponse<T> doGet(GlycerinQuery<R, T> q) throws IOException {
        HttpRequest getRequest = requestFactory.buildGetRequest(urlFor(q));
        log.debug("{}", getRequest.getUrl());
        R parsedResponse = getRequest.execute().parseAs(q.type());
        List<T> results = q.transform(parsedResponse);
        GlycerinQuery<R, T> next = q.responseNext(parsedResponse);
        return new BaseGlycerinResponse<R, T>(results, this, next);
    }
    
    private GenericUrl urlFor(GlycerinQuery<?, ?> q) {
        GenericUrl url = new GenericUrl();
        url.setScheme("http");
        url.setHost(host.toString());
        url.set("api_key", apiKey);
        url.setRawPath(root.or("") + q.resourcePath());
        url.putAll(q.queryParameters());
        return url;
    }

}
