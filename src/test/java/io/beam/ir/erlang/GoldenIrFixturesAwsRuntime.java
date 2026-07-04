package io.beam.ir.erlang;

final class GoldenIrFixturesAwsRuntime {

  private GoldenIrFixturesAwsRuntime() {}

  static Function hostLabelHelpersFunction() {
    return GoldenIrFixturesVerbatim.function("host_label_helpers");
  }

  static Function httpChecksumHelpersFunction() {
    return GoldenIrFixturesVerbatim.function("http_checksum_helpers");
  }

  static Function httpDispatchSplitBaseUrlFunction() {
    return GoldenIrFixturesVerbatim.function("http_dispatch_split_base_url");
  }

  static Function httpDispatchDispatchSignedBasicFunction() {
    return GoldenIrFixturesVerbatim.function("http_dispatch_dispatch_signed_basic");
  }

  static Function httpDispatchDispatchSignedSigv4Function() {
    return GoldenIrFixturesVerbatim.function("http_dispatch_dispatch_signed_sigv4");
  }

  static Function httpDispatchDispatchSignedEndpointRulesFunction() {
    return GoldenIrFixturesVerbatim.function("http_dispatch_dispatch_signed_endpoint_rules");
  }

  static Function httpDispatchDispatchSignedSigv4EndpointRulesFunction() {
    return GoldenIrFixturesVerbatim.function("http_dispatch_dispatch_signed_sigv4_endpoint_rules");
  }

  static Module httpDispatchModule() {
    return GoldenIrFixturesVerbatim.module("http_dispatch_module");
  }

  static Function credentialProviderChainFunction() {
    return GoldenIrFixturesVerbatim.function("credential_provider_chain");
  }

  static Module credentialProviderModule() {
    return GoldenIrFixturesVerbatim.module("credential_provider_module");
  }

  static Function endpointRulesResolveFunction() {
    return GoldenIrFixturesVerbatim.function("endpoint_rules_resolve");
  }

  static Module endpointRulesModule() {
    return GoldenIrFixturesVerbatim.module("endpoint_rules_module");
  }

  static Function presignerPresignUrlFunction() {
    return GoldenIrFixturesVerbatim.function("presigner_presign_url");
  }

  static Module presignerModule() {
    return GoldenIrFixturesVerbatim.module("presigner_module");
  }

  static Function retryWithRetryFunction() {
    return GoldenIrFixturesVerbatim.function("retry_with_retry");
  }

  static Module retryModule() {
    return GoldenIrFixturesVerbatim.module("retry_module");
  }

  static Function runtimeHelpersResolveBaseUrlFunction() {
    return GoldenIrFixturesVerbatim.function("runtime_helpers_resolve_base_url");
  }

  static Module runtimeHelpersLabelModule() {
    return GoldenIrFixturesVerbatim.module("runtime_helpers_label_module");
  }

  static Function s3EndpointRegionHostFunction() {
    return GoldenIrFixturesVerbatim.function("s3_endpoint_region_host");
  }

  static Module s3EndpointModule() {
    return GoldenIrFixturesVerbatim.module("s3_endpoint_module");
  }

  static Function clientPaginationListWidgetsFunction() {
    return GoldenIrFixturesVerbatim.function("client_pagination_list_widgets");
  }
}
