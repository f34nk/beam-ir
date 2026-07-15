package io.beam.dsl.elixir;

public record InfixExpr(Expression left, String op, Expression right) implements Expression {}
