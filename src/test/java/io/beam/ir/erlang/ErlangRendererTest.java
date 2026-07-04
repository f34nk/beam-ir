package io.beam.ir.erlang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErlangRendererTest {

  @Test
  void rendersAtomExpr() {
    AtomExpr atom = AtomExpr.of("foo");
    assertEquals("foo", new ErlangRenderer().renderExpression(atom));
  }

  @Test
  void rendersAtomPattern() {
    AtomPattern pattern = AtomPattern.of("foo");
    ErlangRenderer renderer = new ErlangRenderer();
    String funWrap =
        renderer.renderExpression(Fun.of(List.of(FunClause.of(pattern, null, AtomExpr.of("bar")))));
    assertEquals("fun(foo) -> bar end", funWrap);
  }

  @Test
  void rendersZeroArityFunWithBlockBody() {
    Fun fun =
        Fun.of(
            List.of(
                FunClause.of(
                    List.of(),
                    null,
                    MatchExpr.bind(
                        "Req",
                        AtomExpr.of("ok"),
                        CaseExpr.of(
                            Variable.of("Req"),
                            List.of(
                                Clause.of(
                                    TuplePattern.of(
                                        List.of(AtomPattern.of("ok"), WildcardPattern.of())),
                                    AtomExpr.of("done"))))))));
    String expected =
        """
        fun() ->
            Req = ok,
            case Req of
                {ok, _} -> done
            end
        end""";
    assertEquals(expected, new ErlangRenderer().renderExpression(fun));
  }

  @Test
  void rendersBinaryExpr() {
    // BinaryExpr could mean something like bitstring binary construction in Erlang: <<1,2>>
    BinaryExpr binaryExpr = BinaryExpr.of("foo");
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderExpression(binaryExpr);
    String expected = "<<\"foo\">>";
    assertEquals(expected, result);
  }

  @Test
  void rendersBinaryExprWithSegments() {
    BinaryExpr binaryExpr =
        BinaryExpr.of(
            List.of(
                BinarySegmentExpr.literal("https://"),
                BinarySegmentExpr.of(Variable.of("Prefix"), "binary"),
                BinarySegmentExpr.literal("."),
                BinarySegmentExpr.of(Variable.of("Region"), "binary"),
                BinarySegmentExpr.literal(".amazonaws.com")));
    String expected = "<<\"https://\", Prefix/binary, \".\", Region/binary, \".amazonaws.com\">>";
    assertEquals(expected, new ErlangRenderer().renderExpression(binaryExpr));
  }

  @Test
  void rendersBinaryExprWithParenthesizedSegment() {
    BinaryExpr binaryExpr =
        BinaryExpr.of(
            List.of(
                BinarySegmentExpr.literal(":"),
                BinarySegmentExpr.of(
                    LocalCallExpr.of("integer_to_binary", List.of(Variable.of("Port"))),
                    "binary")));
    String expected = "<<\":\", (integer_to_binary(Port))/binary>>";
    assertEquals(expected, new ErlangRenderer().renderExpression(binaryExpr));
  }

  @Test
  void rendersBinaryPattern() {
    assertEquals("<<>>", new ErlangRenderer().renderPattern(BinaryPattern.of("")));
    assertEquals("<<\"true\">>", new ErlangRenderer().renderPattern(BinaryPattern.of("true")));
  }

  @Test
  void rendersBinaryPatternWithSegments() {
    BinaryPattern pattern =
        BinaryPattern.of(
            List.of(
                BinarySegmentPattern.of(VariablePattern.of("Y"), 4, "binary"),
                BinarySegmentPattern.literal("-"),
                BinarySegmentPattern.of(VariablePattern.of("Mo"), 2, "binary"),
                BinarySegmentPattern.literal("-"),
                BinarySegmentPattern.of(VariablePattern.of("D"), 2, "binary"),
                BinarySegmentPattern.literal("T"),
                BinarySegmentPattern.of(VariablePattern.of("H"), 2, "binary"),
                BinarySegmentPattern.literal(":"),
                BinarySegmentPattern.of(VariablePattern.of("Mi"), 2, "binary"),
                BinarySegmentPattern.literal(":"),
                BinarySegmentPattern.of(VariablePattern.of("S"), 2, "binary"),
                BinarySegmentPattern.of(WildcardPattern.of(), "binary")));
    String expected =
        "<<Y:4/binary, \"-\", Mo:2/binary, \"-\", D:2/binary, \"T\", H:2/binary, \":\","
            + " Mi:2/binary, \":\",\n"
            + "    S:2/binary, _/binary>>";
    assertEquals(expected, new ErlangRenderer().renderPattern(pattern));
  }

  @Test
  void rendersBinaryPatternWithConsHead() {
    BinaryPattern pattern =
        BinaryPattern.of(
            List.of(
                BinarySegmentPattern.literal("{"),
                BinarySegmentPattern.of(VariablePattern.of("Rest"), "binary")));
    String expected = "<<\"{\", Rest/binary>>";
    assertEquals(expected, new ErlangRenderer().renderPattern(pattern));
  }

  @Test
  void rendersCaseExpr() {
    // case X of 1 -> foo; 2 -> bar end
    Variable variable = Variable.of("X");
    Clause clause1 = Clause.of(IntegerPattern.of(1), AtomExpr.of("foo"));
    Clause clause2 = Clause.of(IntegerPattern.of(2), AtomExpr.of("bar"));
    List<Clause> clauses = List.of(clause1, clause2);

    CaseExpr caseExpr = CaseExpr.of(variable, clauses);

    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderExpression(caseExpr);

    String expected =
        """
        case X of
            1 -> foo;
            2 -> bar
        end""";

    assertEquals(expected, result);
  }

  @Test
  void rendersFunction() {
    // Define a function named "inc" with one clause: inc(X) -> X + 1.
    Function function =
        Function.of(
            "inc",
            List.of(
                FunctionClause.of(
                    List.of(VariablePattern.of("X")),
                    InfixExpr.of(Variable.of("X"), "+", IntegerExpr.of(1)))));
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderFunction(function);
    String expected = "inc(X) -> (X + 1).\n";
    assertEquals(expected, result);
  }

  @Test
  void rendersFunctionWithSpec() {
    // Define a function named "inc" with one clause, and add a -spec annotation.
    Spec spec = Spec.of("inc(integer()) -> integer()");
    Function function =
        Function.of(
            "inc",
            List.of(
                FunctionClause.of(
                    List.of(VariablePattern.of("X")),
                    InfixExpr.of(Variable.of("X"), "+", IntegerExpr.of(1)))),
            spec,
            null,
            null);
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderFunction(function);
    String expected =
        """
        -spec inc(integer()) -> integer().
        inc(X) -> (X + 1).
        """;
    assertEquals(expected, result);
  }

  @Test
  void rendersFunctionWithSpecAndDoc() {
    // Define a function named "inc" with one clause, with both -spec and doc annotation.
    Spec spec = Spec.of("inc(integer()) -> integer()");
    Doc doc = Doc.of("Increment X by one.\nWorks with integers.");
    Function function =
        Function.of(
            "inc",
            List.of(
                FunctionClause.of(
                    List.of(VariablePattern.of("X")),
                    InfixExpr.of(Variable.of("X"), "+", IntegerExpr.of(1)))),
            spec,
            doc,
            null);
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderFunction(function);
    String expected =
        """
        %% Increment X by one.
        %% Works with integers.
        -spec inc(integer()) -> integer().
        inc(X) -> (X + 1).
        """;
    assertEquals(expected, result);
  }

  @Test
  void rendersFunctionWithSpecAndEdoc() {
    // Define a function named "inc" with one clause, with both -spec and @doc Edoc annotation.
    Spec spec = Spec.of("inc(integer()) -> integer()");
    Edoc edoc = Edoc.of("Increment X by one.\nWorks with integers.");
    Function function =
        Function.of(
            "inc",
            List.of(
                FunctionClause.of(
                    List.of(VariablePattern.of("X")),
                    InfixExpr.of(Variable.of("X"), "+", IntegerExpr.of(1)))),
            spec,
            edoc,
            null);
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderFunction(function);
    String expected =
        """
        %% @doc Increment X by one.
        %% Works with integers.
        -spec inc(integer()) -> integer().
        inc(X) -> (X + 1).
        """;
    assertEquals(expected, result);
  }

  @Test
  void rendersRecordPatternInFunctionHead() {
    Function function =
        Function.of(
            "decode_get_user_request",
            List.of(
                FunctionClause.of(
                    List.of(
                        RecordPattern.of(
                            "http_request",
                            List.of(RecordPatternField.of("body", VariablePattern.of("Body"))))),
                    Variable.of("Body"))));
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderFunction(function);
    String expected =
        """
        decode_get_user_request(#http_request{body = Body}) ->
            Body.
        """;
    assertEquals(expected, result);
  }

  @Test
  void rendersRecordFieldAccessExpr() {
    assertEquals(
        "Request#http_request.path",
        new ErlangRenderer()
            .renderExpression(
                RecordFieldAccessExpr.of(Variable.of("Request"), "http_request", "path")));
    assertEquals(
        "Record#basic_item.name",
        new ErlangRenderer()
            .renderExpression(
                RecordFieldAccessExpr.of(Variable.of("Record"), "basic_item", "name")));
  }

  @Test
  void rendersRecordFieldAccessExprInCallArguments() {
    LocalCallExpr call =
        LocalCallExpr.of(
            "build_url",
            List.of(
                Variable.of("Host"),
                RecordFieldAccessExpr.of(Variable.of("Request"), "http_request", "path"),
                RecordFieldAccessExpr.of(Variable.of("Request"), "http_request", "query")));
    assertEquals(
        "build_url(Host, Request#http_request.path, Request#http_request.query)",
        new ErlangRenderer().renderExpression(call));
  }

  @Test
  void rendersInfixExpr() {
    InfixExpr infixExpr = InfixExpr.of(IntegerExpr.of(1), "+", IntegerExpr.of(2));
    ErlangRenderer renderer = new ErlangRenderer();
    String result = renderer.renderExpression(infixExpr);
    String expected = "(1 + 2)";
    assertEquals(expected, result);
  }

  @Test
  void rendersTuplePattern() {
    TuplePattern pattern =
        TuplePattern.of(
            List.of(
                VariablePattern.of("Mega"),
                VariablePattern.of("Secs"),
                WildcardPattern.of("Micro")));
    String expected = "{Mega, Secs, _Micro}";
    assertEquals(expected, new ErlangRenderer().renderPattern(pattern));
  }

  @Test
  void rendersMapExpr() {
    assertEquals("#{}", new ErlangRenderer().renderExpression(MapExpr.of(List.of())));
    assertEquals(
        "#{<<\"message\">> => V}",
        new ErlangRenderer()
            .renderExpression(
                MapExpr.of(List.of(MapEntry.of(BinaryExpr.of("message"), Variable.of("V"))))));
    assertEquals(
        "Acc#{Key => Val}",
        new ErlangRenderer()
            .renderExpression(
                MapExpr.of(
                    Variable.of("Acc"),
                    List.of(MapEntry.of(Variable.of("Key"), Variable.of("Val"))))));
  }

  @Test
  void rendersMatchExprWithCase() {
    CaseExpr innerCase =
        CaseExpr.of(
            Variable.of("Body"),
            List.of(
                Clause.of(BinaryPattern.of(""), MapExpr.of(List.of())),
                Clause.of(
                    WildcardPattern.of(),
                    CaseExpr.of(
                        RemoteCallExpr.of("jsone", "try_decode", List.of(Variable.of("Body"))),
                        List.of(
                            Clause.of(
                                TuplePattern.of(
                                    List.of(
                                        AtomPattern.of("ok"),
                                        VariablePattern.of("Val"),
                                        WildcardPattern.of())),
                                Variable.of("Val")),
                            Clause.of(
                                TuplePattern.of(
                                    List.of(AtomPattern.of("error"), WildcardPattern.of())),
                                MapExpr.of(List.of())))))));

    MatchExpr match =
        MatchExpr.bind(
            "Decoded",
            innerCase,
            RecordExpr.of(
                "get_user_input",
                List.of(
                    RecordField.of(
                        "user_name",
                        RemoteCallExpr.of(
                            "maps",
                            "get",
                            List.of(
                                BinaryExpr.of("userName"),
                                Variable.of("Decoded"),
                                AtomExpr.of("undefined")))))));

    Function function =
        Function.of(
            "decode_get_user_request",
            List.of(
                FunctionClause.of(
                    List.of(
                        RecordPattern.of(
                            "http_request",
                            List.of(RecordPatternField.of("body", VariablePattern.of("Body"))))),
                    match)));

    String expected =
        """
        decode_get_user_request(#http_request{body = Body}) ->
            Decoded =
                case Body of
                    <<>> -> #{};
                    _ ->
                        case jsone:try_decode(Body) of
                            {ok, Val, _} -> Val;
                            {error, _} -> #{}
                        end
                end,
            #get_user_input{
                user_name = maps:get(<<"userName">>, Decoded, undefined)
            }.
        """;
    assertEquals(expected, new ErlangRenderer().renderFunction(function));
  }

  @Test
  void rendersMapPattern() {
    assertEquals("Map = #{}", new ErlangRenderer().renderPattern(MapPattern.bind("Map")));
  }

  @Test
  void rendersListPattern() {
    assertEquals(
        "[{<<\"message\">>, V}]",
        new ErlangRenderer()
            .renderPattern(
                ListPattern.of(
                    List.of(
                        TuplePattern.of(
                            List.of(BinaryPattern.of("message"), VariablePattern.of("V")))))));
  }

  @Test
  void rendersConsListPattern() {
    assertEquals(
        "[Base | _]",
        new ErlangRenderer()
            .renderPattern(ListPattern.cons(VariablePattern.of("Base"), WildcardPattern.of())));
    assertEquals(
        "[H | Rest]",
        new ErlangRenderer()
            .renderPattern(ListPattern.cons(VariablePattern.of("H"), VariablePattern.of("Rest"))));
  }

  @Test
  void rendersListComprehensionExprSingleLine() {
    ListComprehensionExpr comprehension =
        ListComprehensionExpr.of(
            Variable.of("V"),
            VariablePattern.of("V"),
            Variable.of("List"),
            InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("null")));

    String expected = "[V || V <- List, V =/= null]";
    assertEquals(expected, new ErlangRenderer().renderExpression(comprehension));
  }

  @Test
  void rendersListComprehensionExprWithCallAndFilter() {
    ListComprehensionExpr comprehension =
        ListComprehensionExpr.of(
            LocalCallExpr.of("decode_basic_item", List.of(Variable.of("V"))),
            VariablePattern.of("V"),
            Variable.of("List"),
            InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("null")));

    String expected = "[decode_basic_item(V) || V <- List, V =/= null]";
    assertEquals(expected, new ErlangRenderer().renderExpression(comprehension));
  }

  @Test
  void rendersListComprehensionExprMultilineWithMultipleFilters() {
    ListComprehensionExpr comprehension =
        ListComprehensionExpr.of(
            Variable.of("C"),
            List.of(
                ListComprehensionGenerator.of(VariablePattern.of("C"), Variable.of("Content")),
                ListComprehensionFilter.of(
                    LocalCallExpr.of("is_element", List.of(Variable.of("C")))),
                ListComprehensionFilter.of(
                    InfixExpr.of(
                        LocalCallExpr.of("element_name", List.of(Variable.of("C"))),
                        "=:=",
                        Variable.of("Name")))));

    String expected =
        """
        [
            C
         || C <- Content,
            is_element(C),
            element_name(C) =:= Name
        ]""";
    assertEquals(expected, new ErlangRenderer().renderExpression(comprehension));
  }

  @Test
  void rendersListComprehensionExprMultilineWithTupleExpressionAndFilter() {
    ListComprehensionExpr comprehension =
        ListComprehensionExpr.of(
            TupleExpr.of(
                List.of(
                    Variable.of("Fun"),
                    LocalCallExpr.of(
                        "make_handler", List.of(Variable.of("Impl"), Variable.of("Fun"))))),
            List.of(
                ListComprehensionGenerator.of(
                    TuplePattern.of(List.of(VariablePattern.of("Fun"), IntegerPattern.of(3))),
                    Variable.of("Callbacks")),
                ListComprehensionFilter.of(
                    RemoteCallExpr.of(
                        "erlang",
                        "function_exported",
                        List.of(Variable.of("Impl"), Variable.of("Fun"), IntegerExpr.of(3))))));

    String expected =
        """
        [
            {Fun, make_handler(Impl, Fun)}
         || {Fun, 3} <- Callbacks,
            erlang:function_exported(Impl, Fun, 3)
        ]""";
    assertEquals(expected, new ErlangRenderer().renderExpression(comprehension));
  }

  @Test
  void rendersCatchPattern() {
    assertEquals("_:_", new ErlangRenderer().renderPattern(CatchPattern.anyAny()));
    assertEquals("_:Reason", new ErlangRenderer().renderPattern(CatchPattern.anyReason("Reason")));
  }

  @Test
  void rendersTryExprWithAnyAnyCatch() {
    TryExpr tryExpr =
        TryExpr.of(
            RemoteCallExpr.of(
                "calendar", "datetime_to_gregorian_seconds", List.of(Variable.of("Dt"))),
            List.of(Clause.of(CatchPattern.anyAny(), AtomExpr.of("undefined"))));

    String expected =
        """
        try
            calendar:datetime_to_gregorian_seconds(Dt)
        catch
            _:_ -> undefined
        end""";
    assertEquals(expected, new ErlangRenderer().renderExpression(tryExpr));
  }

  @Test
  void rendersTryExprWithAnyReasonCatch() {
    TryExpr tryExpr =
        TryExpr.of(
            MatchExpr.bind(
                "Xml",
                RemoteCallExpr.of(
                    "xmerl_scan",
                    "string",
                    List.of(LocalCallExpr.of("binary_to_list", List.of(Variable.of("Body"))))),
                AtomExpr.of("ok")),
            List.of(
                Clause.of(
                    CatchPattern.anyReason("Reason"),
                    TupleExpr.of(
                        List.of(
                            AtomExpr.of("error"),
                            TupleExpr.of(
                                List.of(
                                    AtomExpr.of("xml_parse_error"), Variable.of("Reason"))))))));

    String expected =
        """
        try
            Xml = xmerl_scan:string(binary_to_list(Body)),
            ok
        catch
            _:Reason -> {error, {xml_parse_error, Reason}}
        end""";
    assertEquals(expected, new ErlangRenderer().renderExpression(tryExpr));
  }

  @Test
  void rendersTryExprWithMatchBodyChainAndCase() {
    BinaryPattern isoTimestampPattern =
        BinaryPattern.of(
            List.of(
                BinarySegmentPattern.of(VariablePattern.of("Y"), 4, "binary"),
                BinarySegmentPattern.literal("-"),
                BinarySegmentPattern.of(VariablePattern.of("Mo"), 2, "binary"),
                BinarySegmentPattern.literal("-"),
                BinarySegmentPattern.of(VariablePattern.of("D"), 2, "binary"),
                BinarySegmentPattern.literal("T"),
                BinarySegmentPattern.of(VariablePattern.of("H"), 2, "binary"),
                BinarySegmentPattern.literal(":"),
                BinarySegmentPattern.of(VariablePattern.of("Mi"), 2, "binary"),
                BinarySegmentPattern.literal(":"),
                BinarySegmentPattern.of(VariablePattern.of("S"), 2, "binary"),
                BinarySegmentPattern.of(WildcardPattern.of(), "binary")));
    Expression gregorianSecs =
        RemoteCallExpr.of("calendar", "datetime_to_gregorian_seconds", List.of(Variable.of("Dt")));
    Expression epochSecs =
        InfixExpr.of(Variable.of("GregorianSecs"), "-", IntegerExpr.of(62167219200L));
    Expression mega = InfixExpr.of(Variable.of("EpochSecs"), "div", IntegerExpr.of(1000000));
    Expression result =
        TupleExpr.of(
            List.of(
                Variable.of("Mega"),
                InfixExpr.of(Variable.of("EpochSecs"), "rem", IntegerExpr.of(1000000)),
                IntegerExpr.of(0)));

    TryExpr tryExpr =
        TryExpr.of(
            MatchExpr.of(
                isoTimestampPattern,
                Variable.of("V"),
                MatchExpr.bind(
                    "Dt",
                    TupleExpr.of(
                        List.of(
                            TupleExpr.of(
                                List.of(
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("Y"))),
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("Mo"))),
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("D"))))),
                            TupleExpr.of(
                                List.of(
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("H"))),
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("Mi"))),
                                    LocalCallExpr.of(
                                        "binary_to_integer", List.of(Variable.of("S"))))))),
                    MatchExpr.bind(
                        "GregorianSecs",
                        gregorianSecs,
                        MatchExpr.bind(
                            "EpochSecs", epochSecs, MatchExpr.bind("Mega", mega, result))))),
            List.of(Clause.of(CatchPattern.anyAny(), AtomExpr.of("undefined"))));

    String expected =
        """
        try
            <<Y:4/binary, "-", Mo:2/binary, "-", D:2/binary, "T", H:2/binary, ":", Mi:2/binary, ":",
                S:2/binary, _/binary>> = V,
            Dt = {{binary_to_integer(Y), binary_to_integer(Mo), binary_to_integer(D)}, {
                binary_to_integer(H), binary_to_integer(Mi), binary_to_integer(S)
            }},
            GregorianSecs = calendar:datetime_to_gregorian_seconds(Dt),
            EpochSecs = (GregorianSecs - 62167219200),
            Mega = (EpochSecs div 1000000),
            {Mega, (EpochSecs rem 1000000), 0}
        catch
            _:_ -> undefined
        end""";
    assertEquals(expected, new ErlangRenderer().renderExpression(tryExpr));
  }

  @Test
  void rendersModuleTypeAliases() {
    Module module =
        Module.of(
            "sigv4test_service_sigv4",
            List.of(
                Function.of(
                    "sign",
                    List.of(
                        FunctionClause.of(
                            List.of(
                                VariablePattern.of("Config"),
                                VariablePattern.of("Operation"),
                                VariablePattern.of("Request")),
                            AtomExpr.of("ok"))),
                    Spec.of(
                        "sign(client_config(), Operation :: atom(), http_request()) -> http_request()"),
                    null,
                    null)),
            null,
            null,
            "runtime_types.hrl",
            List.of(TypeAlias.of("client_config()", "#{binary() => term()}")));

    String expected =
        """
        -module(sigv4test_service_sigv4).
        -include("runtime_types.hrl").
        -export([sign/3]).

        -type client_config() :: #{binary() => term()}.

        -spec sign(client_config(), Operation :: atom(), http_request()) -> http_request().
        sign(Config, Operation, Request) -> ok.
        """;

    assertEquals(expected, new ErlangRenderer().render(module));
  }

  @Test
  void rendersRemoteCallExprWithDynamicModuleAndFunction() {
    RemoteCallExpr call =
        RemoteCallExpr.of(
            Variable.of("Impl"),
            Variable.of("Fun"),
            List.of(Variable.of("Ctx"), Variable.of("Input"), Variable.of("Meta")));
    assertEquals("Impl:Fun(Ctx, Input, Meta)", new ErlangRenderer().renderExpression(call));
  }

  @Test
  void rendersRemoteCallExprWithDynamicModule() {
    RemoteCallExpr call =
        RemoteCallExpr.of(
            Variable.of("HttpClient"),
            AtomExpr.of("request"),
            List.of(
                LocalCallExpr.of(
                    "binary_to_atom",
                    List.of(
                        RemoteCallExpr.of("string", "lowercase", List.of(Variable.of("Method"))),
                        AtomExpr.of("utf8"))),
                Variable.of("Req"),
                ListExpr.of(List.of()),
                ListExpr.of(List.of(AtomExpr.of("with_body")))));
    assertEquals(
        "HttpClient:request(binary_to_atom(string:lowercase(Method), utf8), Req, [], [with_body])",
        new ErlangRenderer().renderExpression(call));
  }

  @Test
  void rendersRemoteCallExprWithComplexModuleTarget() {
    RemoteCallExpr call =
        RemoteCallExpr.of(
            InfixExpr.of(Variable.of("A"), "+", Variable.of("B")), AtomExpr.of("run"), List.of());
    assertEquals("(A + B):run()", new ErlangRenderer().renderExpression(call));
  }

  @Test
  void rendersBlockExprWithNewlines() {
    Expression block =
        BlockExpr.newlineSeparated(
            List.of(
                MatchExpr.bindValue("Headers", ListExpr.of(List.of())),
                MatchExpr.bindValue("Body", AtomExpr.of("ok")),
                RecordExpr.of(
                    "http_response",
                    List.of(
                        RecordField.of("status", IntegerExpr.of(200)),
                        RecordField.of("body", Variable.of("Body"))))));
    String expected =
        """
        Headers = []
        Body = ok
        #http_response{
            status = 200,
            body = Body
        }""";
    assertEquals(expected, new ErlangRenderer().renderExpression(block));
  }

  @Test
  void rendersMapEntriesExpr() {
    MapEntriesExpr entries =
        MapEntriesExpr.of(
            List.of(
                MapEntry.of(BinaryExpr.of("name"), Variable.of("Name")),
                MapEntry.of(BinaryExpr.of("count"), Variable.of("Count"))));
    assertEquals(
        """
        <<\"name\">> => Name,
        <<\"count\">> => Count""",
        new ErlangRenderer().renderExpression(entries));
  }

  @Test
  void rendersStatementAddsTrailingPeriod() {
    Expression expression =
        MatchExpr.bind(
            "Req",
            RemoteCallExpr.of("mod", "encode", List.of(Variable.of("Input"))),
            AtomExpr.of("done"));
    assertEquals(
        "Req = mod:encode(Input),\ndone.", new ErlangRenderer().renderStatement(expression));
  }

  @Test
  void rendersEqualGuard() {
    Function function =
        Function.of(
            "query_suffix",
            List.of(
                FunctionClause.of(
                    List.of(VariablePattern.of("Query")),
                    EqualGuard.of(
                        LocalCallExpr.of("map_size", List.of(Variable.of("Query"))),
                        IntegerExpr.of(0)),
                    BinaryExpr.of("")),
                FunctionClause.of(
                    List.of(VariablePattern.of("Query")), null, AtomExpr.of("other"))));
    String expected =
        """
        query_suffix(Query) when map_size(Query) =:= 0 -> <<>>;
        query_suffix(Query) -> other.
        """;
    assertEquals(expected, new ErlangRenderer().renderFunction(function));
  }

  @Test
  void rendersAndGuard() {
    Function function =
        Function.of(
            "resolve_from_env",
            List.of(
                FunctionClause.of(
                    List.of(
                        TuplePattern.of(
                            List.of(VariablePattern.of("Id"), VariablePattern.of("Secret")))),
                    AndGuard.of(
                        NotEqualGuard.of(Variable.of("Id"), AtomExpr.of("false")),
                        NotEqualGuard.of(Variable.of("Secret"), AtomExpr.of("false"))),
                    AtomExpr.of("ok"))));
    String expected =
        """
        resolve_from_env({Id, Secret}) when Id =/= false, Secret =/= false -> ok.
        """;
    assertEquals(expected, new ErlangRenderer().renderFunction(function));
  }

  @Test
  void rendersExpressionGuard() {
    Function function =
        Function.of(
            "retry_clause",
            List.of(
                FunctionClause.of(
                    List.of(AtomPattern.of("true")),
                    ExpressionGuard.of(
                        InfixExpr.of(Variable.of("Attempts"), ">", IntegerExpr.of(1))),
                    AtomExpr.of("backoff"))));
    String expected = """
        retry_clause(true) when Attempts > 1 -> backoff.
        """;
    assertEquals(expected, new ErlangRenderer().renderFunction(function));
  }
}
