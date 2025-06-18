package com.github.tarcalia.multiple.interceptors.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "fooClient",
        url = "${foo.base-url}",
        configuration = FeignConfig.class
)
public interface FooClient {
    @GetMapping("/api/internal/foo")
    String callInternalFoo();
}