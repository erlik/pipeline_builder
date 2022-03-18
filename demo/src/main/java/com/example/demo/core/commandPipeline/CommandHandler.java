package com.example.demo.core.commandPipeline;

import com.example.demo.core.eventPipeline.Event;
import org.erlik.pipeline_builder.PipelineHandler;

import java.util.List;

public interface CommandHandler <TRequest, TReturn> extends PipelineHandler<TRequest, TReturn> {
    TReturn handler(TRequest request);

    boolean addEvent(Event event);

    List<Event> events();

    boolean removeEvent(Event event);

    void clearEventsList();
}
