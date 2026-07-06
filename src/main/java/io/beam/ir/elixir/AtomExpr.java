package io.beam.ir.elixir;

public record AtomExpr(String value, SourceSpan source) implements Expression {

  public static AtomExpr of(String value) {
    return new AtomExpr(value, null);
  }
}
