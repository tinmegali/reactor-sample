package com.tinmegali.reactor.sample.reactor_sample;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class TranformersTest {

    @Test
    void testFlatMap_simple() {
        Flux<Integer> stream = Flux.fromIterable(Arrays.asList("1", "2", "3"))
                .doOnNext(item -> {
                    System.out.println("Item before map: " + item);
                })
                .flatMap(item -> {
                    int n = Integer.parseInt(item);
                    return Flux.just(n*n);
                });

        StepVerifier.create(stream)
                .expectNext(1)
                .expectNext(4)
                .expectNext(9)
                .verifyComplete();
    }

    @Test
    void testFlatMap_complex() {
        List<String> l1 = Arrays.asList("1", "2", "3");
        List<String> l2 = Arrays.asList("4", "5", "6");
        List<String> l3 = Arrays.asList("7", "8", "9");

        List<List<String>> list = Arrays.asList(l1, l2, l3);

        Flux<Integer> stream = Flux.fromIterable(list)
                .flatMap(Flux::fromIterable)
                .flatMap(item -> Flux.just(Integer.parseInt(item)));

        StepVerifier.create(stream)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectNext(6)
                .expectNext(7)
                .expectNext(8)
                .expectNext(9)
                .verifyComplete();
    }

}
