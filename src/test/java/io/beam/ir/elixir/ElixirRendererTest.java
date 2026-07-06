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

  @Test
  void rendersCaseExpr() {
    String rendered =
        ElixirRenderer.renderExpression(
            new CaseExpr(
                RemoteCallExpr.of(
                    "RuntimeHttp",
                    "dispatch",
                    List.of(Variable.of("config"), Variable.of("req"))),
                List.of(
                    Clause.of(
                        TuplePattern.of(
                            List.of(AtomPattern.of("ok"), VariablePattern.of("resp"))),
                        RemoteCallExpr.of(
                            "HttpServiceRestJson1",
                            "decode_get_name_response",
                            List.of(Variable.of("resp")))),
                    Clause.of(
                        TuplePattern.of(
                            List.of(AtomPattern.of("error"), VariablePattern.of("reason"))),
                        TupleExpr.of(
                            List.of(
                                AtomExpr.of("error"), Variable.of("reason"))))),
                null));
    assertEquals(
        """
        case RuntimeHttp.dispatch(config, req) do
          {:ok, resp} -> HttpServiceRestJson1.decode_get_name_response(resp)
          {:error, reason} -> {:error, reason}
        end""",
        rendered);
  }

  @Test
  void rendersInlineIfExpr() {
    assertEquals(
        "if(body == \"\", do: %{}, else: Jason.decode!(body))",
        ElixirRenderer.renderExpression(
            new IfExpr(
                new InfixExpr(
                    Variable.of("body"), "==", StringExpr.of(""), null),
                MapExpr.of(List.of()),
                RemoteCallExpr.of("Jason", "decode!", List.of(Variable.of("body"))),
                true,
                null)));
  }

  @Test
  void rendersAnonFun() {
    assertEquals(
        "fn x -> x end",
        ElixirRenderer.renderExpression(
            new AnonFun(
                List.of(
                    new AnonFunClause(
                        List.of(VariablePattern.of("x")), Variable.of("x"), null)),
                null)));
  }

  @Test
  void rendersRaiseExpr() {
    assertEquals(
        "raise(ArgumentError, \"unknown event\")",
        ElixirRenderer.renderExpression(
            RaiseExpr.parenthesized(Variable.of("ArgumentError"), StringExpr.of("unknown event"))));
  }

  @Test
  void rendersBinaryExpr() {
    assertEquals(
        "<<a::32, b::16, _::4>>",
        ElixirRenderer.renderExpression(
            new BinaryExpr(
                List.of(
                    new BinarySegmentExpr(Variable.of("a"), "32", null),
                    new BinarySegmentExpr(Variable.of("b"), "16", null),
                    new BinarySegmentExpr(Variable.of("_"), "4", null)),
                null)));
  }

  @Test
  void rendersStringPattern() {
    assertEquals("\"FOO\"", ElixirRenderer.renderPattern(StringPattern.of("FOO")));
  }

  @Test
  void rendersPinPattern() {
    assertEquals("^config", ElixirRenderer.renderPattern(PinPattern.of("config")));
  }

  @Test
  void rendersGuards() {
    DefaultElixirRenderer renderer = new DefaultElixirRenderer();
    assertEquals("is_map(map)", renderer.renderGuardForTest(IsTypeGuard.of("is_map", "map")));
    assertEquals(
        "is_binary(id) and is_binary(secret)",
        renderer.renderGuardForTest(
            AndGuard.of(
                List.of(
                    IsTypeGuard.of("is_binary", "id"),
                    IsTypeGuard.of("is_binary", "secret")))));
    assertEquals(
        "map == %{}",
        renderer.renderGuardForTest(
            new ComparisonGuard(Variable.of("map"), "==", MapExpr.of(List.of()), null)));
  }

  @Test
  void rendersOneLinerFunction() {
    assertEquals(
        "defp decode_basic_string(\"FOO\"), do: :foo\n",
        ElixirRenderer.renderFunction(
            new Function(
                "decode_basic_string",
                true,
                List.of(FunctionHead.of(List.of(StringPattern.of("FOO")))),
                AtomExpr.of("foo"),
                null,
                null,
                true,
                null,
                null)));
  }

  @Test
  void rendersFunctionWithDocAndSpec() {
    assertEquals(
        """
        @doc "Encodes events"
        @spec encode(list()) :: binary()
        def encode(events) when is_list(events), do: :ok
        """,
        ElixirRenderer.renderFunction(
            new Function(
                "encode",
                false,
                List.of(
                    FunctionHead.of(
                        List.of(VariablePattern.of("events")),
                        IsTypeGuard.of("is_list", "events"))),
                AtomExpr.of("ok"),
                Spec.of("encode(list()) :: binary()"),
                FunctionDoc.of("Encodes events"),
                true,
                null,
                null)));
  }

  @Test
  void rendersBlockFunction() {
    assertEquals(
        """
        defp decode_basic_string(v) when is_binary(v) do
          {:unknown, v}
        end
        """,
        ElixirRenderer.renderFunction(
            new Function(
                "decode_basic_string",
                true,
                List.of(
                    FunctionHead.of(
                        List.of(VariablePattern.of("v")),
                        IsTypeGuard.of("is_binary", "v"))),
                TupleExpr.of(
                    List.of(AtomExpr.of("unknown"), Variable.of("v"))),
                null,
                null,
                false,
                null,
                null)));
  }
}
