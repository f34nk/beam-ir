package io.beam.dsl.erlang;

public record StringExpr(String value) implements Expression {

  public static StringExpr of(String value) {
    return new StringExpr(value);
  }
}
