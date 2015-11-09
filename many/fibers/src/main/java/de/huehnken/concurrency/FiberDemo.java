package de.huehnken.concurrency;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


class FiberDemo {

    private static final Integer NUM = 10000;
    private final long startTime;

    private List<Integer> store = new Vector<>();

    public FiberDemo() {
        startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM; i++) {
            new SuperSimpleFiber(new Integer(i + 1)).start();
        }
    }

    void append(Integer num) {
        store.add(num);
        if (store.size() == NUM) {
            long sum = store.stream().reduce(0,  (a, b) -> a + b);
            long time = System.currentTimeMillis() - startTime;
            System.out.println("Sum is " + sum + ", took " + time + " milliseconds");
        }
    }

    class SuperSimpleFiber extends Fiber<Void> {
        private final Integer num;

        SuperSimpleFiber(Integer num) {
            this.num = num;
        }

        @Override
        public Void run() {
            append(num);
            try {
                sleep(1000);
            } catch (SuspendExecution | InterruptedException ex) {
                // ignore
            }
            return null;
        }
    }

    public static void main(String[] argv) {
        new FiberDemo();
    }

}
