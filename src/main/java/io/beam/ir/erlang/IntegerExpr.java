package io.beam.ir.erlang;

public record IntegerExpr(long value, SourceSpan source) implements Expression {

  public static IntegerExpr of(long value) {
    return new IntegerExpr(value, null);
  }

  public static IntegerExpr of(long value, SourceSpan source) {
    return new IntegerExpr(value, source);
  }
}
