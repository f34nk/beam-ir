package io.beam.ir.erlang;

public record MacroExpr(String name, SourceSpan source) implements Expression {

  public static MacroExpr of(String name) {
    return new MacroExpr(name, null);
  }

  public static MacroExpr of(String name, SourceSpan source) {
    return new MacroExpr(name, source);
  }
}
