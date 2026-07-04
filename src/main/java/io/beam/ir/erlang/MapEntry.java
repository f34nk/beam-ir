package io.beam.ir.erlang;

public record MapEntry(Expression key, Expression value, boolean updateOnly, SourceSpan source) {

  public static MapEntry of(Expression key, Expression value) {
    return new MapEntry(key, value, false, null);
  }

  public static MapEntry of(Expression key, Expression value, boolean updateOnly) {
    return new MapEntry(key, value, updateOnly, null);
  }

  public static MapEntry of(
      Expression key, Expression value, boolean updateOnly, SourceSpan source) {
    return new MapEntry(key, value, updateOnly, source);
  }
}
