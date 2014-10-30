package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.CounterexampleListener;

public class PandaConfig {
    private static PandaConfig instance;

    private Config config;

    private Boolean verbose;
    private Class<?>[] verboseClasses;
    private Boolean logSMT;
    private TruthValue assertionsDisabled;
    private Boolean printConcreteCounterexample;
    private CounterexampleListener.Format counterexamplePrintFormat;
    private Boolean printRefinedPredicateContexts;
    private Boolean printErrorOnRefinementFailure;
    private Boolean branchAdjustConcreteValues;
    private Boolean branchForceFeasible;
    private Boolean branchForceFeasibleOnce;
    private Boolean branchPruneInfeasible;
    private Boolean branchReevaluatePredicates;
    private Boolean refinement;
    private Boolean refinementGlobal;
    private Boolean refinementMethodGlobal;
    private Boolean refinementKeepUnrefinedPrefix;
    private Boolean languageCheckMinimization;
    private Boolean initializeStaticFields;
    private Boolean initializeObjectFields;
    private Boolean initializeArrayElements;

    protected PandaConfig() {
    }

    public static PandaConfig getInstance() {
        if (instance == null) {
            instance = new PandaConfig();
        }

        return instance;
    }

    private Config getUnderlyingConfig() {
        if (config == null) {
            config = VM.getVM().getJPF().getConfig();
        }

        return config;
    }

    public boolean enabledVerbose() {
        if (verbose == null) {
            verbose = getUnderlyingConfig().getBoolean("panda.verbose");

            if (verbose == null) {
                verbose = false;
            }
        }

        return verbose;
    }

    public boolean enabledVerbose(Class<?> cls) {
        if (enabledVerbose()) {
            return true;
        }

        if (verboseClasses == null) {
            Boolean verbose = getUnderlyingConfig().getBoolean("panda.verbose");

            if (verbose == null) {
                verboseClasses = getUnderlyingConfig().getClasses("panda.verbose");
            } else {
                verboseClasses = new Class<?>[0];
            }
        }

        boolean verbose = false;

        for (Class<?> vCls : verboseClasses) {
            verbose |= cls.equals(vCls);
        }

        return verbose;
    }

    public boolean logSMT() {
        if (logSMT == null) {
            logSMT = getUnderlyingConfig().getBoolean("panda.log_smt");
        }

        return logSMT;
    }

    public TruthValue getAssertionsDisabled() {
        if (assertionsDisabled == null) {
            assertionsDisabled = getUnderlyingConfig().getEnum("panda.assertions_disabled", TruthValue.class.getEnumConstants(), TruthValue.UNDEFINED);
        }

        return assertionsDisabled;
    }

    public boolean printConcreteCounterexample() {
        if (printConcreteCounterexample == null) {
            printConcreteCounterexample = getUnderlyingConfig().getBoolean("panda.counterexample.print_concrete");
        }

        return printConcreteCounterexample;
    }

    public CounterexampleListener.Format getCounterexamplePrintFormat() {
        if (counterexamplePrintFormat == null) {
            counterexamplePrintFormat = getUnderlyingConfig().getEnum("panda.counterexample.print_format", CounterexampleListener.Format.class.getEnumConstants(), CounterexampleListener.Format.SEPARATED);
        }

        return counterexamplePrintFormat;
    }

    public boolean printRefinedPredicateContexts() {
        if (printRefinedPredicateContexts == null) {
            printRefinedPredicateContexts = getUnderlyingConfig().getBoolean("panda.counterexample.print_refined_predicate_contexts");
        }

        return printRefinedPredicateContexts;
    }

    public boolean printErrorOnRefinementFailure() {
        if (printErrorOnRefinementFailure == null) {
            printErrorOnRefinementFailure = getUnderlyingConfig().getBoolean("panda.counterexample.print_error_on_refinement_failure");
        }

        return printErrorOnRefinementFailure;
    }

    public boolean adjustConcreteValues() {
        if (branchAdjustConcreteValues == null) {
            branchAdjustConcreteValues = getUnderlyingConfig().getBoolean("panda.branch.adjust_concrete_values");
        }

        return branchAdjustConcreteValues;
    }

    public boolean forceFeasibleBranches() {
        if (branchForceFeasible == null) {
            branchForceFeasible = getUnderlyingConfig().getBoolean("panda.branch.nondet_force_feasible");
        }

        return branchForceFeasible;
    }

    public boolean forceFeasibleBranchesOnce() {
        if (branchForceFeasibleOnce == null) {
            branchForceFeasibleOnce = getUnderlyingConfig().getBoolean("panda.branch.nondet_force_feasible_once");
        }

        return branchForceFeasibleOnce;
    }

    public boolean pruneInfeasibleBranches() {
        if (branchPruneInfeasible == null) {
            branchPruneInfeasible = getUnderlyingConfig().getBoolean("panda.branch.prune_infeasible");
        }

        return branchPruneInfeasible;
    }

    public boolean reevaluatePredicatesAfterBranching() {
        if (branchReevaluatePredicates == null) {
            branchReevaluatePredicates = getUnderlyingConfig().getBoolean("panda.branch.reevaluate_predicates");
        }

        return branchReevaluatePredicates;
    }

    public boolean enabledRefinement() {
        if (refinement == null) {
            refinement = getUnderlyingConfig().getBoolean("panda.refinement");
        }

        return refinement;
    }

    public boolean enabledGlobalRefinement() {
        if (refinementGlobal == null) {
            refinementGlobal = getUnderlyingConfig().getBoolean("panda.refinement.global");
        }

        return refinementGlobal;
    }

    public boolean enabledMethodGlobalRefinement() {
        if (refinementMethodGlobal == null) {
            refinementMethodGlobal = getUnderlyingConfig().getBoolean("panda.refinement.method_global");
        }

        return refinementMethodGlobal;
    }

    public JenkinsStateSet getStorageInstance() {
        return getUnderlyingConfig().getInstance("panda.storage.class", JenkinsStateSet.class);
    }

    public boolean checkLanguageMinimization() {
        if (languageCheckMinimization == null) {
            languageCheckMinimization = getUnderlyingConfig().getBoolean("panda.language.check_minimization");
        }

        return languageCheckMinimization;
    }

    public boolean keepUnrefinedPrefix() {
        if (refinementKeepUnrefinedPrefix == null) {
            refinementKeepUnrefinedPrefix = getUnderlyingConfig().getBoolean("panda.refinement.keep_unrefined_prefix");
        }

        return refinementKeepUnrefinedPrefix;
    }

    public boolean initializeStaticFields() {
        if (initializeStaticFields == null) {
            initializeStaticFields = getUnderlyingConfig().getBoolean("panda.refinement.initialize_static_fields");
        }

        return initializeStaticFields;
    }

    public boolean initializeObjectFields() {
        if (initializeObjectFields == null) {
            initializeObjectFields = getUnderlyingConfig().getBoolean("panda.refinement.initialize_object_fields");
        }

        return initializeObjectFields;
    }

    public boolean initializeArrayElements() {
        if (initializeArrayElements == null) {
            initializeArrayElements = getUnderlyingConfig().getBoolean("panda.refinement.initialize_array_elements");
        }

        return initializeArrayElements;
    }
}
