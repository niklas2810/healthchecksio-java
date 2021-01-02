package com.niklasarndt.healthchecksio.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Healthchecks.io denied the request. Are you using an invalid or read-only API key?");
    }
}
