package gov.nasa.jpf.abstraction.ReplicatedWorkers;

public final class StandardBarrier {
  private long participants;
  private long numBlocked, numReleased;
  private boolean allBlocked, allReleased;

  public StandardBarrier(long initial) { 
    participants = initial; 
    numReleased = numBlocked = 0; 
  }

  private synchronized void blockAll() {
    ++numBlocked;
    allBlocked = false;
    while ( numBlocked < participants && !allBlocked )
      try { wait(); } catch (InterruptedException ex) {};
    allBlocked = true; 
    numBlocked = 0;
    notifyAll();
  }

  private synchronized void releaseAll() {
    ++numReleased;
    allReleased = false;
    while ( numReleased < participants && !allReleased )
      try { wait(); } catch (InterruptedException ex) {};
    allReleased = true; 
    numReleased = 0;
    notifyAll();
  }

  public synchronized void await() {
    blockAll();
    releaseAll();
  }
}
