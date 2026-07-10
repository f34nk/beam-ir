package io.beam.ir.erlang;

public record QuotedAtomExpr(String value, SourceSpan source) implements Expression {

  public static QuotedAtomExpr of(String value) {
    return new QuotedAtomExpr(value, null);
  }
}
