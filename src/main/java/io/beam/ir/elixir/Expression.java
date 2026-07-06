package io.beam.ir.elixir;

public sealed interface Expression extends Node
    permits AtomExpr,
        IntegerExpr,
        Variable,
        StringExpr,
        NilExpr,
        BooleanExpr,
        OpaqueExpr,
        TupleExpr,
        ListExpr,
        MapExpr,
        StructExpr,
        RemoteCallExpr,
        LocalCallExpr,
        DotCallExpr,
        CaptureExpr,
        InfixExpr,
        PipeExpr,
        MatchExpr,
        InterpolatedStringExpr {}
