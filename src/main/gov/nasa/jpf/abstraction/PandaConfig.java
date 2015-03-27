package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.VM;

import gov.nasa.jpf.abstraction.heuristic.CompoundRefinementHeuristic;
import gov.nasa.jpf.abstraction.heuristic.RefinementHeuristic;
import gov.nasa.jpf.abstraction.smt.SMT;
import gov.nasa.jpf.abstraction.state.SystemPredicateValuation;
import gov.nasa.jpf.abstraction.state.TruthValue;
import gov.nasa.jpf.abstraction.util.CounterexampleListener;

public class PandaConfig {
    private static PandaConfig instance;

    private Config config;

    private Boolean verbose;
    private Boolean debug;
    private Class<?>[] verboseClasses;
    private Class<?>[] debugClasses;
    private Boolean logSMT;
    private SMT.SupportedSMT smt;
    private SMT.SupportedSMT interpolationSMT;
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
    private PredicateAbstraction.Initialization refinementInitialization;
    private Boolean refinementGlobal;
    private Boolean refinementMethodGlobal;
    private Boolean refinementKeepUnrefinedPrefix;
    private Boolean refinementKeepUnrefinedMethodPrefix;
    private Boolean refinementKeepExploredBranches;
    private Boolean refinementDropUnrefinedPrefixOnFailure;
    private Boolean languageCheckMinimization;
    private Boolean initializeStaticFields;
    private Boolean initializeObjectFields;
    private Boolean initializeArrayElements;
    private RefinementHeuristic refinementHeuristic;
    private Boolean checkTraceFeasibilityAtEveryStep;
    private String refinementDumpAbstractionPredicatesTo;
    private Boolean trackExactValueForLoopControlVariable;
    private Boolean monitorEntireUniverse;

    protected PandaConfig() {
    }

    public static PandaConfig getInstance() {
        if (instance == null) {
            instance = new PandaConfig();
        }

        return instance;
    }

    public static void reset() {
        instance = null;
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
            try {
                verboseClasses = getUnderlyingConfig().getClasses("panda.verbose");

                System.out.println("[VERBOSE] Turning on verbose for classes:");
                for (Class<?> c : verboseClasses) {
                    System.out.println("\t" + c);
                }
            } catch (Throwable t) {
                verboseClasses = new Class<?>[0];
            }
        }

        boolean verbose = false;

        for (Class<?> vCls : verboseClasses) {
            verbose |= cls.equals(vCls);
        }

        return verbose;
    }

    public boolean enabledDebug() {
        if (debug == null) {
            debug = getUnderlyingConfig().getBoolean("panda.debug");

            if (debug == null) {
                debug = false;
            }
        }

        return debug;
    }

    public boolean enabledDebug(Class<?> cls) {
        if (enabledDebug()) {
            return true;
        }

        if (debugClasses == null) {
            try {
                debugClasses = getUnderlyingConfig().getClasses("panda.debug");

                System.out.println("[DEBUG] Turning on debug for classes:");
                for (Class<?> c : debugClasses) {
                    System.out.println("\t" + c);
                }
            } catch (Throwable t) {
                debugClasses = new Class<?>[0];
            }
        }

        boolean debug = false;

        for (Class<?> vCls : debugClasses) {
            debug |= cls.equals(vCls);
        }

        return debug;
    }


    public boolean logSMT() {
        if (logSMT == null) {
            logSMT = getUnderlyingConfig().getBoolean("panda.log_smt");
        }

        return logSMT;
    }

    public SMT.SupportedSMT getSMT() {
        if (smt == null) {
            smt = getUnderlyingConfig().getEnum("panda.smt", SMT.SupportedSMT.class.getEnumConstants(), SMT.SupportedSMT.CVC4);
        }

        return smt;
    }

    public SMT.SupportedSMT getInterpolationSMT() {
        if (interpolationSMT == null) {
            interpolationSMT = getUnderlyingConfig().getEnum("panda.smt.interpolation", SMT.SupportedSMT.class.getEnumConstants(), SMT.SupportedSMT.SMTInterpol);
        }

        return interpolationSMT;
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

    public boolean keepUnrefinedMethodPrefix() {
        if (refinementKeepUnrefinedMethodPrefix == null) {
            refinementKeepUnrefinedMethodPrefix = getUnderlyingConfig().getBoolean("panda.refinement.keep_unrefined_method_prefix");
        }

        return refinementKeepUnrefinedMethodPrefix;
    }

    public boolean keepExploredBranches() {
        if (refinementKeepExploredBranches == null) {
            refinementKeepExploredBranches = getUnderlyingConfig().getBoolean("panda.refinement.keep_explored_branches");
        }

        return refinementKeepExploredBranches;
    }

    public boolean dropUnrefinedPrefixOnFailure() {
        if (refinementDropUnrefinedPrefixOnFailure == null) {
            refinementDropUnrefinedPrefixOnFailure = getUnderlyingConfig().getBoolean("panda.refinement.drop_unrefined_prefix_on_failure");
        }

        return refinementDropUnrefinedPrefixOnFailure;
    }

    public boolean initializeStaticFields() {
        if (initializeStaticFields == null) {
            initializeStaticFields = getUnderlyingConfig().getBoolean("panda.refinement.trace.initialize_static_fields");
        }

        return initializeStaticFields;
    }

    public boolean initializeObjectFields() {
        if (initializeObjectFields == null) {
            initializeObjectFields = getUnderlyingConfig().getBoolean("panda.refinement.trace.initialize_object_fields");
        }

        return initializeObjectFields;
    }

    public boolean initializeArrayElements() {
        if (initializeArrayElements == null) {
            initializeArrayElements = getUnderlyingConfig().getBoolean("panda.refinement.trace.initialize_array_elements");
        }

        return initializeArrayElements;
    }

    public RefinementHeuristic refinementHeuristic(SystemPredicateValuation predVal) {
        try {
            if (refinementHeuristic == null) {
                Class<?>[] classes = getUnderlyingConfig().getClasses("panda.refinement.heuristic");
                RefinementHeuristic[] refinementHeuristics = new RefinementHeuristic[classes.length];

                for (int i = 0; i < classes.length; ++i) {
                    refinementHeuristics[i] = (RefinementHeuristic) classes[i].getConstructor(predVal.getClass()).newInstance(predVal);
                }

                // Default trivial heuristic (does nothing)
                if (refinementHeuristics.length < 1) {
                    refinementHeuristic = new RefinementHeuristic(predVal);
                }

                // Just one heuristic
                if (refinementHeuristics.length < 2) {
                    refinementHeuristic = refinementHeuristics[0];
                }

                // Multiple heuristics
                if (refinementHeuristics.length > 1) {
		    refinementHeuristic = new CompoundRefinementHeuristic(refinementHeuristics);
                }
            }

            return refinementHeuristic;
        } catch (Exception e) {
            throw new JPFConfigException("Could not instantiate refinement heuristic");
        }
    }

    public PredicateAbstraction.Initialization refinementInitializationType() {
        if (refinementInitialization == null) {
            refinementInitialization = getUnderlyingConfig().getEnum("panda.refinement.trace.initialization_encoding", PredicateAbstraction.Initialization.class.getEnumConstants(), PredicateAbstraction.Initialization.READ);
        }

        return refinementInitialization;
    }

    public boolean checkTraceFeasibilityAtEveryStep() {
        if (checkTraceFeasibilityAtEveryStep == null) {
            checkTraceFeasibilityAtEveryStep = getUnderlyingConfig().getBoolean("panda.check_trace_feasibility_at_every_step");
        }

        return checkTraceFeasibilityAtEveryStep;
    }

    public String refinementDumpAbstractionPredicatesTo() {
        if (refinementDumpAbstractionPredicatesTo == null) {
            refinementDumpAbstractionPredicatesTo = getUnderlyingConfig().getString("panda.refinement.dump_abstraction_predicates_to");
        }

        return refinementDumpAbstractionPredicatesTo;
    }

    public boolean trackExactValueForLoopControlVariable() {
        if (trackExactValueForLoopControlVariable == null) {
            trackExactValueForLoopControlVariable = getUnderlyingConfig().getBoolean("panda.branch.track_exact_value_for_loop_control_variable");
        }

        return trackExactValueForLoopControlVariable;
    }

    public boolean monitorEntireUniverse() {
        if (monitorEntireUniverse == null) {
            monitorEntireUniverse = getUnderlyingConfig().getBoolean("panda.universe.monitor_whole");
        }

        return monitorEntireUniverse;
    }
}
