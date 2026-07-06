package io.beam.ir.elixir;

public record Variable(String name, SourceSpan source) implements Expression {

  public static Variable of(String name) {
    return new Variable(name, null);
  }
}
