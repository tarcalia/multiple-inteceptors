package com.github.tarcalia.multiple.interceptors.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class LibraryProvidedInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Library", "libValue");
    }
}