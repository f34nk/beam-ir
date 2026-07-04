package io.beam.ir.erlang;

public record ExpressionGuard(Expression expression, SourceSpan source) implements Guard {

  public static ExpressionGuard of(Expression expression) {
    return new ExpressionGuard(expression, null);
  }

  public static ExpressionGuard of(Expression expression, SourceSpan source) {
    return new ExpressionGuard(expression, source);
  }
}
