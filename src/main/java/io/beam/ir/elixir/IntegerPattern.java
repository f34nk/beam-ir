package io.beam.ir.elixir;

public record IntegerPattern(long value, SourceSpan source) implements Pattern {

  public static IntegerPattern of(long value) {
    return new IntegerPattern(value, null);
  }
}
