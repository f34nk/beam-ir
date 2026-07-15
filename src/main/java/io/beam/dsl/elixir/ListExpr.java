package io.beam.dsl.elixir;

import java.util.List;

public record ListExpr(List<Expression> elements) implements Expression {

  public static ListExpr of(List<Expression> elements) {
    return new ListExpr(elements);
  }
}
