package io.beam.ir.erlang;

import java.util.List;

public record MapExpr(Expression base, List<MapEntry> entries, SourceSpan source)
    implements Expression {

  public static MapExpr of(List<MapEntry> entries) {
    return new MapExpr(null, entries, null);
  }

  public static MapExpr of(Expression base, List<MapEntry> entries) {
    return new MapExpr(base, entries, null);
  }

  public static MapExpr of(Expression base, List<MapEntry> entries, SourceSpan source) {
    return new MapExpr(base, entries, source);
  }
}
