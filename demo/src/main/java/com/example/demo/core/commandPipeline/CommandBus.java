package com.example.demo.core.commandPipeline;

import com.example.demo.core.eventPipeline.AbstractEventHandler;
import com.example.demo.core.eventPipeline.EventBus;
import com.example.demo.core.eventPipeline.EventMiddleware;
import org.erlik.pipeline_builder.PipelineSupplier;

public interface CommandBus {

    CommandBus handlers(PipelineSupplier.Supply<CommandHandler> requestHandlers);

    CommandBus middlewares(PipelineSupplier.Supply<CommandMiddleware> middlewares);

    CommandBus eventBus(EventBus eventBus);

    <TRequest extends Command, TReturn> TReturn dispatch(TRequest request);
}
