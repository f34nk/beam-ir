package io.beam.ir.erlang;

import java.util.List;

public record RemoteCallExpr(
    Expression module, Expression function, List<Expression> arguments, SourceSpan source)
    implements Expression {

  public static RemoteCallExpr of(String module, String function, List<Expression> arguments) {
    return of(AtomExpr.of(module), AtomExpr.of(function), arguments);
  }

  public static RemoteCallExpr of(
      String module, String function, List<Expression> arguments, SourceSpan source) {
    return of(AtomExpr.of(module), AtomExpr.of(function), arguments, source);
  }

  public static RemoteCallExpr of(
      Expression module, Expression function, List<Expression> arguments) {
    return new RemoteCallExpr(module, function, arguments, null);
  }

  public static RemoteCallExpr of(
      Expression module, Expression function, List<Expression> arguments, SourceSpan source) {
    return new RemoteCallExpr(module, function, arguments, source);
  }
}
