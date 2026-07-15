package io.beam.dsl.elixir;

import java.util.List;

public record InterpolatedStringExpr(List<InterpolatedSegment> segments) implements Expression {}
