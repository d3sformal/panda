package gov.nasa.jpf.abstraction;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.abstraction.bytecode.*;
import gov.nasa.jpf.abstraction.PredicateAbstraction;
import gov.nasa.jpf.abstraction.PredicateAbstractionFactory;
import gov.nasa.jpf.util.ClassInfoFilter;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;

/**
 * Ensures interpretations of non-standard instructions respecting the selected abstractions.
 *
 * Also ensures construction of the appropriate abstractions at the start of JPF according .jpf file.
 */
public class AbstractInstructionFactory extends gov.nasa.jpf.jvm.bytecode.InstructionFactory {

    ClassInfo ci;
    ClassInfoFilter filter;

    public AbstractInstructionFactory(Config conf) {

        System.out.println("Running Abstract PathFinder ...");

        filter = new ClassInfoFilter(null, null, null, null);

        PredicateAbstractionFactory factory = new PredicateAbstractionFactory();

        String[] abs_str = conf.getStringArray("panda.abstract_domain");
        String[][] args = new String[abs_str.length][];

        for (int i = 0; i < abs_str.length; ++i) {
            args[i] = abs_str[i].split(" ");
            args[i][0]= args[i][0].toLowerCase();
        }

        PredicateAbstraction.setInstance(factory.create(conf, args));
    }

    // bytecodes

    @Override
    public Instruction aaload() {
        return (filter.isPassing(ci) ? new AALOAD() : super.aaload());
    }

    @Override
    public Instruction aastore() {
        return (filter.isPassing(ci) ? new AASTORE() : super.aastore());
    }

    @Override
    public Instruction aconst_null() {
        return (filter.isPassing(ci) ? new ACONST_NULL() : super.aconst_null());
    }

    @Override
    public Instruction aload(int index) {
        return (filter.isPassing(ci) ? new ALOAD(index) : super.aload(index));
    }

    @Override
    public Instruction anewarray(String typeDescriptor) {
        return (filter.isPassing(ci) ? new ANEWARRAY(typeDescriptor) : super.anewarray(typeDescriptor));
    }

    @Override
    public Instruction areturn() {
        return (filter.isPassing(ci) ? new ARETURN() : super.areturn());
    }

    @Override
    public Instruction arraylength() {
        return (filter.isPassing(ci) ? new ARRAYLENGTH() : super.arraylength());
    }

    @Override
    public Instruction astore(int index) {
        return (filter.isPassing(ci) ? new ASTORE(index) : super.astore(index));
    }

    @Override
    public Instruction baload() {
        return (filter.isPassing(ci) ? new BALOAD() : super.baload());
    }

    @Override
    public Instruction bastore() {
        return (filter.isPassing(ci) ? new BASTORE() : super.bastore());
    }

    @Override
    public Instruction bipush(int val) {
        return (filter.isPassing(ci) ? new BIPUSH(val) : super.bipush(val));
    }

    @Override
    public Instruction caload() {
        return (filter.isPassing(ci) ? new CALOAD() : super.caload());
    }

    @Override
    public Instruction castore() {
        return (filter.isPassing(ci) ? new CASTORE() : super.castore());
    }

    @Override
    public Instruction d2f() {
        return (filter.isPassing(ci) ? new D2F() : super.d2f());
    }

    @Override
    public Instruction d2i() {
        return (filter.isPassing(ci) ? new D2I() : super.d2i());
    }

    @Override
    public Instruction d2l() {
        return (filter.isPassing(ci) ? new D2L() : super.d2l());
    }

    @Override
    public Instruction dadd() {
        return (filter.isPassing(ci) ? new DADD() : super.dadd());
    }

    @Override
    public Instruction daload() {
        return (filter.isPassing(ci) ? new DALOAD() : super.daload());
    }

    @Override
    public Instruction dastore() {
        return (filter.isPassing(ci) ? new DASTORE() : super.dastore());
    }

    @Override
    public Instruction dcmpg() {
        return (filter.isPassing(ci) ? new DCMPG() : super.dcmpg());
    }

    @Override
    public Instruction dcmpl() {
        return (filter.isPassing(ci) ? new DCMPL() : super.dcmpl());
    }

    @Override
    public Instruction dconst_0() {
        return (filter.isPassing(ci) ? new DCONST(0) : super.dconst_0());
    }

    @Override
    public Instruction dconst_1() {
        return (filter.isPassing(ci) ? new DCONST(1) : super.dconst_1());
    }

    @Override
    public Instruction ddiv() {
        return (filter.isPassing(ci) ? new DDIV() : super.ddiv());
    }

    @Override
    public Instruction directcallreturn() {
        return (filter.isPassing(ci) ? new DIRECTCALLRETURN() : super.directcallreturn());
    }

    @Override
    public Instruction dload(int index) {
        return (filter.isPassing(ci) ? new DLOAD(index) : super.dload(index));
    }

    @Override
    public Instruction dmul() {
        return (filter.isPassing(ci) ? new DMUL() : super.dmul());
    }

    @Override
    public Instruction dneg() {
        return (filter.isPassing(ci) ? new DNEG() : super.dneg());
    }

    @Override
    public Instruction drem() {
        return (filter.isPassing(ci) ? new DREM() : super.drem());
    }

    @Override
    public Instruction dreturn() {
        return (filter.isPassing(ci) ? new DRETURN() : super.dreturn());
    }

    @Override
    public Instruction dstore(int index) {
        return (filter.isPassing(ci) ? new DSTORE(index) : super.dstore(index));
    }

    @Override
    public Instruction dsub() {
        return (filter.isPassing(ci) ? new DSUB() : super.dsub());
    }

    @Override
    public Instruction dup() {
        return (filter.isPassing(ci) ? new DUP() : super.dup());
    }

    @Override
    public Instruction f2d() {
        return (filter.isPassing(ci) ? new F2D() : super.f2d());
    }

    @Override
    public Instruction f2i() {
        return (filter.isPassing(ci) ? new F2I() : super.f2i());
    }

    @Override
    public Instruction f2l() {
        return (filter.isPassing(ci) ? new F2L() : super.f2l());
    }

    @Override
    public Instruction fadd() {
        return (filter.isPassing(ci) ? new FADD() : super.fadd());
    }

    @Override
    public Instruction faload() {
        return (filter.isPassing(ci) ? new FALOAD() : super.faload());
    }

    @Override
    public Instruction fastore() {
        return (filter.isPassing(ci) ? new FASTORE() : super.fastore());
    }

    @Override
    public Instruction fcmpg() {
        return (filter.isPassing(ci) ? new FCMPG() : super.fcmpg());
    }

    @Override
    public Instruction fcmpl() {
        return (filter.isPassing(ci) ? new FCMPL() : super.fcmpl());
    }

    @Override
    public Instruction fconst_0() {
        return (filter.isPassing(ci) ? new FCONST(0) : super.fconst_0());
    }

    @Override
    public Instruction fconst_1() {
        return (filter.isPassing(ci) ? new FCONST(1) : super.fconst_1());
    }

    @Override
    public Instruction fconst_2() {
        return (filter.isPassing(ci) ? new FCONST(2) : super.fconst_2());
    }

    @Override
    public Instruction fdiv() {
        return (filter.isPassing(ci) ? new FDIV() : super.fdiv());
    }

    @Override
    public Instruction fload(int index) {
        return (filter.isPassing(ci) ? new FLOAD(index) : super.fload(index));
    }

    @Override
    public Instruction fmul() {
        return (filter.isPassing(ci) ? new FMUL() : super.fmul());
    }

    @Override
    public Instruction fneg() {
        return (filter.isPassing(ci) ? new FNEG() : super.fneg());
    }

    @Override
    public Instruction frem() {
        return (filter.isPassing(ci) ? new FREM() : super.frem());
    }

    @Override
    public Instruction freturn() {
        return (filter.isPassing(ci) ? new FRETURN() : super.freturn());
    }

    @Override
    public Instruction fstore(int index) {
        return (filter.isPassing(ci) ? new FSTORE(index) : super.fstore(index));
    }

    @Override
    public Instruction fsub() {
        return (filter.isPassing(ci) ? new FSUB() : super.fsub());
    }

    @Override
    public Instruction getfield(String fieldName, String clsName, String fieldDescriptor) {
        return (filter.isPassing(ci) ? new GETFIELD(fieldName, clsName, fieldDescriptor) : super.getfield(fieldName, clsName, fieldDescriptor));
    }

    @Override
    public Instruction getstatic(String fieldName, String clsName, String fieldDescriptor) {
        return (filter.isPassing(ci) ? new GETSTATIC(fieldName, clsName, fieldDescriptor) : super.getstatic(fieldName, clsName, fieldDescriptor));
    }

    @Override
    public Instruction i2d() {
        return (filter.isPassing(ci) ? new I2D() : super.i2d());
    }

    @Override
    public Instruction i2f() {
        return (filter.isPassing(ci) ? new I2F() : super.i2f());
    }

    @Override
    public Instruction i2l() {
        return (filter.isPassing(ci) ? new I2L() : super.i2l());
    }

    @Override
    public Instruction i2s() {
        return (filter.isPassing(ci) ? new I2S() : super.i2s());
    }

    @Override
    public Instruction iadd() {
        return (filter.isPassing(ci) ? new IADD() : super.iadd());
    }

    @Override
    public Instruction iand() {
        return (filter.isPassing(ci) ? new IAND() : super.iand());
    }

    @Override
    public Instruction iaload() {
        return (filter.isPassing(ci) ? new IALOAD() : super.iaload());
    }

    @Override
    public Instruction iastore() {
        return (filter.isPassing(ci) ? new IASTORE() : super.iastore());
    }

    @Override
    public Instruction iconst_m1() {
        return (filter.isPassing(ci) ? new ICONST(-1) : super.iconst_m1());
    }

    @Override
    public Instruction iconst_0() {
        return (filter.isPassing(ci) ? new ICONST(0) : super.iconst_0());
    }

    @Override
    public Instruction iconst_1() {
        return (filter.isPassing(ci) ? new ICONST(1) : super.iconst_1());
    }

    @Override
    public Instruction iconst_2() {
        return (filter.isPassing(ci) ? new ICONST(2) : super.iconst_2());
    }

    @Override
    public Instruction iconst_3() {
        return (filter.isPassing(ci) ? new ICONST(3) : super.iconst_3());
    }

    @Override
    public Instruction iconst_4() {
        return (filter.isPassing(ci) ? new ICONST(4) : super.iconst_4());
    }

    @Override
    public Instruction iconst_5() {
        return (filter.isPassing(ci) ? new ICONST(5) : super.iconst_5());
    }

    @Override
    public Instruction idiv() {
        return (filter.isPassing(ci) ? new IDIV() : super.idiv());
    }

    @Override
    public Instruction if_icmpeq(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPEQ(targetPc)
                : super.if_icmpeq(targetPc));
    }

    @Override
    public Instruction if_icmpge(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPGE(targetPc)
                : super.if_icmpge(targetPc));
    }

    @Override
    public Instruction if_icmpgt(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPGT(targetPc)
                : super.if_icmpgt(targetPc));
    }

    @Override
    public Instruction if_icmple(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPLE(targetPc)
                : super.if_icmple(targetPc));
    }

    @Override
    public Instruction if_icmplt(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPLT(targetPc)
                : super.if_icmplt(targetPc));
    }

    @Override
    public Instruction if_icmpne(int targetPc) {
        return (filter.isPassing(ci) ? new IF_ICMPNE(targetPc)
                : super.if_icmpne(targetPc));
    }

    @Override
    public Instruction ifeq(int targetPc) {
        return (filter.isPassing(ci) ? new IFEQ(targetPc) : super
                .ifeq(targetPc));
    }

    @Override
    public Instruction ifge(int targetPc) {
        return (filter.isPassing(ci) ? new IFGE(targetPc) : super
                .ifge(targetPc));
    }

    @Override
    public Instruction ifgt(int targetPc) {
        return (filter.isPassing(ci) ? new IFGT(targetPc) : super
                .ifgt(targetPc));
    }

    @Override
    public Instruction ifle(int targetPc) {
        return (filter.isPassing(ci) ? new IFLE(targetPc) : super
                .ifle(targetPc));
    }

    @Override
    public Instruction iflt(int targetPc) {
        return (filter.isPassing(ci) ? new IFLT(targetPc) : super
                .iflt(targetPc));
    }

    @Override
    public Instruction ifne(int targetPc) {
        return (filter.isPassing(ci) ? new IFNE(targetPc) : super
                .ifne(targetPc));
    }

    @Override
    public Instruction ifnonnull(int targetPc) {
        return (filter.isPassing(ci) ? new IFNONNULL(targetPc) : super
                .ifnonnull(targetPc));
    }

    @Override
    public Instruction ifnull(int targetPc) {
        return (filter.isPassing(ci) ? new IFNULL(targetPc) : super
                .ifnull(targetPc));
    }

    @Override
    public Instruction iinc(int localVarIndex, int incConstant) {
        return (filter.isPassing(ci) ? new IINC(localVarIndex, incConstant) : super.iinc(localVarIndex, incConstant));
    }

    @Override
    public Instruction iload(int index) {
        return (filter.isPassing(ci) ? new ILOAD(index) : super.iload(index));
    }

    @Override
    public Instruction imul() {
        return (filter.isPassing(ci) ? new IMUL() : super.imul());
    }

    @Override
    public Instruction ineg() {
        return (filter.isPassing(ci) ? new INEG() : super.ineg());
    }

    @Override
    public Instruction invokeinterface(String clsName, String methodName, String methodSignature) {
        return (filter.isPassing(ci) ? new INVOKEINTERFACE(clsName, methodName, methodSignature) : super.invokeinterface(clsName, methodName, methodSignature));
    }

    @Override
    public Instruction invokeclinit(ClassInfo info) {
        return (filter.isPassing(ci) ? new INVOKECLINIT(info) : super.invokeclinit(info));
    }

    @Override
    public Instruction invokespecial(String clsName, String methodName, String methodSignature) {
        return (filter.isPassing(ci) ? new INVOKESPECIAL(clsName, methodName, methodSignature) : super.invokespecial(clsName, methodName, methodSignature));
    }

    @Override
    public Instruction invokestatic(String clsName, String methodName, String methodSignature) {
        return (filter.isPassing(ci) ? new INVOKESTATIC(clsName, methodName, methodSignature) : super.invokestatic(clsName, methodName, methodSignature));
    }

    @Override
    public Instruction invokevirtual(String clsName, String methodName, String methodSignature) {
        return (filter.isPassing(ci) ? new INVOKEVIRTUAL(clsName, methodName, methodSignature) : super.invokevirtual(clsName, methodName, methodSignature));
    }

    @Override
    public Instruction ior() {
        return (filter.isPassing(ci) ? new IOR() : super.ior());
    }

    @Override
    public Instruction irem() {
        return (filter.isPassing(ci) ? new IREM() : super.irem());
    }

    @Override
    public Instruction ireturn() {
        return (filter.isPassing(ci) ? new IRETURN() : super.ireturn());
    }

    @Override
    public Instruction ishl() {
        return (filter.isPassing(ci) ? new ISHL() : super.ishl());
    }

    @Override
    public Instruction istore(int index) {
        return (filter.isPassing(ci) ? new ISTORE(index) : super.istore(index));
    }

    @Override
    public Instruction ishr() {
        return (filter.isPassing(ci) ? new ISHR() : super.ishr());
    }

    @Override
    public Instruction isub() {
        return (filter.isPassing(ci) ? new ISUB() : super.isub());
    }

    @Override
    public Instruction iushr() {
        return (filter.isPassing(ci) ? new IUSHR() : super.iushr());
    }

    @Override
    public Instruction ixor() {
        return (filter.isPassing(ci) ? new IXOR() : super.ixor());
    }

    @Override
    public Instruction l2d() {
        return (filter.isPassing(ci) ? new L2D() : super.l2d());
    }

    @Override
    public Instruction l2f() {
        return (filter.isPassing(ci) ? new L2F() : super.l2f());
    }

    @Override
    public Instruction l2i() {
        return (filter.isPassing(ci) ? new L2I() : super.l2i());
    }

    @Override
    public Instruction ladd() {
        return (filter.isPassing(ci) ? new LADD() : super.ladd());
    }

    @Override
    public Instruction land() {
        return (filter.isPassing(ci) ? new LAND() : super.land());
    }

    @Override
    public Instruction laload() {
        return (filter.isPassing(ci) ? new LALOAD() : super.laload());
    }

    @Override
    public Instruction lastore() {
        return (filter.isPassing(ci) ? new LASTORE() : super.lastore());
    }

    @Override
    public Instruction lconst_0() {
        return (filter.isPassing(ci) ? new LCONST(0) : super.lconst_0());
    }

    @Override
    public Instruction lconst_1() {
        return (filter.isPassing(ci) ? new LCONST(1) : super.lconst_1());
    }

    @Override
    public Instruction ldc(int v) {
        return (filter.isPassing(ci) ? new LDC(v) : super.ldc(v));
    }

    @Override
    public Instruction ldc(float f) {
        return (filter.isPassing(ci) ? new LDC(f) : super.ldc(f));
    }

    @Override
    public Instruction ldc(String v, boolean isClass) {
        return (filter.isPassing(ci) ? new LDC(v, isClass) : super.ldc(v, isClass));
    }

    @Override
    public Instruction ldc_w(int v) {
        return (filter.isPassing(ci) ? new LDC_W(v) : super.ldc_w(v));
    }

    @Override
    public Instruction ldc_w(float f) {
        return (filter.isPassing(ci) ? new LDC_W(f) : super.ldc_w(f));
    }

    @Override
    public Instruction ldc_w(String v, boolean isClass) {
        return (filter.isPassing(ci) ? new LDC_W(v, isClass) : super.ldc_w(v, isClass));
    }

    @Override
    public Instruction ldc2_w(long l) {
        return (filter.isPassing(ci) ? new LDC2_W(l) : super.ldc2_w(l));
    }

    @Override
    public Instruction ldc2_w(double d) {
        return (filter.isPassing(ci) ? new LDC2_W(d) : super.ldc2_w(d));
    }

    @Override
    public Instruction ldiv() {
        return (filter.isPassing(ci) ? new LDIV() : super.ldiv());
    }

    @Override
    public Instruction lload(int index) {
        return (filter.isPassing(ci) ? new LLOAD(index) : super.lload(index));
    }

    @Override
    public Instruction lmul() {
        return (filter.isPassing(ci) ? new LMUL() : super.lmul());
    }

    @Override
    public Instruction lneg() {
        return (filter.isPassing(ci) ? new LNEG() : super.lneg());
    }

    @Override
    public Instruction lookupswitch(int defaultTargetPc, int nEntries) {
        return filter.isPassing(ci)
                ? new LOOKUPSWITCH(defaultTargetPc, nEntries)
                : super.lookupswitch(defaultTargetPc, nEntries);
    }

    @Override
    public Instruction lor() {
        return (filter.isPassing(ci) ? new LOR() : super.lor());
    }

    @Override
    public Instruction lrem() {
        return (filter.isPassing(ci) ? new LREM() : super.lrem());
    }

    @Override
    public Instruction lreturn() {
        return (filter.isPassing(ci) ? new LRETURN() : super.lreturn());
    }

    @Override
    public Instruction lshl() {
        return (filter.isPassing(ci) ? new LSHL() : super.lshl());
    }

    @Override
    public Instruction lshr() {
        return (filter.isPassing(ci) ? new LSHR() : super.lshr());
    }

    @Override
    public Instruction lstore(int index) {
        return (filter.isPassing(ci) ? new LSTORE(index) : super.lstore(index));
    }

    @Override
    public Instruction lsub() {
        return (filter.isPassing(ci) ? new LSUB() : super.lsub());
    }

    @Override
    public Instruction lushr() {
        return (filter.isPassing(ci) ? new LUSHR() : super.lushr());
    }

    @Override
    public Instruction lxor() {
        return (filter.isPassing(ci) ? new LXOR() : super.lxor());
    }

    @Override
    public Instruction multianewarray(String typeName, int dimensions) {
        return (filter.isPassing(ci) ? new MULTIANEWARRAY(typeName, dimensions) : super.multianewarray(typeName, dimensions));
    }

    @Override
    public Instruction nativereturn() {
        return (filter.isPassing(ci) ? new NATIVERETURN() : super.nativereturn());
    }

    @Override
    public Instruction new_(String clsName) {
        return (filter.isPassing(ci) ? new NEW(clsName) : super.new_(clsName));
    }

    @Override
    public Instruction newarray(int typeCode) {
        return (filter.isPassing(ci) ? new NEWARRAY(typeCode) : super.newarray(typeCode));
    }

    @Override
    public Instruction putfield(String fieldName, String clsName, String fieldDescriptor) {
        return (filter.isPassing(ci) ? new PUTFIELD(fieldName, clsName, fieldDescriptor) : super.putfield(fieldName, clsName, fieldDescriptor));
    }

    @Override
    public Instruction putstatic(String fieldName, String clsName, String fieldDescriptor) {
        return (filter.isPassing(ci) ? new PUTSTATIC(fieldName, clsName, fieldDescriptor) : super.putstatic(fieldName, clsName, fieldDescriptor));
    }

    @Override
    public Instruction return_() {
        return (filter.isPassing(ci) ? new RETURN() : super.return_());
    }

    @Override
    public Instruction runstart(MethodInfo mi) {
        return (filter.isPassing(ci) ? new RUNSTART() : super.runstart(mi));
    }

    @Override
    public Instruction saload() {
        return (filter.isPassing(ci) ? new SALOAD() : super.saload());
    }

    @Override
    public Instruction sastore() {
        return (filter.isPassing(ci) ? new SASTORE() : super.sastore());
    }

    @Override
    public Instruction sipush(int val) {
        return (filter.isPassing(ci) ? new SIPUSH(val) : super.sipush(val));
    }

    @Override
    public Instruction tableswitch(int defaultTargetPc, int low, int high) {
        return filter.isPassing(ci)
                ? new TABLESWITCH(defaultTargetPc, low, high)
                : super.tableswitch(defaultTargetPc, low, high);
    }
}
