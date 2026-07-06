package io.beam.ir.elixir;

import java.util.List;

public record InterpolatedStringExpr(List<InterpolatedSegment> segments, SourceSpan source)
    implements Expression {}
