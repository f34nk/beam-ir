package io.beam.ir.erlang;

import java.util.List;

final class GoldenIrFixturesProtocolCodec {

  private GoldenIrFixturesProtocolCodec() {}

  static Function awsQueryServerDecodeListUsersRequestFunction() {
    return Function.of(
        "decode_list_users_request",
        List.of(
            FunctionClause.of(
                List.of(
                    RecordPattern.of(
                        "http_request",
                        List.of(RecordPatternField.of("body", VariablePattern.of("Body"))))),
                MatchExpr.bind(
                    "Params",
                    LocalCallExpr.of("parse_query_params", List.of(Variable.of("Body"))),
                    LocalCallExpr.of(
                        "parse_list_users_input_input", List.of(Variable.of("Params")))))),
        Spec.of("decode_list_users_request(#http_request{}) -> list_users_input()"),
        Edoc.of("Decode AWS Query server request for smithy.beam.test.awsquery#ListUsers."),
        null);
  }

  static Function sigv4SignRequestFunction() {
    return GoldenIrFixturesVerbatim.function("sigv4_sign_request");
  }

  static List<Function> awsQueryFlattenHelpersAwsFunctions() {
    return GoldenIrFixturesVerbatim.functions("aws_query_flatten_helpers_aws");
  }

  static List<Function> awsQueryFlattenHelpersEc2Functions() {
    return GoldenIrFixturesVerbatim.functions("aws_query_flatten_helpers_ec2");
  }

  // Remaining fixtures delegate to golden-backed verbatim rendering until hand-written.
  static List<Function> awsQueryXmlHelpersAwsFunctions() {
    return GoldenIrFixturesVerbatim.functions("aws_query_xml_helpers_aws");
  }

  static List<Function> awsQueryFormDecodeAwsFunctions() {
    return GoldenIrFixturesVerbatim.functions("aws_query_form_decode_aws");
  }

  static Function awsQueryEncodeListUsersRequestFunction() {
    return GoldenIrFixturesVerbatim.function("aws_query_encode_list_users_request");
  }

  static Function awsQueryDecodeListUsersResponseFunction() {
    return GoldenIrFixturesVerbatim.function("aws_query_decode_list_users_response");
  }

  static Function awsQueryServerEncodeListUsersResponseFunction() {
    return GoldenIrFixturesVerbatim.function("aws_query_server_encode_list_users_response");
  }

  static Function awsJsonEncodeGetUserRequestFunction() {
    return GoldenIrFixturesVerbatim.function("aws_json_encode_get_user_request");
  }

  static Function awsJsonDecodeGetUserResponseFunction() {
    return GoldenIrFixturesVerbatim.function("aws_json_decode_get_user_response");
  }

  static Function awsJsonEncodeGetUserResponseFunction() {
    return GoldenIrFixturesVerbatim.function("aws_json_encode_get_user_response");
  }

  static List<Function> restXmlHelpersFunctions() {
    return GoldenIrFixturesVerbatim.functions("rest_xml_helpers");
  }

  static Function restXmlDecodeGetNameRequestFunction() {
    return GoldenIrFixturesVerbatim.function("rest_xml_decode_get_name_request");
  }

  static Function restXmlDecodeGetNameResponseFunction() {
    return GoldenIrFixturesVerbatim.function("rest_xml_decode_get_name_response");
  }

  static Function restXmlEncodeGetNameRequestFunction() {
    return GoldenIrFixturesVerbatim.function("rest_xml_encode_get_name_request");
  }

  static Function restXmlEncodeGetNameResponseFunction() {
    return GoldenIrFixturesVerbatim.function("rest_xml_encode_get_name_response");
  }

  static Function restJsonDecodeGetNameResponseFunction() {
    return GoldenIrFixturesVerbatim.function("rest_json_decode_get_name_response");
  }

  static Function restJsonEncodeGetNameRequestFunction() {
    return GoldenIrFixturesVerbatim.function("rest_json_encode_get_name_request");
  }

  static Function restJsonEncodeGetNameResponseFunction() {
    return GoldenIrFixturesVerbatim.function("rest_json_encode_get_name_response");
  }

  static Function restJsonEncodeNotFoundErrorResponseFunction() {
    return GoldenIrFixturesVerbatim.function("rest_json_encode_not_found_error_response");
  }

  static Module sigv4Module() {
    return GoldenIrFixturesVerbatim.module("sigv4_module");
  }

  static Module httpServiceRestJson1ClientCodecModule() {
    return GoldenIrFixturesVerbatim.module("http_service_rest_json_1_client_codec");
  }
}
