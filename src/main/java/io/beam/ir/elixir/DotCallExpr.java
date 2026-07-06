package io.beam.ir.elixir;

import java.util.List;

public record DotCallExpr(
    Expression receiver, String function, List<Expression> args, SourceSpan source)
    implements Expression {}
