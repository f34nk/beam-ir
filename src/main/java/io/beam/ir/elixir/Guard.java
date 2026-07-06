package io.beam.ir.elixir;

public sealed interface Guard
    permits IsTypeGuard,
        ComparisonGuard,
        AndGuard,
        OrGuard,
        FunctionArityGuard {}
