package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Property;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.vm.Path;
import gov.nasa.jpf.vm.ThreadList;
import gov.nasa.jpf.vm.VM;

public class PredicateAbstractionRefinementSearch extends DFSearch {
    public PredicateAbstractionRefinementSearch(Config config, VM vm) {
        super(config, vm);
    }

    @Override
    public void error(Property property, Path path, ThreadList threadList) {
        if (PredicateAbstraction.getInstance().error()) {
            super.error(property, path, threadList);
        }
    }
}
