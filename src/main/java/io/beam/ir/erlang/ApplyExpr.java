package io.beam.ir.erlang;

import java.util.List;

public record ApplyExpr(Expression callee, List<Expression> arguments, SourceSpan source)
    implements Expression {

  public static ApplyExpr of(Expression callee, List<Expression> arguments) {
    return new ApplyExpr(callee, arguments, null);
  }

  public static ApplyExpr of(Expression callee, List<Expression> arguments, SourceSpan source) {
    return new ApplyExpr(callee, arguments, source);
  }
}
