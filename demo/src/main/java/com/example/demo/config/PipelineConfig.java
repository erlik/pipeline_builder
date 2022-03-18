package com.example.demo.config;

import com.example.demo.core.commandPipeline.*;
import com.example.demo.core.eventPipeline.*;
import com.example.demo.core.queryPipeline.AbstractQueryHandler;
import com.example.demo.core.queryPipeline.QueryBus;
import com.example.demo.core.queryPipeline.QueryBusImpl;
import com.example.demo.core.queryPipeline.QueryMiddleware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PipelineConfig {

    @Bean
    CommandBus commandBus(
            ObjectProvider<CommandHandler> commandHandlers,
            ObjectProvider<CommandMiddleware> middlewares,
            ObjectProvider<EventHandler> eventHandlers,
            ObjectProvider<EventMiddleware> eventMiddlewares
            ) {

        var eventBus = new EventBusImpl()
                .handlers(eventHandlers::stream)
                .middlewares(eventMiddlewares::orderedStream);

        return new CommandBusImpl()
                .handlers(commandHandlers::stream)
                .middlewares(middlewares::orderedStream)
                .eventBus(eventBus);
    }

    @Bean
    QueryBus queryBus(ObjectProvider<AbstractQueryHandler> queryHandlers,
            ObjectProvider<QueryMiddleware> middlewares) {
        return new QueryBusImpl()
                .handlers(queryHandlers::stream)
                .middlewares(middlewares::orderedStream);
    }
}
