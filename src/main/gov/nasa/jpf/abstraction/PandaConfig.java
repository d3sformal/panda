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
    private Boolean refinementKeepUnrefined;
    private Boolean languageCheckMinimization;

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
            branchForceFeasible = getUnderlyingConfig().getBoolean("panda.branch.force_feasible");
        }

        return branchForceFeasible;
    }

    public boolean forceFeasibleBranchesOnce() {
        if (branchForceFeasibleOnce == null) {
            branchForceFeasibleOnce = getUnderlyingConfig().getBoolean("panda.branch.force_feasible_once");
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

    public JenkinsStateSet getStorageInstance() {
        return getUnderlyingConfig().getInstance("panda.storage.class", JenkinsStateSet.class);
    }

    public boolean checkLanguageMinimization() {
        if (languageCheckMinimization == null) {
            languageCheckMinimization = getUnderlyingConfig().getBoolean("panda.language.check_minimization");
        }

        return languageCheckMinimization;
    }

    public boolean keepUnrefined() {
        if (refinementKeepUnrefined == null) {
            refinementKeepUnrefined = getUnderlyingConfig().getBoolean("panda.refinement.keep_unrefined");
        }

        return refinementKeepUnrefined;
    }
}
