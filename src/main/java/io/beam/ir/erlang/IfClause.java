package io.beam.ir.erlang;

public record IfClause(Guard guard, Expression body, SourceSpan source) implements Node {

  public static IfClause of(Guard guard, Expression body) {
    return new IfClause(guard, body, null);
  }

  public static IfClause of(Guard guard, Expression body, SourceSpan source) {
    return new IfClause(guard, body, source);
  }
}
