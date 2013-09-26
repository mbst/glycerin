package com.metabroadcast.atlas.glycerin;


public class GlycerinUnauthorizedException extends GlycerinException {

    public GlycerinUnauthorizedException() {
    }

    public GlycerinUnauthorizedException(String message) {
        super(message);
    }

    public GlycerinUnauthorizedException(Throwable cause) {
        super(cause);
    }

    public GlycerinUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

}
