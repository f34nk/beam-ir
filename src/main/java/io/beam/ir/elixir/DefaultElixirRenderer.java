package io.beam.ir.elixir;

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
    } else {
      throw new IllegalArgumentException("Unsupported expression: " + expression);
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
    throw new UnsupportedOperationException("Pattern rendering not implemented");
  }
}
