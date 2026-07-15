package io.beam.dsl.elixir;

public record CatchClause(Pattern kind, Pattern reason, Expression body) {}
