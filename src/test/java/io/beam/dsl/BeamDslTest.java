package io.beam.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BeamDslTest {

  @Test
  void nameReturnsLibraryName() {
    assertEquals("beam-dsl", BeamDsl.name());
  }
}
