package ac;

import static gov.nasa.jpf.abstraction.Verifier.unknownNonNegativeInt;

public class SerialAlarmClock {
    public static void main(String[] args) {
        int maxTime = 20;
        Monitor m = new Monitor(maxTime);
        Clock c = new Clock(m, maxTime);

        Client[] cl = new Client[2];
        cl[0] = new Client(2, m);
        cl[1] = new Client(2, m);

        while (schedule(m, c, cl)) {
        }
    }

    public static boolean schedule(Monitor m, Clock c, Client[] cl) {
        c.timeQuantumElapsed();

        boolean clockDone = c.isDone();
        boolean clockSleeping = c.isSleeping();
        boolean clockReady = !clockDone && !clockSleeping;

        boolean clDone = true;
        boolean clReady = false;

        for (int k = 0; k < cl.length; ++k) {
            if (!cl[k].isDone()) {
                clDone = false;

                if (!cl[k].isWaiting()) {
                    clReady = true;
                }
            }
        }

        /*
        System.out.println(
            (c.isSleeping() ? "S" : (c.isDone() ? "D" : "C")) + " " +
            (cl[0].isWaiting() ? "W" : (cl[0].isDone() ? "D" : "1")) + " " +
            (cl[1].isWaiting() ? "W" : (cl[1].isDone() ? "D" : "1")));
        */

        if (!clockDone || !clDone) {
            assert !clockDone || clReady; // Deadlock

            int i = unknownNonNegativeInt() % ((clockReady ? 1 : 0) + cl.length);

            if (clockReady && clReady) {
                if (i == 0) {
                    wake(c);
                } else {
                    wake(cl, i - 1);
                }
            } else if (clockReady) {
                wake(c);
            } else if (clReady) {
                wake(cl, i);
            }

            return true;
        }

        return false;
    }

    public static void wake(Clock c) {
        c.step();
    }

    public static void wake(Client[] cl, int i) {
        int count = 0;

        while (cl[i].isDone() || cl[i].isWaiting()) {
            assert count++ < cl.length;

            i = (i + 1) % cl.length;
        }

        cl[i].step();
    }

    private static class Clock {
        private Monitor monitor;
        private int max;
        private boolean done = false;
        private int sleep = 0;

        public Clock(Monitor m, int maxTime) {
            monitor = m;
            max = maxTime;
        }

        public void step() {
            assert !done;
            assert sleep <= 0;

            sleep = 0;

            if (monitor.getTime() < max) {
                monitor.tick();

                sleep += 3;
            } else {
                done = true;
            }
        }

        public void timeQuantumElapsed() {
            --sleep;
        }

        public boolean isDone() {
            return done;
        }

        public boolean isSleeping() {
            return sleep > 0;
        }
    }

    private static class Client {
        private int name;
        private Monitor monitor;
        private boolean done = false;
        private boolean waiting = false;
        private Phase phase = Phase.INIT;

        private enum Phase {
            INIT,
            TERM
        };

        public Client(int n, Monitor m) {
            name = n;
            monitor = m;
        }

        public void step() {
            assert !done;
            assert !waiting;

            switch (phase) {
                case INIT:
                    monitor.wakeme(this, unknownNonNegativeInt() % 5);
                    phase = Phase.TERM;
                    break;

                case TERM:
                    done = true;
                    break;
            }
        }

        public void puttosleep() {
            waiting = true;
        }

        public void wakeup() {
            waiting = false;
        }

        public boolean isDone() {
            return done;
        }

        public boolean isWaiting() {
            return waiting;
        }
    }

    private static class Monitor {
        private int now;
        private MyLinkedList waitList;
        private int max;

        public Monitor(int maxTime) {
            now = 0;
            waitList = new MyLinkedList();
            max = maxTime;
        }

        public void tick() {
            ++now;

            if (!waitList.isEmpty()) {
                if (waitList.getFirstWakeTime() == now) {
                    Client wakeup = waitList.getFirstElement();

                    waitList.removeFirstElement();

                    wakeup.wakeup();
                }
            }
        }

        public void wakeme(Client c, int interval) {
            int waketime = now + interval;

            if (waketime >= max) {
                return;
            }

            waitList.add(waketime, c);

            c.puttosleep();
        }

        public int getTime() {
            return now;
        }
    }

    private static class MyLinkedList {
        private class Node {
            private Client client;
            private int waketime;
            private Node next = null;

            public Node(Client c, int t) {
                client = c;
                waketime = t;
            }

            public Client getClient() {
                return client;
            }

            public int getWakeTime() {
                return waketime;
            }

            public Node getNext() {
                return next;
            }

            public void setNext(Node n) {
                next = n;
            }
        }

        private Node head = null;

        public void add(int t, Client c) {
            Node n = new Node(c, t);

            // Empty
            if (isEmpty()) {
                head = n;
            } else {
                Node p = head;

                // First
                if (p.getWakeTime() > t) {
                    head = n;
                    head.setNext(p);
                } else {
                    // Insert
                    while (p.getNext() != null && p.getNext().getWakeTime() < t) {
                        p = p.getNext();
                    }

                    p.setNext(n);
                }
            }
        }

        public Client getFirstElement() {
            return head.getClient();
        }

        public int getFirstWakeTime() {
            return head.getWakeTime();
        }

        public void removeFirstElement() {
            head = head.getNext();
        }

        public boolean isEmpty() {
            return head == null;
        }
    }
}
