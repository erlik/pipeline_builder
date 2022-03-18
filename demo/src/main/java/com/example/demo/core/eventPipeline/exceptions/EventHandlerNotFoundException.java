package com.example.demo.core.eventPipeline.exceptions;

import com.example.demo.core.eventPipeline.Event;

public class EventHandlerNotFoundException
        extends RuntimeException {

    private final String eventClass;

    public <TEvent extends Event> EventHandlerNotFoundException(TEvent event) {
        this.eventClass = event.getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return "Cannot find a matching handler for " + eventClass + " event";
    }
}