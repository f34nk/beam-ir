package io.beam.ir.erlang;

public record NotEqualGuard(Expression left, Expression right, SourceSpan source) implements Guard {

  public static NotEqualGuard of(Expression left, Expression right) {
    return new NotEqualGuard(left, right, null);
  }

  public static NotEqualGuard of(Expression left, Expression right, SourceSpan source) {
    return new NotEqualGuard(left, right, source);
  }
}
