package io.beam.ir.erlang;

import java.util.List;

public record TupleExpr(List<Expression> elements, SourceSpan source) implements Expression {

  public static TupleExpr of(List<Expression> elements) {
    return new TupleExpr(elements, null);
  }

  public static TupleExpr of(List<Expression> elements, SourceSpan source) {
    return new TupleExpr(elements, source);
  }
}
