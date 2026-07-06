package io.beam.ir.elixir;

public record AssignPattern(String name, Pattern pattern, SourceSpan source) implements Pattern {

  public static AssignPattern of(String name, Pattern pattern) {
    return new AssignPattern(name, pattern, null);
  }
}
