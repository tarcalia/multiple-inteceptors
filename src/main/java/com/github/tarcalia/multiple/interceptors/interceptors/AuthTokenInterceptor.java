package com.github.tarcalia.multiple.interceptors.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Auth-Token", "mockedTokenValue");
    }
}