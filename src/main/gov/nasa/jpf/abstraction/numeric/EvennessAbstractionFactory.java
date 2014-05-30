package gov.nasa.jpf.abstraction.numeric;

import gov.nasa.jpf.abstraction.Abstraction;
import gov.nasa.jpf.abstraction.AbstractionFactory;

import gov.nasa.jpf.Config;

public class EvennessAbstractionFactory extends AbstractionFactory {

    @Override
    public Abstraction create(Config config, String[] args) {
        System.out.printf("### jpf-abstraction: EVENNESS turned on\n");

        return EvennessAbstraction.getInstance();
    }

}
