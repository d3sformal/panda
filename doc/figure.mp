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
  Class.ClassZ("ClassZ")("+field : int")();
  ClassZ.info.iMethodStack.top := 0;
  ClassZ.info.iMethodStack.bottom := 0;

  Instance.a("a")();
  a.info.iAttributeStack.top := 0;
  a.info.iAttributeStack.bottom := 0;

  Instance.b("b")();
  b.info.iAttributeStack.top := 0;
  b.info.iAttributeStack.bottom := 0;

  Instance.c("")("field = 42");
  c.info.iName.top := 0;
  c.info.iName.bottom := 0;

  Note.Anonymous("Accessible as a.y.z.field or b.z.field", "", "Therefore it may prove necessary that these paths are reported upon manipulation of the value", "to ensure the ability to recognize what predicates are affected and should be updated.");

  leftToRight(60)(ClassX, ClassY, ClassZ);
  topToBottom(60)(ClassX, a);
  topToBottom(60)(ClassY, b);
  same.midx(ClassZ, c);
  same.midy(b, c);
  topToBottom(60)(c, Anonymous);

  drawObjects(a, b, c, ClassX, ClassY, ClassZ, Anonymous);

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

  link(dashedLink)(c.attributeStack.pict[0].s--Anonymous.n);
endfig;
end
.
