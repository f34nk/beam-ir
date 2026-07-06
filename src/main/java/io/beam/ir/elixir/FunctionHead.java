package io.beam.ir.elixir;

import java.util.List;

public record FunctionHead(List<Pattern> params, Guard guardOrNull, SourceSpan source)
    implements Node {

  public static FunctionHead of(List<Pattern> params) {
    return new FunctionHead(params, null, null);
  }

  public static FunctionHead of(List<Pattern> params, Guard guard) {
    return new FunctionHead(params, guard, null);
  }

  @Override
  public SourceSpan source() {
    return source;
  }
}
