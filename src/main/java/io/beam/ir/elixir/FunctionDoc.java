package io.beam.ir.elixir;

public record FunctionDoc(String text) {

  public static FunctionDoc of(String text) {
    return new FunctionDoc(text);
  }
}
