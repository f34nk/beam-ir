package io.beam.ir.elixir;

public record ConcatPattern(Pattern left, Pattern right, SourceSpan source) implements Pattern {

  public static ConcatPattern of(Pattern left, Pattern right) {
    return new ConcatPattern(left, right, null);
  }
}
