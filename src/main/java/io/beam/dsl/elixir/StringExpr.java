package io.beam.dsl.elixir;

public record StringExpr(String value) implements Expression {

  public static StringExpr of(String value) {
    return new StringExpr(value);
  }
}
