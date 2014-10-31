package gov.nasa.jpf.abstraction;

// Taken from CPAchecker repository
public class StaticInitializerTest extends BaseTest {
    public StaticInitializerTest() {
        config.add("+panda.refinement=true");
    }

    @Test
    public static void test() {
        assert StaticInitializer.n == 42;
    }
}

class StaticInitializer {
  public static int n;

  static {
    n = 42;
  }
}
