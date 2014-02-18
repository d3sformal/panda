package serializethread;

public class SerializeThread {
    public static void main(String[] args) throws Exception {
        new SerializeThread().test();
    }

    public void test() throws Exception {
        final SerializeThread lock = this;

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
