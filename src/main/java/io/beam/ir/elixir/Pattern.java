package io.beam.ir.elixir;

public sealed interface Pattern extends Node
    permits AtomPattern,
        VariablePattern,
        WildcardPattern,
        TuplePattern,
        ListPattern,
        StringPattern,
        StructPattern,
        PinPattern,
        MapPattern,
        NilPattern,
        ConsListPattern,
        AssignPattern,
        IntegerPattern,
        BinaryPattern,
        ConcatPattern,
        OpaquePattern {}
