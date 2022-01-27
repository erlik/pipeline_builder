package org.erlik.pipeline_builder;

@FunctionalInterface
public interface PipelineMiddleware {

    <TNext> TNext invoke(Next<TNext> next);

    interface Next<TReturn> {

        TReturn invoke();
    }
}
