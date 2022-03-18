package com.example.demo.core.queryPipeline;

import org.erlik.pipeline_builder.PipelineHandler;

public interface QueryHandler <TRequest, TReturn>
        extends PipelineHandler<TRequest, TReturn> {
    TReturn handler(TRequest request);
}
