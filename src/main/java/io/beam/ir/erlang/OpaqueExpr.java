package io.beam.ir.erlang;

public record OpaqueExpr(String text, SourceSpan source) implements Expression {

  public static OpaqueExpr of(String text) {
    return new OpaqueExpr(text, null);
  }
}
