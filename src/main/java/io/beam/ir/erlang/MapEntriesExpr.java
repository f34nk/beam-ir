package io.beam.ir.erlang;

import java.util.List;

public record MapEntriesExpr(List<MapEntry> entries, SourceSpan source) implements Expression {

  public static MapEntriesExpr of(List<MapEntry> entries) {
    return new MapEntriesExpr(entries, null);
  }
}
