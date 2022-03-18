package com.example.demo.domain.commands;

import com.example.demo.core.commandPipeline.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddBookCommandHandler extends AbstractCommandHandler<AddBookCommand, UUID> {

    @Override
    public UUID handler(AddBookCommand addPoemCommand) {
        return UUID.randomUUID();
    }
}
