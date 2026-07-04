package io.beam.ir.erlang;

import java.util.List;

public record Fun(List<FunClause> clauses, SourceSpan source) implements Expression {

  public static Fun of(List<FunClause> clauses) {
    return new Fun(clauses, null);
  }

  public static Fun of(List<FunClause> clauses, SourceSpan source) {
    return new Fun(clauses, source);
  }
}
