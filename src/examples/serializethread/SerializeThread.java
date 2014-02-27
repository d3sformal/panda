package serializethread;

public class SerializeThread {
    public static void main(String[] args) throws Exception {
        new SerializeThread().test();
    }

    public void test() throws Exception {
        final SerializeThread lockObject = this;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lockObject) {
                    // MAYBE DO SOMETHING
                }
            }
        });

        t.start();

        synchronized (lockObject) {
            // MAYBE DO SOMETHING
        }

        t.join();
    }
}
