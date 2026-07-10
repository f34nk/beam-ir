package io.beam.ir.erlang;

public record FunRefExpr(String name, int arity, SourceSpan source) implements Expression {

  public static FunRefExpr of(String name, int arity) {
    return new FunRefExpr(name, arity, null);
  }
}
