package serialize;

public class Serialize {
    public static void main(String[] args) throws Exception {
        new Serialize().test();
    }

    public void test() throws Exception {
        final Serialize lock = this;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    // MAYBE DO SOMETHING
                }
            }
        });

        t.start();

        synchronized (lock) {
            // MAYBE DO SOMETHING
        }

        t.join();
    }
}
