package io.beam.dsl.elixir;

public record ComparisonGuard(Expression left, String op, Expression right) implements Guard {}
