package io.beam.ir.elixir;

public record NilPattern(SourceSpan source) implements Pattern {

  public static NilPattern of() {
    return new NilPattern(null);
  }
}
