package io.beam.ir.erlang;

public record BinarySegmentExpr(
    Expression expression,
    String literal,
    Integer size,
    String type,
    Integer unit,
    SourceSpan source) {

  public static BinarySegmentExpr literal(String literal) {
    return new BinarySegmentExpr(null, literal, null, null, null, null);
  }

  public static BinarySegmentExpr of(Expression expression) {
    return new BinarySegmentExpr(expression, null, null, null, null, null);
  }

  public static BinarySegmentExpr of(Expression expression, String type) {
    return new BinarySegmentExpr(expression, null, null, type, null, null);
  }

  public static BinarySegmentExpr of(Expression expression, Integer size, String type) {
    return new BinarySegmentExpr(expression, null, size, type, null, null);
  }

  public static BinarySegmentExpr of(
      Expression expression, Integer size, String type, Integer unit) {
    return new BinarySegmentExpr(expression, null, size, type, unit, null);
  }
}
