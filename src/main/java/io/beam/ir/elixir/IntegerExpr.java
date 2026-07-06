package io.beam.ir.elixir;

public record IntegerExpr(long value, SourceSpan source) implements Expression {

  public static IntegerExpr of(long value) {
    return new IntegerExpr(value, null);
  }
}
