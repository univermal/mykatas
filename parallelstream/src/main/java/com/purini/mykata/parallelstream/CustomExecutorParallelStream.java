package com.purini.mykata.parallelstream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Created by nitinpuri on 30-12-2016.
 */
public class CustomExecutorParallelStream {

    private static final int PARALLELISM = 7;

    public static void main(String[] args) {

        ForkJoinPool forkJoinPool = null;
        List<String> strings = new ArrayList<>(Arrays.asList(
                "abc0", "def0", "ghi0", "jkl0", "mno0", "pqr0", "stu0",
                "abc1", "def1", "ghi1", "jkl1", "mno1", "pqr1", "stu1",
                "abc2", "def2", "ghi2", "jkl2", "mno2", "pqr2", "stu2",
                "abc3", "def3", "ghi3", "jkl3", "mno3", "pqr3", "stu3",
                "abc4", "def4", "ghi4", "jkl4", "mno4", "pqr4", "stu4",
                "abc5", "def5", "ghi5", "jkl5", "mno5", "pqr5", "stu5",
                "abc6", "def6", "ghi6", "jkl6", "mno6", "pqr6", "stu6"
                ));
        try {
            forkJoinPool = new ForkJoinPool(PARALLELISM); //14 operations would be printed each second
            forkJoinPool.submit(() ->
                    strings.parallelStream()
                    .map(CustomExecutorParallelStream::printStart)
                    .map(CustomExecutorParallelStream::printEnd)
                    .collect(Collectors.toList())).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (forkJoinPool != null) {
                forkJoinPool.shutdown(); //always shutdown the pool
            }
        }
    }

    private static String printStart(String string) {
        System.out.println(new Date() + " - start - " + string);
        return string;
    }

    private static String printEnd(String string) {
        System.out.println(new Date() + " - end - " + string);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return string;
    }
}
