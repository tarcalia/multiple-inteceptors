package com.github.tarcalia.multiple.interceptors.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.UUID;

public class TrackingIdInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Tracking-ID", UUID.randomUUID().toString());
    }
}