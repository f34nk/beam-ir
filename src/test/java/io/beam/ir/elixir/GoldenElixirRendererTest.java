package io.beam.ir.elixir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class GoldenElixirRendererTest {

  private final Renderer renderer = ElixirRenderer.create();

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
    String path = "/elixir/" + resourceName;
    try (InputStream in = GoldenElixirRendererTest.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IOException("Missing golden resource: " + path);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
