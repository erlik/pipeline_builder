package com.example.demo.domain.commands;

import com.example.demo.core.commandPipeline.Command;

public record AddBookCommand(String title,
                             String author)
        implements Command {
}
