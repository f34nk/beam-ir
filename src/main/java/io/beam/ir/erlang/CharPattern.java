package io.beam.ir.erlang;

public record CharPattern(char value, SourceSpan source) implements Pattern {

  public static CharPattern of(char value) {
    return new CharPattern(value, null);
  }
}
