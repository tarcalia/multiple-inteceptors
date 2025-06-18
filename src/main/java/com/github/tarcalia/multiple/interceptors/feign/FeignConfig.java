package com.github.tarcalia.multiple.interceptors.feign;

import com.github.tarcalia.multiple.interceptors.interceptors.AuthTokenInterceptor;
import com.github.tarcalia.multiple.interceptors.interceptors.LibraryProvidedInterceptor;
import com.github.tarcalia.multiple.interceptors.interceptors.TrackingIdInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor compositeInterceptor() {
        return new CompositeRequestInterceptor(
                new AuthTokenInterceptor(), new TrackingIdInterceptor(), new LibraryProvidedInterceptor());
    }
}
