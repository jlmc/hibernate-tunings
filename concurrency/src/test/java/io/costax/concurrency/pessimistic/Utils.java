package io.costax.concurrency.pessimistic;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static void sleepMilliseconds(long milliseconds) {
        try {

            System.out.printf("===> Thread [%s - %d - %s] Waiting for [%d] milliseconds ... \n",
                    Thread.currentThread().getName(),
                    Thread.currentThread().getId(),
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:SS")),
                    milliseconds);

            TimeUnit.MILLISECONDS.sleep(milliseconds);

        } catch (InterruptedException e) {
            throw new AssertionError("This should not happen!");
        }
    }

    public static void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new AssertionError("This should not happen!");
        }
    }
}
