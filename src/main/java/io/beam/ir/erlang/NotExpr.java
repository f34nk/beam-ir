package io.beam.ir.erlang;

public record NotExpr(Expression expression, SourceSpan source) implements Expression {

  public static NotExpr of(Expression expression) {
    return new NotExpr(expression, null);
  }
}
