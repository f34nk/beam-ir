package io.beam.ir.elixir;

public record FunctionArityGuard(String variable, int arity, SourceSpan source) implements Guard {

  public static FunctionArityGuard of(String variable, int arity) {
    return new FunctionArityGuard(variable, arity, null);
  }
}
