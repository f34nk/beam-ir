package io.beam.ir.erlang;

public record Variable(String name, SourceSpan source) implements Expression {

  public static Variable of(String name) {
    return new Variable(name, null);
  }

  public static Variable of(String name, SourceSpan source) {
    return new Variable(name, source);
  }
}
