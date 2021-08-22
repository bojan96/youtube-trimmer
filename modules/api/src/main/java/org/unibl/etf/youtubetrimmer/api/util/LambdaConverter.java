package org.unibl.etf.youtubetrimmer.api.util;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LambdaConverter<S, D> extends AbstractConverter<S, D> {

    private final Function<S, D> lambda;

    @Override
    protected D convert(S s) {
        return lambda.apply(s);
    }

    public static <S, D> LambdaConverter<S, D> of(Function<S, D> lambda) {
        return new LambdaConverter<>(lambda);
    }
}
