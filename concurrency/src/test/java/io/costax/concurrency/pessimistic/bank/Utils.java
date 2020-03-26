package io.costax.concurrency.pessimistic.bank;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {

//    static void printWaitingMessage() {
//        System.out.printf("===> Thread [%s - %d - %s] Waiting ... \n", Thread.currentThread().getName(), Thread.currentThread().getId(), LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:SS")));
//    }

    static void sleep(long millis) {
        try {
            System.out.printf("===> Thread [%s - %d - %s] Waiting for [%d] milliseconds ... \n", Thread.currentThread().getName(), Thread.currentThread().getId(), LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:SS")), millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new AssertionError("this should not happen");
        }
    }
}
