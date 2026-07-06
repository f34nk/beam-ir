package io.beam.ir.elixir;

public record Clause(
    Pattern pattern, Guard guardOrNull, Expression body, SourceSpan source) implements Node {

  public static Clause of(Pattern pattern, Expression body) {
    return new Clause(pattern, null, body, null);
  }

  public static Clause of(Pattern pattern, Guard guard, Expression body) {
    return new Clause(pattern, guard, body, null);
  }
}
