package com.example.demo.domain.queries;

import com.example.demo.core.queryPipeline.AbstractQueryHandler;
import com.example.demo.domain.Book;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class FindBookByIdQueryHandler extends AbstractQueryHandler<FindBookByIdQuery, Book> {
    @Override
    public Book handler(FindBookByIdQuery findBookByIdQuery) {
        var book = new Book(findBookByIdQuery.id(),
                "title",
                "author",
                new Date());
        return book;
    }
}
