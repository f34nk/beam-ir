package io.beam.ir.erlang;

public sealed interface HeaderEntry
    permits HeaderComment,
        HeaderBlankLine,
        HeaderIfndef,
        HeaderDefine,
        HeaderEndif,
        HeaderRecordEntry,
        HeaderTypeAliasEntry {}
