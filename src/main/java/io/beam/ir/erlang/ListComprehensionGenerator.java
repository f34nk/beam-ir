package io.beam.ir.erlang;

public record ListComprehensionGenerator(Pattern pattern, Expression source, SourceSpan sourceSpan)
    implements ListComprehensionQualifier {

  public static ListComprehensionGenerator of(Pattern pattern, Expression source) {
    return new ListComprehensionGenerator(pattern, source, null);
  }

  public static ListComprehensionGenerator of(
      Pattern pattern, Expression source, SourceSpan sourceSpan) {
    return new ListComprehensionGenerator(pattern, source, sourceSpan);
  }
}
