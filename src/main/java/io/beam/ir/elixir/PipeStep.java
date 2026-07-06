package io.beam.ir.elixir;

import java.util.List;

public record PipeStep(Expression callable, List<Expression> extraArgs, SourceSpan source)
    implements Node {}
