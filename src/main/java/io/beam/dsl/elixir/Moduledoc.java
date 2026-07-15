package io.beam.dsl.elixir;

public record Moduledoc(String text, boolean literal) {

  public static Moduledoc of(String text) {
    return new Moduledoc(text, false);
  }

  public static Moduledoc falseLiteral() {
    return new Moduledoc("false", true);
  }
}
