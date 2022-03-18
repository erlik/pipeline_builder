package com.example.demo.controller;

import com.example.demo.domain.Book;

import java.util.Date;
import java.util.UUID;

public record BookOutput(UUID id,
                         String title,
                         String author,
                         Date creationDate) {

    public BookOutput(Book book) {
        this(
                book.id(),
                book.title(),
                book.author(),
                book.creationDate()
        );
    }
}