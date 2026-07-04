package io.beam.ir.erlang;

public record Callback(
    String name, String inputTypes, String outputTypes, FunctionDoc docOrNull, SourceSpan source) {

  public static Callback of(String name, String inputTypes, String outputTypes) {
    return new Callback(name, inputTypes, outputTypes, null, null);
  }

  public static Callback of(
      String name, String inputTypes, String outputTypes, FunctionDoc docOrNull) {
    return new Callback(name, inputTypes, outputTypes, docOrNull, null);
  }
}
