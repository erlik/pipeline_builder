package com.example.demo.core.eventPipeline;

import org.erlik.pipeline_builder.PipelineHandler;

public interface EventHandler<TRequest, TReturn>
        extends PipelineHandler<TRequest, TReturn> {
    TReturn handler(TRequest request);
}
