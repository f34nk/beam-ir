package io.beam.ir.erlang;

import java.util.List;

public record ListExpr(List<Expression> elements, Expression tail, SourceSpan source)
    implements Expression {

  public static ListExpr of(List<Expression> elements) {
    return new ListExpr(elements, null, null);
  }

  public static ListExpr of(List<Expression> elements, Expression tail) {
    return new ListExpr(elements, tail, null);
  }

  public static ListExpr of(List<Expression> elements, SourceSpan source) {
    return new ListExpr(elements, null, source);
  }
}
