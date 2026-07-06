package io.beam.ir.elixir;

public record BinarySegmentPattern(
    Pattern patternOrNull, String literalOrNull, String typeOrNull, SourceSpan source) {

  public static BinarySegmentPattern literal(String literal) {
    return new BinarySegmentPattern(null, literal, null, null);
  }

  public static BinarySegmentPattern of(Pattern pattern, String type) {
    return new BinarySegmentPattern(pattern, null, type, null);
  }
}
