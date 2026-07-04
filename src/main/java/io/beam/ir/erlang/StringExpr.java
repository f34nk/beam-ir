package io.beam.ir.erlang;

public record StringExpr(String value, SourceSpan source) implements Expression {

  public static StringExpr of(String value) {
    return new StringExpr(value, null);
  }

  public static StringExpr of(String value, SourceSpan source) {
    return new StringExpr(value, source);
  }
}
