package org.erlik.pipeline_builder;

import org.erlik.pipeline_builder.PipelineSupplier.Supply;

import java.util.List;
import java.util.function.Function;

public interface Pipeline {

    <THandler extends PipelineHandler> Pipeline handlers(Supply<THandler> requestHandlers);

    <TMiddleware extends PipelineMiddleware> Pipeline middlewares(Supply<TMiddleware> middlewares);

    <TRequest> Dispatcher submit(TRequest request);

    interface Dispatcher {

        PipelineBuilder.Dispatcher<?> validate(Function<? super List<? extends PipelineHandler>, Boolean> mapper);

        <TReturn> List<TReturn> dispatch();

        <TReturn> TReturn first();

        List<PipelineHandler> handlers();

        PipelineHandler handler();
    }
}
