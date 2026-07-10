package io.beam.ir.erlang;

import java.util.List;

public record IfExpr(List<IfClause> clauses, SourceSpan source) implements Expression {

  public static IfExpr of(List<IfClause> clauses) {
    return new IfExpr(clauses, null);
  }

  public static IfExpr of(List<IfClause> clauses, SourceSpan source) {
    return new IfExpr(clauses, source);
  }
}
