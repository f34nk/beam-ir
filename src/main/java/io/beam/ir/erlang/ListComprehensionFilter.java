package io.beam.ir.erlang;

public record ListComprehensionFilter(Expression expression, SourceSpan source)
    implements ListComprehensionQualifier {

  public static ListComprehensionFilter of(Expression expression) {
    return new ListComprehensionFilter(expression, null);
  }

  public static ListComprehensionFilter of(Expression expression, SourceSpan source) {
    return new ListComprehensionFilter(expression, source);
  }
}
