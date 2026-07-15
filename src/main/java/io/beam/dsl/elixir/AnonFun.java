package io.beam.dsl.elixir;

import java.util.List;

public record AnonFun(List<AnonFunClause> clauses) implements Expression {}
