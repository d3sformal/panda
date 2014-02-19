package gov.nasa.jpf.abstraction.predicate;

class NullTestDriver {
    public static void main(String[] args) {
        new NullTest().bootstrap();
    }
}

public class NullTest extends FailingBaseTest {

	public NullTest()
	{
		config.add("+search.multiple_errors=true");
	}

    public static void main(String[] args) {
        Object[] array = new Object[2];
        int i = 0;
        boolean b = true;

        array[0] = new Object();
        assertNumberOfPossibleValues("array[0]", 1);
        assert array[0] != null : "Cannot guarantee array[0] to be not null"; // Should not fail - array[0] is definitely not null

        if (b) {
            array[i] = new Object();
            assertNumberOfPossibleValues("array[1]", 2);
            assert array[1] != null : "Cannot guarantee array[1] to be not null"; // Should fail - cannot know what element was overwritten, array[1] still can be null
        } else {
            assertConjunction("array[0] = null: false"); // Should not be provable - missing predicates
        }
    }
}
