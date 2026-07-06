package io.beam.ir.elixir;

import java.util.List;

public record CaseExpr(Expression subjectOrNull, List<Clause> clauses, SourceSpan source)
    implements Expression {

  public static CaseExpr piped(List<Clause> clauses) {
    return new CaseExpr(null, clauses, null);
  }
}
