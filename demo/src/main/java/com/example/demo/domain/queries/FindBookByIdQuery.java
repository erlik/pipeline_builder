package com.example.demo.domain.queries;

import com.example.demo.core.queryPipeline.Query;

import java.util.UUID;

public record FindBookByIdQuery(UUID id) implements Query {
}
