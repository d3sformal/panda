package gov.nasa.jpf.abstraction.ReplicatedWorkers;

public final class StandardCountingSemaphore {
  protected long permits_;

  public StandardCountingSemaphore(long initial) { permits_ = initial; }
  public StandardCountingSemaphore() { permits_ = 0; }

  public synchronized void await() {
    if (--permits_ < 0)
      try { wait(); } catch (InterruptedException ex) {}
  }

  public synchronized void signal() {
    if (permits_++ < 0)
      notify();
  }
}
