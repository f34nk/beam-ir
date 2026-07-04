package io.beam.ir.erlang;

import java.util.List;

public record TryExpr(Expression body, List<Clause> catchClauses, SourceSpan source)
    implements Expression {

  public static TryExpr of(Expression body, List<Clause> catchClauses) {
    return new TryExpr(body, catchClauses, null);
  }

  public static TryExpr of(Expression body, List<Clause> catchClauses, SourceSpan source) {
    return new TryExpr(body, catchClauses, source);
  }
}
