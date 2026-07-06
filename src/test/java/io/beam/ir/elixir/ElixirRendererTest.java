package io.beam.ir.elixir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
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

  @Test
  void rendersTupleExpr() {
    assertEquals(
        "{:ok, resp}",
        ElixirRenderer.renderExpression(
            TupleExpr.of(List.of(AtomExpr.of("ok"), Variable.of("resp")))));
  }

  @Test
  void rendersListExpr() {
    assertEquals(
        "[1, 2, 3]",
        ElixirRenderer.renderExpression(
            ListExpr.of(
                List.of(IntegerExpr.of(1), IntegerExpr.of(2), IntegerExpr.of(3)))));
  }

  @Test
  void rendersMapExpr() {
    assertEquals("%{}", ElixirRenderer.renderExpression(MapExpr.of(List.of())));
    assertEquals(
        "%{key: val}",
        ElixirRenderer.renderExpression(
            MapExpr.of(List.of(MapEntry.atomKey("key", Variable.of("val"))))));
    assertEquals(
        "%{\"k\" => v}",
        ElixirRenderer.renderExpression(
            MapExpr.of(List.of(MapEntry.stringKey("k", Variable.of("v"))))));
    assertEquals(
        "acc %{key: val}",
        ElixirRenderer.renderExpression(
            MapExpr.of(
                Variable.of("acc"), List.of(MapEntry.atomKey("key", Variable.of("val"))))));
  }

  @Test
  void rendersStructExpr() {
    assertEquals(
        "%RuntimeTypes.HttpRequest{}",
        ElixirRenderer.renderExpression(StructExpr.of("RuntimeTypes.HttpRequest", List.of())));
    assertEquals(
        "%Types.BasicItem{name: item}",
        ElixirRenderer.renderExpression(
            StructExpr.of(
                "Types.BasicItem", List.of(StructField.of("name", Variable.of("item"))))));
    assertEquals(
        "%HttpRequest{request | headers: headers}",
        ElixirRenderer.renderExpression(
            StructExpr.update(
                Variable.of("request"),
                "HttpRequest",
                List.of(StructField.of("headers", Variable.of("headers"))))));
  }

  @Test
  void rendersStructPattern() {
    assertEquals(
        "%RuntimeTypes.HttpResponse{status: status}",
        ElixirRenderer.renderPattern(
            new StructPattern(
                "RuntimeTypes.HttpResponse",
                List.of(new StructPatternField("status", VariablePattern.of("status"), null)),
                null)));
    assertEquals(
        "%{:headers => headers}",
        ElixirRenderer.renderPattern(
            new StructPattern(
                null,
                List.of(
                    new StructPatternField("headers", VariablePattern.of("headers"), null)),
                null)));
  }

  @Test
  void rendersRemoteCallExpr() {
    assertEquals(
        "Map.fetch!(config, :credentials)",
        ElixirRenderer.renderExpression(
            RemoteCallExpr.of(
                "Map",
                "fetch!",
                List.of(Variable.of("config"), AtomExpr.of("credentials")))));
  }

  @Test
  void rendersLocalCallExpr() {
    assertEquals(
        "inspect(value)",
        ElixirRenderer.renderExpression(
            LocalCallExpr.of("inspect", List.of(Variable.of("value")))));
  }

  @Test
  void rendersCaptureExpr() {
    assertEquals(
        "&encode_event_stream_event/1",
        ElixirRenderer.renderExpression(CaptureExpr.of("encode_event_stream_event", 1)));
  }

  @Test
  void rendersInfixExpr() {
    assertEquals(
        "\"/names/\" <> uri_encode(name)",
        ElixirRenderer.renderExpression(
            new InfixExpr(
                StringExpr.of("/names/"),
                "<>",
                LocalCallExpr.of("uri_encode", List.of(Variable.of("name"))),
                null)));
  }

  @Test
  void rendersPipeExpr() {
    assertEquals(
        "body |> AwsEventStream.decode_frames()",
        ElixirRenderer.renderExpression(
            new PipeExpr(
                Variable.of("body"),
                List.of(
                    new PipeStep(
                        RemoteCallExpr.of("AwsEventStream", "decode_frames", List.of()),
                        List.of(),
                        null)),
                null)));
  }

  @Test
  void rendersMatchExpr() {
    assertEquals(
        "path = \"/names/\"",
        ElixirRenderer.renderExpression(MatchExpr.bind("path", StringExpr.of("/names/"))));
  }

  @Test
  void rendersInterpolatedStringExpr() {
    assertEquals(
        "\"#{part_a}-#{part_b}\"",
        ElixirRenderer.renderExpression(
            new InterpolatedStringExpr(
                List.of(
                    new InterpolatedExpr(Variable.of("part_a")),
                    new InterpolatedLiteral("-"),
                    new InterpolatedExpr(Variable.of("part_b"))),
                null)));
  }
}
