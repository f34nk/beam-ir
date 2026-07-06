package io.beam.ir.elixir;

public record MatchExpr(String name, Expression value, Expression bodyOrNull, SourceSpan source)
    implements Expression {

  public static MatchExpr bind(String name, Expression value) {
    return new MatchExpr(name, value, null, null);
  }

  public static MatchExpr bind(String name, Expression value, Expression body) {
    return new MatchExpr(name, value, body, null);
  }
}
