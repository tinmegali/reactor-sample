package com.tinmegali.reactor.sample.reactor_sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class CreatingStreamsSampleTest {

    @Test
    void test_create_sinksMany() {
        Sinks.Many<Integer> replaySink = Sinks.many()
                .replay().all();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.tryEmitNext(3);
        replaySink.tryEmitComplete();

        StepVerifier.create(replaySink.asFlux())
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectComplete()
                .verify();
    }


    @Test
    void test_createFlux_given_lambda() {

        Flux<Integer> myFlux = Flux.create(emitter -> this.count(emitter, 5));

        StepVerifier.create(myFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .expectComplete()
                .verify();
    }

    void count(FluxSink<Integer> sink, int count) {
        int i = 0; 
        while(i < count) {
            i++;
            sink.next(i);
        }
        sink.complete();
    }
    
}
