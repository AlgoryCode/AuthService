package com.ael.authservice.pipeline;


import java.util.function.Function;

@FunctionalInterface
public interface AuthStep<T, R> {
    R apply(T input);

    default <V> AuthStep<T, V> andThen(AuthStep<R, V> next) {
        return input -> next.apply(this.apply(input));
    }

    // Hata yönetimi için
    default AuthStep<T, R> onError(Function<Exception, R> fallback) {
        return input -> {
            try {
                return this.apply(input);
            } catch (Exception e) {
                return fallback.apply(e);
            }
        };
    }
}
