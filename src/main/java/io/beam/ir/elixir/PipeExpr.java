package io.beam.ir.elixir;

import java.util.List;

public record PipeExpr(Expression initial, List<PipeStep> steps, SourceSpan source)
    implements Expression {}
