package com.tinmegali.reactor.sample.reactor_sample;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class BasicOperatorsTest {

    @Test
    void thenMany() {
        Flux<String> letters = Flux.just("a", "b", "c");
        Flux<Integer> numbers = Flux.just(1, 2, 3);

        Flux<Integer> ignoreLetters = letters.thenMany(numbers);

        StepVerifier.create(ignoreLetters)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void map() {
        Flux<String> letters = Flux.just("a", "b", "c")
                .map(String::toUpperCase);
        StepVerifier.create(letters)
                .expectNext("A", "B", "C")
                .verifyComplete();
    }

    @Test
    void flatMap() {
        Flux<Integer> numbers = Flux.just(1, 2, 3)
                .flatMap(n -> Flux.just(n+1));
        numbers.subscribe(System.out::println);
    }

    Flux<Integer> count() {
        return Flux.just(4, 5, 6);
    }

}
