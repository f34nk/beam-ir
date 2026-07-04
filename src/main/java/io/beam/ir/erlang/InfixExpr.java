package io.beam.ir.erlang;

public record InfixExpr(Expression left, String operator, Expression right, SourceSpan source)
    implements Expression {

  public static InfixExpr of(Expression left, String operator, Expression right) {
    return new InfixExpr(left, operator, right, null);
  }

  public static InfixExpr of(
      Expression left, String operator, Expression right, SourceSpan source) {
    return new InfixExpr(left, operator, right, source);
  }
}
