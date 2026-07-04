package io.beam.ir.erlang;

public sealed interface Guard
    permits AndGuard, EqualGuard, ExpressionGuard, IsTypeGuard, NotEqualGuard {}
