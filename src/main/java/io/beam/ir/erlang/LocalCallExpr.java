package io.beam.ir.erlang;

import java.util.List;

public record LocalCallExpr(String function, List<Expression> arguments, SourceSpan source)
    implements Expression {

  public static LocalCallExpr of(String function, List<Expression> arguments) {
    return new LocalCallExpr(function, arguments, null);
  }

  public static LocalCallExpr of(String function, List<Expression> arguments, SourceSpan source) {
    return new LocalCallExpr(function, arguments, source);
  }
}
