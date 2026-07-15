package io.beam.dsl.elixir;

import java.util.List;

public record TypesModule(
    String name,
    Moduledoc moduledocOrNull,
    TypeDef typeDef,
    List<DefstructField> defstructFields) {}
