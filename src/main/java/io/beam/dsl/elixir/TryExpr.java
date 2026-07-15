package io.beam.dsl.elixir;

import java.util.List;

public record TryExpr(Expression body, List<CatchClause> catchClauses) implements Expression {}
