package io.beam.ir.erlang;

public sealed interface Pattern extends Node
    permits AtomPattern,
        BinaryPattern,
        IntegerPattern,
        ListPattern,
        MapPattern,
        MatchPattern,
        RecordPattern,
        TuplePattern,
        VariablePattern,
        WildcardPattern,
        CatchPattern,
        CharPattern {}
