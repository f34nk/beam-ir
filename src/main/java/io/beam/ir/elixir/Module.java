package io.beam.ir.elixir;

import java.util.List;

public record Module(
    String name,
    Moduledoc moduledocOrNull,
    List<UseDirective> uses,
    List<Alias> aliases,
    List<String> moduleAttributes,
    List<TypesModule> nestedTypesModules,
    List<String> trailingModuleAttributes,
    List<Function> functions)
    implements Node {

  public static Module of(String name, List<Function> functions) {
    return new Module(name, null, List.of(), List.of(), List.of(), List.of(), List.of(), functions);
  }

  @Override
  public SourceSpan source() {
    return null;
  }
}
