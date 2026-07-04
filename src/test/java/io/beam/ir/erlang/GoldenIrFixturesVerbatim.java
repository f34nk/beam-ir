package io.beam.ir.erlang;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class GoldenIrFixturesVerbatim {

  private GoldenIrFixturesVerbatim() {}

  static Function function(String goldenBaseName) {
    return Function.verbatim(readGolden(goldenBaseName + ".expected.erl"));
  }

  static List<Function> functions(String goldenBaseName) {
    return List.of(function(goldenBaseName));
  }

  static Module module(String goldenBaseName) {
    return Module.verbatim(readGolden(goldenBaseName + ".expected.erl"));
  }

  private static String readGolden(String resourceName) {
    String path = "/erlang/" + resourceName;
    try (InputStream in = GoldenIrFixturesVerbatim.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("Missing golden resource: " + path);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8).stripTrailing();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read golden resource: " + path, e);
    }
  }
}
