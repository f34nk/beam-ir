package io.beam.ir.erlang;

import java.util.List;

public record CaseExpr(Expression expression, List<Clause> clauses, SourceSpan source)
    implements Expression {

  public static CaseExpr of(Expression expression, List<Clause> clauses) {
    return new CaseExpr(expression, clauses, null);
  }

  public static CaseExpr of(Expression expression, List<Clause> clauses, SourceSpan source) {
    return new CaseExpr(expression, clauses, source);
  }
}
