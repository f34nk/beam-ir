package io.beam.ir.elixir;

import java.util.List;

public record BinaryPattern(List<BinarySegmentPattern> segments, SourceSpan source)
    implements Pattern {

  public static BinaryPattern of(List<BinarySegmentPattern> segments) {
    return new BinaryPattern(segments, null);
  }
}
