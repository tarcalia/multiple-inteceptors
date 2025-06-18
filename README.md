# Feign Clients with Multiple Interceptors in Spring Boot

When working with Spring Boot and OpenFeign, it's quite common to add custom headers to outbound HTTP requests — like tracking IDs, authentication tokens, or metadata provided by libraries. But what happens when you want to add **multiple** interceptors in a clean, reusable, and testable way?

This blog post shows you how to chain multiple Feign `RequestInterceptor` instances using a **composite pattern**. You'll learn how to centralize header management, avoid duplication, and write integration tests to verify everything works as expected.

---

## Step 1: Define a Feign Client

Let's start with a basic Feign client interface:

```java
@FeignClient(
    name = "fooClient",
    url = "${foo.base-url}",
    configuration = FeignConfig.class
)
public interface FooClient {
    @GetMapping("/api/internal/foo")
    String callInternalFoo();
}
```

In `application.yml`, we define the base URL:

```yaml
foo:
  base-url: ${FOO_BASE_URL:http://localhost:8080}
```

> ⚠️ **Important:** If you're calling your own application (localhost), be careful not to create a circular call like `/foo` → Feign → `/internal/foo` → Feign → ... This can cause a stack overflow or exhaust your thread pool. Ideally, use a mock server or a different port/environment for internal calls.

---

## Step 2: The Magic — Composite Interceptor

Here's the core idea: one interceptor that delegates to multiple others.

```java
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
```

---

## Step 3: Define Individual Interceptors

You can create small, focused interceptors for each concern — even put them into separate modules/libraries.

```java
public class TrackingIdInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Tracking-ID", UUID.randomUUID().toString());
    }
}

public class AuthTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Auth-Token", "mockedTokenValue");
    }
}

public class LibraryProvidedInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-Library", "libValue");
    }
}
```

---

## Step 4: Configure the Feign Client

Now we plug everything together in a configuration class:

```java
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor compositeInterceptor() {
        return new CompositeRequestInterceptor(
            new AuthTokenInterceptor(),
            new TrackingIdInterceptor(),
            new LibraryProvidedInterceptor()
        );
    }
}
```

> 💡 Pro tip: Avoid mixing `@Component` and `@Bean` registration for interceptors. Pick one method to avoid registering the same interceptor multiple times.

---

## Step 5: Enable Feign Clients

Make sure your application is configured to scan for Feign clients:

```java
@EnableFeignClients
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

---

## ✅ Result — Interceptors Are Applied

When your application makes a call via the `FooClient`, all configured interceptors will be applied and headers added to the request. For example:

```
X-Auth-Token: [mockedTokenValue]
X-Library: [libValue]
X-Tracking-ID: [4dfd7515-b73b-4a09-bc52-0d4de77d850f]
```

---

## 🧪 Bonus: Verifying Headers with WireMock

You can easily write an integration test using WireMock to verify that all headers are correctly sent.

```java
verify(getRequestedFor(urlEqualTo("/api/internal/foo"))
    .withHeader("X-Auth-Token", equalTo("mockedTokenValue"))
    .withHeader("X-Library", equalTo("libValue"))
    .withHeader("X-Tracking-ID", matchingUuid()));
```

This ensures that all interceptors are executed as expected.

---

## 🙌 Wrap-up

This pattern makes your Feign interceptors more modular, testable, and maintainable. You can add/remove behavior without rewriting configuration logic, and avoid the trap of overcomplicated header management.

Thanks for reading — and if this helped you, feel free to ⭐ the [GitHub repo](https://github.com/tarcalia/multiple-inteceptors) or share the article!

Happy coding!

