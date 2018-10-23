package io.costax.jpa.helper;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureTest {

    private static final ExecutorService mes = Executors.newFixedThreadPool(2);

    @Test
    public void test1() throws ExecutionException, InterruptedException {

        CompletableFuture.supplyAsync(this::authenticate, mes)
                .thenApply(this::getReportEmbedded)
                //.exceptionally(this::onErrorgetReportEmbedded)
                //.handle(this::hanhler);
                .exceptionally(this::customResponse)
                .thenAccept(this::response)
                .get();
                //.exceptionally(this::onErrorgetReportEmbedded);

    }

    private String customResponse(final Throwable throwable) {
        System.out.println("--- " + throwable);
        return "abc";

    }

    private void onErrorgetReportEmbedded(final Throwable throwable) {
        System.out.println("--" + throwable);
        //return "CORRONPED";
    }

    private String hanhler(final String response, final Throwable throwable) {
        if (response != null)
            this.response(response);

        if (throwable != null) {
            System.out.println("---error: " + throwable);
        }
        return "error";
    }

    private void response(final String s) {
        System.out.println("--- Response: " + s);

    }

    private String getReportEmbedded(final String token) {
        throw new RuntimeException("ERRROR get getReportEmbedded");

        //return "RESPONSE - of the Token " + token;
    }

    private String authenticate() {

        return "TOKEN-" + UUID.randomUUID();

    }
}
