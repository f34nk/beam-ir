package io.beam.ir.erlang;

public record RecordFieldAccessExpr(
    Expression receiver, String recordName, String fieldName, SourceSpan source)
    implements Expression {

  public static RecordFieldAccessExpr of(Expression receiver, String recordName, String fieldName) {
    return new RecordFieldAccessExpr(receiver, recordName, fieldName, null);
  }

  public static RecordFieldAccessExpr of(
      Expression receiver, String recordName, String fieldName, SourceSpan source) {
    return new RecordFieldAccessExpr(receiver, recordName, fieldName, source);
  }
}
