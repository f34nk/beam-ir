package io.beam.ir.elixir;

import java.util.List;

public record AnonFunClause(
    List<Pattern> params, Guard guardOrNull, Expression body, SourceSpan source) implements Node {

  public static AnonFunClause of(List<Pattern> params, Expression body) {
    return new AnonFunClause(params, null, body, null);
  }

  public static AnonFunClause of(List<Pattern> params, Guard guard, Expression body) {
    return new AnonFunClause(params, guard, body, null);
  }
}
