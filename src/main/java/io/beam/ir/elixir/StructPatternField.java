package io.beam.ir.elixir;

public record StructPatternField(String nameOrNull, Pattern pattern, SourceSpan source)
    implements Node {

  public static StructPatternField of(String name, Pattern pattern) {
    return new StructPatternField(name, pattern, null);
  }
}
