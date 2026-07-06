package io.beam.ir.elixir;

import java.util.List;

public record StructExpr(
    String moduleName, Expression baseOrNull, List<StructField> fields, SourceSpan source)
    implements Expression {

  public static StructExpr of(String moduleName, List<StructField> fields) {
    return new StructExpr(moduleName, null, fields, null);
  }

  public static StructExpr update(Expression base, String moduleName, List<StructField> fields) {
    return new StructExpr(moduleName, base, fields, null);
  }
}
