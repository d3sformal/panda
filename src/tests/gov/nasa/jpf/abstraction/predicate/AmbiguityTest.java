package gov.nasa.jpf.abstraction.predicate;

class AmbA
{
    AmbB x;

    public AmbA() {
        x = new AmbB();
    }
}

class AmbB
{
    AmbC y;

    public AmbB() {
        y = new AmbC();
    }
}

class AmbC
{
    int z;
}

public class AmbiguityTest extends BaseTest
{
    static AmbA static_a = new AmbA();
    static AmbB static_b;
    static AmbC static_c = new AmbC();

    public static void main(String[] args) {
        AmbA a = new AmbA();
        AmbB b;
        AmbC c = new AmbC();

        // LOCAL / HEAP

        a.x.y.z = 0;
        c.z = 3;

        b = a.x;
	
		assertConjunction("b.y.z = 0: true");

        a.x.y = c;

		assertConjunction("b.y.z = 3: true");

		assertAliased("c", "b.y");

        // STATIC

        static_a.x.y.z = 0;
        static_c.z = 3;

        static_b = static_a.x;
        static_a.x.y = static_c;
    }
}
