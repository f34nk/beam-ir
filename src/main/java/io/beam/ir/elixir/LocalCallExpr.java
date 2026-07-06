package io.beam.ir.elixir;

import java.util.List;

public record LocalCallExpr(String function, List<Expression> args, SourceSpan source)
    implements Expression {

  public static LocalCallExpr of(String function, List<Expression> args) {
    return new LocalCallExpr(function, args, null);
  }
}
