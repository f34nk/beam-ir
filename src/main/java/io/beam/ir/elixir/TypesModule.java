package io.beam.ir.elixir;

import java.util.List;

public record TypesModule(
    String name, Moduledoc moduledocOrNull, TypeDef typeDef, List<DefstructField> defstructFields)
    implements Node {

  @Override
  public SourceSpan source() {
    return null;
  }
}
