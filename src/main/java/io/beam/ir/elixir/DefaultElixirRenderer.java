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

  String renderGuardForTest(Guard guard) {
    StringBuilder out = new StringBuilder();
    render(guard, out);
    return out.toString();
  }

  private void render(Guard guard, StringBuilder out) {
    if (guard instanceof IsTypeGuard isTypeGuard) {
      out.append(isTypeGuard.type()).append('(').append(isTypeGuard.variable()).append(')');
    } else if (guard instanceof ComparisonGuard comparison) {
      render(comparison.left(), out, "");
      out.append(' ').append(comparison.op()).append(' ');
      render(comparison.right(), out, "");
    } else if (guard instanceof AndGuard andGuard) {
      List<Guard> guards = andGuard.guards();
      for (int i = 0; i < guards.size(); i++) {
        if (i > 0) {
          out.append(" and ");
        }
        render(guards.get(i), out);
      }
    } else if (guard instanceof OrGuard orGuard) {
      List<Guard> guards = orGuard.guards();
      for (int i = 0; i < guards.size(); i++) {
        if (i > 0) {
          out.append(" or ");
        }
        render(guards.get(i), out);
      }
    } else if (guard instanceof FunctionArityGuard fnGuard) {
      out.append("is_function(")
          .append(fnGuard.variable())
          .append(", ")
          .append(fnGuard.arity())
          .append(')');
    } else if (guard instanceof OpaqueGuard opaqueGuard) {
      out.append(opaqueGuard.text());
    } else {
      throw new IllegalArgumentException("Unsupported guard: " + guard);
    }
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
    } else if (expression instanceof CaseExpr caseExpr) {
      render(caseExpr, out, indent);
    } else if (expression instanceof IfExpr ifExpr) {
      render(ifExpr, out, indent);
    } else if (expression instanceof BlockExpr block) {
      render(block, out, indent);
    } else if (expression instanceof AnonFun fun) {
      render(fun, out, indent);
    } else if (expression instanceof TryExpr tryExpr) {
      render(tryExpr, out, indent);
    } else if (expression instanceof RaiseExpr raise) {
      render(raise, out, indent);
    } else if (expression instanceof BinaryExpr binary) {
      render(binary, out, indent);
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

  private boolean collectionExceedsPrintWidth(List<Expression> elements, char open, char close) {
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
      out.append('\n').append(indent).append("|> ");
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
    render(match.pattern(), out);
    out.append(" =");
    if (match.value() instanceof CaseExpr || matchValueExceedsPrintWidth(match.value())) {
      out.append('\n').append(indent).append(INDENT);
      render(match.value(), out, indent + INDENT);
    } else {
      out.append(' ');
      render(match.value(), out, indent);
    }
    if (match.bodyOrNull() != null) {
      out.append("\n\n").append(indent);
      render(match.bodyOrNull(), out, indent);
    }
  }

  private boolean matchValueExceedsPrintWidth(Expression value) {
    return exceedsPrintWidth(scratch -> render(value, scratch, ""));
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

  private void render(CaseExpr caseExpr, StringBuilder out, String indent) {
    out.append("case");
    if (caseExpr.subjectOrNull() != null) {
      out.append(' ');
      render(caseExpr.subjectOrNull(), out, indent);
    }
    out.append(" do\n");
    List<Clause> clauses = caseExpr.clauses();
    boolean multilineCase = clauses.stream().anyMatch(c -> usesMultilineCaseBody(c.body()));
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0 && usesBlankLineBetweenCaseClauses(clauses.get(i - 1), clauses.get(i))) {
        out.append('\n');
      }
      out.append(indent).append(INDENT);
      render(clauses.get(i).pattern(), out);
      if (clauses.get(i).guardOrNull() != null) {
        out.append(" when ");
        render(clauses.get(i).guardOrNull(), out);
      }
      out.append(" ->");
      Expression body = clauses.get(i).body();
      if (multilineCase || usesMultilineCaseBody(body)) {
        out.append('\n');
        out.append(indent).append(INDENT).append(INDENT);
        render(body, out, indent + INDENT + INDENT);
      } else {
        out.append(' ');
        render(body, out, indent + INDENT);
      }
      if (i < clauses.size() - 1) {
        out.append('\n');
      }
    }
    out.append('\n').append(indent).append("end");
  }

  private boolean usesBlankLineBetweenCaseClauses(Clause previous, Clause next) {
    return usesMultilineCaseBody(previous.body()) || usesMultilineCaseBody(next.body());
  }

  private boolean usesMultilineCaseBody(Expression body) {
    return body instanceof CaseExpr
        || body instanceof BlockExpr
        || body instanceof MatchExpr
        || body instanceof TryExpr
        || body instanceof AnonFun
        || body instanceof IfExpr ifExpr && !ifExpr.inline();
  }

  private void render(IfExpr ifExpr, StringBuilder out, String indent) {
    if (ifExpr.inline()) {
      out.append("if(");
      render(ifExpr.condition(), out, indent);
      out.append(", do: ");
      render(ifExpr.thenBranch(), out, indent);
      if (ifExpr.elseBranchOrNull() != null) {
        out.append(", else: ");
        render(ifExpr.elseBranchOrNull(), out, indent);
      }
      out.append(')');
      return;
    }
    out.append("if ");
    render(ifExpr.condition(), out, indent);
    out.append(" do\n");
    out.append(indent).append(INDENT);
    render(ifExpr.thenBranch(), out, indent + INDENT);
    if (ifExpr.elseBranchOrNull() != null) {
      out.append("\n").append(indent).append("else\n");
      out.append(indent).append(INDENT);
      render(ifExpr.elseBranchOrNull(), out, indent + INDENT);
    }
    out.append('\n').append(indent).append("end");
  }

  private void render(BlockExpr block, StringBuilder out, String indent) {
    for (int i = 0; i < block.statements().size(); i++) {
      if (i > 0) {
        out.append('\n');
        Expression previous = block.statements().get(i - 1);
        Expression current = block.statements().get(i);
        if (previous instanceof MatchExpr match) {
          boolean multiline =
              match.value() instanceof CaseExpr || matchValueExceedsPrintWidth(match.value());
          if (multiline || !(current instanceof MatchExpr)) {
            out.append('\n');
          }
        }
        if (!indent.isEmpty()) {
          out.append(indent);
        }
      }
      render(block.statements().get(i), out, indent);
    }
  }

  private void render(AnonFun fun, StringBuilder out, String indent) {
    List<AnonFunClause> clauses = fun.clauses();
    if (clauses.size() == 1
        && isCompactAnonFun(clauses.get(0))
        && clauses.get(0).guardOrNull() == null) {
      AnonFunClause clause = clauses.get(0);
      out.append("fn ");
      renderAnonFunParams(clause.params(), out);
      out.append(" -> ");
      render(clause.body(), out, indent);
      out.append(" end");
      return;
    }
    out.append("fn\n");
    for (int i = 0; i < clauses.size(); i++) {
      out.append(indent).append(INDENT);
      renderAnonFunParams(clauses.get(i).params(), out);
      if (clauses.get(i).guardOrNull() != null) {
        out.append(" when ");
        render(clauses.get(i).guardOrNull(), out);
      }
      out.append(" -> ");
      render(clauses.get(i).body(), out, indent + INDENT);
      if (i < clauses.size() - 1) {
        out.append('\n');
      }
    }
    out.append('\n').append(indent).append("end");
  }

  private boolean isCompactAnonFun(AnonFunClause clause) {
    return !usesMultilineCaseBody(clause.body());
  }

  private void renderAnonFunParams(List<Pattern> params, StringBuilder out) {
    if (params.size() == 1) {
      render(params.get(0), out);
      return;
    }
    out.append('(');
    for (int i = 0; i < params.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(params.get(i), out);
    }
    out.append(')');
  }

  private void render(TryExpr tryExpr, StringBuilder out, String indent) {
    out.append("try do\n");
    out.append(indent).append(INDENT);
    render(tryExpr.body(), out, indent + INDENT);
    out.append("\n").append(indent).append("catch\n");
    for (int i = 0; i < tryExpr.catchClauses().size(); i++) {
      CatchClause clause = tryExpr.catchClauses().get(i);
      out.append(indent).append(INDENT);
      render(clause.kind(), out);
      out.append(", ");
      render(clause.reason(), out);
      out.append(" -> ");
      render(clause.body(), out, indent + INDENT);
      if (i < tryExpr.catchClauses().size() - 1) {
        out.append('\n');
      }
    }
    out.append('\n').append(indent).append("end");
  }

  private void render(RaiseExpr raise, StringBuilder out, String indent) {
    if (raise.parenthesized()) {
      out.append("raise(");
      render(raise.exception(), out, indent);
      if (raise.messageOrNull() != null) {
        out.append(", ");
        render(raise.messageOrNull(), out, indent);
      }
      out.append(')');
      return;
    }
    out.append("raise ");
    render(raise.exception(), out, indent);
    if (raise.messageOrNull() != null) {
      out.append(", ");
      render(raise.messageOrNull(), out, indent);
    }
  }

  private void render(BinaryExpr binary, StringBuilder out, String indent) {
    List<BinarySegmentExpr> segments = binary.segments();
    if (segments.isEmpty()) {
      out.append("<<>>");
      return;
    }
    if (!binaryExceedsPrintWidth(segments, indent)) {
      out.append("<<");
      for (int i = 0; i < segments.size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        render(segments.get(i), out, indent);
      }
      out.append(">>");
      return;
    }
    out.append("<<");
    for (int i = 0; i < segments.size(); i++) {
      if (i > 0) {
        out.append(",\n").append(indent).append(INDENT);
      }
      render(segments.get(i), out, indent + INDENT);
    }
    out.append(">>");
  }

  private void render(BinarySegmentExpr segment, StringBuilder out, String indent) {
    render(segment.value(), out, indent);
    if (segment.typeOrNull() != null) {
      out.append("::").append(segment.typeOrNull());
    }
  }

  private boolean binaryExceedsPrintWidth(List<BinarySegmentExpr> segments, String indent) {
    return exceedsPrintWidth(
        scratch -> {
          scratch.append("<<");
          for (int i = 0; i < segments.size(); i++) {
            if (i > 0) {
              scratch.append(", ");
            }
            render(segments.get(i), scratch, indent);
          }
          scratch.append(">>");
        });
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
    if ("()".equals(call.function()) || ".".equals(call.function())) {
      render(call.receiver(), out, indent);
      out.append(".(");
      renderCommaSeparated(call.args(), out, indent);
      out.append(')');
      return;
    }
    if (call.args().isEmpty() && !call.function().contains(".")) {
      render(call.receiver(), out, indent);
      out.append('.').append(call.function());
      return;
    }
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
    if (struct.fields().size() > 1) {
      return true;
    }
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
    } else if (pattern instanceof AtomPattern atom) {
      out.append(':').append(atom.value());
    } else if (pattern instanceof WildcardPattern) {
      out.append('_');
    } else if (pattern instanceof TuplePattern tuple) {
      out.append('{');
      for (int i = 0; i < tuple.elements().size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        render(tuple.elements().get(i), out);
      }
      out.append('}');
    } else if (pattern instanceof ListPattern list) {
      out.append('[');
      for (int i = 0; i < list.elements().size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        render(list.elements().get(i), out);
      }
      out.append(']');
    } else if (pattern instanceof StringPattern string) {
      out.append('"').append(escapeString(string.value())).append('"');
    } else if (pattern instanceof PinPattern pin) {
      out.append('^').append(pin.name());
    } else if (pattern instanceof MapPattern map) {
      out.append("%{");
      for (int i = 0; i < map.entries().size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        MapPatternEntry entry = map.entries().get(i);
        render(entry.key(), out, "");
        out.append(" => ");
        render(entry.value(), out);
      }
      out.append('}');
    } else if (pattern instanceof NilPattern) {
      out.append("nil");
    } else if (pattern instanceof ConsListPattern cons) {
      out.append('[');
      render(cons.head(), out);
      out.append(" | ");
      render(cons.tail(), out);
      out.append(']');
    } else if (pattern instanceof AssignPattern assign) {
      out.append(assign.name()).append(" = ");
      render(assign.pattern(), out);
    } else if (pattern instanceof IntegerPattern integer) {
      out.append(integer.value());
    } else if (pattern instanceof BinaryPattern binary) {
      out.append("<<");
      for (int i = 0; i < binary.segments().size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        BinarySegmentPattern seg = binary.segments().get(i);
        if (seg.literalOrNull() != null) {
          out.append('"').append(escapeString(seg.literalOrNull())).append('"');
        } else {
          render(seg.patternOrNull(), out);
        }
        if (seg.typeOrNull() != null) {
          out.append("::").append(seg.typeOrNull());
        }
      }
      out.append(">>");
    } else if (pattern instanceof ConcatPattern concat) {
      render(concat.left(), out);
      out.append(" <> ");
      render(concat.right(), out);
    } else if (pattern instanceof OpaquePattern opaque) {
      out.append(opaque.text());
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

  private void renderStructPatternFields(List<StructPatternField> fields, StringBuilder out) {
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

  private void render(Function function, StringBuilder out, String indent) {
    if (function.docOrNull() != null) {
      out.append(indent)
          .append("@doc \"")
          .append(escapeString(function.docOrNull().text()))
          .append("\"\n");
    }
    if (function.specOrNull() != null) {
      renderSpec(function.specOrNull(), out, indent);
    }
    List<FunctionHead> heads = function.heads();
    for (int i = 0; i < heads.size(); i++) {
      if (i > 0) {
        out.append('\n');
      }
      renderFunctionClause(function, heads.get(i), out, indent);
    }
  }

  private void renderFunctionClause(
      Function function, FunctionHead head, StringBuilder out, String indent) {
    out.append(indent).append(function.private_() ? "defp " : "def ").append(function.name());
    out.append('(');
    renderFunctionParams(head.params(), out);
    out.append(')');
    if (head.guardOrNull() != null) {
      out.append(" when ");
      render(head.guardOrNull(), out);
    }
    if (function.oneLiner()) {
      out.append(", do: ");
      render(function.body(), out, indent);
      return;
    }
    out.append(" do\n");
    out.append(indent).append(INDENT);
    render(function.body(), out, indent + INDENT);
    out.append('\n').append(indent).append("end");
  }

  private void renderFunctionParams(List<Pattern> params, StringBuilder out) {
    for (int i = 0; i < params.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(params.get(i), out);
    }
  }

  private void renderSpec(Spec spec, StringBuilder out, String indent) {
    String text = spec.text();
    String line = "@spec " + text;
    if (line.length() <= PRINT_WIDTH) {
      out.append(indent).append(line).append('\n');
      return;
    }
    int split = text.lastIndexOf(" | ");
    String separator = " | ";
    if (split < 0) {
      split = text.indexOf(" :: ");
      separator = " :: ";
    }
    if (split < 0) {
      out.append(indent).append(line).append('\n');
      return;
    }
    out.append(indent).append("@spec ").append(text, 0, split).append('\n');
    out.append(indent)
        .append(INDENT)
        .append(separator.trim())
        .append(' ')
        .append(text.substring(split + separator.length()))
        .append('\n');
  }

  @Override
  public String renderExpression(Expression expression) {
    StringBuilder out = new StringBuilder();
    render(expression, out, "");
    return out.toString();
  }

  @Override
  public String render(Module module) {
    StringBuilder out = new StringBuilder();
    out.append("defmodule ").append(module.name()).append(" do\n");
    boolean hasHeader = false;
    if (module.moduledocOrNull() != null) {
      out.append(INDENT)
          .append("@moduledoc ")
          .append(formatModuledoc(module.moduledocOrNull()))
          .append('\n');
      hasHeader = true;
    }
    if (!module.uses().isEmpty()) {
      if (hasHeader) {
        out.append('\n');
      }
      for (UseDirective use : module.uses()) {
        render(use, out, INDENT);
        out.append('\n');
      }
      hasHeader = true;
    }
    if (!module.aliases().isEmpty()) {
      if (hasHeader) {
        out.append('\n');
      }
      for (Alias alias : module.aliases()) {
        render(alias, out, INDENT);
        out.append('\n');
      }
      hasHeader = true;
    }
    if (!module.moduleAttributes().isEmpty()) {
      if (hasHeader) {
        out.append('\n');
      }
      for (int i = 0; i < module.moduleAttributes().size(); i++) {
        out.append(INDENT).append(module.moduleAttributes().get(i)).append('\n');
      }
      hasHeader = true;
    }
    for (int i = 0; i < module.nestedTypesModules().size(); i++) {
      if (hasHeader && i == 0) {
        out.append('\n');
      }
      renderNestedTypesModule(module.nestedTypesModules().get(i), out, INDENT);
      if (i < module.nestedTypesModules().size() - 1) {
        out.append('\n');
      }
      hasHeader = true;
    }
    if (!module.trailingModuleAttributes().isEmpty()) {
      if (hasHeader) {
        out.append('\n');
      }
      for (int i = 0; i < module.trailingModuleAttributes().size(); i++) {
        int trailingCount = module.trailingModuleAttributes().size();
        if ((trailingCount == 2 && i == 1) || (trailingCount > 2 && i == trailingCount - 1)) {
          out.append('\n');
        }
        out.append(INDENT).append(module.trailingModuleAttributes().get(i)).append('\n');
      }
      hasHeader = true;
    }
    if (!module.functions().isEmpty() && hasHeader) {
      out.append('\n');
    }
    for (int i = 0; i < module.functions().size(); i++) {
      if (i > 0) {
        Function previous = module.functions().get(i - 1);
        Function current = module.functions().get(i);
        out.append('\n');
        if (needsBlankLineBetweenFunctions(previous, current)) {
          out.append('\n');
        }
      }
      render(module.functions().get(i), out, INDENT);
    }
    appendModuleEnd(out);
    return out.toString().stripTrailing() + "\n";
  }

  private static void appendModuleEnd(StringBuilder out) {
    if (!out.isEmpty() && out.charAt(out.length() - 1) == '\n') {
      out.append("end\n");
    } else {
      out.append('\n').append("end\n");
    }
  }

  private void renderNestedTypesModule(TypesModule typesModule, StringBuilder out, String indent) {
    out.append(indent).append("defmodule ").append(typesModule.name()).append(" do\n");
    render(typesModule.typeDef(), out, indent + INDENT);
    out.append("\n\n");
    renderDefstruct(typesModule.defstructFields(), out, indent + INDENT);
    out.append('\n').append(indent).append("end\n");
  }

  private static boolean needsBlankLineBetweenFunctions(Function previous, Function current) {
    if (!previous.name().equals(current.name())) {
      return true;
    }
    return !(previous.oneLiner() && current.oneLiner());
  }

  private static String formatModuledoc(Moduledoc moduledoc) {
    if (moduledoc.literal()) {
      return moduledoc.text();
    }
    return "\"" + moduledoc.text().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }

  private void render(UseDirective use, StringBuilder out, String indent) {
    out.append(indent).append("use ").append(use.module());
    for (int i = 0; i < use.options().size(); i++) {
      if (i == 0) {
        out.append(", ");
      } else {
        out.append(", ");
      }
      UseOption option = use.options().get(i);
      out.append(option.key()).append(": ");
      render(option.value(), out, indent);
    }
  }

  private void render(Alias alias, StringBuilder out, String indent) {
    out.append(indent).append("alias ").append(alias.module());
    if (alias.asOrNull() != null) {
      out.append(", as: ").append(alias.asOrNull());
    }
  }

  @Override
  public String render(TypesModule typesModule) {
    StringBuilder out = new StringBuilder();
    out.append("defmodule ").append(typesModule.name()).append(" do\n");
    if (typesModule.moduledocOrNull() != null) {
      out.append(INDENT)
          .append("@moduledoc ")
          .append(formatModuledoc(typesModule.moduledocOrNull()))
          .append("\n\n");
    }
    render(typesModule.typeDef(), out, INDENT);
    out.append("\n\n");
    renderDefstruct(typesModule.defstructFields(), out, INDENT);
    appendModuleEnd(out);
    return out.toString().stripTrailing() + "\n";
  }

  private void render(TypeDef typeDef, StringBuilder out, String indent) {
    String body = typeDef.body();
    if (!body.contains("\n")) {
      out.append(indent).append("@type ").append(typeDef.name()).append(" :: ").append(body);
      return;
    }
    int firstNewline = body.indexOf('\n');
    out.append(indent)
        .append("@type ")
        .append(typeDef.name())
        .append(" :: ")
        .append(body, 0, firstNewline)
        .append('\n');
    out.append(body.substring(firstNewline + 1));
  }

  private void renderDefstruct(List<DefstructField> fields, StringBuilder out, String indent) {
    boolean hasDefaults = fields.stream().anyMatch(field -> field.defaultOrNull() != null);
    if (!hasDefaults) {
      out.append(indent).append("defstruct [");
      for (int i = 0; i < fields.size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        DefstructField field = fields.get(i);
        if (field.nameOrNil() != null) {
          out.append(':').append(field.nameOrNil());
        } else {
          out.append("nil");
        }
      }
      out.append(']');
      return;
    }
    if (defstructKeywordsExceedPrintWidth(fields, indent)) {
      out.append(indent).append("defstruct");
      for (int i = 0; i < fields.size(); i++) {
        out.append(i == 0 ? "\n" : ",\n");
        out.append(indent).append("          ");
        renderDefstructKeywordField(fields.get(i), out, indent);
      }
      return;
    }
    out.append(indent).append("defstruct ");
    for (int i = 0; i < fields.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      renderDefstructKeywordField(fields.get(i), out, indent);
    }
  }

  private void renderDefstructKeywordField(DefstructField field, StringBuilder out, String indent) {
    out.append(field.nameOrNil()).append(": ");
    render(field.defaultOrNull(), out, indent);
  }

  private boolean defstructKeywordsExceedPrintWidth(List<DefstructField> fields, String indent) {
    return exceedsPrintWidth(
        scratch -> {
          scratch.append("defstruct ");
          for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
              scratch.append(", ");
            }
            renderDefstructKeywordField(fields.get(i), scratch, indent);
          }
        });
  }

  @Override
  public String renderFunction(Function function) {
    StringBuilder out = new StringBuilder();
    render(function, out, "");
    return out.toString().stripTrailing() + "\n";
  }

  @Override
  public String renderPattern(Pattern pattern) {
    StringBuilder out = new StringBuilder();
    render(pattern, out);
    return out.toString();
  }
}
