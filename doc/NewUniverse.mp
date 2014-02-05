verbatimtex
%&latex
\documentclass{article}
\begin{document}
etex

input metauml;
%input TEX;

beginfig(1)

bboxmargin := 30;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Class.SymbolTable("SymbolTable")(
    "- localVariables: Map<Root, LocalVariable>",
    "- returnValues: Map<ReturnValue, LocalVariable>",
    "- loadedClasses: Map<PackageAndClass, LoadedClass>",
    "- universe: Universe"
)(
    "+ getLocalVariables(): LocalVariable[0..*]",
    "+ getReturnValues(): LocalVariable[0..*]",
    "+ getLoadedClasses(): LoadedClass[0..*]",
    "+ addPrimitiveLocalVariable(Root name): void",
    "+ addStructuredLocalVariable(Root name): void",
    "+ addPrimitiveReturn(ReturnValue value): void",
    "+ addStructuredReturn(ReturnValue value): void",
    "+ addClass(ThreadInfo thread, ElementInfo classObject): void",
    "+ addThread(ThreadInfo thread, ElementInfo elementInfo): void",
    "+ lookupValues(AccessExpression access): UniverseIdentifier[0..*]",
    "+ valueToAccessExpressions(UniverseIdentifier value, int max): AccessExpression[0..*]",
    "+ processPrimitiveStore(Expression from, SymbolTable fromTable, AccessExpression to): AccessExpression[0..*]",
    "+ processObjectStore(Expression from, SymbolTable fromTable, AccessExpression to): AccessExpression[0..*]",
    "- ensureAnonymousObjectExistence(Expression expression): void",
    "+ isObject(AccessExpression expression): boolean",
    "+ isArray(AccessExpression expression): boolean",
    "+ isPrimitive(AccessExpression expression): boolean",
    "+ toString(): String"
);

Class.Universe("Universe")(
    "- currentStructuredRealization: Map<StructuredValueIdentifier, StructuredValue>",
    "- currentPrimitiveRealization: Map<PrimitiveValueIdentifier, PrimitiveValue>",
    "- NULL: int",
    "- nullReference: Reference"
)(
    "+ contains(UniverseIdentifier id): boolean",
    "+ contains(PrimitiveValueIdentifier id): boolean",
    "+ contains(StructuredValueIdentifier id): boolean",
    "+ get(UniverseIdentifier id): UniverseValue",
    "+ get(PrimitiveValueIdentifier id): PrimitiveValue",
    "+ get(StructuredValueIdentifier id): StructuredValue",
    "+ put(UniverseIdentifier id, UniverseValue value): void",
    "+ put(PrimitiveValueIdentifier id, PrimitiveValue value): void",
    "+ put(StructuredValueIdentifier id, StructuredValue value): void",
    "+ lookupValues(Root[0..*] roots, AccessExpression access): Value[0..*]",
    "+ getStructuredValues(): StructuredValue[0..*]",
    "+ toString(): String"
);

Class.Identifier("Identifier")()(
    "+ hashCode(): int",
    "+ equals(Object object): boolean"
);
classStereotypes.Identifier("<<interface>>");
Identifier.info.iAttributeStack.top    := 0;
Identifier.info.iAttributeStack.bottom := 0;

Class.UniverseIdentifier("UniverseIdentifier")()();
classStereotypes.UniverseIdentifier("<<interface>>");
UniverseIdentifier.info.iAttributeStack.top    := 0;
UniverseIdentifier.info.iAttributeStack.bottom := 0;
UniverseIdentifier.info.iMethodStack.top       := 0;
UniverseIdentifier.info.iMethodStack.bottom    := 0;

Class.StructuredValueIdentifier("StructuredValueIdentifier")()();
classStereotypes.StructuredValueIdentifier("<<interface>>");
StructuredValueIdentifier.info.iAttributeStack.top    := 0;
StructuredValueIdentifier.info.iAttributeStack.bottom := 0;
StructuredValueIdentifier.info.iMethodStack.top       := 0;
StructuredValueIdentifier.info.iMethodStack.bottom    := 0;

Class.PrimitiveValueIdentifier("PrimitiveValueIdentifier")(
    "- uuid: UUID"
)(
    "+ getUUID(): UUID"
);

Class.Reference("Reference")(
    "- elementInfo: ElementInfo",
    "- threadInfo: ThreadInfo"
)(
    "+ getReference(): Integer"
);
Class.ClazzName("ClassName")(
    "- staticElementInfo: StaticElementInfo",
    "- threadInfo: ThreadInfo"
)(
    "+ getClassName(): String"
);

Class.LocalVariable("LocalVariable")()(
    "+ getAccessExpression(): Root",
    "+ createShallowCopy(): LocalVariable"
);
classStereotypes.LocalVariable("<<interface>>");
LocalVariable.info.iAttributeStack.top    := 0;
LocalVariable.info.iAttributeStack.bottom := 0;

leftToRight.midy(160)(UniverseIdentifier, Identifier, LocalVariable);

leftToRight.top(160)(StructuredValueIdentifier, PrimitiveValueIdentifier);
Group.subIdentifiers(StructuredValueIdentifier, PrimitiveValueIdentifier);

leftToRight.top(80)(Reference, ClazzName);
Group.subSubIdentifiers(Reference, ClazzName);

topToBottom.midx(30)(UniverseIdentifier, subIdentifiers);
topToBottom.midx(30)(StructuredValueIdentifier, subSubIdentifiers);
Group.identifiers(UniverseIdentifier, Identifier, LocalVariable, subIdentifiers, subSubIdentifiers);

Class.UniverseValue("UniverseValue")(
    "# frozen: boolean",
    "# parentSlots: <Identifier, UniverseSlotKey>[0..*]"
)(
    "+ getParentSlots(): <Identifier, UniverseSlotKey>[0..*]",
    "+ addParentSlot(Identifier parent, UniverseSlotKey slotKey): void",
    "+ getIdentifier(): UniverseIdentifier",
    "+ createShallowCopy(): UniverseValue",
    "+ hashCode(): int",
    "+ equals(Object object): boolean"
);
classStereotypes.UniverseValue("<<abstract>>");

Class.StructuredValue("StructuredValue")()(
    "+ getIdentifier(): StructuredValueIdentifier",
    "+ getSlot(UniverseSlotKey key): UniverseSlot",
    "+ getSlots(): Map<UniverseSlotKey, UniverseSlot>",
    "+ addSlot(UniverseSlotKey slotKey, UniverseSlot slot): void",
    "+ removeSlot(UniverseSlotKey slotKey): void",
    "+ createShallowCopy(): StructuredValue"
);
StructuredValue.info.iAttributeStack.top    := 0;
StructuredValue.info.iAttributeStack.bottom := 0;
classStereotypes.StructuredValue("<<abstract>>");

Class.PrimitiveValue("PrimitiveValue")(
    "- identifier: PrimitiveValueIdentifier"
)(
    "+ getIdentifier(): PrimitiveValueIdentifier",
    "+ createShallowCopy(): PrimitiveValue"
);

Class.UniverseClass("UniverseClass")(
    "- identifier: ClassName",
    "- fields: Map<FieldName, UniverseSlot>"
)(
    "+ getClassName(): ClassName"
);

Class.HeapValue("HeapValue")(
    "- identifier: Reference"
)(
    "+ getReference(): Reference"
);
classStereotypes.HeapValue("<<abstract>>");

Class.UniverseObject("UniverseObject")(
    "- fields: Map<FieldName, UniverseSlot>"
)();
UniverseObject.info.iMethodStack.top    := 0;
UniverseObject.info.iMethodStack.bottom := 0;

Class.UniverseNull("UniverseNull")()();
UniverseNull.info.iAttributeStack.top    := 0;
UniverseNull.info.iAttributeStack.bottom := 0;
UniverseNull.info.iMethodStack.top       := 0;
UniverseNull.info.iMethodStack.bottom    := 0;

Class.UniverseArray("UniverseArray")(
    "- elements: Map<ElementIndex, UniverseSlot>"
)();
UniverseArray.info.iMethodStack.top    := 0;
UniverseArray.info.iMethodStack.bottom := 0;

leftToRight.top(80)(StructuredValue, PrimitiveValue);
Group.subValues(StructuredValue, PrimitiveValue);

leftToRight.top(80)(UniverseClass, HeapValue);
Group.subSubValues(UniverseClass, HeapValue);

leftToRight.top(80)(UniverseObject, UniverseNull, UniverseArray);
Group.subSubSubValues(UniverseObject, UniverseNull, UniverseArray);

topToBottom.midx(30)(UniverseValue, subValues);
topToBottom.midx(30)(StructuredValue, subSubValues);
topToBottom.midx(30)(HeapValue, subSubSubValues);
Group.values(UniverseValue, subValues, subSubValues, subSubSubValues);

Class.UniverseSlotKey("UniverseSlotKey")()(
    "+ hashCode(): int",
    "+ equals(Object object): boolean"
);
classStereotypes.UniverseSlotKey("<<interface>>");
UniverseSlotKey.info.iAttributeStack.top    := 0;
UniverseSlotKey.info.iAttributeStack.bottom := 0;

Class.FieldName("FieldName")(
    "- name: String"
)(
    "+ getName(): String"
);
classStereotypes.FieldName("<<comparable>>");

Class.ElementIndex("ElementIndex")(
    "- index: Integer"
)(
    "+ getIndex(): Integer"
);
classStereotypes.ElementIndex("<<comparable>>");

leftToRight.top(80)(FieldName, ElementIndex);
Group.subSlotKeys(FieldName, ElementIndex);

topToBottom.midx(30)(UniverseSlotKey, subSlotKeys);
Group.slotKeys(UniverseSlotKey, subSlotKeys);

Class.Associative("Associative")()(
    "+ getField(FieldName name): UniverseSlot",
    "+ getFields(): Map<FieldName, UniverseSlot>"
);
classStereotypes.Associative("<<interface>>");
Associative.info.iAttributeStack.top    := 0;
Associative.info.iAttributeStack.bottom := 0;

Class.Indexed("Indexed")()(
    "+ getElement(ElementIndex index): UniverseSlot",
    "+ getElements(): Map<ElementIndex, UniverseSlot>",
    "+ getLength(): Integer"
);
classStereotypes.Indexed("<<interface>>");
Indexed.info.iAttributeStack.top    := 0;
Indexed.info.iAttributeStack.bottom := 0;

Group.entities(Associative, Indexed);

topToBottom.midx(60)(UniverseObject, Associative);
topToBottom.midx(60)(UniverseArray, Indexed);

Class.UniverseSlot("UniverseSlot")()(
    "+ getParent(): Identifier",
    "+ setParent(Identifier identifier): void",
    "+ getSlotKey(): UniverseSlotKey",
    "+ setSlotKey(UniverseSlotKey slotKey): void",
    "+ getPossibleValues(): UniverseIdentifier[0..*]",
    "+ clear(): void",
    "+ createShallowCopy(): UniverseSlot"
);
classStereotypes.UniverseSlot("<<interface>>");
UniverseSlot.info.iAttributeStack.top    := 0;
UniverseSlot.info.iAttributeStack.bottom := 0;

Class.AbstractUniverseSlot("AbstractUniverseSlot")(
    "# frozen: boolean",
    "- parent: Identifier",
    "- slotKey: UniverseSlotKey"
)();
classStereotypes.AbstractUniverseSlot("<<abstract>>");
AbstractUniverseSlot.info.iMethodStack.top    := 0;
AbstractUniverseSlot.info.iMethodStack.bottom := 0;

Class.StructuredValueSlot("StructuredValueSlot")(
    "- possibleValues: StructuredValueIdentifier[0..*]"
)(
    "+ getPossibleStructuredValues(): StructuredValueIdentifier[0..*]",
    "+ addPossibleStructuredValue(StructuredValueIdentifier value): void",
    "+ createShallowCopy(): StructuredValueSlot",
);

Class.PrimitiveValueSlot("PrimitiveValueSlot")(
    "- possibleValues: PrimitiveValueIdentifier[0..*]"
)(
    "+ getPossiblePrimitiveValues(): PrimitiveValueIdentifier[0..*]",
    "+ addPossiblePrimitiveValue(PrimitiveValueIdentifier value): void",
    "+ createShallowCopy(): PrimitiveValueSlot",
);

Class.LoadedClass("LoadedClass")()(
    "+ getAccessExpression(): PackageAndClass",
    "+ createShallowCopy(): LoadedClass"
);
LoadedClass.info.iAttributeStack.top    := 0;
LoadedClass.info.iAttributeStack.bottom := 0;

Class.StructuredLocalVariable("StructuredLocalVariable")()(
    "+ createShallowCopy(): StructuredLocalVariable"
);
StructuredLocalVariable.info.iAttributeStack.top    := 0;
StructuredLocalVariable.info.iAttributeStack.bottom := 0;

Class.PrimitiveLocalVariable("PrimitiveLocalVariable")()(
    "+ createShallowCopy(): PrimitiveLocalVariable"
);
PrimitiveLocalVariable.info.iAttributeStack.top    := 0;
PrimitiveLocalVariable.info.iAttributeStack.bottom := 0;

leftToRight.top(80)(PrimitiveValueSlot, StructuredValueSlot);
Group.subSlots(PrimitiveValueSlot, StructuredValueSlot);

leftToRight.top(80)(StructuredLocalVariable, LoadedClass);
Group.subSubSlotsi(StructuredLocalVariable, LoadedClass);
Group.subSubSlotsii(PrimitiveLocalVariable);

topToBottom.midx(30)(StructuredValueSlot, subSubSlotsi);
topToBottom.midx(30)(PrimitiveValueSlot, subSubSlotsii);

topToBottom.midx(30)(UniverseSlot, AbstractUniverseSlot, subSlots);
Group.slots(UniverseSlot, AbstractUniverseSlot, subSlots, subSubSlotsi, subSubSlotsii);

topToBottom.midx(80)(SymbolTable, Universe);

same.midy(LocalVariable, UniverseSlot);
same.top(SymbolTable, UniverseSlotKey, UniverseValue);

LocalVariable.right = UniverseSlot.left - 480;
SymbolTable.left    = Reference.left;
Indexed.right       = StructuredValueSlot.right;

UniverseSlotKey.left - SymbolTable.right = UniverseValue.left - UniverseSlotKey.right;

Universe.midy = Identifier.top + 160;

Class.Freezable("Freezable")()(
    "+ freeze(): void",
    "+ isFrozen(): boolean",
    "+ createShallowCopy(): Freezable"
);
classStereotypes.Freezable("<<interface>>");
Freezable.info.iAttributeStack.top    := 0;
Freezable.info.iAttributeStack.bottom := 0;

Freezable.midy = 0.5(UniverseValue.bottom + UniverseSlot.top);
Freezable.left = Indexed.right + 80;

drawObjects(SymbolTable, Universe, identifiers, values, slots, entities, slotKeys, Freezable);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

link(composition)(Universe.n -- SymbolTable.s);

link(inheritance)(UniverseIdentifier.e -- Identifier.w);
link(inheritance)(LoadedClass.s        -- (LoadedClass.s + (0, -40)) -- (Identifier.midx, LoadedClass.bottom - 40) -- Identifier.s);
link(inheritance)(LocalVariable.w      -- Identifier.e);

link(inheritance)(StructuredValueIdentifier.n -- (UniverseIdentifier.s + (-20, 0)));
link(inheritance)(PrimitiveValueIdentifier.n  -- (UniverseIdentifier.s + ( 20, 0)));

link(inheritance)(Reference.n -- (StructuredValueIdentifier.s + (-10, 0)));
link(inheritance)(ClazzName.n -- (StructuredValueIdentifier.s + ( 10, 0)));

link(inheritance)(UniverseValue.e -- (Freezable.midx, UniverseValue.midy) -- Freezable.n);
link(inheritance)(UniverseSlot.e  -- (Freezable.midx, UniverseSlot.midy)  -- Freezable.s);

link(inheritance)(StructuredValue.n -- (UniverseValue.s + (-20, 0)));
link(inheritance)(PrimitiveValue.n  -- (UniverseValue.s + ( 20, 0)));

link(inheritance)(UniverseClass.n -- (StructuredValue.s + (-20, 0)));
link(inheritance)(HeapValue.n     -- (StructuredValue.s + ( 20, 0)));

link(inheritance)(UniverseObject.n -- (HeapValue.s + (-20, 0)));
link(inheritance)(UniverseNull.n   -- (HeapValue.s + (  0, 0)));
link(inheritance)(UniverseArray.n  -- (HeapValue.s + ( 20, 0)));

link(inheritance)(UniverseClass.w  -- (Associative.left - 20, UniverseClass.midy) -- (Associative.w + (-20, 0)) -- Associative.w);
link(inheritance)(UniverseObject.s --  Associative.n);
link(inheritance)(UniverseNull.s   -- (Associative.n + ( 20, 0)));
link(inheritance)(UniverseNull.s   -- (Indexed.n  + (-20, 0)));
link(inheritance)(UniverseArray.s  --  Indexed.n);

link(inheritance)(LocalVariable.e        -- UniverseSlot.w);
link(inheritance)(AbstractUniverseSlot.n -- UniverseSlot.s);

link(inheritance)(PrimitiveValueSlot.n  -- (AbstractUniverseSlot.s + (-20, 0)));
link(inheritance)(StructuredValueSlot.n -- (AbstractUniverseSlot.s + ( 20, 0)));

link(inheritance)(PrimitiveLocalVariable.n -- PrimitiveValueSlot.s);

link(inheritance)(StructuredLocalVariable.n -- (StructuredValueSlot.s + (-20, 0)));
link(inheritance)(LoadedClass.n             -- (StructuredValueSlot.s + ( 20, 0)));

link(inheritance)(StructuredLocalVariable.s -- (StructuredLocalVariable.s + (0, -20)) -- (LocalVariable.midx, StructuredLocalVariable.bottom - 20) -- LocalVariable.s);
link(association)(PrimitiveLocalVariable.s -- (PrimitiveLocalVariable.s + (0, -20)));

link(inheritance)(FieldName.n    -- (UniverseSlotKey.s + (-20, 0)));
link(inheritance)(ElementIndex.n -- (UniverseSlotKey.s + ( 20, 0)));

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pair ul, ur, lr, ll;

ul := (ulcorner currentpicture) + (-30,  30);
ur := (urcorner currentpicture) + ( 30,  30);
lr := (lrcorner currentpicture) + ( 30, -30);
ll := (llcorner currentpicture) + (-30, -30);

setbounds currentpicture to (ul--ur--lr--ll--cycle);
endfig;

end
