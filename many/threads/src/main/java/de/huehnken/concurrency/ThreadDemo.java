package de.huehnken.concurrency;

import java.util.ArrayList;
import java.util.List;

class ThreadDemo {

    private static final Integer NUM = 10000;
    private final long startTime;

    private List<Integer> store = new ArrayList<>();

    public ThreadDemo() {
        startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM; i++) {
            new SuperSimpleThread(new Integer(i)).start();
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

    class SuperSimpleThread extends Thread {
        private final Integer num;

        SuperSimpleThread(Integer num) {
            this.num = num;
        }

        @Override
        public void run() {
            append(num);
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }

    public static void main(String[] argv) {
        new ThreadDemo();
    }

}
