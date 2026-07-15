package io.beam.dsl.elixir;

public record IfExpr(
    Expression condition, Expression thenBranch, Expression elseBranchOrNull, boolean inline)
    implements Expression {}
