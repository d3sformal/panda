package gov.nasa.jpf.abstraction.predicate;

class SymbA {
	SymbB b;
}

class SymbB {
	int i;
}

public class SymbolsTest extends BaseTest {

	public static void main(String[] args) {
		SymbA k[] = new SymbA[2];
		SymbA l[] = new SymbA[1];
		SymbA m = new SymbA();
		SymbA n = new SymbA();
		SymbA o[][] = new SymbA[2][2];
		
		k[0] = new SymbA();
		k[1] = new SymbA();

		o[0] = k;
		
		k[0].b = new SymbB();
		k[0].b.i = 1;
		
		k[1].b = new SymbB();
		k[1].b.i = 2;

		assertNumberOfPossibleValues("k[0]", 1);
		assertNumberOfPossibleValues("k[0].b", 1);
		
		m.b = new SymbB();
		m.b.i = 3;

		k[0].b = m.b;

		assertConjunction("k[0].b.i = 3: true");

		assertNumberOfPossibleValues("k[0].b", 1);

		m = n;
		k = new SymbA[10];
		
		k = l;
	}

}
