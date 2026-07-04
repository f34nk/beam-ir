package io.beam.ir.erlang;

import java.util.List;

public record RecordExpr(String name, Expression base, List<RecordField> fields, SourceSpan source)
    implements Expression {

  public static RecordExpr of(String name, List<RecordField> fields) {
    return new RecordExpr(name, null, fields, null);
  }

  public static RecordExpr of(String name, List<RecordField> fields, SourceSpan source) {
    return new RecordExpr(name, null, fields, source);
  }

  public static RecordExpr update(Expression base, String name, List<RecordField> fields) {
    return new RecordExpr(name, base, fields, null);
  }
}
