package io.beam.ir.erlang;

import java.util.List;

public record AndGuard(List<Guard> guards, SourceSpan source) implements Guard {

  public static AndGuard of(Guard left, Guard right) {
    return new AndGuard(List.of(left, right), null);
  }

  public static AndGuard of(List<Guard> guards) {
    return new AndGuard(guards, null);
  }

  public static AndGuard of(List<Guard> guards, SourceSpan source) {
    return new AndGuard(guards, source);
  }
}
