package com.example.demo.core.eventPipeline;

import org.erlik.pipeline_builder.PipelineSupplier;

public interface EventBus {

    EventBusImpl handlers(PipelineSupplier.Supply<EventHandler> requestHandlers);

    EventBusImpl middlewares(PipelineSupplier.Supply<EventMiddleware> middlewares);

    <TRequest extends Event> void dispatch(TRequest request);
}
