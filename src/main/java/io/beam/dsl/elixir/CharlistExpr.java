package io.beam.dsl.elixir;

public record CharlistExpr(String value) implements Expression {

  public static CharlistExpr of(String value) {
    return new CharlistExpr(value);
  }
}
