package io.beam.ir.erlang;

public record IsTypeGuard(String type, Expression expression, SourceSpan source) implements Guard {

  public static IsTypeGuard of(String type, Expression expression) {
    return new IsTypeGuard(type, expression, null);
  }

  public static IsTypeGuard of(String type, Expression expression, SourceSpan source) {
    return new IsTypeGuard(type, expression, source);
  }
}
