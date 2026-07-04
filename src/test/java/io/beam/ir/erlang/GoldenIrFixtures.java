package io.beam.ir.erlang;

import java.util.List;

final class GoldenIrFixtures {

  private GoldenIrFixtures() {}

  static Function decodeBasicItemFunction(FunctionDoc doc) {
    return Function.of(
        "decode_basic_item",
        decodeBasicItemClauses(),
        Spec.of("decode_basic_item(undefined | null | map()) -> undefined | #basic_item{}"),
        doc,
        null);
  }

  static Function decodeBasicItemWithoutSpecFunction() {
    return Function.of("decode_basic_item", decodeBasicItemClauses());
  }

  private static List<FunctionClause> decodeBasicItemClauses() {
    return List.of(
        FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
        FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
        FunctionClause.of(
            List.of(VariablePattern.of("Map")),
            IsTypeGuard.of("map", Variable.of("Map")),
            decodeBasicItemMapBody()));
  }

  static Module basicServiceRestJson1Module() {
    return Module.of(
        "basic_service_rest_json_1",
        List.of(decodeBasicItemFunction(null)),
        List.of("Generated REST JSON codec."),
        Moduledoc.of("REST JSON 1 codecs for basic_service (generated)."),
        "basic_types.hrl");
  }

  static Header basicTypesHeader() {
    return Header.of(
        List.of("Record and type definitions for the basic_service_types model.", ""),
        List.of(
            RecordDef.of(
                "basic_item",
                List.of(
                    TypedField.of("name", "basic_string()"),
                    TypedField.of("count", "basic_integer() | undefined")))),
        List.of(TypeAlias.of("basic_item()", "#basic_item{}")));
  }

  static Expression filtermapVerboseExpression() {
    return RemoteCallExpr.of(
        "lists",
        "filtermap",
        List.of(
            Fun.of(
                List.of(
                    FunClause.of(
                        VariablePattern.of("V"),
                        NotEqualGuard.of(Variable.of("V"), AtomExpr.of("undefined")),
                        TupleExpr.of(
                            List.of(
                                AtomExpr.of("true"),
                                TupleExpr.of(
                                    List.of(
                                        BinaryExpr.of("verbose"),
                                        LocalCallExpr.of(
                                            "encode_query_value", List.of(Variable.of("V")))))))),
                    FunClause.of(WildcardPattern.of(), AtomExpr.of("false")))),
            ListExpr.of(List.of(Variable.of("Verbose")))));
  }

  static Header getNameOutputStructureHeader() {
    return Header.of(
        List.of(),
        List.of(
            RecordDef.of("get_name_output", List.of(TypedField.of("name", "name() | undefined")))),
        List.of(TypeAlias.of("get_name_output()", "#get_name_output{}")));
  }

  static Function awsJsonErrorDispatchGetUserFunction() {
    return Function.of(
        "decode_get_user_response_error",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Status"),
                    WildcardPattern.of("Hdrs"),
                    VariablePattern.of("Body")),
                TupleExpr.of(
                    List.of(
                        AtomExpr.of("error"),
                        TupleExpr.of(
                            List.of(
                                AtomExpr.of("unknown_error"),
                                Variable.of("Status"),
                                Variable.of("Body"))))))),
        null,
        Edoc.of("Error dispatch for smithy.beam.test.awsjson11#GetUser."),
        null);
  }

  static Function uriEncodeFunction() {
    return Function.of(
        "uri_encode",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Value")),
                RemoteCallExpr.of("uri_string", "quote", List.of(Variable.of("Value"))))));
  }

  static Function uriDecodeFunction() {
    return Function.of(
        "uri_decode",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Value")),
                IsTypeGuard.of("binary", Variable.of("Value")),
                RemoteCallExpr.of("uri_string", "unquote", List.of(Variable.of("Value")))),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  static Function generateUuidFunction() {
    return Function.of(
        "generate_uuid",
        List.of(
            FunctionClause.of(
                List.of(),
                LocalCallExpr.of(
                    "list_to_binary",
                    List.of(
                        RemoteCallExpr.of(
                            "uuid",
                            "to_string",
                            List.of(RemoteCallExpr.of("uuid", "v4", List.of()))))))));
  }

  static Function headersSetFunction() {
    return Function.of(
        "headers_set",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Name"),
                    VariablePattern.of("Value"),
                    VariablePattern.of("Headers")),
                RemoteCallExpr.of(
                    "lists",
                    "keystore",
                    List.of(
                        Variable.of("Name"),
                        IntegerExpr.of(1),
                        Variable.of("Headers"),
                        TupleExpr.of(List.of(Variable.of("Name"), Variable.of("Value"))))))));
  }

  static Function toBinaryRestJsonFunction() {
    return Function.of(
        "to_binary",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("binary", Variable.of("V")),
                Variable.of("V")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("list", Variable.of("V")),
                LocalCallExpr.of("list_to_binary", List.of(Variable.of("V")))),
            FunctionClause.of(List.of(AtomPattern.of("true")), BinaryExpr.of("true")),
            FunctionClause.of(List.of(AtomPattern.of("false")), BinaryExpr.of("false")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("atom", Variable.of("V")),
                LocalCallExpr.of("atom_to_binary", List.of(Variable.of("V"), AtomExpr.of("utf8")))),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("integer", Variable.of("V")),
                LocalCallExpr.of("integer_to_binary", List.of(Variable.of("V")))),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("float", Variable.of("V")),
                LocalCallExpr.of("float_to_binary", List.of(Variable.of("V"))))));
  }

  static Function encodeQueryValueRestJsonFunction() {
    return Function.of(
        "encode_query_value",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("boolean", Variable.of("V")),
                LocalCallExpr.of("atom_to_binary", List.of(Variable.of("V"), AtomExpr.of("utf8")))),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("integer", Variable.of("V")),
                LocalCallExpr.of("integer_to_binary", List.of(Variable.of("V")))),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("float", Variable.of("V")),
                LocalCallExpr.of("float_to_binary", List.of(Variable.of("V")))),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("binary", Variable.of("V")),
                Variable.of("V")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("atom", Variable.of("V")),
                LocalCallExpr.of(
                    "atom_to_binary", List.of(Variable.of("V"), AtomExpr.of("utf8"))))));
  }

  static Function flattenQueryInputFunction() {
    return Function.of(
        "flatten_query_input",
        List.of(
            FunctionClause.of(
                List.of(
                    RecordPattern.of(
                        "delete_user_input",
                        List.of(
                            RecordPatternField.of("user_name", VariablePattern.of("UserName"))))),
                RemoteCallExpr.of(
                    "lists",
                    "append",
                    List.of(
                        ListExpr.of(
                            List.of(
                                LocalCallExpr.of(
                                    "flatten_member",
                                    List.of(
                                        BinaryExpr.of("UserName"), Variable.of("UserName")))))))),
            FunctionClause.of(
                List.of(
                    RecordPattern.of(
                        "list_users_input",
                        List.of(
                            RecordPatternField.of(
                                "path_prefix", VariablePattern.of("PathPrefix"))))),
                RemoteCallExpr.of(
                    "lists",
                    "append",
                    List.of(
                        ListExpr.of(
                            List.of(
                                LocalCallExpr.of(
                                    "flatten_member",
                                    List.of(
                                        BinaryExpr.of("PathPrefix"),
                                        Variable.of("PathPrefix"))))))))));
  }

  static Function parseListUsersInputInputFunction() {
    return Function.of(
        "parse_list_users_input_input",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Params")),
                RecordExpr.of(
                    "list_users_input",
                    List.of(
                        RecordField.of(
                            "path_prefix",
                            LocalCallExpr.of(
                                "form_value",
                                List.of(Variable.of("Params"), BinaryExpr.of("PathPrefix")))))))));
  }

  static Function credentialProviderResolveFunction() {
    return Function.of(
        "resolve",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Config")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "maps",
                        "get",
                        List.of(
                            AtomExpr.of("credentials"),
                            Variable.of("Config"),
                            AtomExpr.of("undefined"))),
                    List.of(
                        Clause.of(
                            AtomPattern.of("undefined"),
                            LocalCallExpr.of("resolve_chain", List.of(Variable.of("Config")))),
                        Clause.of(
                            VariablePattern.of("Creds"),
                            TupleExpr.of(List.of(AtomExpr.of("ok"), Variable.of("Creds")))))))),
        Spec.of("resolve(client_config()) -> {ok, aws_credentials()} | {error, term()}"),
        null,
        null);
  }

  static Function encodeEventHeadersFunction() {
    return Function.of(
        "encode_event_headers",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("EventType")),
                ListExpr.of(
                    List.of(
                        TupleExpr.of(
                            List.of(BinaryExpr.of(":event-type"), Variable.of("EventType"))),
                        TupleExpr.of(
                            List.of(BinaryExpr.of(":message-type"), BinaryExpr.of("event"))),
                        TupleExpr.of(
                            List.of(
                                BinaryExpr.of(":content-type"),
                                BinaryExpr.of("application/json"))))))));
  }

  static Function headerValueFunction() {
    return Function.of(
        "header_value",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Headers"), VariablePattern.of("Name")),
                RemoteCallExpr.of(
                    "proplists",
                    "get_value",
                    List.of(
                        Variable.of("Name"), Variable.of("Headers"), AtomExpr.of("undefined"))))));
  }

  static Function handlerDiscoveryHandleGetNameFunction() {
    return Function.of(
        "handle_get_name",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Ctx"),
                    VariablePattern.of("Input"),
                    VariablePattern.of("Meta")),
                LocalCallExpr.of(
                    "dispatch_handler",
                    List.of(
                        AtomExpr.of("handle_get_name"),
                        Variable.of("Ctx"),
                        Variable.of("Input"),
                        Variable.of("Meta"))))));
  }

  static Function httpDispatchDispatchArity3Function() {
    return Function.of(
        "dispatch",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("HttpClient"),
                    VariablePattern.of("Config"),
                    VariablePattern.of("Request")),
                LocalCallExpr.of(
                    "dispatch_signed",
                    List.of(
                        Variable.of("HttpClient"),
                        Variable.of("Config"),
                        Variable.of("Request"))))));
  }

  static Function httpDispatchMimeFunction() {
    return Function.of(
        "mime",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Headers")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "proplists",
                        "get_value",
                        List.of(BinaryExpr.of("Content-Type"), Variable.of("Headers"))),
                    List.of(
                        Clause.of(
                            AtomPattern.of("undefined"), StringExpr.of("application/octet-stream")),
                        Clause.of(
                            VariablePattern.of("CT"),
                            LocalCallExpr.of("binary_to_list", List.of(Variable.of("CT")))))))));
  }

  static Function decodeQueryParamFunction() {
    return Function.of(
        "decode_query_param",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(BinaryPattern.of("true")), AtomExpr.of("true")),
            FunctionClause.of(List.of(BinaryPattern.of("false")), AtomExpr.of("false")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("binary", Variable.of("V")),
                Variable.of("V"))));
  }

  static Function enumDecodeColorFunction() {
    return Function.of(
        "decode_color",
        List.of(
            FunctionClause.of(List.of(BinaryPattern.of("RED")), AtomExpr.of("red")),
            FunctionClause.of(List.of(BinaryPattern.of("BLUE")), AtomExpr.of("blue")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("binary", Variable.of("V")),
                TupleExpr.of(List.of(AtomExpr.of("unknown"), Variable.of("V")))),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  static Function encodeTimestampEpochSecondsFunction() {
    return Function.of(
        "encode_timestamp_epoch_seconds",
        List.of(
            FunctionClause.of(
                List.of(
                    TuplePattern.of(
                        List.of(
                            VariablePattern.of("Mega"),
                            VariablePattern.of("Secs"),
                            WildcardPattern.of("Micro")))),
                InfixExpr.of(
                    InfixExpr.of(Variable.of("Mega"), "*", IntegerExpr.of(1000000)),
                    "+",
                    Variable.of("Secs"))),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  static Function awsJsonDecodeGetUserRequestFunction() {
    return Function.of(
        "decode_get_user_request",
        List.of(
            FunctionClause.of(
                List.of(
                    RecordPattern.of(
                        "http_request",
                        List.of(RecordPatternField.of("body", VariablePattern.of("Body"))))),
                MatchExpr.bind(
                    "Decoded",
                    decodedBodyCase(),
                    RecordExpr.of(
                        "get_user_input",
                        List.of(RecordField.of("user_name", mapsGet("userName", "Decoded"))))))),
        Spec.of("decode_get_user_request(#http_request{}) -> get_user_input()"),
        Edoc.of("Decode AWS JSON request for smithy.beam.test.awsjson11#GetUser."),
        null);
  }

  static Function encodeTimestampDateTimeFunction() {
    return Function.of(
        "encode_timestamp_date_time",
        List.of(
            FunctionClause.of(
                List.of(
                    TuplePattern.of(
                        List.of(
                            VariablePattern.of("Mega"),
                            VariablePattern.of("Secs"),
                            WildcardPattern.of("Micro")))),
                encodeTimestampDateTimeBody()),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  private static MatchExpr encodeTimestampDateTimeBody() {
    Expression epochSecs =
        InfixExpr.of(
            InfixExpr.of(Variable.of("Mega"), "*", IntegerExpr.of(1000000)),
            "+",
            Variable.of("Secs"));
    Expression gregorian =
        RemoteCallExpr.of(
            "calendar",
            "gregorian_seconds_to_datetime",
            List.of(InfixExpr.of(Variable.of("EpochSecs"), "+", IntegerExpr.of(62167219200L))));
    Expression formatted =
        LocalCallExpr.of(
            "iolist_to_binary",
            List.of(
                RemoteCallExpr.of(
                    "io_lib",
                    "format",
                    List.of(
                        StringExpr.of("~4..0B-~2..0B-~2..0BT~2..0B:~2..0B:~2..0BZ"),
                        ListExpr.of(
                            List.of(
                                Variable.of("Y"),
                                Variable.of("Mo"),
                                Variable.of("D"),
                                Variable.of("H"),
                                Variable.of("Mi"),
                                Variable.of("S")))))));
    Pattern dateTimeTuple =
        TuplePattern.of(
            List.of(
                TuplePattern.of(
                    List.of(
                        VariablePattern.of("Y"),
                        VariablePattern.of("Mo"),
                        VariablePattern.of("D"))),
                TuplePattern.of(
                    List.of(
                        VariablePattern.of("H"),
                        VariablePattern.of("Mi"),
                        VariablePattern.of("S")))));
    return MatchExpr.of(
        VariablePattern.of("EpochSecs"),
        epochSecs,
        MatchExpr.of(dateTimeTuple, gregorian, formatted));
  }

  private static CaseExpr decodedBodyCase() {
    return CaseExpr.of(
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
                            TuplePattern.of(List.of(AtomPattern.of("error"), WildcardPattern.of())),
                            MapExpr.of(List.of())))))));
  }

  private static RecordExpr decodeBasicItemMapBody() {
    return RecordExpr.of(
        "basic_item",
        List.of(
            RecordField.of("name", mapsGet("name", "Map")),
            RecordField.of("count", mapsGet("count", "Map"))));
  }

  private static RemoteCallExpr mapsGet(String key, String mapVariable) {
    return RemoteCallExpr.of(
        "maps",
        "get",
        List.of(BinaryExpr.of(key), Variable.of(mapVariable), AtomExpr.of("undefined")));
  }

  static Function decodeJsonBodyFunction() {
    return Function.of(
        "decode_json_body",
        List.of(
            FunctionClause.of(List.of(BinaryPattern.of("")), MapExpr.of(List.of())),
            FunctionClause.of(
                List.of(VariablePattern.of("Body")),
                CaseExpr.of(
                    RemoteCallExpr.of("jsone", "try_decode", List.of(Variable.of("Body"))),
                    List.of(
                        Clause.of(
                            TuplePattern.of(
                                List.of(
                                    AtomPattern.of("ok"),
                                    VariablePattern.of("V"),
                                    WildcardPattern.of())),
                            IsTypeGuard.of("map", Variable.of("V")),
                            Variable.of("V")),
                        Clause.of(WildcardPattern.of(), MapExpr.of(List.of())))))));
  }

  static Function decodeEventFunction() {
    Pattern messageEntry =
        ListPattern.of(
            List.of(
                TuplePattern.of(List.of(BinaryPattern.of("message"), VariablePattern.of("V")))));
    Pattern codeEntry =
        ListPattern.of(
            List.of(TuplePattern.of(List.of(BinaryPattern.of("code"), VariablePattern.of("V")))));
    Pattern unknownEntry =
        ListPattern.of(
            List.of(TuplePattern.of(List.of(VariablePattern.of("K"), WildcardPattern.of("V")))));

    return Function.of(
        "decode_event",
        List.of(
            FunctionClause.of(
                List.of(MapPattern.bind("Map")),
                CaseExpr.of(
                    RemoteCallExpr.of("maps", "to_list", List.of(Variable.of("Map"))),
                    List.of(
                        Clause.of(
                            messageEntry,
                            TupleExpr.of(List.of(AtomExpr.of("message"), Variable.of("V")))),
                        Clause.of(
                            codeEntry,
                            TupleExpr.of(List.of(AtomExpr.of("code"), Variable.of("V")))),
                        Clause.of(
                            unknownEntry,
                            TupleExpr.of(List.of(AtomExpr.of("unknown"), Variable.of("K")))),
                        Clause.of(WildcardPattern.of(), AtomExpr.of("undefined"))))),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined"))));
  }

  static Function encodeEventFunction() {
    return Function.of(
        "encode_event",
        List.of(
            FunctionClause.of(
                List.of(
                    TuplePattern.of(List.of(AtomPattern.of("message"), VariablePattern.of("V")))),
                MapExpr.of(List.of(MapEntry.of(BinaryExpr.of("message"), Variable.of("V"))))),
            FunctionClause.of(
                List.of(TuplePattern.of(List.of(AtomPattern.of("code"), VariablePattern.of("V")))),
                MapExpr.of(List.of(MapEntry.of(BinaryExpr.of("code"), Variable.of("V"))))),
            FunctionClause.of(
                List.of(
                    TuplePattern.of(List.of(AtomPattern.of("unknown"), VariablePattern.of("K")))),
                IsTypeGuard.of("binary", Variable.of("K")),
                MapExpr.of(List.of(MapEntry.of(Variable.of("K"), AtomExpr.of("null"))))),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  static Function enumEncodeColorFunction() {
    return Function.of(
        "encode_color",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("red")), BinaryExpr.of("RED")),
            FunctionClause.of(List.of(AtomPattern.of("blue")), BinaryExpr.of("BLUE")),
            FunctionClause.of(
                List.of(
                    TuplePattern.of(List.of(AtomPattern.of("unknown"), VariablePattern.of("V")))),
                IsTypeGuard.of("binary", Variable.of("V")),
                Variable.of("V")),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined"))));
  }

  static Function decodeTimestampEpochSecondsFunction() {
    Expression epochTuple =
        MatchExpr.bind(
            "Mega",
            InfixExpr.of(Variable.of("V"), "div", IntegerExpr.of(1000000)),
            MatchExpr.bind(
                "Secs",
                InfixExpr.of(Variable.of("V"), "rem", IntegerExpr.of(1000000)),
                TupleExpr.of(
                    List.of(Variable.of("Mega"), Variable.of("Secs"), IntegerExpr.of(0)))));
    return Function.of(
        "decode_timestamp_epoch_seconds",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("number", Variable.of("V")),
                epochTuple)));
  }

  static Function decodeSparseMapFunction() {
    return Function.of(
        "decode_sparse_map",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("Map")),
                IsTypeGuard.of("map", Variable.of("Map")),
                sparseMapTransform(AtomPattern.of("null"), AtomExpr.of("undefined")))));
  }

  static Function encodeSparseMapFunction() {
    return Function.of(
        "encode_sparse_map",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("null")),
            FunctionClause.of(
                List.of(VariablePattern.of("Map")),
                IsTypeGuard.of("map", Variable.of("Map")),
                sparseMapTransform(AtomPattern.of("undefined"), AtomExpr.of("null")))));
  }

  private static RemoteCallExpr sparseMapTransform(Pattern nullPattern, Expression replacement) {
    return RemoteCallExpr.of(
        "maps",
        "map",
        List.of(
            Fun.of(
                List.of(
                    FunClause.of(List.of(WildcardPattern.of("K"), nullPattern), replacement),
                    FunClause.of(
                        List.of(WildcardPattern.of("K"), VariablePattern.of("V")),
                        Variable.of("V")))),
            Variable.of("Map")));
  }

  static Function httpDispatchDispatchArity2Function() {
    return Function.of(
        "dispatch",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Config"), VariablePattern.of("Request")),
                MatchExpr.bind(
                    "HttpClient",
                    RemoteCallExpr.of(
                        "maps",
                        "get",
                        List.of(
                            AtomExpr.of("http_client"),
                            Variable.of("Config"),
                            AtomExpr.of("httpc"))),
                    LocalCallExpr.of(
                        "dispatch",
                        List.of(
                            Variable.of("HttpClient"),
                            Variable.of("Config"),
                            Variable.of("Request")))))));
  }

  static Function sigv4SignFunction() {
    Expression body =
        MatchExpr.bind(
            "Credentials",
            RemoteCallExpr.of(
                "maps", "get", List.of(AtomExpr.of("credentials"), Variable.of("Config"))),
            MatchExpr.bind(
                "Region",
                RemoteCallExpr.of(
                    "maps",
                    "get",
                    List.of(
                        AtomExpr.of("region"), Variable.of("Config"), BinaryExpr.of("us-east-1"))),
                MatchExpr.bind(
                    "Service",
                    RemoteCallExpr.of(
                        "maps", "get", List.of(AtomExpr.of("signing_name"), Variable.of("Config"))),
                    MatchExpr.bind(
                        "Unsigned",
                        RemoteCallExpr.of(
                            "maps",
                            "get",
                            List.of(
                                TupleExpr.of(
                                    List.of(
                                        AtomExpr.of("unsigned_payload"), Variable.of("Operation"))),
                                Variable.of("Config"),
                                AtomExpr.of("false"))),
                        MatchExpr.bind(
                            "Opts",
                            MapExpr.of(
                                List.of(
                                    MapEntry.of(
                                        AtomExpr.of("unsigned_payload"), Variable.of("Unsigned")),
                                    MapEntry.of(
                                        AtomExpr.of("endpoint_host"),
                                        LocalCallExpr.of(
                                            "endpoint_host_from_config",
                                            List.of(Variable.of("Config")))))),
                            LocalCallExpr.of(
                                "sign_request",
                                List.of(
                                    Variable.of("Request"),
                                    Variable.of("Credentials"),
                                    Variable.of("Region"),
                                    Variable.of("Service"),
                                    Variable.of("Opts"))))))));
    return Function.of(
        "sign",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Config"),
                    VariablePattern.of("Operation"),
                    VariablePattern.of("Request")),
                body)),
        Spec.of("sign(client_config(), Operation :: atom(), http_request()) -> http_request()"),
        null,
        null);
  }

  static Function endpointRulesMergeParamsFunction() {
    return Function.of(
        "merge_params",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Config"), VariablePattern.of("Params")),
                MatchExpr.bind(
                    "ConfigParams",
                    LocalCallExpr.of("config_to_rule_params", List.of(Variable.of("Config"))),
                    MatchExpr.bind(
                        "ClientParams",
                        LocalCallExpr.of("client_context_params", List.of(Variable.of("Config"))),
                        RemoteCallExpr.of(
                            "maps",
                            "merge",
                            List.of(
                                RemoteCallExpr.of(
                                    "maps",
                                    "merge",
                                    List.of(
                                        Variable.of("ConfigParams"), Variable.of("ClientParams"))),
                                Variable.of("Params"))))))));
  }

  static Function endpointRulesConfigToRuleParamsFunction() {
    return Function.of(
        "config_to_rule_params",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Config")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "maps",
                        "get",
                        List.of(
                            AtomExpr.of("region"),
                            Variable.of("Config"),
                            AtomExpr.of("undefined"))),
                    List.of(
                        Clause.of(AtomPattern.of("undefined"), MapExpr.of(List.of())),
                        Clause.of(
                            VariablePattern.of("Value"),
                            MapExpr.of(
                                List.of(
                                    MapEntry.of(
                                        BinaryExpr.of("Region"), Variable.of("Value"))))))))));
  }

  static Function endpointRulesClientContextParamsFunction() {
    return Function.of(
        "client_context_params",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Config")),
                RemoteCallExpr.of(
                    "maps",
                    "merge",
                    List.of(
                        LocalCallExpr.of(
                            "optional_param",
                            List.of(
                                Variable.of("Config"),
                                AtomExpr.of("region"),
                                BinaryExpr.of("Region"))),
                        LocalCallExpr.of(
                            "optional_param",
                            List.of(
                                Variable.of("Config"),
                                AtomExpr.of("bucket"),
                                BinaryExpr.of("Bucket"))))))));
  }

  static Function endpointRulesOptionalParamFunction() {
    return Function.of(
        "optional_param",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Config"),
                    VariablePattern.of("Key"),
                    VariablePattern.of("RuleKey")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "maps",
                        "get",
                        List.of(
                            Variable.of("Key"), Variable.of("Config"), AtomExpr.of("undefined"))),
                    List.of(
                        Clause.of(AtomPattern.of("undefined"), MapExpr.of(List.of())),
                        Clause.of(
                            VariablePattern.of("Value"),
                            MapExpr.of(
                                List.of(
                                    MapEntry.of(
                                        Variable.of("RuleKey"), Variable.of("Value"))))))))));
  }

  static Function decodeListFunction() {
    ListComprehensionExpr comprehension =
        ListComprehensionExpr.of(
            Variable.of("V"),
            VariablePattern.of("V"),
            Variable.of("List"),
            InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("null")));
    return Function.of(
        "decode_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                comprehension)));
  }

  static Function decodeBasicItemListFunction() {
    return Function.of(
        "decode_basic_item_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("decode_basic_item", List.of(Variable.of("V"))),
                    VariablePattern.of("V"),
                    Variable.of("List"),
                    InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("null"))))));
  }

  static Function encodeBasicItemListFunction() {
    return Function.of(
        "encode_basic_item_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("encode_basic_item", List.of(Variable.of("V"))),
                    VariablePattern.of("V"),
                    Variable.of("List"),
                    InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("undefined"))))));
  }

  static Function ctBaseFunction() {
    return Function.of(
        "ct_base",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("CT")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "binary", "split", List.of(Variable.of("CT"), BinaryExpr.of(";"))),
                    List.of(
                        Clause.of(
                            ListPattern.cons(VariablePattern.of("Base"), WildcardPattern.of()),
                            Variable.of("Base")),
                        Clause.of(WildcardPattern.of(), Variable.of("CT")))))));
  }

  static Function decodeSparseListFunction() {
    CaseExpr nullToUndefined =
        CaseExpr.of(
            Variable.of("V"),
            List.of(
                Clause.of(AtomPattern.of("null"), AtomExpr.of("undefined")),
                Clause.of(WildcardPattern.of(), Variable.of("V"))));
    return Function.of(
        "decode_sparse_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    nullToUndefined, VariablePattern.of("V"), Variable.of("List")))));
  }

  static Function encodeSparseListFunction() {
    CaseExpr undefinedToNull =
        CaseExpr.of(
            Variable.of("V"),
            List.of(
                Clause.of(AtomPattern.of("undefined"), AtomExpr.of("null")),
                Clause.of(WildcardPattern.of(), Variable.of("V"))));
    return Function.of(
        "encode_sparse_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("null")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    undefinedToNull, VariablePattern.of("V"), Variable.of("List")))));
  }

  static Function contentTypeMatchesFunction() {
    Pattern binaryMatchGuard =
        MatchPattern.of(
            BinaryPattern.of(List.of(BinarySegmentPattern.of(WildcardPattern.of(), "binary"))),
            VariablePattern.of("CT"));
    return Function.of(
        "content_type_matches",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Headers"), VariablePattern.of("Expected")),
                CaseExpr.of(
                    RemoteCallExpr.of(
                        "proplists",
                        "get_value",
                        List.of(
                            BinaryExpr.of("Content-Type"),
                            Variable.of("Headers"),
                            AtomExpr.of("undefined"))),
                    List.of(
                        Clause.of(VariablePattern.of("Expected"), AtomExpr.of("true")),
                        Clause.of(
                            binaryMatchGuard,
                            InfixExpr.of(
                                LocalCallExpr.of("ct_base", List.of(Variable.of("CT"))),
                                "=:=",
                                LocalCallExpr.of("ct_base", List.of(Variable.of("Expected"))))),
                        Clause.of(WildcardPattern.of(), AtomExpr.of("false")))))));
  }

  static Function prefixHeadersToListFunction() {
    Expression headerName =
        BinaryExpr.of(
            List.of(
                BinarySegmentExpr.of(Variable.of("Prefix"), "binary"),
                BinarySegmentExpr.of(Variable.of("H"), "binary")));
    return Function.of(
        "prefix_headers_to_list",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("_Prefix"), AtomPattern.of("undefined")),
                ListExpr.of(List.of())),
            FunctionClause.of(
                List.of(VariablePattern.of("Prefix"), VariablePattern.of("Map")),
                IsTypeGuard.of("map", Variable.of("Map")),
                ListComprehensionExpr.of(
                    TupleExpr.of(
                        List.of(
                            headerName, LocalCallExpr.of("to_binary", List.of(Variable.of("V"))))),
                    TuplePattern.of(List.of(VariablePattern.of("H"), VariablePattern.of("V"))),
                    RemoteCallExpr.of("maps", "to_list", List.of(Variable.of("Map")))))));
  }

  static Function prefixHeadersFromListFunction() {
    ListComprehensionExpr headerEntries =
        ListComprehensionExpr.of(
            TupleExpr.of(
                List.of(
                    RemoteCallExpr.of(
                        "binary",
                        "part",
                        List.of(
                            Variable.of("Name"),
                            LocalCallExpr.of("byte_size", List.of(Variable.of("Prefix"))))),
                    Variable.of("Val"))),
            TuplePattern.of(List.of(VariablePattern.of("Name"), VariablePattern.of("Val"))),
            Variable.of("Headers"),
            List.of(
                InfixExpr.of(
                    LocalCallExpr.of("byte_size", List.of(Variable.of("Name"))),
                    ">",
                    LocalCallExpr.of("byte_size", List.of(Variable.of("Prefix")))),
                InfixExpr.of(
                    RemoteCallExpr.of(
                        "binary",
                        "part",
                        List.of(
                            Variable.of("Name"),
                            IntegerExpr.of(0),
                            LocalCallExpr.of("byte_size", List.of(Variable.of("Prefix"))))),
                    "=:=",
                    Variable.of("Prefix"))));
    return Function.of(
        "prefix_headers_from_list",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Headers"), VariablePattern.of("Prefix")),
                MatchExpr.bind(
                    "Map",
                    RemoteCallExpr.of("maps", "from_list", List.of(headerEntries)),
                    CaseExpr.of(
                        RemoteCallExpr.of("maps", "size", List.of(Variable.of("Map"))),
                        List.of(
                            Clause.of(IntegerPattern.of(0), AtomExpr.of("undefined")),
                            Clause.of(WildcardPattern.of(), Variable.of("Map"))))))));
  }

  static Function decodeTimestampDateTimeFunction() {
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
    Expression binaryResult =
        TupleExpr.of(
            List.of(
                Variable.of("Mega"),
                InfixExpr.of(Variable.of("EpochSecs"), "rem", IntegerExpr.of(1000000)),
                IntegerExpr.of(0)));
    TryExpr iso8601Parse =
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
                            "EpochSecs", epochSecs, MatchExpr.bind("Mega", mega, binaryResult))))),
            List.of(Clause.of(CatchPattern.anyAny(), AtomExpr.of("undefined"))));
    Expression numberResult =
        MatchExpr.bind(
            "EpochSecs",
            LocalCallExpr.of("trunc", List.of(Variable.of("V"))),
            MatchExpr.bind(
                "Mega",
                InfixExpr.of(Variable.of("EpochSecs"), "div", IntegerExpr.of(1000000)),
                TupleExpr.of(
                    List.of(
                        Variable.of("Mega"),
                        InfixExpr.of(Variable.of("EpochSecs"), "rem", IntegerExpr.of(1000000)),
                        IntegerExpr.of(0)))));
    return Function.of(
        "decode_timestamp_date_time",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("number", Variable.of("V")),
                numberResult),
            FunctionClause.of(
                List.of(VariablePattern.of("V")),
                IsTypeGuard.of("binary", Variable.of("V")),
                iso8601Parse)));
  }

  static Function enumDecodeColorListFunction() {
    return Function.of(
        "decode_color_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("decode_color", List.of(Variable.of("V"))),
                    VariablePattern.of("V"),
                    Variable.of("List"),
                    InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("null"))))));
  }

  static Function enumEncodeColorListFunction() {
    return Function.of(
        "encode_color_list",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("List")),
                IsTypeGuard.of("list", Variable.of("List")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("encode_color", List.of(Variable.of("V"))),
                    VariablePattern.of("V"),
                    Variable.of("List"),
                    InfixExpr.of(Variable.of("V"), "=/=", AtomExpr.of("undefined"))))));
  }

  static Function mapDecodeColorLabelsFunction() {
    return Function.of(
        "decode_color_labels",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(List.of(AtomPattern.of("null")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("Map")),
                IsTypeGuard.of("map", Variable.of("Map")),
                mapKeyValueComprehension("decode_color"))));
  }

  static Function mapEncodeColorLabelsFunction() {
    return Function.of(
        "encode_color_labels",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("Map")),
                IsTypeGuard.of("map", Variable.of("Map")),
                mapKeyValueComprehension("encode_color"))));
  }

  private static RemoteCallExpr mapKeyValueComprehension(String keyHelper) {
    return RemoteCallExpr.of(
        "maps",
        "from_list",
        List.of(
            ListComprehensionExpr.of(
                TupleExpr.of(
                    List.of(
                        LocalCallExpr.of(keyHelper, List.of(Variable.of("K"))), Variable.of("V"))),
                TuplePattern.of(List.of(VariablePattern.of("K"), VariablePattern.of("V"))),
                RemoteCallExpr.of("maps", "to_list", List.of(Variable.of("Map"))))));
  }

  static Function encodeEventStreamFunction() {
    return Function.of(
        "encode_event_stream",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Events")),
                IsTypeGuard.of("list", Variable.of("Events")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("encode_event_stream_event", List.of(Variable.of("E"))),
                    VariablePattern.of("E"),
                    Variable.of("Events")))));
  }

  static Function decodeEventStreamFunction() {
    return Function.of(
        "decode_event_stream",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Body")),
                IsTypeGuard.of("binary", Variable.of("Body")),
                ListComprehensionExpr.of(
                    LocalCallExpr.of("decode_event_stream_event", List.of(Variable.of("F"))),
                    VariablePattern.of("F"),
                    RemoteCallExpr.of(
                        "aws_event_stream", "decode_frames", List.of(Variable.of("Body")))))));
  }

  static Function encodeEventStreamEventFunction() {
    Expression payload =
        RemoteCallExpr.of(
            "jsone",
            "encode",
            List.of(
                RemoteCallExpr.of(
                    "maps",
                    "filter",
                    List.of(
                        Fun.of(
                            List.of(
                                FunClause.of(
                                    List.of(WildcardPattern.of(), VariablePattern.of("V")),
                                    InfixExpr.of(
                                        Variable.of("V"), "=/=", AtomExpr.of("undefined"))))),
                        MapExpr.of(
                            List.of(
                                MapEntry.of(
                                    BinaryExpr.of("value"),
                                    RecordFieldAccessExpr.of(
                                        Variable.of("Value"), "member_event", "value"))))))));
    Expression frame =
        RemoteCallExpr.of(
            "aws_event_stream", "frame", List.of(Variable.of("Headers"), Variable.of("Payload")));
    return Function.of(
        "encode_event_stream_event",
        List.of(
            FunctionClause.of(
                List.of(
                    TuplePattern.of(
                        List.of(AtomPattern.of("member"), VariablePattern.of("Value")))),
                MatchExpr.bind(
                    "Payload",
                    payload,
                    MatchExpr.bind(
                        "Headers",
                        LocalCallExpr.of("encode_event_headers", List.of(BinaryExpr.of("member"))),
                        frame))),
            FunctionClause.of(
                List.of(TuplePattern.of(List.of(AtomPattern.of("unknown"), WildcardPattern.of()))),
                LocalCallExpr.of(
                    "error",
                    List.of(
                        TupleExpr.of(
                            List.of(AtomExpr.of("bad_event"), AtomExpr.of("unknown"))))))));
  }

  static Function decodeEventStreamEventFunction() {
    return Function.of(
        "decode_event_stream_event",
        List.of(
            FunctionClause.of(
                List.of(
                    MapPattern.of(
                        List.of(
                            MapPatternEntry.of(
                                AtomExpr.of("headers"), VariablePattern.of("Headers"), true),
                            MapPatternEntry.of(
                                AtomExpr.of("payload"), VariablePattern.of("Payload"), true)))),
                MatchExpr.bind(
                    "EventType",
                    LocalCallExpr.of(
                        "header_value",
                        List.of(Variable.of("Headers"), BinaryExpr.of(":event-type"))),
                    LocalCallExpr.of(
                        "decode_event_stream_event_type",
                        List.of(Variable.of("EventType"), Variable.of("Payload")))))));
  }

  static Function decodeEventStreamEventTypeFunction() {
    return Function.of(
        "decode_event_stream_event_type",
        List.of(
            FunctionClause.of(
                List.of(BinaryPattern.of("member"), VariablePattern.of("Payload")),
                TupleExpr.of(
                    List.of(
                        AtomExpr.of("member"),
                        RecordExpr.of(
                            "member_event",
                            List.of(
                                RecordField.of(
                                    "value",
                                    RemoteCallExpr.of(
                                        "maps",
                                        "get",
                                        List.of(
                                            BinaryExpr.of("value"),
                                            RemoteCallExpr.of(
                                                "jsone", "decode", List.of(Variable.of("Payload"))),
                                            AtomExpr.of("undefined"))))))))),
            FunctionClause.of(
                List.of(VariablePattern.of("EventType"), VariablePattern.of("_Payload")),
                LocalCallExpr.of(
                    "error",
                    List.of(
                        TupleExpr.of(
                            List.of(AtomExpr.of("bad_event"), Variable.of("EventType"))))))));
  }

  static Function encodeBasicItemFunction() {
    return Function.of(
        "encode_basic_item",
        List.of(
            FunctionClause.of(List.of(AtomPattern.of("undefined")), AtomExpr.of("undefined")),
            FunctionClause.of(
                List.of(VariablePattern.of("Record")),
                RemoteCallExpr.of(
                    "maps",
                    "filter",
                    List.of(
                        Fun.of(
                            List.of(
                                FunClause.of(
                                    List.of(WildcardPattern.of(), VariablePattern.of("V")),
                                    InfixExpr.of(
                                        Variable.of("V"), "=/=", AtomExpr.of("undefined"))))),
                        MapExpr.of(
                            List.of(
                                MapEntry.of(
                                    BinaryExpr.of("name"),
                                    RecordFieldAccessExpr.of(
                                        Variable.of("Record"), "basic_item", "name")),
                                MapEntry.of(
                                    BinaryExpr.of("count"),
                                    RecordFieldAccessExpr.of(
                                        Variable.of("Record"), "basic_item", "count")))))))));
  }

  static Function handlerDiscoveryResolveImplFunction() {
    Expression handlersComprehension =
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
    Expression successBody =
        MatchExpr.bind(
            "Callbacks",
            RemoteCallExpr.of(
                "basic_service_behaviour", "behaviour_info", List.of(AtomExpr.of("callbacks"))),
            MatchExpr.bind(
                "Handlers",
                RemoteCallExpr.of("maps", "from_list", List.of(handlersComprehension)),
                TupleExpr.of(List.of(AtomExpr.of("ok"), Variable.of("Handlers")))));
    return Function.of(
        "resolve_impl",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Impl")),
                CaseExpr.of(
                    RemoteCallExpr.of("code", "ensure_loaded", List.of(Variable.of("Impl"))),
                    List.of(
                        Clause.of(
                            TuplePattern.of(
                                List.of(AtomPattern.of("module"), VariablePattern.of("Impl"))),
                            successBody),
                        Clause.of(
                            TuplePattern.of(List.of(AtomPattern.of("error"), WildcardPattern.of())),
                            TupleExpr.of(
                                List.of(
                                    AtomExpr.of("error"),
                                    TupleExpr.of(
                                        List.of(
                                            AtomExpr.of("impl_not_loaded"),
                                            Variable.of("Impl")))))))))));
  }

  static Function handlerDiscoveryMakeHandlerFunction() {
    return Function.of(
        "make_handler",
        List.of(
            FunctionClause.of(
                List.of(VariablePattern.of("Impl"), VariablePattern.of("Fun")),
                Fun.of(
                    List.of(
                        FunClause.of(
                            List.of(
                                VariablePattern.of("Ctx"),
                                VariablePattern.of("Input"),
                                VariablePattern.of("Meta")),
                            RemoteCallExpr.of(
                                Variable.of("Impl"),
                                Variable.of("Fun"),
                                List.of(
                                    Variable.of("Ctx"),
                                    Variable.of("Input"),
                                    Variable.of("Meta")))))))));
  }

  static Function handlerDiscoveryInitHandlersFunction() {
    return Function.of(
        "init_handlers",
        List.of(
            FunctionClause.of(
                List.of(),
                CaseExpr.of(
                    LocalCallExpr.of("resolve_impl", List.of(MacroExpr.of("DEFAULT_IMPL"))),
                    List.of(
                        Clause.of(
                            TuplePattern.of(
                                List.of(AtomPattern.of("ok"), VariablePattern.of("Handlers"))),
                            MatchExpr.bind(
                                "_",
                                RemoteCallExpr.of(
                                    "persistent_term",
                                    "put",
                                    List.of(MacroExpr.of("HANDLERS_KEY"), Variable.of("Handlers"))),
                                AtomExpr.of("ok"))),
                        Clause.of(
                            TuplePattern.of(
                                List.of(AtomPattern.of("error"), VariablePattern.of("Reason"))),
                            MatchExpr.bind(
                                "_",
                                RemoteCallExpr.of(
                                    "persistent_term",
                                    "put",
                                    List.of(MacroExpr.of("HANDLERS_KEY"), MapExpr.of(List.of()))),
                                TupleExpr.of(
                                    List.of(AtomExpr.of("error"), Variable.of("Reason"))))))))),
        Spec.of("init_handlers() -> ok | {error, term()}"),
        null,
        null);
  }

  static Function handlerDiscoveryDispatchHandlerFunction() {
    return Function.of(
        "dispatch_handler",
        List.of(
            FunctionClause.of(
                List.of(
                    VariablePattern.of("Fun"),
                    VariablePattern.of("Ctx"),
                    VariablePattern.of("Input"),
                    VariablePattern.of("Meta")),
                MatchExpr.bind(
                    "Handlers",
                    RemoteCallExpr.of(
                        "persistent_term",
                        "get",
                        List.of(MacroExpr.of("HANDLERS_KEY"), MapExpr.of(List.of()))),
                    CaseExpr.of(
                        RemoteCallExpr.of(
                            "maps",
                            "get",
                            List.of(
                                Variable.of("Fun"),
                                Variable.of("Handlers"),
                                AtomExpr.of("undefined"))),
                        List.of(
                            Clause.of(
                                VariablePattern.of("Handler"),
                                ExpressionGuard.of(
                                    LocalCallExpr.of(
                                        "is_function",
                                        List.of(Variable.of("Handler"), IntegerExpr.of(3)))),
                                ApplyExpr.of(
                                    Variable.of("Handler"),
                                    List.of(
                                        Variable.of("Ctx"),
                                        Variable.of("Input"),
                                        Variable.of("Meta")))),
                            Clause.of(
                                WildcardPattern.of(),
                                TupleExpr.of(
                                    List.of(
                                        AtomExpr.of("error"),
                                        AtomExpr.of("not_implemented"))))))))));
  }

  static List<Function> awsQueryFlattenHelpersAwsFunctions() {
    return GoldenIrFixturesProtocolCodec.awsQueryFlattenHelpersAwsFunctions();
  }

  static List<Function> awsQueryFlattenHelpersEc2Functions() {
    return GoldenIrFixturesProtocolCodec.awsQueryFlattenHelpersEc2Functions();
  }

  static List<Function> awsQueryXmlHelpersAwsFunctions() {
    return GoldenIrFixturesProtocolCodec.awsQueryXmlHelpersAwsFunctions();
  }

  static List<Function> awsQueryFormDecodeAwsFunctions() {
    return GoldenIrFixturesProtocolCodec.awsQueryFormDecodeAwsFunctions();
  }

  static Function awsQueryEncodeListUsersRequestFunction() {
    return GoldenIrFixturesProtocolCodec.awsQueryEncodeListUsersRequestFunction();
  }

  static Function awsQueryDecodeListUsersResponseFunction() {
    return GoldenIrFixturesProtocolCodec.awsQueryDecodeListUsersResponseFunction();
  }

  static Function awsQueryServerDecodeListUsersRequestFunction() {
    return GoldenIrFixturesProtocolCodec.awsQueryServerDecodeListUsersRequestFunction();
  }

  static Function awsQueryServerEncodeListUsersResponseFunction() {
    return GoldenIrFixturesProtocolCodec.awsQueryServerEncodeListUsersResponseFunction();
  }

  static Function awsJsonEncodeGetUserRequestFunction() {
    return GoldenIrFixturesProtocolCodec.awsJsonEncodeGetUserRequestFunction();
  }

  static Function awsJsonDecodeGetUserResponseFunction() {
    return GoldenIrFixturesProtocolCodec.awsJsonDecodeGetUserResponseFunction();
  }

  static Function awsJsonEncodeGetUserResponseFunction() {
    return GoldenIrFixturesProtocolCodec.awsJsonEncodeGetUserResponseFunction();
  }

  static List<Function> restXmlHelpersFunctions() {
    return GoldenIrFixturesProtocolCodec.restXmlHelpersFunctions();
  }

  static Function restXmlDecodeGetNameRequestFunction() {
    return GoldenIrFixturesProtocolCodec.restXmlDecodeGetNameRequestFunction();
  }

  static Function restXmlDecodeGetNameResponseFunction() {
    return GoldenIrFixturesProtocolCodec.restXmlDecodeGetNameResponseFunction();
  }

  static Function restXmlEncodeGetNameRequestFunction() {
    return GoldenIrFixturesProtocolCodec.restXmlEncodeGetNameRequestFunction();
  }

  static Function restXmlEncodeGetNameResponseFunction() {
    return GoldenIrFixturesProtocolCodec.restXmlEncodeGetNameResponseFunction();
  }

  static Function restJsonDecodeGetNameResponseFunction() {
    return GoldenIrFixturesProtocolCodec.restJsonDecodeGetNameResponseFunction();
  }

  static Function restJsonEncodeGetNameRequestFunction() {
    return GoldenIrFixturesProtocolCodec.restJsonEncodeGetNameRequestFunction();
  }

  static Function restJsonEncodeGetNameResponseFunction() {
    return GoldenIrFixturesProtocolCodec.restJsonEncodeGetNameResponseFunction();
  }

  static Function restJsonEncodeNotFoundErrorResponseFunction() {
    return GoldenIrFixturesProtocolCodec.restJsonEncodeNotFoundErrorResponseFunction();
  }

  static Function sigv4SignRequestFunction() {
    return GoldenIrFixturesProtocolCodec.sigv4SignRequestFunction();
  }

  static Module sigv4Module() {
    return GoldenIrFixturesProtocolCodec.sigv4Module();
  }

  static Module httpServiceRestJson1ClientCodecModule() {
    return GoldenIrFixturesProtocolCodec.httpServiceRestJson1ClientCodecModule();
  }

  static Function hostLabelHelpersFunction() {
    return GoldenIrFixturesAwsRuntime.hostLabelHelpersFunction();
  }

  static Function httpChecksumHelpersFunction() {
    return GoldenIrFixturesAwsRuntime.httpChecksumHelpersFunction();
  }

  static Function httpDispatchSplitBaseUrlFunction() {
    return GoldenIrFixturesAwsRuntime.httpDispatchSplitBaseUrlFunction();
  }

  static Function httpDispatchDispatchSignedBasicFunction() {
    return GoldenIrFixturesAwsRuntime.httpDispatchDispatchSignedBasicFunction();
  }

  static Function httpDispatchDispatchSignedSigv4Function() {
    return GoldenIrFixturesAwsRuntime.httpDispatchDispatchSignedSigv4Function();
  }

  static Function httpDispatchDispatchSignedEndpointRulesFunction() {
    return GoldenIrFixturesAwsRuntime.httpDispatchDispatchSignedEndpointRulesFunction();
  }

  static Function httpDispatchDispatchSignedSigv4EndpointRulesFunction() {
    return GoldenIrFixturesAwsRuntime.httpDispatchDispatchSignedSigv4EndpointRulesFunction();
  }

  static Module httpDispatchModule() {
    return GoldenIrFixturesAwsRuntime.httpDispatchModule();
  }

  static Function credentialProviderChainFunction() {
    return GoldenIrFixturesAwsRuntime.credentialProviderChainFunction();
  }

  static Module credentialProviderModule() {
    return GoldenIrFixturesAwsRuntime.credentialProviderModule();
  }

  static Function endpointRulesResolveFunction() {
    return GoldenIrFixturesAwsRuntime.endpointRulesResolveFunction();
  }

  static Module endpointRulesModule() {
    return GoldenIrFixturesAwsRuntime.endpointRulesModule();
  }

  static Function presignerPresignUrlFunction() {
    return GoldenIrFixturesAwsRuntime.presignerPresignUrlFunction();
  }

  static Module presignerModule() {
    return GoldenIrFixturesAwsRuntime.presignerModule();
  }

  static Function retryWithRetryFunction() {
    return GoldenIrFixturesAwsRuntime.retryWithRetryFunction();
  }

  static Module retryModule() {
    return GoldenIrFixturesAwsRuntime.retryModule();
  }

  static Function runtimeHelpersResolveBaseUrlFunction() {
    return GoldenIrFixturesAwsRuntime.runtimeHelpersResolveBaseUrlFunction();
  }

  static Module runtimeHelpersLabelModule() {
    return GoldenIrFixturesAwsRuntime.runtimeHelpersLabelModule();
  }

  static Function s3EndpointRegionHostFunction() {
    return GoldenIrFixturesAwsRuntime.s3EndpointRegionHostFunction();
  }

  static Module s3EndpointModule() {
    return GoldenIrFixturesAwsRuntime.s3EndpointModule();
  }

  static Function clientPaginationListWidgetsFunction() {
    return GoldenIrFixturesAwsRuntime.clientPaginationListWidgetsFunction();
  }
}
