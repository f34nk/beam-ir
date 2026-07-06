package io.beam.ir.elixir;

public record NilExpr(SourceSpan source) implements Expression {

  public static NilExpr of() {
    return new NilExpr(null);
  }
}
