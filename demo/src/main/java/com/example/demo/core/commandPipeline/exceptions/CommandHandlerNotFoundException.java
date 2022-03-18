package com.example.demo.core.commandPipeline.exceptions;

import com.example.demo.core.commandPipeline.Command;

public class CommandHandlerNotFoundException
        extends RuntimeException {

    private final String commandClass;

    public <TCommand extends Command> CommandHandlerNotFoundException(TCommand command) {
        this.commandClass = command.getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return "Cannot find a matching handler for " + commandClass + " command";
    }
}