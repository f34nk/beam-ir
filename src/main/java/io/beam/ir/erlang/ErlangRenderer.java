package io.beam.ir.erlang;

import java.util.List;

public final class ErlangRenderer implements Renderer {

  private static final String INDENT = "    ";
  private static final int PRINT_WIDTH = 100;

  /** Render an expression/pattern into a scratch buffer and return its length. */
  private int compactLength(java.util.function.Consumer<StringBuilder> renderFn) {
    StringBuilder scratch = new StringBuilder();
    renderFn.accept(scratch);
    return scratch.length();
  }

  /** True when a single-line form would meet or exceed erlfmt's print width. */
  private boolean exceedsPrintWidth(java.util.function.Consumer<StringBuilder> renderFn) {
    return compactLength(renderFn) >= PRINT_WIDTH;
  }

  static int printWidthForTests() {
    return PRINT_WIDTH;
  }

  @Override
  public String render(Module module) {
    if (module.verbatimOrNull() != null) {
      return module.verbatimOrNull().endsWith("\n")
          ? module.verbatimOrNull()
          : module.verbatimOrNull() + "\n";
    }
    StringBuilder out = new StringBuilder();

    if (module.headerComments() != null) {
      for (String comment : module.headerComments()) {
        renderCommentLine(comment, out);
      }
    }
    if (module.moduledoc() != null) {
      out.append("-moduledoc \"").append(module.moduledoc().text()).append("\".\n");
    }

    out.append("-module(").append(module.name()).append(").\n");
    if (module.includeHeaders() != null) {
      for (String includeHeader : module.includeHeaders()) {
        out.append("-include(\"").append(includeHeader).append("\").\n");
      }
    } else {
      out.append('\n');
    }
    out.append("-export([");
    if (module.exports() != null && !module.exports().isEmpty()) {
      renderNamedExports(module.exports(), out);
    } else {
      renderExports(module.functions(), out);
    }
    out.append("]).\n\n");

    if (module.typeAliases() != null && !module.typeAliases().isEmpty()) {
      renderTypeAliases(module.typeAliases(), out);
      out.append('\n');
    }

    for (int i = 0; i < module.functions().size(); i++) {
      renderFunctionDefinition(module.functions().get(i), out, true);
      if (i < module.functions().size() - 1) {
        out.append('\n');
      }
    }

    return out.toString().stripTrailing() + "\n";
  }

  @Override
  public String render(Header header) {
    StringBuilder out = new StringBuilder();

    if (header.comments() != null) {
      for (String comment : header.comments()) {
        renderCommentLine(comment, out);
      }
    }

    for (RecordDef record : header.records()) {
      render(record, out);
    }

    if (header.typeAliases() != null) {
      renderTypeAliases(header.typeAliases(), out);
    }

    return out.toString().stripTrailing() + "\n";
  }

  private void renderTypeAliases(List<TypeAlias> typeAliases, StringBuilder out) {
    for (TypeAlias typeAlias : typeAliases) {
      out.append("-type ")
          .append(typeAlias.name())
          .append(" :: ")
          .append(typeAlias.definition())
          .append(".\n");
    }
  }

  @Override
  public String renderFunction(Function function) {
    if (function.verbatimOrNull() != null) {
      return function.verbatimOrNull().endsWith("\n")
          ? function.verbatimOrNull()
          : function.verbatimOrNull() + "\n";
    }
    StringBuilder out = new StringBuilder();
    renderFunctionDefinition(function, out, true);
    return out.toString().stripTrailing() + "\n";
  }

  @Override
  public String renderExpression(Expression expression) {
    StringBuilder out = new StringBuilder();
    render(expression, out, "");
    return out.toString();
  }

  @Override
  public String renderPattern(Pattern pattern) {
    StringBuilder out = new StringBuilder();
    render(pattern, out);
    return out.toString();
  }

  private void renderExports(List<Function> functions, StringBuilder out) {
    for (int i = 0; i < functions.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      Function function = functions.get(i);
      out.append(function.name()).append('/').append(function.arity());
    }
  }

  private void renderNamedExports(List<String> exports, StringBuilder out) {
    for (int i = 0; i < exports.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      out.append(exports.get(i));
    }
  }

  private void renderFunctionDefinition(
      Function function, StringBuilder out, boolean trailingNewline) {

    render(function.doc(), out);
    render(function.spec(), out);

    List<FunctionClause> clauses = function.clauses();
    boolean multilineClauses = clauses.stream().anyMatch(this::usesMultilineClauseLayout);
    for (int i = 0; i < clauses.size(); i++) {
      FunctionClause clause = clauses.get(i);
      out.append(function.name()).append('(');
      renderPatterns(clause.patterns(), out);
      out.append(')');
      if (clause.guard() != null) {
        out.append(" when ");
        render(clause.guard(), out);
      }
      out.append(" ->");
      if (!multilineClauses && isSingleLineBody(clause.body())) {
        out.append(' ');
        render(clause.body(), out, "");
      } else {
        out.append('\n');
        out.append(INDENT);
        render(clause.body(), out, INDENT);
      }

      if (i < clauses.size() - 1) {
        out.append(";\n");
      } else {
        out.append('.');
        if (trailingNewline) {
          out.append('\n');
        }
      }
    }
  }

  private boolean usesMultilineClauseLayout(FunctionClause clause) {
    if (!isSingleLineBody(clause.body())) {
      return true;
    }
    if (hasRecordPattern(clause.patterns())) {
      return true;
    }
    return isWideCall(clause.body());
  }

  private boolean hasRecordPattern(List<Pattern> patterns) {
    for (Pattern pattern : patterns) {
      if (pattern instanceof RecordPattern) {
        return true;
      }
    }
    return false;
  }

  private boolean isWideCall(Expression body) {
    if (body instanceof LocalCallExpr call) {
      return call.arguments().size() >= 4;
    }
    return false;
  }

  private boolean isSingleLineBody(Expression body) {
    if (body instanceof ListExpr list) {
      return list.elements().size() <= 1;
    }
    if (body instanceof ListComprehensionExpr comprehension) {
      return !usesMultilineListComprehensionLayout(comprehension);
    }
    if (body instanceof OpaqueExpr opaque) {
      return !opaque.text().contains("\n");
    }
    return !(body instanceof RecordExpr)
        && !(body instanceof CaseExpr)
        && !(body instanceof Fun)
        && !(body instanceof MatchExpr)
        && !(body instanceof TryExpr);
  }

  private void renderPatterns(List<Pattern> patterns, StringBuilder out) {
    for (int i = 0; i < patterns.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(patterns.get(i), out);
    }
  }

  private void render(FunctionDoc functionDoc, StringBuilder out) {
    if (functionDoc instanceof Edoc edoc) {
      render(edoc, out);
    } else if (functionDoc instanceof Doc doc) {
      render(doc, out);
    }
  }

  private void render(Doc doc, StringBuilder out) {
    if (doc == null || doc.text() == null || doc.text().isEmpty()) {
      return;
    }
    // Split doc text into lines, prefixing each with %%
    String[] lines = doc.text().split("\n");
    for (String line : lines) {
      out.append("%% ");
      out.append(line);
      out.append('\n');
    }
  }

  private void render(Edoc edoc, StringBuilder out) {
    if (edoc == null || edoc.text() == null || edoc.text().isEmpty()) {
      return;
    }
    // Split edoc text into lines, prefixing the first with %% @doc and the rest with %%
    String[] lines = edoc.text().split("\n");
    if (lines.length > 0) {
      out.append("%% @doc ");
      out.append(lines[0]);
      out.append('\n');
      for (int i = 1; i < lines.length; i++) {
        out.append("%% ");
        out.append(lines[i]);
        out.append('\n');
      }
    }
  }

  private void render(Guard guard, StringBuilder out) {
    if (guard instanceof IsTypeGuard isTypeGuard) {
      out.append("is_").append(isTypeGuard.type()).append('(');
      render(isTypeGuard.expression(), out, "");
      out.append(')');
    } else if (guard instanceof NotEqualGuard notEqualGuard) {
      render(notEqualGuard.left(), out, "");
      out.append(" =/= ");
      render(notEqualGuard.right(), out, "");
    } else if (guard instanceof EqualGuard equalGuard) {
      render(equalGuard.left(), out, "");
      out.append(" =:= ");
      render(equalGuard.right(), out, "");
    } else if (guard instanceof AndGuard andGuard) {
      List<Guard> guards = andGuard.guards();
      for (int i = 0; i < guards.size(); i++) {
        if (i > 0) {
          out.append(", ");
        }
        render(guards.get(i), out);
      }
    } else if (guard instanceof ExpressionGuard expressionGuard) {
      renderGuardExpression(expressionGuard.expression(), out, "");
    }
  }

  private void render(Spec spec, StringBuilder out) {
    if (spec == null || spec.text() == null || spec.text().isEmpty()) {
      return;
    }
    out.append("-spec ").append(spec.text()).append(".\n");
  }

  private void render(RecordDef record, StringBuilder out) {
    out.append("-record(").append(record.name()).append(", {\n");
    List<TypedField> fields = record.fields();
    for (int i = 0; i < fields.size(); i++) {
      out.append(INDENT).append(fields.get(i).name()).append(" :: ").append(fields.get(i).type());
      if (i < fields.size() - 1) {
        out.append(',');
      }
      out.append('\n');
    }
    out.append("}).\n");
  }

  private void render(Expression expression, StringBuilder out, String indent) {
    if (expression instanceof AtomExpr atom) {
      out.append(atom.value());
    } else if (expression instanceof IntegerExpr integer) {
      out.append(integer.value());
    } else if (expression instanceof Variable variable) {
      out.append(variable.name());
    } else if (expression instanceof BinaryExpr binary) {
      render(binary, out, indent);
    } else if (expression instanceof InfixExpr infix) {
      out.append('(');
      render(infix.left(), out, indent);
      out.append(' ').append(infix.operator()).append(' ');
      render(infix.right(), out, indent);
      out.append(')');
    } else if (expression instanceof CaseExpr caseExpr) {
      render(caseExpr, out, indent);
    } else if (expression instanceof RemoteCallExpr call) {
      render(call, out, indent);
    } else if (expression instanceof LocalCallExpr call) {
      render(call, out, indent);
    } else if (expression instanceof ApplyExpr apply) {
      render(apply, out, indent);
    } else if (expression instanceof MacroExpr macro) {
      out.append('?').append(macro.name());
    } else if (expression instanceof RecordExpr record) {
      render(record, out, indent);
    } else if (expression instanceof RecordFieldAccessExpr fieldAccess) {
      render(fieldAccess, out, indent);
    } else if (expression instanceof TupleExpr tuple) {
      render(tuple, out, indent);
    } else if (expression instanceof ListExpr list) {
      render(list, out, indent);
    } else if (expression instanceof ListComprehensionExpr comprehension) {
      render(comprehension, out, indent);
    } else if (expression instanceof Fun fun) {
      render(fun, out, indent);
    } else if (expression instanceof StringExpr string) {
      out.append('"').append(string.value()).append('"');
    } else if (expression instanceof MapExpr map) {
      render(map, out, indent);
    } else if (expression instanceof MatchExpr match) {
      render(match, out, indent);
    } else if (expression instanceof TryExpr tryExpr) {
      render(tryExpr, out, indent);
    } else if (expression instanceof BlockExpr block) {
      render(block, out, indent);
    } else if (expression instanceof MapEntriesExpr mapEntries) {
      render(mapEntries, out, indent);
    } else if (expression instanceof OpaqueExpr opaque) {
      renderOpaque(opaque, out, indent);
    }
  }

  private void render(BlockExpr block, StringBuilder out, String indent) {
    List<Expression> expressions = block.expressions();
    BlockExpr.BlockSeparator separator = block.separator();
    for (int i = 0; i < expressions.size(); i++) {
      if (i > 0) {
        if (separator == BlockExpr.BlockSeparator.COMMA) {
          out.append(",\n");
          if (!indent.isEmpty()) {
            out.append(indent);
          }
        } else {
          out.append('\n');
        }
      }
      render(expressions.get(i), out, indent);
    }
    if (block.terminateWithPeriod()) {
      out.append('.');
    }
  }

  private void render(MapEntriesExpr mapEntries, StringBuilder out, String indent) {
    List<MapEntry> entries = mapEntries.entries();
    for (int i = 0; i < entries.size(); i++) {
      if (i > 0) {
        out.append(",\n");
      }
      MapEntry entry = entries.get(i);
      render(entry.key(), out, indent);
      out.append(entry.updateOnly() ? " := " : " => ");
      render(entry.value(), out, indent);
    }
  }

  private void renderOpaque(OpaqueExpr opaque, StringBuilder out, String indent) {
    String text = opaque.text();
    if (!text.contains("\n")) {
      out.append(text);
      return;
    }
    String[] lines = text.split("\n", -1);
    for (int i = 0; i < lines.length; i++) {
      if (i > 0) {
        out.append('\n').append(indent);
      }
      out.append(lines[i]);
    }
  }

  private void render(RemoteCallExpr call, StringBuilder out, String indent) {
    if (useVerticalCallLayout(call.arguments())) {
      renderRemoteTarget(call.module(), out, indent);
      out.append(':');
      renderRemoteTarget(call.function(), out, indent);
      out.append("(\n");
      List<Expression> arguments = call.arguments();
      for (int i = 0; i < arguments.size(); i++) {
        out.append(indent).append(INDENT);
        render(arguments.get(i), out, indent + INDENT);
        if (i < arguments.size() - 1) {
          out.append(',');
        }
        out.append('\n');
      }
      out.append(indent).append(')');
    } else {
      renderRemoteTarget(call.module(), out, indent);
      out.append(':');
      renderRemoteTarget(call.function(), out, indent);
      out.append('(');
      renderArguments(call.arguments(), out, indent);
      out.append(')');
    }
  }

  private void renderRemoteTarget(Expression target, StringBuilder out, String indent) {
    if (needsRemoteTargetParens(target)) {
      out.append('(');
      render(target, out, indent);
      out.append(')');
    } else {
      render(target, out, indent);
    }
  }

  private void render(LocalCallExpr call, StringBuilder out, String indent) {
    out.append(call.function()).append('(');
    renderArguments(call.arguments(), out, indent);
    out.append(')');
  }

  private void render(ApplyExpr apply, StringBuilder out, String indent) {
    render(apply.callee(), out, indent);
    out.append('(');
    renderArguments(apply.arguments(), out, indent);
    out.append(')');
  }

  private void renderArguments(List<Expression> arguments, StringBuilder out, String indent) {
    for (int i = 0; i < arguments.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(arguments.get(i), out, indent);
    }
  }

  private boolean useVerticalCallLayout(List<Expression> arguments) {
    for (Expression argument : arguments) {
      if (argument instanceof Fun || argument instanceof RecordExpr) {
        return true;
      }
    }
    return false;
  }

  private void render(RecordFieldAccessExpr fieldAccess, StringBuilder out, String indent) {
    render(fieldAccess.receiver(), out, indent);
    out.append('#').append(fieldAccess.recordName()).append('.').append(fieldAccess.fieldName());
  }

  private void render(RecordExpr record, StringBuilder out, String indent) {
    if (record.base() != null
        && record.fields().size() == 1
        && isSingleLineBody(record.fields().get(0).value())) {
      render(record.base(), out, indent);
      out.append('#').append(record.name()).append("{ ");
      RecordField field = record.fields().get(0);
      out.append(field.name()).append(" = ");
      render(field.value(), out, indent);
      out.append(" }");
      return;
    }
    if (record.base() != null) {
      render(record.base(), out, indent);
      out.append('#');
    } else {
      out.append('#');
    }
    out.append(record.name()).append("{\n");
    List<RecordField> fields = record.fields();
    for (int i = 0; i < fields.size(); i++) {
      out.append(indent).append(INDENT).append(fields.get(i).name()).append(" = ");
      render(fields.get(i).value(), out, indent + INDENT);
      if (i < fields.size() - 1) {
        out.append(',');
      }
      out.append('\n');
    }
    out.append(indent).append('}');
  }

  private void render(TupleExpr tuple, StringBuilder out, String indent) {
    out.append('{');
    renderArguments(tuple.elements(), out, indent);
    out.append('}');
  }

  private void render(MapExpr map, StringBuilder out, String indent) {
    if (map.base() != null) {
      render(map.base(), out, indent);
    }
    out.append("#{");
    renderMapEntries(map.entries(), out, indent);
    out.append('}');
  }

  private void renderMapEntries(List<MapEntry> entries, StringBuilder out, String indent) {
    for (int i = 0; i < entries.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      MapEntry entry = entries.get(i);
      render(entry.key(), out, indent);
      out.append(entry.updateOnly() ? " := " : " => ");
      render(entry.value(), out, indent);
    }
  }

  private void render(MatchExpr match, StringBuilder out, String indent) {
    render(match.pattern(), out);
    if (usesMultilineMatchValue(match.value())) {
      out.append(" =\n");
      out.append(indent).append(INDENT);
      render(match.value(), out, indent + INDENT);
    } else {
      out.append(" = ");
      render(match.value(), out, indent);
    }
    if (match.body() != null) {
      out.append(",\n");
      out.append(indent);
      render(match.body(), out, indent);
    }
  }

  private boolean usesMultilineMatchValue(Expression value) {
    return value instanceof CaseExpr || value instanceof MatchExpr || value instanceof TryExpr;
  }

  private void render(ListExpr list, StringBuilder out, String indent) {
    List<Expression> elements = list.elements();
    if (elements.size() <= 1 && list.tail() == null) {
      out.append('[');
      renderArguments(elements, out, indent);
      out.append(']');
      return;
    }

    if (elements.size() <= 1 && list.tail() != null) {
      out.append('[');
      renderArguments(elements, out, indent);
      out.append(" | ");
      render(list.tail(), out, indent);
      out.append(']');
      return;
    }

    out.append('[').append('\n');
    for (int i = 0; i < elements.size(); i++) {
      out.append(indent).append(INDENT);
      render(elements.get(i), out, indent + INDENT);
      if (i < elements.size() - 1) {
        out.append(',');
      }
      out.append('\n');
    }
    if (list.tail() != null) {
      out.append(indent).append(INDENT).append('|');
      out.append(' ');
      render(list.tail(), out, indent + INDENT);
      out.append('\n');
    }
    out.append(indent).append(']');
  }

  private void render(ListComprehensionExpr comprehension, StringBuilder out, String indent) {
    if (usesMultilineListComprehensionLayout(comprehension)) {
      out.append('[').append('\n');
      out.append(indent).append(INDENT);
      render(comprehension.expression(), out, indent + INDENT);
      out.append('\n');
      out.append(indent).append(" ||");
      renderListComprehensionQualifiers(comprehension.qualifiers(), out, indent, true);
      out.append('\n');
      out.append(indent).append(']');
      return;
    }

    out.append('[');
    render(comprehension.expression(), out, indent);
    renderListComprehensionQualifiers(comprehension.qualifiers(), out, indent, false);
    out.append(']');
  }

  private void renderListComprehensionQualifiers(
      List<ListComprehensionQualifier> qualifiers,
      StringBuilder out,
      String indent,
      boolean multiline) {
    if (qualifiers.isEmpty()) {
      return;
    }

    if (!multiline) {
      out.append(" || ");
    } else if (!qualifiers.isEmpty()) {
      out.append(' ');
    }

    for (int i = 0; i < qualifiers.size(); i++) {
      if (i > 0) {
        out.append(',');
        if (multiline) {
          out.append('\n');
          out.append(indent).append(INDENT);
        } else {
          out.append(' ');
        }
      }
      renderListComprehensionQualifier(qualifiers.get(i), out, indent + INDENT);
    }
  }

  private void renderListComprehensionQualifier(
      ListComprehensionQualifier qualifier, StringBuilder out, String indent) {
    if (qualifier instanceof ListComprehensionGenerator generator) {
      render(generator.pattern(), out);
      out.append(" <- ");
      render(generator.source(), out, indent);
    } else if (qualifier instanceof ListComprehensionFilter filter) {
      renderComprehensionFilter(filter.expression(), out, indent);
    }
  }

  private void renderComprehensionFilter(Expression expression, StringBuilder out, String indent) {
    renderGuardExpression(expression, out, indent);
  }

  private void renderGuardExpression(Expression expression, StringBuilder out, String indent) {
    if (expression instanceof OpaqueExpr opaque) {
      out.append(opaque.text());
      return;
    }
    if (expression instanceof InfixExpr infix) {
      render(infix.left(), out, indent);
      out.append(' ').append(infix.operator()).append(' ');
      render(infix.right(), out, indent);
    } else {
      render(expression, out, indent);
    }
  }

  private boolean usesMultilineListComprehensionLayout(ListComprehensionExpr comprehension) {
    Expression expression = comprehension.expression();
    if (expression instanceof CaseExpr
        || expression instanceof MatchExpr
        || expression instanceof Fun
        || expression instanceof RecordExpr) {
      return true;
    }

    int filterCount = 0;
    int generatorCount = 0;
    for (ListComprehensionQualifier qualifier : comprehension.qualifiers()) {
      if (qualifier instanceof ListComprehensionFilter) {
        filterCount++;
      } else if (qualifier instanceof ListComprehensionGenerator) {
        generatorCount++;
      }
    }

    if (filterCount > 1 || generatorCount > 1) {
      return true;
    }
    return filterCount >= 1 && expression instanceof TupleExpr;
  }

  private void render(Fun fun, StringBuilder out, String indent) {
    out.append("fun\n");
    List<FunClause> clauses = fun.clauses();
    for (int i = 0; i < clauses.size(); i++) {
      out.append(indent).append(INDENT);
      renderFunClause(clauses.get(i), out);
      if (i < clauses.size() - 1) {
        out.append(';');
      }
      out.append('\n');
    }
    out.append(indent).append("end");
  }

  private void renderFunClause(FunClause clause, StringBuilder out) {
    out.append('(');
    renderPatterns(clause.patterns(), out);
    out.append(')');
    if (clause.guard() != null) {
      out.append(" when ");
      render(clause.guard(), out);
    }
    out.append(" -> ");
    render(clause.body(), out, "");
  }

  private void render(CaseExpr caseExpr, StringBuilder out, String indent) {
    out.append("case ");
    render(caseExpr.expression(), out, indent);
    out.append(" of\n");

    List<Clause> clauses = caseExpr.clauses();
    for (int i = 0; i < clauses.size(); i++) {
      out.append(indent).append(INDENT);
      render(clauses.get(i).pattern(), out);
      if (clauses.get(i).guard() != null) {
        out.append(" when ");
        render(clauses.get(i).guard(), out);
      }
      out.append(" ->");
      Expression body = clauses.get(i).body();
      if (usesMultilineCaseBody(body)) {
        out.append('\n');
        out.append(indent).append(INDENT).append(INDENT);
        render(body, out, indent + INDENT + INDENT);
      } else {
        out.append(' ');
        render(body, out, indent + INDENT);
      }
      if (i < clauses.size() - 1) {
        out.append(';');
      }
      out.append('\n');
    }

    out.append(indent).append("end");
  }

  private boolean usesMultilineCaseBody(Expression body) {
    return body instanceof CaseExpr
        || body instanceof RecordExpr
        || body instanceof MatchExpr
        || body instanceof Fun
        || body instanceof TryExpr;
  }

  private void render(TryExpr tryExpr, StringBuilder out, String indent) {
    out.append("try\n");
    out.append(indent).append(INDENT);
    render(tryExpr.body(), out, indent + INDENT);
    out.append("\n");
    out.append(indent).append("catch\n");

    List<Clause> clauses = tryExpr.catchClauses();
    for (int i = 0; i < clauses.size(); i++) {
      out.append(indent).append(INDENT);
      render(clauses.get(i).pattern(), out);
      if (clauses.get(i).guard() != null) {
        out.append(" when ");
        render(clauses.get(i).guard(), out);
      }
      out.append(" ->");
      Expression catchBody = clauses.get(i).body();
      if (usesMultilineCaseBody(catchBody)) {
        out.append('\n');
        out.append(indent).append(INDENT).append(INDENT);
        render(catchBody, out, indent + INDENT + INDENT);
      } else {
        out.append(' ');
        render(catchBody, out, indent + INDENT);
      }
      if (i < clauses.size() - 1) {
        out.append(';');
      }
      out.append('\n');
    }

    out.append(indent).append("end");
  }

  private void renderCommentLine(String comment, StringBuilder out) {
    if (comment.isEmpty()) {
      out.append("%%\n");
    } else {
      out.append("%% ").append(comment).append('\n');
    }
  }

  private void render(Pattern pattern, StringBuilder out) {
    if (pattern instanceof AtomPattern atom) {
      out.append(atom.value());
    } else if (pattern instanceof IntegerPattern integer) {
      out.append(integer.value());
    } else if (pattern instanceof VariablePattern variable) {
      out.append(variable.name());
    } else if (pattern instanceof WildcardPattern wildcard) {
      out.append('_');
      if (wildcard.name() != null) {
        out.append(wildcard.name());
      }
    } else if (pattern instanceof RecordPattern record) {
      render(record, out);
    } else if (pattern instanceof BinaryPattern binary) {
      render(binary, out);
    } else if (pattern instanceof TuplePattern tuple) {
      render(tuple, out);
    } else if (pattern instanceof MapPattern map) {
      render(map, out);
    } else if (pattern instanceof ListPattern list) {
      render(list, out);
    } else if (pattern instanceof MatchPattern match) {
      render(match.left(), out);
      out.append(" = ");
      render(match.right(), out);
    } else if (pattern instanceof CatchPattern catchPattern) {
      render(catchPattern.classPattern(), out);
      out.append(':');
      render(catchPattern.reasonPattern(), out);
    } else if (pattern instanceof OpaquePattern opaque) {
      out.append(opaque.text());
    }
  }

  private void render(BinaryExpr binary, StringBuilder out, String indent) {
    List<BinarySegmentExpr> segments = binary.segments();
    if (segments.isEmpty()) {
      out.append("<<>>");
      return;
    }
    if (segments.size() == 1 && isSimpleLiteralSegment(segments.get(0))) {
      out.append("<<\"").append(segments.get(0).literal()).append("\">>");
      return;
    }
    out.append("<<");
    for (int i = 0; i < segments.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(segments.get(i), out, indent);
    }
    out.append(">>");
  }

  private void render(BinarySegmentExpr segment, StringBuilder out, String indent) {
    if (segment.literal() != null) {
      out.append('"').append(segment.literal()).append('"');
    } else {
      boolean needsParens =
          hasBinarySegmentSpecifiers(segment) && needsParens(segment.expression());
      if (needsParens) {
        out.append('(');
      }
      render(segment.expression(), out, indent);
      if (needsParens) {
        out.append(')');
      }
    }
    renderBinarySegmentSpecifiers(segment.size(), segment.type(), segment.unit(), out);
  }

  private void render(BinaryPattern binary, StringBuilder out) {
    List<BinarySegmentPattern> segments = binary.segments();
    if (segments.isEmpty()) {
      out.append("<<>>");
      return;
    }
    if (segments.size() == 1 && isSimpleLiteralSegment(segments.get(0))) {
      out.append("<<\"").append(segments.get(0).literal()).append("\">>");
      return;
    }
    out.append("<<");
    for (int i = 0; i < segments.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      render(segments.get(i), out);
    }
    out.append(">>");
  }

  private void render(BinarySegmentPattern segment, StringBuilder out) {
    if (segment.literal() != null) {
      out.append('"').append(segment.literal()).append('"');
    } else {
      render(segment.pattern(), out);
    }
    renderBinarySegmentSpecifiers(segment.size(), segment.type(), segment.unit(), out);
  }

  private void renderBinarySegmentSpecifiers(
      Integer size, String type, Integer unit, StringBuilder out) {
    if (size != null) {
      out.append(':').append(size);
    }
    if (type != null) {
      out.append('/').append(type);
      if (unit != null) {
        out.append("-unit:").append(unit);
      }
    }
  }

  private boolean isSimpleLiteralSegment(BinarySegmentExpr segment) {
    return segment.literal() != null
        && segment.expression() == null
        && segment.size() == null
        && segment.type() == null
        && segment.unit() == null;
  }

  private boolean isSimpleLiteralSegment(BinarySegmentPattern segment) {
    return segment.literal() != null
        && segment.pattern() == null
        && segment.size() == null
        && segment.type() == null
        && segment.unit() == null;
  }

  private boolean hasBinarySegmentSpecifiers(BinarySegmentExpr segment) {
    return segment.size() != null || segment.type() != null || segment.unit() != null;
  }

  private boolean needsParens(Expression expression) {
    return !(expression instanceof Variable
        || expression instanceof AtomExpr
        || expression instanceof IntegerExpr);
  }

  private boolean needsRemoteTargetParens(Expression expression) {
    if (expression instanceof InfixExpr) {
      return false;
    }
    return needsParens(expression);
  }

  private void render(TuplePattern tuple, StringBuilder out) {
    out.append('{');
    renderPatterns(tuple.elements(), out);
    out.append('}');
  }

  private void render(RecordPattern record, StringBuilder out) {
    if (record.alias() != null) {
      out.append(record.alias()).append(" = ");
    }
    out.append('#').append(record.name()).append('{');
    List<RecordPatternField> fields = record.fields();
    for (int i = 0; i < fields.size(); i++) {
      RecordPatternField field = fields.get(i);
      if (field.pattern() != null) {
        out.append(field.name()).append(" = ");
        render(field.pattern(), out);
      } else {
        out.append(field.name());
      }
      if (i < fields.size() - 1) {
        out.append(", ");
      }
    }
    out.append('}');
  }

  private void render(MapPattern map, StringBuilder out) {
    if (map.variable() != null) {
      out.append(map.variable()).append(" = ");
    }
    out.append("#{");
    renderMapPatternEntries(map.entries(), out);
    out.append('}');
  }

  private void renderMapPatternEntries(List<MapPatternEntry> entries, StringBuilder out) {
    for (int i = 0; i < entries.size(); i++) {
      if (i > 0) {
        out.append(", ");
      }
      MapPatternEntry entry = entries.get(i);
      render(entry.key(), out, "");
      out.append(entry.updateOnly() ? " := " : " => ");
      render(entry.value(), out);
    }
  }

  private void render(ListPattern list, StringBuilder out) {
    out.append('[');
    renderPatterns(list.elements(), out);
    if (list.tail() != null) {
      out.append(" | ");
      render(list.tail(), out);
    }
    out.append(']');
  }
}
