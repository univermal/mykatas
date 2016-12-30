package com.purini.mykatas.mpc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nitinpuri on 30-12-2016.
 */
public class MultiProducerConsumer {

    private static final int MAX_PRODUCERS = 4;
    private static final int MAX_CONSUMERS = 5;

    private ExecutorService producerPool = new ThreadPoolExecutor(2, MAX_PRODUCERS, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    private ExecutorService consumerPool = new ThreadPoolExecutor(2, MAX_CONSUMERS, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    //ThreadPool for holding the main threads for consumer and producer
    private ExecutorService mainPool = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    /**
     * Indicates the stopping condition for the consumer, without this it has no idea when to stop
     */
    private AtomicBoolean readerComplete = new AtomicBoolean(false);

    /**
     * This is the queue for passing message from producer to consumer.
     * Keep queue size depending on how slow is your consumer relative to producer, or base it on resource constraints
     */
    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        MultiProducerConsumer multiProducerConsumer = new MultiProducerConsumer();
        multiProducerConsumer.process();
        System.out.println("Time taken in seconds - " + (System.currentTimeMillis() - startTime)/1000f);
    }

    private void process() throws InterruptedException {
        mainPool.execute(this::consume);
        mainPool.execute(this::produce);
        Thread.sleep(10); // allow the pool to get initiated
        mainPool.shutdown();
        mainPool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private void consume() {
        try {
            while (!readerComplete.get()) { //wait for reader to complete
                consumeAndExecute();
            }
            while (!queue.isEmpty()) { //process any residue tasks
                consumeAndExecute();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                consumerPool.shutdown();
                consumerPool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void consumeAndExecute() throws InterruptedException {
        if (!queue.isEmpty()) {
            String msg = queue.take(); //takes or waits if queue is empty
            consumerPool.execute(() -> {
                System.out.println("c-" + Thread.currentThread().getName() + "-" + msg);
            });
        }
    }


    private void produce() {
        try {
            for (int i = 0; i < MAX_PRODUCERS; i++) {
                producerPool.execute(() -> {
                    try {
                        String random = getRandomNumber() + "";
                        queue.put(random);
                        System.out.println("p-" + Thread.currentThread().getName() + "-" + random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        } finally {
            try {
                Thread.sleep(10); //allow pool to get initiated
                producerPool.shutdown();
                producerPool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readerComplete.set(true); //mark producer as done, so that consumer can exit
        }
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 50 + 1);
    }

}
