package io.beam.ir.erlang;

public record MatchExpr(Pattern pattern, Expression value, Expression body, SourceSpan source)
    implements Expression {

  public static MatchExpr of(Pattern pattern, Expression value, Expression body) {
    return new MatchExpr(pattern, value, body, null);
  }

  public static MatchExpr of(
      Pattern pattern, Expression value, Expression body, SourceSpan source) {
    return new MatchExpr(pattern, value, body, source);
  }

  public static MatchExpr bind(String name, Expression value, Expression body) {
    return of(VariablePattern.of(name), value, body);
  }

  public static MatchExpr bindValue(String name, Expression value) {
    return of(VariablePattern.of(name), value, null);
  }
}
