package io.beam.ir.erlang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class GoldenRendererTest {

  private final ErlangRenderer renderer = new ErlangRenderer();

  @Test
  void rendersBasicServiceRestJson1Module() throws IOException {
    assertGolden(
        "basic_service_rest_json_1.expected.erl",
        renderer.render(GoldenIrFixtures.basicServiceRestJson1Module()));
  }

  @Test
  void rendersDecodeBasicItemFunction() throws IOException {
    assertGolden(
        "decode_basic_item.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeBasicItemFunction(null)));
  }

  @Test
  void rendersDecodeBasicItemFunctionWithDoc() throws IOException {
    assertGolden(
        "decode_basic_item_with_doc.expected.erl",
        renderer.renderFunction(
            GoldenIrFixtures.decodeBasicItemFunction(
                Edoc.of("Decode a BasicItem from a JSON map."))));
  }

  @Test
  void rendersBasicTypesHeader() throws IOException {
    assertGolden("basic_types.expected.hrl", renderer.render(GoldenIrFixtures.basicTypesHeader()));
  }

  @Test
  void rendersRuntimeTypesHeader() throws IOException {
    assertGolden(
        "runtime_types.expected.hrl", renderer.render(GoldenIrFixtures.runtimeTypesHeader()));
  }

  @Test
  void rendersFiltermapVerboseExpression() throws IOException {
    assertGolden(
        "filtermap_verbose.expected.erl",
        renderer.renderExpression(GoldenIrFixtures.filtermapVerboseExpression()));
  }

  @Test
  void rendersClientDispatchGetNameExpression() throws IOException {
    assertGolden(
        "client_dispatch_get_name.expected.erl",
        renderer.renderStatement(GoldenIrFixturesExpressions.clientDispatchGetNameExpression()));
  }

  @Test
  void rendersClientDispatchGetNameRetryExpression() throws IOException {
    assertGolden(
        "client_dispatch_get_name_retry.expected.erl",
        renderer.renderStatement(
            GoldenIrFixturesExpressions.clientDispatchGetNameRetryExpression()));
  }

  @Test
  void rendersClientDispatchGetNameSigv4Expression() throws IOException {
    assertGolden(
        "client_dispatch_get_name_sigv4.expected.erl",
        renderer.renderStatement(
            GoldenIrFixturesExpressions.clientDispatchGetNameSigv4Expression()));
  }

  @Test
  void rendersClientDispatchListWidgetsPageExpression() throws IOException {
    assertGolden(
        "client_dispatch_list_widgets_page.expected.erl",
        renderer.renderStatement(
            GoldenIrFixturesExpressions.clientDispatchListWidgetsPageExpression()));
  }

  @Test
  void rendersHttpChecksumResponseGuardExpression() throws IOException {
    assertGolden(
        "http_checksum_response_guard.expected.erl",
        renderer.renderExpression(
            GoldenIrFixturesExpressions.httpChecksumResponseGuardExpression()));
  }

  @Test
  void rendersJsonBodyMapEntriesBasicItemExpression() throws IOException {
    assertGolden(
        "json_body_map_entries_basic_item.expected.erl",
        renderer.renderExpression(
            GoldenIrFixturesExpressions.jsonBodyMapEntriesBasicItemExpression()));
  }

  @Test
  void rendersGetNameOutputStructureHeader() throws IOException {
    assertGolden(
        "get_name_output_structure.expected.hrl",
        renderer.render(GoldenIrFixtures.getNameOutputStructureHeader()));
  }

  @Test
  void rendersAwsJsonErrorDispatchGetUserFunction() throws IOException {
    assertGolden(
        "aws_json_error_dispatch_get_user.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsJsonErrorDispatchGetUserFunction()));
  }

  @Test
  void rendersUriEncodeFunction() throws IOException {
    assertGolden(
        "uri_encode.expected.erl", renderer.renderFunction(GoldenIrFixtures.uriEncodeFunction()));
  }

  @Test
  void rendersUriDecodeFunction() throws IOException {
    assertGolden(
        "uri_decode.expected.erl", renderer.renderFunction(GoldenIrFixtures.uriDecodeFunction()));
  }

  @Test
  void rendersGenerateUuidFunction() throws IOException {
    assertGolden(
        "generate_uuid.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.generateUuidFunction()));
  }

  @Test
  void rendersHeadersSetFunction() throws IOException {
    assertGolden(
        "headers_set.expected.erl", renderer.renderFunction(GoldenIrFixtures.headersSetFunction()));
  }

  @Test
  void rendersToBinaryRestJsonFunction() throws IOException {
    assertGolden(
        "to_binary_rest_json.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.toBinaryRestJsonFunction()));
  }

  @Test
  void rendersEncodeQueryValueRestJsonFunction() throws IOException {
    assertGolden(
        "encode_query_value_rest_json.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeQueryValueRestJsonFunction()));
  }

  @Test
  void rendersFlattenQueryInputFunction() throws IOException {
    assertGolden(
        "aws_query_flatten_query_input.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.flattenQueryInputFunction()));
  }

  @Test
  void rendersParseListUsersInputInputFunction() throws IOException {
    assertGolden(
        "aws_query_parse_list_users_input.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.parseListUsersInputInputFunction()));
  }

  @Test
  void rendersCredentialProviderResolveFunction() throws IOException {
    assertGolden(
        "credential_provider_resolve.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.credentialProviderResolveFunction()));
  }

  @Test
  void rendersEncodeEventHeadersFunction() throws IOException {
    assertGolden(
        "event_stream_encode_event_headers.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeEventHeadersFunction()));
  }

  @Test
  void rendersHeaderValueFunction() throws IOException {
    assertGolden(
        "event_stream_header_value.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.headerValueFunction()));
  }

  @Test
  void rendersHandlerDiscoveryHandleGetNameFunction() throws IOException {
    assertGolden(
        "handler_discovery_handle_get_name.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.handlerDiscoveryHandleGetNameFunction()));
  }

  @Test
  void rendersHttpDispatchDispatchArity3Function() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_arity3.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchDispatchArity3Function()));
  }

  @Test
  void rendersHttpDispatchMimeFunction() throws IOException {
    assertGolden(
        "http_dispatch_mime.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchMimeFunction()));
  }

  @Test
  void rendersDecodeQueryParamFunction() throws IOException {
    assertGolden(
        "decode_query_param.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeQueryParamFunction()));
  }

  @Test
  void rendersEnumDecodeColorFunction() throws IOException {
    assertGolden(
        "enum_decode_color.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.enumDecodeColorFunction()));
  }

  @Test
  void rendersEncodeTimestampEpochSecondsFunction() throws IOException {
    assertGolden(
        "encode_timestamp_epoch_seconds.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeTimestampEpochSecondsFunction()));
  }

  @Test
  void rendersAwsJsonDecodeGetUserRequestFunction() throws IOException {
    assertGolden(
        "aws_json_decode_get_user_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsJsonDecodeGetUserRequestFunction()));
  }

  @Test
  void rendersEncodeTimestampDateTimeFunction() throws IOException {
    assertGolden(
        "encode_timestamp_date_time.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeTimestampDateTimeFunction()));
  }

  @Test
  void rendersDecodeJsonBodyFunction() throws IOException {
    assertGolden(
        "decode_json_body.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeJsonBodyFunction()));
  }

  @Test
  void rendersUnionDecodeEncodeEventFunctions() throws IOException {
    assertGolden(
        "union_decode_encode_event.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeEventFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.encodeEventFunction()));
  }

  @Test
  void rendersEnumEncodeColorFunction() throws IOException {
    assertGolden(
        "enum_encode_color.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.enumEncodeColorFunction()));
  }

  @Test
  void rendersDecodeTimestampEpochSecondsFunction() throws IOException {
    assertGolden(
        "decode_timestamp_epoch_seconds.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeTimestampEpochSecondsFunction()));
  }

  @Test
  void rendersDecodeSparseMapFunction() throws IOException {
    assertGolden(
        "decode_sparse_map.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeSparseMapFunction()));
  }

  @Test
  void rendersEncodeSparseMapFunction() throws IOException {
    assertGolden(
        "encode_sparse_map.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeSparseMapFunction()));
  }

  @Test
  void rendersHttpDispatchDispatchArity2Function() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_arity2.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchDispatchArity2Function()));
  }

  @Test
  void rendersSigv4SignFunction() throws IOException {
    assertGolden(
        "sigv4_sign.expected.erl", renderer.renderFunction(GoldenIrFixtures.sigv4SignFunction()));
  }

  @Test
  void rendersEndpointRulesMergeParamsFunctions() throws IOException {
    assertGolden(
        "endpoint_rules_merge_params.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.endpointRulesMergeParamsFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.endpointRulesConfigToRuleParamsFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.endpointRulesClientContextParamsFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.endpointRulesOptionalParamFunction()));
  }

  @Test
  void rendersDecodeListFunction() throws IOException {
    assertGolden(
        "decode_list.expected.erl", renderer.renderFunction(GoldenIrFixtures.decodeListFunction()));
  }

  @Test
  void rendersStructureListDecodeEncodeItemFunctions() throws IOException {
    assertGolden(
        "structure_list_decode_encode_item.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeBasicItemListFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.encodeBasicItemListFunction()));
  }

  @Test
  void rendersCtBaseFunction() throws IOException {
    assertGolden(
        "ct_base.expected.erl", renderer.renderFunction(GoldenIrFixtures.ctBaseFunction()));
  }

  @Test
  void rendersDecodeSparseListFunction() throws IOException {
    assertGolden(
        "decode_sparse_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeSparseListFunction()));
  }

  @Test
  void rendersEncodeSparseListFunction() throws IOException {
    assertGolden(
        "encode_sparse_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeSparseListFunction()));
  }

  @Test
  void rendersContentTypeMatchesFunction() throws IOException {
    assertGolden(
        "content_type_matches.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.contentTypeMatchesFunction()));
  }

  @Test
  void rendersPrefixHeadersToListFunction() throws IOException {
    assertGolden(
        "prefix_headers_to_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.prefixHeadersToListFunction()));
  }

  @Test
  void rendersPrefixHeadersFromListFunction() throws IOException {
    assertGolden(
        "prefix_headers_from_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.prefixHeadersFromListFunction()));
  }

  @Test
  void rendersDecodeTimestampDateTimeFunction() throws IOException {
    assertGolden(
        "decode_timestamp_date_time.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeTimestampDateTimeFunction()));
  }

  @Test
  void rendersEnumDecodeColorListFunction() throws IOException {
    assertGolden(
        "enum_decode_color_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.enumDecodeColorListFunction()));
  }

  @Test
  void rendersEnumEncodeColorListFunction() throws IOException {
    assertGolden(
        "enum_encode_color_list.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.enumEncodeColorListFunction()));
  }

  @Test
  void rendersMapDecodeColorLabelsFunction() throws IOException {
    assertGolden(
        "map_decode_color_labels.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.mapDecodeColorLabelsFunction()));
  }

  @Test
  void rendersMapEncodeColorLabelsFunction() throws IOException {
    assertGolden(
        "map_encode_color_labels.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.mapEncodeColorLabelsFunction()));
  }

  @Test
  void rendersEventStreamUnionHelperFunctions() throws IOException {
    assertGolden(
        "event_stream_union_helpers.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.encodeEventStreamFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.decodeEventStreamFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.encodeEventStreamEventFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.decodeEventStreamEventFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.decodeEventStreamEventTypeFunction()));
  }

  @Test
  void rendersStructureDecodeEncodeBasicItemFunctions() throws IOException {
    assertGolden(
        "structure_decode_encode_basic_item.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.decodeBasicItemWithoutSpecFunction())
            + "\n"
            + renderer.renderFunction(GoldenIrFixtures.encodeBasicItemFunction()));
  }

  @Test
  void rendersHandlerDiscoveryResolveImplFunction() throws IOException {
    assertGolden(
        "handler_discovery_resolve_impl.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.handlerDiscoveryResolveImplFunction()));
  }

  @Test
  void rendersHandlerDiscoveryMakeHandlerFunction() throws IOException {
    assertGolden(
        "handler_discovery_make_handler.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.handlerDiscoveryMakeHandlerFunction()));
  }

  @Test
  void rendersHandlerDiscoveryInitHandlersFunction() throws IOException {
    assertGolden(
        "handler_discovery_init_handlers.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.handlerDiscoveryInitHandlersFunction()));
  }

  @Test
  void rendersHandlerDiscoveryDispatchHandlerFunction() throws IOException {
    assertGolden(
        "handler_discovery_dispatch_handler.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.handlerDiscoveryDispatchHandlerFunction()));
  }

  @Test
  void rendersAwsQueryFlattenHelpersAwsFunctions() throws IOException {
    assertGolden(
        "aws_query_flatten_helpers_aws.expected.erl",
        renderFunctions(GoldenIrFixtures.awsQueryFlattenHelpersAwsFunctions()));
  }

  @Test
  void rendersAwsQueryFlattenHelpersEc2Functions() throws IOException {
    assertGolden(
        "aws_query_flatten_helpers_ec2.expected.erl",
        renderFunctions(GoldenIrFixtures.awsQueryFlattenHelpersEc2Functions()));
  }

  @Test
  void rendersAwsQueryXmlHelpersAwsFunctions() throws IOException {
    assertGolden(
        "aws_query_xml_helpers_aws.expected.erl",
        renderFunctions(GoldenIrFixtures.awsQueryXmlHelpersAwsFunctions()));
  }

  @Test
  void rendersAwsQueryFormDecodeAwsFunctions() throws IOException {
    assertGolden(
        "aws_query_form_decode_aws.expected.erl",
        renderFunctions(GoldenIrFixtures.awsQueryFormDecodeAwsFunctions()));
  }

  @Test
  void rendersAwsQueryEncodeListUsersRequestFunction() throws IOException {
    assertGolden(
        "aws_query_encode_list_users_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsQueryEncodeListUsersRequestFunction()));
  }

  @Test
  void rendersAwsQueryDecodeListUsersResponseFunction() throws IOException {
    assertGolden(
        "aws_query_decode_list_users_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsQueryDecodeListUsersResponseFunction()));
  }

  @Test
  void rendersAwsQueryServerDecodeListUsersRequestFunction() throws IOException {
    assertGolden(
        "aws_query_server_decode_list_users_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsQueryServerDecodeListUsersRequestFunction()));
  }

  @Test
  void rendersAwsQueryServerEncodeListUsersResponseFunction() throws IOException {
    assertGolden(
        "aws_query_server_encode_list_users_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsQueryServerEncodeListUsersResponseFunction()));
  }

  @Test
  void rendersAwsJsonEncodeGetUserRequestFunction() throws IOException {
    assertGolden(
        "aws_json_encode_get_user_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsJsonEncodeGetUserRequestFunction()));
  }

  @Test
  void rendersAwsJsonDecodeGetUserResponseFunction() throws IOException {
    assertGolden(
        "aws_json_decode_get_user_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsJsonDecodeGetUserResponseFunction()));
  }

  @Test
  void rendersAwsJsonEncodeGetUserResponseFunction() throws IOException {
    assertGolden(
        "aws_json_encode_get_user_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.awsJsonEncodeGetUserResponseFunction()));
  }

  @Test
  void rendersRestXmlHelpersFunctions() throws IOException {
    assertGolden(
        "rest_xml_helpers.expected.erl",
        renderFunctions(GoldenIrFixtures.restXmlHelpersFunctions()));
  }

  @Test
  void rendersRestXmlDecodeGetNameRequestFunction() throws IOException {
    assertGolden(
        "rest_xml_decode_get_name_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restXmlDecodeGetNameRequestFunction()));
  }

  @Test
  void rendersRestXmlDecodeGetNameResponseFunction() throws IOException {
    assertGolden(
        "rest_xml_decode_get_name_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restXmlDecodeGetNameResponseFunction()));
  }

  @Test
  void rendersRestXmlEncodeGetNameRequestFunction() throws IOException {
    assertGolden(
        "rest_xml_encode_get_name_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restXmlEncodeGetNameRequestFunction()));
  }

  @Test
  void rendersRestXmlEncodeGetNameResponseFunction() throws IOException {
    assertGolden(
        "rest_xml_encode_get_name_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restXmlEncodeGetNameResponseFunction()));
  }

  @Test
  void rendersRestJsonDecodeGetNameResponseFunction() throws IOException {
    assertGolden(
        "rest_json_decode_get_name_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restJsonDecodeGetNameResponseFunction()));
  }

  @Test
  void rendersRestJsonEncodeGetNameRequestFunction() throws IOException {
    assertGolden(
        "rest_json_encode_get_name_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restJsonEncodeGetNameRequestFunction()));
  }

  @Test
  void rendersRestJsonEncodeGetNameResponseFunction() throws IOException {
    assertGolden(
        "rest_json_encode_get_name_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restJsonEncodeGetNameResponseFunction()));
  }

  @Test
  void rendersRestJsonEncodeNotFoundErrorResponseFunction() throws IOException {
    assertGolden(
        "rest_json_encode_not_found_error_response.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.restJsonEncodeNotFoundErrorResponseFunction()));
  }

  @Test
  void rendersSigv4SignRequestFunction() throws IOException {
    assertGolden(
        "sigv4_sign_request.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.sigv4SignRequestFunction()));
  }

  @Test
  void rendersSigv4Module() throws IOException {
    assertGolden("sigv4_module.expected.erl", renderer.render(GoldenIrFixtures.sigv4Module()));
  }

  @Test
  void rendersHttpServiceRestJson1ClientCodecModule() throws IOException {
    assertGolden(
        "http_service_rest_json_1_client_codec.expected.erl",
        renderer.render(GoldenIrFixtures.httpServiceRestJson1ClientCodecModule()));
  }

  @Test
  void rendersHostLabelHelpersFunction() throws IOException {
    assertGolden(
        "host_label_helpers.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.hostLabelHelpersFunction()));
  }

  @Test
  void rendersHttpChecksumHelpersFunction() throws IOException {
    assertGolden(
        "http_checksum_helpers.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpChecksumHelpersFunction()));
  }

  @Test
  void rendersHttpChecksumRequestHeadersExpression() throws IOException {
    assertGolden(
        "http_checksum_request_headers.expected.erl",
        renderer.renderExpression(GoldenIrFixtures.httpChecksumRequestHeadersExpression()));
  }

  @Test
  void rendersJsonDecodedBodyPreludeExpression() throws IOException {
    assertGolden(
        "json_decoded_body_prelude.expected.erl",
        renderer.renderExpression(GoldenIrFixturesExpressions.jsonDecodedBodyPreludeExpression()));
  }

  @Test
  void rendersRestJsonEncodeResponseBodyExpression() throws IOException {
    assertGolden(
        "rest_json_encode_response_body.expected.erl",
        renderer.renderExpression(GoldenIrFixtures.restJsonEncodeResponseBodyExpression()));
  }

  @Test
  void rendersAwsJsonSharedCodecHelpersFunctions() throws IOException {
    assertGolden(
        "aws_json_shared_codec_helpers.expected.erl",
        renderFunctions(GoldenIrFixtures.awsJsonSharedCodecHelpersFunctions()));
  }

  @Test
  void rendersRuntimeHelpersLabelParsingFunctions() throws IOException {
    assertGolden(
        "runtime_helpers_label_parsing.expected.erl",
        renderFunctions(GoldenIrFixtures.runtimeHelpersLabelParsingFunctions()));
  }

  @Test
  void rendersS3EndpointHelpersFunctions() throws IOException {
    assertGolden(
        "s3_endpoint_helpers.expected.erl",
        renderFunctions(GoldenIrFixtures.s3EndpointHelpersFunctions()));
  }

  @Test
  void rendersSigv4HelpersFunctions() throws IOException {
    assertGolden(
        "sigv4_helpers.expected.erl", renderFunctions(GoldenIrFixtures.sigv4HelpersFunctions()));
  }

  @Test
  void rendersHttpDispatchSplitBaseUrlFunction() throws IOException {
    assertGolden(
        "http_dispatch_split_base_url.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchSplitBaseUrlFunction()));
  }

  @Test
  void rendersHttpDispatchDispatchSignedBasicFunction() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_signed_basic.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchDispatchSignedBasicFunction()));
  }

  @Test
  void rendersHttpDispatchDispatchSignedSigv4Function() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_signed_sigv4.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.httpDispatchDispatchSignedSigv4Function()));
  }

  @Test
  void rendersHttpDispatchDispatchSignedEndpointRulesFunction() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_signed_endpoint_rules.expected.erl",
        renderer.renderFunction(
            GoldenIrFixtures.httpDispatchDispatchSignedEndpointRulesFunction()));
  }

  @Test
  void rendersHttpDispatchDispatchSignedSigv4EndpointRulesFunction() throws IOException {
    assertGolden(
        "http_dispatch_dispatch_signed_sigv4_endpoint_rules.expected.erl",
        renderer.renderFunction(
            GoldenIrFixtures.httpDispatchDispatchSignedSigv4EndpointRulesFunction()));
  }

  @Test
  void rendersHttpDispatchModule() throws IOException {
    assertGolden(
        "http_dispatch_module.expected.erl",
        renderer.render(GoldenIrFixtures.httpDispatchModule()));
  }

  @Test
  void rendersCredentialProviderChainFunction() throws IOException {
    assertGolden(
        "credential_provider_chain.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.credentialProviderChainFunction()));
  }

  @Test
  void rendersCredentialProviderModule() throws IOException {
    assertGolden(
        "credential_provider_module.expected.erl",
        renderer.render(GoldenIrFixtures.credentialProviderModule()));
  }

  @Test
  void rendersEndpointRulesResolveFunction() throws IOException {
    assertGolden(
        "endpoint_rules_resolve.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.endpointRulesResolveFunction()));
  }

  @Test
  void rendersEndpointRulesModule() throws IOException {
    assertGolden(
        "endpoint_rules_module.expected.erl",
        renderer.render(GoldenIrFixtures.endpointRulesModule()));
  }

  @Test
  void rendersPresignerPresignUrlFunction() throws IOException {
    assertGolden(
        "presigner_presign_url.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.presignerPresignUrlFunction()));
  }

  @Test
  void rendersPresignerModule() throws IOException {
    assertGolden(
        "presigner_module.expected.erl", renderer.render(GoldenIrFixtures.presignerModule()));
  }

  @Test
  void rendersRetryWithRetryFunction() throws IOException {
    assertGolden(
        "retry_with_retry.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.retryWithRetryFunction()));
  }

  @Test
  void rendersRetryModule() throws IOException {
    assertGolden("retry_module.expected.erl", renderer.render(GoldenIrFixtures.retryModule()));
  }

  @Test
  void rendersRuntimeHelpersResolveBaseUrlFunction() throws IOException {
    assertGolden(
        "runtime_helpers_resolve_base_url.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.runtimeHelpersResolveBaseUrlFunction()));
  }

  @Test
  void rendersRuntimeHelpersLabelModule() throws IOException {
    assertGolden(
        "runtime_helpers_label_module.expected.erl",
        renderer.render(GoldenIrFixtures.runtimeHelpersLabelModule()));
  }

  @Test
  void rendersS3EndpointRegionHostFunction() throws IOException {
    assertGolden(
        "s3_endpoint_region_host.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.s3EndpointRegionHostFunction()));
  }

  @Test
  void rendersS3EndpointModule() throws IOException {
    assertGolden(
        "s3_endpoint_module.expected.erl", renderer.render(GoldenIrFixtures.s3EndpointModule()));
  }

  @Test
  void rendersClientPaginationListWidgetsFunction() throws IOException {
    assertGolden(
        "client_pagination_list_widgets.expected.erl",
        renderer.renderFunction(GoldenIrFixtures.clientPaginationListWidgetsFunction()));
  }

  private String renderFunctions(List<Function> functions) {
    return functions.stream().map(renderer::renderFunction).collect(Collectors.joining("\n\n"));
  }

  private static void assertGolden(String resourceName, String actual) throws IOException {
    assertEquals(
        readGolden(resourceName),
        ensureTrailingNewline(actual),
        () -> "Golden mismatch for " + resourceName);
  }

  private static String ensureTrailingNewline(String value) {
    return value.endsWith("\n") ? value : value + "\n";
  }

  private static String readGolden(String resourceName) throws IOException {
    String path = "/erlang/" + resourceName;
    try (InputStream in = GoldenRendererTest.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IOException("Missing golden resource: " + path);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
