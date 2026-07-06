package io.beam.ir.elixir;

public record ConsListPattern(Pattern head, Pattern tail, SourceSpan source) implements Pattern {

  public static ConsListPattern of(Pattern head, Pattern tail) {
    return new ConsListPattern(head, tail, null);
  }
}
