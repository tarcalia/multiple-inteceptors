package com.github.tarcalia.multiple.interceptors.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Arrays;
import java.util.List;

public class CompositeRequestInterceptor implements RequestInterceptor {

    private final List<RequestInterceptor> interceptors;

    public CompositeRequestInterceptor(RequestInterceptor... interceptors) {
        this.interceptors = Arrays.stream(interceptors).toList();
    }

    @Override
    public void apply(RequestTemplate template) {
        for (RequestInterceptor interceptor : interceptors) {
            interceptor.apply(template);
        }
    }
}
