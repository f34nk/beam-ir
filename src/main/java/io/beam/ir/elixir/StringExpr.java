package io.beam.ir.elixir;

public record StringExpr(String value, SourceSpan source) implements Expression {

  public static StringExpr of(String value) {
    return new StringExpr(value, null);
  }
}
