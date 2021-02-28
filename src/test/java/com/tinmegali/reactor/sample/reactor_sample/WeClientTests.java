package com.tinmegali.reactor.sample.reactor_sample;

import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

class WeClientTests {

    static MockWebServer mockBackEnd;
    WebClient webClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Test
    void test_Sinks() throws Exception {

        mockBackEnd.enqueue(new MockResponse()
                .setBody("one"));
        mockBackEnd.enqueue(new MockResponse()
                .setBody("two"));
        mockBackEnd.enqueue(new MockResponse()
                .setBody("three"));
        mockBackEnd.enqueue(new MockResponse()
                .setBody("four"));

        // Sinks.Many<String> sinks = Sinks.many().replay().all(5);
        Sinks.Many<String> sinks = Sinks.unsafe().many().replay().latest();
        AtomicReference<Sinks.Many<String>> atomicSinks = new AtomicReference<>(sinks);

        Scheduler schedullerPoll = Schedulers.boundedElastic();

        for( int i = 0; i < 5; i++ ) {
                webClient.get().uri("/")
                        .retrieve()
                        .bodyToMono(String.class)
                        .subscribeOn(schedullerPoll)
                        .subscribe(item -> emit(sinks, item));
        }

        StepVerifier.create(sinks.asFlux())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(pred -> true)
                .consumeRecordedWith(record -> {
                    Assertions.assertTrue(record.contains("one"));
                    Assertions.assertTrue(record.contains("two"));
                    Assertions.assertTrue(record.contains("three"));
                    Assertions.assertTrue(record.contains("four"));
                })
                .verifyTimeout(Duration.ofSeconds(10));
    }

    synchronized void emit(Sinks.Many<String> sink, String item) {
        sink.tryEmitNext(item);
    }



}
