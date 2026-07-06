package io.beam.ir.elixir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ElixirRendererTest {

  @Test
  void printWidthMatchesMixFormat() {
    assertEquals(98, DefaultElixirRenderer.printWidthForTests());
  }

  @Test
  void rendersAtomExpr() {
    assertEquals(":foo", ElixirRenderer.renderExpression(AtomExpr.of("foo")));
  }

  @Test
  void rendersNilAndBoolean() {
    assertEquals("nil", ElixirRenderer.renderExpression(NilExpr.of()));
    assertEquals("true", ElixirRenderer.renderExpression(BooleanExpr.of(true)));
  }

  @Test
  void rendersStringExpr() {
    assertEquals("\"hello\"", ElixirRenderer.renderExpression(StringExpr.of("hello")));
  }
}
