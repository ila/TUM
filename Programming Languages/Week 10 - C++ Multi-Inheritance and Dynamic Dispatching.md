
# C++: Multi-Inheritance and Dynamic Dispatching

What happens if we combine Multi-Dispatching and Virtual Tables?

If $C(A, B)$, in the memory layout, we would need a vptr before $A$ as well as before $B$!

##### C++ example
Recall [[Week 9 - Multi-Inheritance#Ambiguities from Multi-Inheritance|this example]], but now with a virtual overwritten method `f`.

```c++
class A {	public:
	int a; 
	virtual int f(int); 
};
class B {	public:
	int b; 
	virtual int f(int); 
	virtual int g(int); 
};
class C : public A, public B {
	int c;
	virtual int f(int);
};

// ...somewhere in the code:
C c;
B* pb = &c;
pb->f(42);
```

At the point where `pb` is of type `B*`, all the compiler is supposed to use is the fact that this points to an object of type `B`. So the vptr at the beginning of the memory layout of this `B` object must point to a vtable which contains the `C::f` version, not the `B::f` version.

![[vtable-multiinh-ABC-1.png]]

## Basic Virtual Tables
Basic virtual tables are made up of
- the *offset to top*, i.e. how much higher you need to go to reach the enclosing object's top, starting at the vptr (e.g. $\Delta B$)
- *typeinfo pointer* to an RTTI object (= Run-Time Type Information)
- multiple *virtual function pointers*, used to resolve virtual methods

When multiple inheritance is used, *virtual tables are composed*: The vptrs are pointing to the corresponding *virtual subtables*. 

(QUESTION: Is this distinction into vtables/v-subtables actually relevant?)

With this setup, casting preserves the link between an object and its corresponding virtual subtable.

##### Finding the vtables of a program
The vtables of a compilation unit are output by the command
```bash
clang -cc1 -fdump-vtable-layouts -emit-llvm source.cpp
```

### Thunks
##### Casting issues when calling virtual methods
The following problem can occur when casting: Imagine the overwritten `C::f` method is called from an object of type `B`. Then the `C::f` methods expects as this reference an object of type `C`, so the `B` object must be cast to `C`. This information is not available, however, in the vtable as presented until now!

![[vtable-multiinh-this-references.png]]

##### Thunks to the rescue
*Thunks* are "trampoline methods" that adapt the `this` reference before delegating to the original virtual method implementation.

In our example, in the B-in-C-vtable, `f(int)` is represented by the thunk `_f(int)`. It adds the compiletime constant $\Delta B$ to `this` and then calls `f(int)`.

Compiled code:
```llvm
define i32 @__f(%class.B* %this, i32 %i) {  ; thunk definition
	%1 = bitcast %class.B* %this to i8*     
	%2 = getelementptr i8* %1, i64 -16      ; subtract ΔB = size(A) = 16
	%3 = bitcast i8* %2 to %class.C*        ; interpret as C pointer...
	%4 = call i32 @_f(%class.C* %3, i32 %i) ; ...and call the f method
	ret i32 %4
}
```

## Common Ancestors
What if there are common ancestors? Where to place them in the memory layout? Classical example: Diamond problem.

(Maybe this means you should rethink your class structure... But if the language allows it, the compiler must nevertheless handle that case.)

### Standard C++ approach: Duplicated Bases
Standard C++ Multi-Inheritance: conceptually, duplicates of common ancestors. 
![[duplicated-base-class.png]]

Memory layout:
![[duplicated-base-class-memory-layout.png]]
```llvm
%class.C = type { %class.A, %class.B,
i32, [4 x i8] }
%class.A = type { [12 x i8], i32 }
%class.B = type { [12 x i8], i32 }
%class.L = type { i32 (...)**, i32 }
```
One can see that only `L` needs a vptr.

##### Examples of Ambiguities of Duplicated Bases
The following code fails to compile:
```c++
C c;
L* pl = &c; // 'L' is an ambiguous base of 'C'
```
There are two L objects stored inside `c`, the compiler wouldn't know to which to point!

This works:
```c++
C c;
L* pl = (B*)&c;
```

This also fails:
```c++
C c;
L* pl = (B*)&c;
C* pc = (C*)pl; // 'L' is an ambiguous base of 'C'
```

This works:
```c++
C c;
L* pl = (B*)&c;

// Even the call is allowed (other than c.f(42)): On an L object,
// calling f(...) is unambiguous (the ambiguity would already
// kick in during the cast to L if we didn't resolve it)
pl->f(42); 

// For the cast back to C, give the compiler a hint (static casts 
// need compile-time constant offsets!)
C* pc = (C*)(B*)pl;
```


### Virtual base clases: Allow Common Bases
C++ allows diamond-pattern-style shared base classes with the `virtual` keyword:

```c++
class W { public:
	int w;
	virtual int f(int); virtual int g(int); virtual int h(int);
};
class A : public virtual W { public:
	int a;
  	int f(int);								   
};
class B : public virtual W {	public:
	int b;
	int g(int);
};
class C : public A, public B { public:
	int c;
	int h(int);
};
```

![[virtual-base-classes-example.png]]

Ambiguities can occur (e.g. if `f` is overwritten in both `A` and `B`), resolved by explicit qualification (`pc->B::f`).

#### Memory Layout with *Offset to Virtual Base* entries
In the memory layout of `C`, the shared base class `W` cannot be placed both
- directly above `A`
- and directly above of `B`

This violates the assumption that *the parent can be found within an offset of its child which is constant throughout each occurence of the particular parent in some inheritance relation*. We therefore have to drop this assumption to be able to layout shared base classes in memory.

Each child of the virtual base class stores an *offset to virtual base* (`vbase_offset`) entry in the v-subtable.

Disadvantage: Each time you access a field of a virtual parent, you will have an indirect memory access!

##### Example Memory Layout with Offsets to Virtual Bases
Memory layout (in this particular case):
- place `W` at the end of the `C` representation
- In the vtables for "`A` in `C`" and "`B` in `C`" include offsets to the virtual base class `W` within `C` (in addition to the "offset to top" entries)
	- e.g. from `A`, the offset is $\Delta W = |A| + |B| + |C|$
	- from `B`, the offset is $\Delta W - \Delta B = \Delta W - |A| = |B| + |C|$

![[virtual-baseclass-vtables.png]]

Some more details: see [[Week 10.2 - VTable experiments]]

##### Dynamic Casting
Since there is no guaranteed offset between virtual bases and their childs, *static casting* becomes impossible. Example: If `C(A, B)` and `D(C, B)`, then in the `C` layout we have $A|B|C|W$ and in the D layout, $A|B|C|B|D|W$. So a `W` pointer cannot be statically cast into a `C` pointer!

```c++
C c;
W* pw = &c;
C* pc = (C*)pw; 			  // This gives a compiler error
C* pc = dynamic_cast<C*>(pw); // This works (uses offset-to-top fields)
```


#### Virtual Thunks
Recall that [[#Thunks to the rescue|thunks]] added a constant (statically known) offset to the this reference before calling the original method.

This doesn't work anymore in the virtual base class setting, due to [[#Dynamic Casting|casts being dynamic]]. Instead we need *virtual thunks*, which obtain the offset by which to translate the `this` pointer from the vtable.

The virtual table is extended by one additional entry for each method this is relevant for: The entry corresponding to a method pointer, e.g. `A::Wf`, contains by how much to shift the `this` pointer in the virtual thunk (e.g. `DeltaA-DeltaW` to get from W to the top, then down to A). These offset entries are called *virtual call offsets* (`vcall_offset`).

(Recall notation: `A::Wf` means calling the `A::f` method from a `W` pointer).

#### Complete convention for virtual subtable memory layout
- entries $0$ to $n$: virtual function pointers
- entry $-1$: RTTI pointer
- entry $-2$: offset to top `offset_to_top`
- entry $-3$: offset to virtual base `vbase_offset` (in case there is a virtual base)
- entry $-4-i$ or $-3-i$, $i$ from $0$ to $n$: offsets for virtual thunks `vcall_offset`

![[virtual-subtable-conventions.png]]

### C++ Memory Layout: Compiler/Runtime assumptions
Compiler generates:
- *one codeblock* per method
	- i.e. not different codeblocks depending on the type (like e.g. Rust?)
- *one virtual table* per class composition
	- referencing the *most recent* implementations of methods ("of a unique common signature"). I.e. single-dispatching.
	- containing sub-tables for the composed sub-classes,
	- top-of-object offsets per sub-table,
	- virtual base offsets and virtual call offsets per method/subclass if needed

Runtime behaviour:
- globally create vtables at startup (copied in from binary)
- creating new object: allocate memory, call constructor; constructors stores vtable pointers in the objects
- method calls: call methods *statically* or *dynamically from vtables*; *unaware* of real class identity
- dynamic casts (usually downcasts): use offset-to-top fields.

### Advantages and Disadvantages of Multi-Inheritance
Pros of *Full Multiple Inheritance* (FMI):
- Removes an (unneeded? inconvenient?) inheritance constraint
- Can be convenient in common cases
- Diamond patterns may occur, but are not as frequent as it seems from the discussion around it

Pros of *Multiple Interface Inheritance* (MII):
- simpler to implement
- already expressive (enough?)
- using FMI too frequently often considered a flaw in the class hierarchy design

### Sidenote about Applicability (MS VC++)
The discussion about implementation of FMI applies to GNU C++ and LLVM.

In Microsoft's Visual C++, FMI is implemented a bit differently however:
- split virtual table into several smaller tables
- keep a *virtual base pointer* (`vbptr`) in the *object representation* which points to the virtual base of a child class