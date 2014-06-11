package gov.nasa.jpf.abstraction.state.universe;

public class LengthSlotKey implements UniverseSlotKey {
    private static LengthSlotKey instance;

    public static LengthSlotKey getInstance() {
        if (instance == null) {
            instance = new LengthSlotKey();
        }

        return instance;
    }
}
