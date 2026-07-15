package io.beam.dsl.elixir;

import java.util.List;

public record PipeExpr(Expression initial, List<PipeStep> steps) implements Expression {}
