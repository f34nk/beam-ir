package io.beam.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BeamIrTest {

  @Test
  void nameReturnsLibraryName() {
    assertEquals("beam-ir", BeamIr.name());
  }
}
