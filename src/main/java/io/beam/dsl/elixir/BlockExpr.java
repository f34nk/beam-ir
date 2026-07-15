package io.beam.dsl.elixir;

import java.util.List;

public record BlockExpr(List<Expression> statements) implements Expression {}
