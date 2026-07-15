package io.beam.dsl.elixir;

import java.util.List;

public record UseDirective(String module, List<UseOption> options) {}
