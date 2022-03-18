package com.example.demo.domain;

import java.util.Date;
import java.util.UUID;

public record Book(UUID id,
                   String title,
                   String author,
                   Date creationDate) {
}