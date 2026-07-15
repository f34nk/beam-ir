package io.beam.dsl.elixir;

import java.util.List;

public record PipeStep(Expression callable, List<Expression> extraArgs) {}
