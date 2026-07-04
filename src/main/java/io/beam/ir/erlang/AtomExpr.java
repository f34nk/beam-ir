package io.beam.ir.erlang;

public record AtomExpr(String value, SourceSpan source) implements Expression {

  public static AtomExpr of(String value) {
    return new AtomExpr(value, null);
  }

  public static AtomExpr of(String value, SourceSpan source) {
    return new AtomExpr(value, source);
  }
}
