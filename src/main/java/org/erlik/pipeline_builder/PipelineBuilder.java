package org.erlik.pipeline_builder;

import org.erlik.pipeline_builder.PipelineMiddleware.Next;
import org.erlik.pipeline_builder.PipelineSupplier.Supply;
import org.erlik.pipeline_builder.validators.ValidationFailedException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class PipelineBuilder
        implements Pipeline {

    @SuppressWarnings("rawtypes")
    protected PipelineSupplier<PipelineHandler> requestHandlers = Stream::empty;
    protected PipelineSupplier<PipelineMiddleware> requestMiddlewares = Stream::empty;

    @Override
    @SuppressWarnings({"rawtypes",
                       "unchecked"})
    public <THandler extends PipelineHandler> PipelineBuilder handlers(Supply<THandler> requestHandlers) {
        Preconditions.isNotNull(requestHandlers, "Handlers must not be null");
        this.requestHandlers = () -> (Stream<PipelineHandler>) requestHandlers.supply();
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <TMiddleware extends PipelineMiddleware> PipelineBuilder middlewares(Supply<TMiddleware> middlewares) {
        Preconditions.isNotNull(middlewares, "Middlewares must not be null");
        this.requestMiddlewares = () -> (Stream<PipelineMiddleware>) middlewares.supply();
        return this;
    }

    @Override
    public <TRequest> Dispatcher<TRequest> submit(TRequest request) {
        return new Dispatcher<>(request);
    }

    private record HandleRequest<TReturn>(Supplier<TReturn> handler)
            implements PipelineMiddleware.Next<TReturn> {

        @Override
        public TReturn invoke() {
            return handler().get();
        }
    }

    public class Dispatcher<TRequest>
            implements Pipeline.Dispatcher {

        @SuppressWarnings({"rawtypes"})
        private final List<PipelineHandler> handlers;
        private final TRequest request;

        @SuppressWarnings({"unchecked"})
        private Dispatcher(TRequest request) {
            this.request = request;
            this.handlers = requestHandlers
                    .get()
                    .filter(handler -> handler.matches(request))
                    .collect(toList());
        }

        @Override
        @SuppressWarnings({"rawtypes"})
        public Dispatcher<?> validate(Function<? super List<? extends PipelineHandler>, Boolean> mapper) {
            if (mapper.apply(this.handlers)) {
                return this;
            }
            throw new ValidationFailedException();
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public <TReturn> List<TReturn> dispatch() {
            return handlers
                    .stream()
                    .map(handler -> {
                        Next<TReturn> supplier = new HandleRequest<>(() ->
                                (TReturn) handler.handle(request));
                        return requestMiddlewares
                                .spread(supplier, (step, next) -> () -> step.invoke(next))
                                .invoke();
                    })
                    .collect(toList());
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public <TReturn> TReturn first() {
            return (TReturn) dispatch().stream().findFirst().orElse(null);
        }
    }
}
