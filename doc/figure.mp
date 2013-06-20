u := 15;

input metauml;

filenametemplate "%j%c.mps";

beginfig(0)
  label(btex \tt undefined etex, (2u, 0u));
  label(btex \tt 0 etex, (0u, 2u));
  label(btex \tt 1 etex, (4u, 2u));
  label(btex \tt unknown etex, (2u, 4u));

  draw (2u, 0.5u)--(0.5u, 1.5u);
  draw (2u, 0.5u)--(3.5u, 1.5u);

  draw (0.5u, 2.5u)--(2u, 3.5u);
  draw (3.5u, 2.5u)--(2u, 3.5u);
endfig;

beginfig(1)
  Class.ClassX("ClassX")()();
  ClassX.info.iAttributeStack.top := 0;
  ClassX.info.iAttributeStack.bottom := 0;
  ClassX.info.iMethodStack.top := 0;
  ClassX.info.iMethodStack.bottom := 0;
  Class.ClassY("ClassY")()();
  ClassY.info.iAttributeStack.top := 0;
  ClassY.info.iAttributeStack.bottom := 0;
  ClassY.info.iMethodStack.top := 0;
  ClassY.info.iMethodStack.bottom := 0;
  Class.ClassZ("ClassZ")("+f : int")();
  ClassZ.info.iMethodStack.top := 0;
  ClassZ.info.iMethodStack.bottom := 0;

  Instance.a("a")();
  a.info.iAttributeStack.top := 0;
  a.info.iAttributeStack.bottom := 0;

  Instance.b("b")();
  b.info.iAttributeStack.top := 0;
  b.info.iAttributeStack.bottom := 0;

  Instance.c("")("f = 42");
  c.info.iName.top := 0;
  c.info.iName.bottom := 0;

  leftToRight(60)(ClassX, ClassY, ClassZ);
  topToBottom(40)(ClassX, a);
  topToBottom(40)(ClassY, b);
  same.midx(ClassZ, c);
  same.midy(b, c);

  drawObjects(a, b, c, ClassX, ClassY, ClassZ);

  link(aggregation)(ClassY.w--ClassX.e);
  link(aggregation)(ClassZ.w--ClassY.e);
  link(associationUni)(a.n--ClassX.s);
  link(associationUni)(b.n--ClassY.s);
  link(associationUni)(c.n--ClassZ.s);
  link(association)(a.e--b.w);
  link(association)(b.e--c.w);

  item(iStereo)("<<instanceof>>")(obj.w = .5[ClassX.s, a.n]);
  item(iStereo)("<<instanceof>>")(obj.w = .5[ClassY.s, b.n]);
  item(iStereo)("<<instanceof>>")(obj.w = .5[ClassZ.s, c.n]);
  item(iAssoc)("y")(obj.sw = ClassX.ne);
  item(iAssoc)("z")(obj.sw = ClassY.ne);
  item(iAssoc)("y")(obj.sw = a.ne);
  item(iAssoc)("z")(obj.sw = b.ne);
endfig;

beginfig(2)
  Activity.instr("Instruction Executed");
  Activity.attr("Load attributes", "Especially access paths to", "the operands");
  Activity.pred("Select predicates", "using the access paths");
  Activity.val("Resolve all paths in the predicate", "to concrete numerical values", "(if not replaced with 0)");
  Activity.smt("Run SMT solver on the", "selected predicates");
  Activity.upd("Update abstract values and access paths");

  topToBottom(30)(instr, attr, pred, val, smt, upd);

  Note.nInstr("Example:", "", "FADD(f1, f2)");
  Note.nAttr("Let f1 be obtained from a.y.z.f and f2 from b.z.f.", "The two paths refer the same piece of data in memory.", "Therefore both attributes need to yield the two access paths.", "However, the attributes are different.");
  Note.nPred("Affected predicate P1", "", "a.y.z.f = c");
  Note.nVal("a.y.z.f = 3", "c = 1");
  Note.nSmt("SAT a.y.z.f = c and a.y.z.f = 3 and c = 1 ?");
  Note.nUpd("not P1", "", "The result value does not correspond to any path,", "it is an intermediate result.");

  nInstr.midy = instr.midy;
  nAttr.midy = attr.midy;
  nPred.midy = pred.midy;
  nVal.midy = val.midy;
  nSmt.midy = smt.midy;
  nUpd.midy = upd.midy;

  same.left(nInstr, nAttr, nPred, nVal, nSmt, nUpd);

  nInstr.left = 200;

  drawObjects(instr, attr, pred, val, smt, upd, nInstr, nAttr, nPred, nVal, nSmt, nUpd);

  link(associationUni)(instr.s--attr.n);
  link(associationUni)(attr.s--pred.n);
  link(associationUni)(pred.s--val.n);
  link(associationUni)(val.s--smt.n);
  link(associationUni)(smt.s--upd.n);
  link(associationUni)(rpathManhattanX(instr.w, upd.w));

  clink(dashedLink)(instr, nInstr);
  clink(dashedLink)(attr, nAttr);
  clink(dashedLink)(pred, nPred);
  clink(dashedLink)(val, nVal);
  clink(dashedLink)(smt, nSmt);
  clink(dashedLink)(upd, nUpd);
endfig;

beginfig(3)
  Class.StackItem("StackItem")("concreteValue", "attribute : Attribute")();
  StackItem.info.iMethodStack.top := 0;
  StackItem.info.iMethodStack.bottom := 0;

  Class.Attribute("Attribute")()();
  Attribute.info.iAttributeStack.top := 0;
  Attribute.info.iAttributeStack.bottom := 0;
  Attribute.info.iMethodStack.top := 0;
  Attribute.info.iMethodStack.bottom := 0;

  Class.AbstractValue("AbstractValue")()();
  AbstractValue.info.iAttributeStack.top := 0;
  AbstractValue.info.iAttributeStack.bottom := 0;
  AbstractValue.info.iMethodStack.top := 0;
  AbstractValue.info.iMethodStack.bottom := 0;

  Class.Meta("Meta")("access path(s)")();
  Meta.info.iMethodStack.top := 0;
  Meta.info.iMethodStack.bottom := 0;

  Class.Global("GlobalAbstractValue")("valuation : Map<Predicate, TruthValue>")();
  Global.info.iMethodStack.top := 0;
  Global.info.iMethodStack.bottom := 0;
  classStereotypes.Global("<<multiton>>");

  Note.Multiton("Instance of GlobalAbstractValue", "per Abstraction instance");

  leftToRight(30)(StackItem.attributeStack.pict[1], Attribute);
  topToBottom(30)(AbstractValue, Meta);

  Group.g(AbstractValue, Meta);

  leftToRight(30)(Attribute, g);
  leftToRight(30)(AbstractValue, Global);
  topToBottom(30)(Global, Multiton);

  drawObjects(StackItem, Attribute, g, Global, Multiton);

  link(aggregation)(AbstractValue.w -- Attribute.ne);
  link(aggregation)(Meta.w -- Attribute.se);
  link(dashedLink)(StackItem.attributeStack.pict[1].e -- Attribute.w);
  link(dashedLink)(AbstractValue.e -- Global.w);
  clink(dashedLink)(Global, Multiton);
endfig;

end
.
