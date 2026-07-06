package io.beam.ir.elixir;

import java.util.List;

final class DefaultElixirRenderer implements Renderer {

  private static final String INDENT = "  ";
  private static final int PRINT_WIDTH = 98;

  private int compactLength(java.util.function.Consumer<StringBuilder> renderFn) {
    StringBuilder scratch = new StringBuilder();
    renderFn.accept(scratch);
    return scratch.length();
  }

  private boolean exceedsPrintWidth(java.util.function.Consumer<StringBuilder> renderFn) {
    return compactLength(renderFn) >= PRINT_WIDTH;
  }

  private boolean exceedsPrintWidthWithLinePrefix(
      String linePrefix, java.util.function.Consumer<StringBuilder> renderFn) {
    return linePrefix.length() + compactLength(renderFn) >= PRINT_WIDTH;
  }

  static int printWidthForTests() {
    return PRINT_WIDTH;
  }

  private void render(Expression expression, StringBuilder out, String indent) {
    if (expression instanceof AtomExpr atom) {
      out.append(':').append(atom.value());
    } else if (expression instanceof IntegerExpr integer) {
      out.append(integer.value());
    } else if (expression instanceof Variable variable) {
      out.append(variable.name());
    } else if (expression instanceof StringExpr string) {
      out.append('"').append(escapeString(string.value())).append('"');
    } else if (expression instanceof NilExpr) {
      out.append("nil");
    } else if (expression instanceof BooleanExpr bool) {
      out.append(bool.value());
    } else if (expression instanceof OpaqueExpr opaque) {
      out.append(opaque.text());
    } else if (expression instanceof TupleExpr tuple) {
      render(tuple, out, indent);
    } else if (expression instanceof ListExpr list) {
      render(list, out, indent);
    } else if (expression instanceof MapExpr map) {
      render(map, out, indent);
    } else if (expression instanceof StructExpr struct) {
      render(struct, out, indent);
    } else if (expression instanceof RemoteCallExpr call) {
      render(call, out, indent);
    } else if (expression instanceof LocalCallExpr call) {
      render(call, out, indent);
    } else if (expression instanceof DotCallExpr call) {
      render(call, out, indent);
    } else if (expression instanceof CaptureExpr capture) {
      out.append('&').append(capture.function()).append('/').append(capture.arity());
    } else if (expression instanceof InfixExpr infix) {
      render(infix, out, indent);
    } else if (expression instanceof PipeExpr pipe) {
      render(pipe, out, indent);
    } else if (expression instanceof MatchExpr match) {
      render(match, out, indent);
    } else if (expression instanceof InterpolatedStringExpr string) {
      render(string, out, indent);
    } else {
      throw new IllegalArgumentException("Unsupported expression: " + expression);
    }
  }

  private void render(TupleExpr tuple, StringBuilder out, String indent) {
    if (!collectionExceedsPrintWidth(tuple.elements(), '{', '}')) {
      out.append('{');
      renderCommaSeparated(tuple.elements(), out, indent);
      out.append('}');
      return;
    }
    renderCollectionVertical(tuple.elements(), out, indent, '{', '}');
  }

  private void render(ListExpr list, StringBuilder out, String indent) {
    if (!collectionExceedsPrintWidth(list.elements(), '[', ']')) {
      out.append('[');
      renderCommaSeparated(list.elements(), out, indent);
      out.append(']');
      return;
    }
    renderCollectionVertical(list.elements(), out, indent, '[', ']');
  }

  private void render(MapExpr map, StringBuilder out, String indent) {
    if (map.baseOrNull() != null) {
      render(map.baseOrNull(), out, indent);
      out.append(' ');
    }
    out.append('%');
    if (!mapExceedsPrintWidth(map)) {
      out.append('{');
      renderMapEntries(map.entries(), out, indent);
      out.append('}');
      return;
    }
    out.append("{\n");
    String entryIndent = indent + INDENT;
    for (int i = 0; i < map.entries().size(); i++) {
      if (i > 0) {
        out.append(",\n");
      }
      out.append(entryIndent);
      renderMapEntry(map.entries().get(i), out, entryIndent);
    }
    out.append('\n').append(indent).append('}');
  }

  private void renderMapEntries(List<MapEntry> entries, StringBuilder out, String indent) {
    for (int i = 0; i < entries.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      renderMapEntry(entries.get(i), out, indent);
    }
  }

  private void renderMapEntry(MapEntry entry, StringBuilder out, String indent) {
    if (!entry.arrowSyntax() && entry.key() instanceof AtomExpr atom) {
      out.append(atom.value()).append(": ");
    } else {
      render(entry.key(), out, indent);
      if (entry.arrowSyntax()) {
        out.append(" => ");
      } else {
        out.append(": ");
      }
    }
    render(entry.value(), out, indent);
  }

  private void renderCommaSeparated(List<Expression> elements, StringBuilder out, String indent) {
    for (int i = 0; i < elements.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(elements.get(i), out, indent);
    }
  }

  private void renderCollectionVertical(
      List<Expression> elements, StringBuilder out, String indent, char open, char close) {
    out.append(open).append('\n');
    String elementIndent = indent + INDENT;
    for (int i = 0; i < elements.size(); i++) {
      if (i > 0) {
        out.append(",\n");
      }
      out.append(elementIndent);
      render(elements.get(i), out, elementIndent);
    }
    out.append('\n').append(indent).append(close);
  }

  private boolean collectionExceedsPrintWidth(
      List<Expression> elements, char open, char close) {
    return exceedsPrintWidth(
        scratch -> {
          scratch.append(open);
          renderCommaSeparated(elements, scratch, "");
          scratch.append(close);
        });
  }

  private boolean mapExceedsPrintWidth(MapExpr map) {
    return exceedsPrintWidth(
        scratch -> {
          if (map.baseOrNull() != null) {
            render(map.baseOrNull(), scratch, "");
            scratch.append(' ');
          }
          scratch.append('%');
          scratch.append('{');
          renderMapEntries(map.entries(), scratch, "");
          scratch.append('}');
        });
  }

  private void render(InfixExpr infix, StringBuilder out, String indent) {
    if (!infixExceedsPrintWidth(infix)) {
      render(infix.left(), out, indent);
      out.append(' ').append(infix.op()).append(' ');
      render(infix.right(), out, indent);
      return;
    }
    render(infix.left(), out, indent);
    out.append("\n").append(indent).append(infix.op()).append(' ');
    render(infix.right(), out, indent + INDENT);
  }

  private boolean infixExceedsPrintWidth(InfixExpr infix) {
    return exceedsPrintWidth(
        scratch -> {
          render(infix.left(), scratch, "");
          scratch.append(' ').append(infix.op()).append(' ');
          render(infix.right(), scratch, "");
        });
  }

  private void render(PipeExpr pipe, StringBuilder out, String indent) {
    if (!pipeExceedsPrintWidth(pipe)) {
      render(pipe.initial(), out, indent);
      for (PipeStep step : pipe.steps()) {
        out.append(" |> ");
        renderPipeStep(step, out, indent);
      }
      return;
    }
    render(pipe.initial(), out, indent);
    for (PipeStep step : pipe.steps()) {
      out.append("\n|> ");
      renderPipeStep(step, out, indent);
    }
  }

  private void renderPipeStep(PipeStep step, StringBuilder out, String indent) {
    render(step.callable(), out, indent);
    for (Expression arg : step.extraArgs()) {
      out.append(", ");
      render(arg, out, indent);
    }
  }

  private boolean pipeExceedsPrintWidth(PipeExpr pipe) {
    return exceedsPrintWidth(
        scratch -> {
          render(pipe.initial(), scratch, "");
          for (PipeStep step : pipe.steps()) {
            scratch.append(" |> ");
            renderPipeStep(step, scratch, "");
          }
        });
  }

  private void render(MatchExpr match, StringBuilder out, String indent) {
    out.append(match.name()).append(" = ");
    render(match.value(), out, indent);
    if (match.bodyOrNull() != null) {
      out.append('\n').append(indent);
      render(match.bodyOrNull(), out, indent);
    }
  }

  private void render(InterpolatedStringExpr string, StringBuilder out, String indent) {
    out.append('"');
    for (InterpolatedSegment segment : string.segments()) {
      if (segment instanceof InterpolatedLiteral literal) {
        out.append(escapeString(literal.text()));
      } else if (segment instanceof InterpolatedExpr expr) {
        out.append("#{");
        render(expr.expression(), out, indent);
        out.append('}');
      }
    }
    out.append('"');
  }

  private void render(RemoteCallExpr call, StringBuilder out, String indent) {
    if (!callExceedsPrintWidth(call.module(), call.function(), call.args(), null)) {
      out.append(call.module()).append('.').append(call.function()).append('(');
      renderCommaSeparated(call.args(), out, indent);
      out.append(')');
      return;
    }
    renderCallVertical(call.module() + "." + call.function(), call.args(), out, indent, null);
  }

  private void render(LocalCallExpr call, StringBuilder out, String indent) {
    if (!callExceedsPrintWidth(null, call.function(), call.args(), null)) {
      out.append(call.function()).append('(');
      renderCommaSeparated(call.args(), out, indent);
      out.append(')');
      return;
    }
    renderCallVertical(call.function(), call.args(), out, indent, null);
  }

  private void render(DotCallExpr call, StringBuilder out, String indent) {
    String qualified = call.function();
    int dot = qualified.lastIndexOf('.');
    String target = dot >= 0 ? qualified : qualified;
    List<Expression> allArgs = prependReceiver(call.receiver(), call.args());
    if (!callExceedsPrintWidth(
        dot >= 0 ? qualified.substring(0, dot) : null,
        dot >= 0 ? qualified.substring(dot + 1) : qualified,
        allArgs,
        call.receiver())) {
      if (dot >= 0) {
        out.append(qualified, 0, dot).append('.').append(qualified.substring(dot + 1));
      } else {
        out.append(qualified);
      }
      out.append('(');
      render(call.receiver(), out, indent);
      for (Expression arg : call.args()) {
        out.append(", ");
        render(arg, out, indent);
      }
      out.append(')');
      return;
    }
    renderCallVertical(target, allArgs, out, indent, call.receiver());
  }

  private List<Expression> prependReceiver(Expression receiver, List<Expression> args) {
    java.util.ArrayList<Expression> all = new java.util.ArrayList<>(args.size() + 1);
    all.add(receiver);
    all.addAll(args);
    return all;
  }

  private void renderCallVertical(
      String name, List<Expression> args, StringBuilder out, String indent, Expression firstArg) {
    out.append(name).append("(\n");
    String argIndent = indent + INDENT;
    for (int i = 0; i < args.size(); i++) {
      if (i > 0) {
        out.append(",\n");
      }
      out.append(argIndent);
      render(args.get(i), out, argIndent);
    }
    out.append('\n').append(indent).append(')');
  }

  private boolean callExceedsPrintWidth(
      String module, String function, List<Expression> args, Expression receiverOrNull) {
    return exceedsPrintWidth(
        scratch -> {
          if (module != null) {
            scratch.append(module).append('.');
          }
          scratch.append(function).append('(');
          if (receiverOrNull != null) {
            render(receiverOrNull, scratch, "");
            for (Expression arg : args) {
              scratch.append(", ");
              render(arg, scratch, "");
            }
          } else {
            renderCommaSeparated(args, scratch, "");
          }
          scratch.append(')');
        });
  }

  private void render(StructExpr struct, StringBuilder out, String indent) {
    if (!structExceedsPrintWidth(struct)) {
      out.append('%').append(struct.moduleName()).append('{');
      if (struct.baseOrNull() != null) {
        render(struct.baseOrNull(), out, indent);
        out.append(" | ");
      }
      renderStructFields(struct.fields(), out, indent);
      out.append('}');
      return;
    }
    out.append('%').append(struct.moduleName()).append("{\n");
    String fieldIndent = indent + INDENT;
    if (struct.baseOrNull() != null) {
      out.append(fieldIndent);
      render(struct.baseOrNull(), out, fieldIndent);
      out.append(" |\n");
    }
    for (int i = 0; i < struct.fields().size(); i++) {
      if (i > 0) {
        out.append(",\n");
      }
      out.append(fieldIndent);
      StructField field = struct.fields().get(i);
      out.append(field.name()).append(": ");
      render(field.value(), out, fieldIndent);
    }
    out.append('\n').append(indent).append('}');
  }

  private void renderStructFields(List<StructField> fields, StringBuilder out, String indent) {
    for (int i = 0; i < fields.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      StructField field = fields.get(i);
      out.append(field.name()).append(": ");
      render(field.value(), out, indent);
    }
  }

  private boolean structExceedsPrintWidth(StructExpr struct) {
    return exceedsPrintWidth(
        scratch -> {
          scratch.append('%').append(struct.moduleName()).append('{');
          if (struct.baseOrNull() != null) {
            render(struct.baseOrNull(), scratch, "");
            scratch.append(" | ");
          }
          renderStructFields(struct.fields(), scratch, "");
          scratch.append('}');
        });
  }

  private void render(Pattern pattern, StringBuilder out) {
    if (pattern instanceof StructPattern struct) {
      render(struct, out);
    } else if (pattern instanceof VariablePattern variable) {
      out.append(variable.name());
    } else {
      throw new IllegalArgumentException("Unsupported pattern: " + pattern);
    }
  }

  private void render(StructPattern struct, StringBuilder out) {
    if (struct.moduleNameOrNull() != null) {
      out.append('%').append(struct.moduleNameOrNull()).append('{');
      renderStructPatternFields(struct.fields(), out);
      out.append('}');
      return;
    }
    out.append("%{");
    for (int i = 0; i < struct.fields().size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      StructPatternField field = struct.fields().get(i);
      if (field.nameOrNull() != null) {
        out.append(':').append(field.nameOrNull()).append(" => ");
        render(field.pattern(), out);
      } else {
        render(field.pattern(), out);
      }
    }
    out.append('}');
  }

  private void renderStructPatternFields(
      List<StructPatternField> fields, StringBuilder out) {
    for (int i = 0; i < fields.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      StructPatternField field = fields.get(i);
      if (field.nameOrNull() != null) {
        out.append(field.nameOrNull()).append(": ");
        render(field.pattern(), out);
      } else {
        render(field.pattern(), out);
      }
    }
  }

  private static String escapeString(String value) {
    return value.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  @Override
  public String renderExpression(Expression expression) {
    StringBuilder out = new StringBuilder();
    render(expression, out, "");
    return out.toString();
  }

  @Override
  public String render(Module module) {
    throw new UnsupportedOperationException("Module rendering not implemented");
  }

  @Override
  public String render(TypesModule typesModule) {
    throw new UnsupportedOperationException("TypesModule rendering not implemented");
  }

  @Override
  public String renderFunction(Function function) {
    throw new UnsupportedOperationException("Function rendering not implemented");
  }

  @Override
  public String renderPattern(Pattern pattern) {
    StringBuilder out = new StringBuilder();
    render(pattern, out);
    return out.toString();
  }
}
