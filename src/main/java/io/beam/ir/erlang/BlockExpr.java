package io.beam.ir.erlang;

import java.util.List;

public record BlockExpr(
    List<Expression> expressions,
    BlockSeparator separator,
    boolean terminateWithPeriod,
    SourceSpan source)
    implements Expression {

  public enum BlockSeparator {
    COMMA,
    NEWLINE
  }

  public static BlockExpr newlineSeparated(List<Expression> expressions) {
    return new BlockExpr(expressions, BlockSeparator.NEWLINE, false, null);
  }

  public static BlockExpr newlineSeparated(
      List<Expression> expressions, boolean terminateWithPeriod) {
    return new BlockExpr(expressions, BlockSeparator.NEWLINE, terminateWithPeriod, null);
  }

  public static BlockExpr commaSeparated(
      List<Expression> expressions, boolean terminateWithPeriod) {
    return new BlockExpr(expressions, BlockSeparator.COMMA, terminateWithPeriod, null);
  }
}
