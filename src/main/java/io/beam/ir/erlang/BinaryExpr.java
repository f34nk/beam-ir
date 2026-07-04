package io.beam.ir.erlang;

import java.util.List;

public record BinaryExpr(List<BinarySegmentExpr> segments, SourceSpan source)
    implements Expression {

  public static BinaryExpr of(String value) {
    if (value.isEmpty()) {
      return new BinaryExpr(List.of(), null);
    }
    return new BinaryExpr(List.of(BinarySegmentExpr.literal(value)), null);
  }

  public static BinaryExpr of(String value, SourceSpan source) {
    if (value.isEmpty()) {
      return new BinaryExpr(List.of(), source);
    }
    return new BinaryExpr(List.of(BinarySegmentExpr.literal(value)), source);
  }

  public static BinaryExpr of(List<BinarySegmentExpr> segments) {
    return new BinaryExpr(segments, null);
  }

  public static BinaryExpr of(List<BinarySegmentExpr> segments, SourceSpan source) {
    return new BinaryExpr(segments, source);
  }
}
