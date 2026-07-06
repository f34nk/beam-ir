package io.beam.ir.elixir;

public record BooleanExpr(boolean value, SourceSpan source) implements Expression {

  public static BooleanExpr of(boolean value) {
    return new BooleanExpr(value, null);
  }
}
