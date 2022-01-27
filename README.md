# PipelineBuilder

[![Java CI with Maven](https://github.com/erlik/pipeline_builder/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/erlik/pipeline_builder/actions/workflows/maven.yml)
[![Coverage Report](https://github.com/erlik/pipeline_builder/actions/workflows/coverage.yml/badge.svg)](https://github.com/erlik/pipeline_builder/actions/workflows/coverage.yml)

> **PipelineBuilder** is a lightweight pipeline processing and a powerfull helper to create requestBus and requestBus

- [How to use](#how-to-use)
- [Create a simple Request Bus](#create-a-simple-request-bus)
    - [Requests](#requests)
    - [Handlers](#handlers)
    - [Middlewares](#middlewares)
    - [Submit a request](#submit-a-request)
- [Create a Custom RequestBus](#create-a-custom-requestBus)

# How to use

PipelineBuilder has no dependencies. All you need is a single 17KB library:
Java version required: 17+.

# Create a simple Request Bus

A **requestBus** mediates between requests and handlers. You send requests to the requestBus. When
the requestBus receives a request, it sends the request through a sequence of middlewares and
finally invokes the matching request requestHandler.

## Requests

A **Request** is a request that can return a value. The `Ping` request below returns a string:

```java
class Ping {

    public final String host;

    public Ping(String host) {
        this.host = host;
    }
}
```

or, with Java 17

```java
record Ping(String host) {

}
```

## Handlers

For every request you must define a **Handler**, that knows how to handle the request.

Create a `RequestHandler<Q, R>` interface, extending PipelineHandler, where `Q` is a request type
and `R` the return type.

```java
public interface RequestHandler<TRequest, TReturn>
    extends RequestHandler<TRequest, TReturn> {

    @Override
    TReturn handle(TRequest request) {
        return handler(request);
    }

    TReturn handler(TRequest request);
}
```

If a request has nothing to return, you can use a `Void` return type and :

```java
public interface RequestHandler<TRequest>
    extends RequestHandler<TRequest, Void> {

    @Override
    Void handle(TRequest request) {
        handler(request);
        return;
    }

    void handler(TRequest request);
}
```

Now create a requestHandler by implementing `RequestHandler`
Handler's return type must match request's return type:

```java
class PongHandler
    implements RequestHandler<Ping, String> {

    @Override
    public String handler(Ping request) {
        return "Pong from " + request.host;
    }
}
```

If a request has nothing to return, you can use a `Void` return type:

```java
class NothingHandler
    implements RequestHandler<Ping, Void> {

    @Override
    public Void handler(Ping request) {
        "Pong from " + request.host;
        return;
    }
}
```

### Custom Matching
By default, request handlers are being resolved using generics. By overriding request
requestHandler's `matches` method, you can dynamically select a matching requestHandler:

```java
class LocalhostPong
    implements RequestHandler<Ping, String> {

    @Override
    public boolean matches(Ping request) {
        return request.host.equals("localhost");
    }

}
```

```java
class NonLocalhostPong
    implements RequestHandler<Ping, String> {

    @Override
    public boolean matches(Ping request) {
        return !request.host.equals("localhost");
    }
}
```

## Middlewares

`PipelineBuilder` can receive an optional, **ordered list** of custom middlewares. Every request
will go through the middlewares before being handled. Use middlewares when you want to add extra
behavior to request handlers, such as logging, transactions or metrics:

```java
// middleware that logs every request and the result it returns
class Loggable
    implements RequestMiddleware {

    @Override
    public <R, C> R invoke(C request, Next<R> next) {
        // log request
        R response = next.invoke();
        // log response
        return response;
    }
}

// middleware that wraps a request in a transaction
class Transactional
    implements RequestMiddleware {

    @Override
    public <R, C> R invoke(C request, Next<R> next) {
        // start tx
        R response = next.invoke();
        // end tx
        return response;
    }
}
```

In the following requestBus, every request and its response will be logged, plus requests will be
wrapped in a transaction:

```java
RequestBus requestBus=new PipelineBuilder()
    .handlers(new Pong())
    .middlewares(new Loggable(),new Transactional())
    );
```

## Submit a request

When a request is submitted, `PipelineBuilder` finds handlers that match the request, calls
their `handle` method and return the results.

```java
 List responses=requestBus.submit(request)
    .dispatch();
```

To get the first response, it's possible tu use `first()` methode :

```java
 TReturn response=requestBus.submit(request)
    .dispatch()
    .first;
```

### Validators

`PipelineBuilder` supports a custom validator system, using GenericValidation :
It's can throw an exception if conditions are not valid. For exemple, if no handler matches with a
request :

```java
TReturn result=requestBus.submit(request)
    .dispatch()
        .validate(h->GenericValidation.from(h)
        .expected(PipelineValidatorUtil.notEmpty())
        .orThrow(HandlerNotFoundException::new))
    .first();
```

It is possible to chain the validations :

```java
TReturn result=requestBus.submit(request)
    .dispatch()
    .validate(h->GenericValidation.from(h)
        .expected(PipelineValidatorUtil.notEmpty())
        .orThrow(HandlerNotFoundException::new))
    .validate(handlers->GenericValidation.from(handlers)
        .expected(PipelineValidatorUtil.onlyOne())
        .orThrow(HasMultipleHandlersException::new))
    .first();
```

## Create a Custom RequestBus
To construct a `RequestBus`, it's possible to use directly an instance of `PipelineBuilder` and
provide a list of request handlers.
But a better practice is to create a `RequestBus` interface and a default
implementation `RequestBusImpl` using `PipelineBuilder`:

```java
public interface RequestBus {

    RequestBusImpl handlers(Supply<RequestHandler> requestHandlers);

    RequestBusImpl middlewares(Supply<RequestMiddleware> middlewares);

    <TRequest extends Request, TReturn> TReturn dispatch(TRequest request);
}
```

```java
public class RequestBusImpl
    implements RequestBus {

    private final Pipeline genericPipeline = new PipelineBuilder();

    @Override
    public RequestBusImpl handlers(Supply<RequestHandler> requestHandlers) {
        this.genericPipeline.handlers(requestHandlers);
        return this;
    }

    @Override
    public RequestBusImpl middlewares(Supply<RequestMiddleware> middlewares) {
        this.genericPipeline.middlewares(middlewares);
        return this;
    }

    @Override
    public <TRequest extends Request, TReturn> TReturn dispatch(TRequest request) {
        RequestResponse<TReturn> result = this.genericPipeline.submit(request)
            .validate(handlers -> GenericValidation.from(handlers)
                .expected(PipelineValidatorUtil.notEmpty())
                .orThrow(() -> new RequestHandlerNotFoundException(request)))
            .validate(handlers -> GenericValidation.from(handlers)
                .expected(PipelineValidatorUtil.onlyOne())
                .orThrow(() -> new RequestHasMultipleHandlersException(request,
                    handlers)))
            .first();

        return result.result();
    }
}
```
