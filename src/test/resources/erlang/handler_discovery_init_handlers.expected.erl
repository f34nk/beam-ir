-spec init_handlers() -> ok | {error, term()}.
init_handlers() ->
    case resolve_impl(?DEFAULT_IMPL) of
        {ok, Handlers} ->
            _ = persistent_term:put(?HANDLERS_KEY, Handlers),
            ok;
        {error, Reason} ->
            _ = persistent_term:put(?HANDLERS_KEY, #{}),
            {error, Reason}
    end.
