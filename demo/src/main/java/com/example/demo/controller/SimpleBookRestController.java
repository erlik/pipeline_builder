package com.example.demo.controller;

import com.example.demo.core.commandPipeline.CommandBus;
import com.example.demo.core.queryPipeline.QueryBus;
import com.example.demo.domain.commands.AddBookCommand;
import com.example.demo.domain.Book;
import com.example.demo.domain.queries.FindBookByIdQuery;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("book")
public class SimpleBookRestController {

    private final QueryBus queryBus;
    private final CommandBus commandBus;

    public SimpleBookRestController(QueryBus queryBus, CommandBus commandBus) {
        this.queryBus = queryBus;
        this.commandBus = commandBus;
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public BookOutput getBook(@PathVariable UUID id) {
        var findBookByIdQuery = new FindBookByIdQuery(id);
        Book book = queryBus.dispatch(findBookByIdQuery);
        return new BookOutput(book);
    }

    @PostMapping(produces = "application/json")
    public UUID addBook(BookInput book) {
        var addBookCommand = new AddBookCommand(book.title(), book.author());
        return commandBus.dispatch(addBookCommand);
    }
}