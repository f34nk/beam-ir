package io.beam.ir.erlang;

public record EqualGuard(Expression left, Expression right, SourceSpan source) implements Guard {

  public static EqualGuard of(Expression left, Expression right) {
    return new EqualGuard(left, right, null);
  }

  public static EqualGuard of(Expression left, Expression right, SourceSpan source) {
    return new EqualGuard(left, right, source);
  }
}
