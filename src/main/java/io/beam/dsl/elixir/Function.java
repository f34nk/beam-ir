package io.beam.dsl.elixir;

import java.util.List;

public record Function(
    String name,
    boolean private_,
    List<FunctionHead> heads,
    Expression body,
    Spec specOrNull,
    FunctionDoc docOrNull,
    boolean oneLiner) {}
