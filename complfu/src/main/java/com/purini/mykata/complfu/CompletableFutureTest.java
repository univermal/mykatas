package com.purini.mykata.complfu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by nitinpuri on 27-12-2016.
 */
public class CompletableFutureTest {

    public static void main(String[] args) throws InterruptedException {

        new CompletableFutureTest().process();
    }

    private void process() {
        CompletableFuture<Void> cf =
                CompletableFuture
                        .supplyAsync(this::createMessage)
                        .thenApplyAsync(this::printMessage)
                        .thenAccept(this::printMessage);

        try {
            Thread.sleep(7);
            System.out.println("final message");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            cf.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private String createMessage() {
        int random = (int) (Math.random() * 50 + 1);
        System.out.println("created message - " + random);
        return "msg-" + random;
    }

    private String printMessage(String message) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("printing message - " + message);
        return message;
    }
}
