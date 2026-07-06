package io.beam.ir.elixir;

public record RaiseExpr(
    Expression exception, Expression messageOrNull, boolean parenthesized, SourceSpan source)
    implements Expression {

  public static RaiseExpr of(Expression exception, Expression message) {
    return new RaiseExpr(exception, message, false, null);
  }

  public static RaiseExpr parenthesized(Expression exception, Expression message) {
    return new RaiseExpr(exception, message, true, null);
  }
}
