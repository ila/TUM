# Multi-Inheritance
## Implementation vs. Interface Inheritance
##### Implementation inheritance
Usual/classic motivation for inheritance: *code reusage*.

The parent implements common behaviours (acts as library of common behaviours), the child replaces particular behaviours with its own ones.

"An Aircraft Carrier is both a Ship and an Airport"
![[implementation-inheritance.png]]

##### Interface inheritance
In interface inheritance, this relation is inverted: inheritance as *behavioural contract*. 

The parent formalizes a type requirement to allow childs to *participate in shared code frames*. The child provides functionality for the method signatures specified by the parent.

"A List can be used both as a Queue and a Stack", and some other parts of the code (here BufferedWriter and JSInterpreter) use a Queue/Stack without specifying a specific implementing class.
![[interface-inheritance.png]]

## Excursion: Intro to LLVM IR
LLVM IR = LLVM Intermediate Representation (LLVM used to be an acronym, but isn't anymore). LLVM is used for one common C++ compiler. We have a look at LLVM to look at how objects are layed out in memory.

### LLLVM IR Example

Struct definitions: (LLVM IR has a concept of types, which makes it a little bit more abstract than assembly!)
```llvm
;(recursive) struct definitions
%struct.A = type { i32, %struct.B, i32(i32)* }
%struct.B = type { i64, [10 x [20 x i32]], i8 }
```
For example, B has an i64 as first field, a 10x20 i32 array as second field, an i8 as third field. A has a struct B nested into it. The third field in A is a *function pointer taking i32 returning i32*.

Allocating objects:
```llvm
;(stack-) allocation of objects
%a = alloca %struct.A
```

Compute address to a struct field:
```llvm
;adress computation for selection in structure (pointers):
%1 = getelementptr %struct.A* %a, i64 0, i64 2
```
The first "index by zero" `i64 0` dereferences the pointer. The second index `i64 2` indexes the result of that in position 2 (i.e. the function pointer in A)

Loading from an address:
```llvm
;load from memory
%2 = load i32(i32)* %1
```
We know load the function pointer whose address we computed before from memory.

Indirect call:
```llvm
;indirect call
%retval = call i32 (i32)* %2(i32 42)
```
The function at the pointer is called and the return value stored. 

- the `%a`, `%1` etc. are local variables
- `getelementptr` abstracts away from the address computation (which may differ depending on the target). It takes a type context (e.g. `%struct.A*`) and a base pointer of that type (e.g. `%a`)


### Retrieving LLVM Code
Retrieving IR code:
```bash
# the llvm-cxxfilt call at the end ungarbles the names
clang -O1 -S -emit-llvm source.cpp -fno-discard-value-names -o /dev/stdout | llvm-cxxfilt
```

Retrieving memory layout:
```bash
clang -cc1 -x c++ -v -fdump-record-layouts -emit-llvm source.cpp
```


### Object memory layout
Consider the simple example:
```c++
// Some class structure:
class A 		   { public: int a; int f(int); };
class B : public A { public: int b; int g(int); };
class C : public B { public: int c; int h(int); };

// Some method implemenation:
int B::g(int p) {
	return p+b;
}

// ... somewhere in the code:
C c;
c.g(42);
```

![[classes-A-B-C.png|200]]

LLVM IR compiles the class structure to
```llvm
%class.C = type { %class.B, i32 }
%class.B = type { %class.A, i32 }
%class.A = type { i32 }
```

and the instructions to
```llvm
%c = alloca %class.C
; compiler found B to have a matching method signature for g(42)
%1 = bitcast %class.C* to %class.B*
; receiver of the call becomes the first parameter; g is statically known
%2 = call i32 @_g(%class.B* %1, i32 42)
```

The method body is compiled to
```llvm
define i32 @_g(%class.B* %this, i32 %p) {
	%1 = getelementptr %class.B* %this, i64 0, i32 1
	%2 = load i32* %1,
	%3 = add i32 %2, %p
	ret i32 %3
}
```

The implicit contract is: whoever calls the method has to make sure that the `%this` parameter passed needs to be compatible with the B memory layout.

## Implementing Single Dispatching
Multiple ways to do it:

##### Java Interpreter-Style: Manual Search through Super Chain
Requires a call to some ```@__dispatch(...)``` function with the concrete type and the method signature on every call.

This is inefficient and used to be quite a problem in the early Java days!

##### Java Hotspot/JIT Style: cache dispatch result
If we recently computed the required dispatch target, we obtain it from some cache and reuse it.


##### C++ style: Precomputing Dispatch results in Tables
The static way of doing things. keep a table where for each class and each possible method, the memory address to dispatch to is stored.

Several ways how to do this concretely:
- full 2-dimensional table (all classes x all methods)
- 1-dimensional "row displacement dispatch" tables (TODO what is this exactly? Is it relevant?)
	- this is a way to save some space
- Virtual tables: e.g. LLVM/GNU C++. Will be treated in more detail here.

### Virtual Methods and Virtual Tables in C++
Consider the example like above, but with *virtual* functions `f`, `g`, `h`. Recall that in C++, methods need to be explicitly marked `virtual` to be overridable, i.e. for dynamic dispatch to work.

```c++
// Some class structure:
class A	{ public: int a; virtual int f(int);
						 virtual int g(int);
						 virtual int h(int); };

// B's g and C's h are *implicitly virtual* since A's g and h are virtual
class B : public A { public: int b; int g(int); };
class C : public B { public: int c; int h(int); };

// ... somewhere in the code:
C c;
c.g(42);
```

Memory layout now: In the class C layout, the first "field" is a pointer to the virtual table, called the `vptr`.

![[memory-layout-vptr.png|250]]

In LLVM: the %class.A struct has a pointer to an array of function pointers as first field.
```llvm
%class.C = type { %class.B, i32, [4 x i8] }
%class.B = type { [12 x i8], i32 } ; the [12 x i8] stands for %class.A
%class.A = type { i32 (...)**, i32 }
```

The instructions get compiled to:
```
%c.vptr = bitcast %class.C* %c to i32 (%class.B*, i32)*** ; vtbl
%1 = load (%class.B*, i32)*** %c.vptr ; dereference vptr
%2 = getelementptr %1, i64 1 		  ; select g()-entry
%3 = load (%class.B*, i32)** %2 	  ; dereference g()-entry
%4 = call i32 %3(%class.B* %c, i32 42)
```

Step by step:
- convert C-pointer to vtable pointer
- dereference the pointer and select the second entry, corresponding to `g()`
- dereference the `g()` entry and call it with the `%c` interpreted as B-pointer

Comment: this code is longer than what Hotspot JIT produces; but *much* shorter than dynamic dispatch binary search.


## Multi-Inheritance
Modify our example: `A` and `B` become independent, and `C` inherits from both.

![[multi-inheritance-ABC.png]]

Problem: Memory layout! We would like child class fields to be directly preceded by the parent class fields. But this is not possible with multiple parents.

### Multiple Inheritance Layout difficulties
Example:
```llvm
%class.C = type { %class.A, %class.B, i32 }
%class.A = type { i32 }
%class.B = type { i32 }
```

Now `B` has an offset in `C`: $\Delta B = |i32| = 4|i8|$.

We will now see: multi-inheritance makes the implementation harder on the compiler side.

##### Implicit call example
An implicit casts now potentially needs to add an offset:
```llvm
; C++ code: B* b = new C();
%1 = ... 						 ; pointer to new C object
%2 = getelementptr i8* %1, i64 4 ; select B offset in C
%b = bitcast i8* %2 to %class.B*
```

##### Calling instance method example
Now create a `C` object and call a `B` method on it:
```C++
C c;
c.g(42);
```

LLVM casts the C pointer to an i8 pointer, selects the B offset, and calls the method with this pointer.
```llvm
%c = alloca %class.C
%1 = bitcast %class.C* %c to i8*
%2 = getelementptr %i8* %1, i64 4
%3 = call i32 @_g(%class.B* %2, i32 42) ; g is statically known
```


### Ambiguities from Multi-Inheritance
Now: Also for the users of the language, the implementation becomes harder: Multi-inheritance introduces ambiguities!

Easiest example:
```C++
class A { public: void f(int); };
class B	{ public: void f(int); };
class C : public A, public B {};

// ...somewhere in the code:
C* pc;
pc->f(42);
```

*Which version of the `f` method is called?*

Two approaches:
- explicit qualification (programmer must write e.g. `pc->A::f(42);` to make clear which method is meant)
- automagical resolution: linearization (the compiler has some rules on which parent classes take precedence)

#### Linearization
Goal: go back from the current set view of superclasses (due tu Multi-Inheritance) to a linear set view.

Define two relations: *inheritance* and *multiplicity*.

> Principle 1. *Inheritance*
> "⇾" points from childs to parents. Example: C(A, B) induces C ⇾ A and C ⇾ B

> Principle 2. *Multiplicity*
> "⇢" points from a parent with higher precedence to the next sibling with lower precedence. Example: C(A, B) induces A ⇢ B.
> (In the script also written as a simple arrow A -> B)

Obeying the two principles means to find a linearization where for any A, B such that A ⇢ B or A ⇾ B, A comes before B in the linearization.

General rules:
- inheritance relation applies uniformly (i.e. not a different inheritance structure for, say, fields and methods)
- the method by which a linearization is created is also called *Method Resolution Order* (MRO)
- Linearization can only be best-effort

#### Simple Linearizations
##### Leftmost Preorder DFS as MRO
LPDFS = leftmost preorder depth-first search. More or less the simplest way to achieve linearization, but not very good!

Example: A(B, C) B(W) C(W).
Then $L[A] = ABWC$: Inheritance is violated, W comes before C!

*Classical Python objects (before 2.1) use this approach.*

##### LPDFS(DC) as MRO
LPDFS(DC) = LPDFS with Duplicate Cancellation. Idea: perform LPDFS, once you encounter a class for the second time you delete its first occurence and switch to the second one.

This fulfills inheritance. It has another odd behaviour though:

Example: A(B, C) B(V, W) C(W, V).
Then $L[A] = ABCWV$.

Multiplicity is not fulfilled, but that's fine since it's not fulfilable. However a bit odd: B comes before C, yet W comes before V (even though the other way around would work too!).

*New Python 2 objects (after 2.2) use this approach.*

##### Reverse Postorder DFS as MRO
Before: DFS leftmost preorder. Now: RPRDFS = rightmost postorder DFS, and reverse the result.

Example: A(B, C) B(F, D) C(E, H) D(G) E(G) F(W) G(W) H(W).
Then $L[A] = ABFDCEGHW$. Everything is fine!

Another example: A(B, C) B(F, G) C(D, E) D(G) E(F).
Then $L[A] = ABCDGEF$. Multiplicity violated! G comes before F, even though there is a clear precedence of $F$ over $G$.

##### Refined RPRDFS as MRO
Idea: in clear cases as we just saw, introduce an additional *inheritance* edge where before there was only a multiplicity edge, if a conflict is detected. Then rerun RPRDFS on the refined graph.

Example from above becomes:
$L(A) = ABCDEFG$

*CLOS = Common Lisp Object System uses Refined RPDFS*.

##### Another principle: *Monotonicity*
> Principle 3. *Monotonicity*
> If $C_1$ comes before $C_2$ in C's linearization, then $C_1$ should come before $C_2$ in the linearization of every child of $C$.

Refined RPDFS violates this principle! Counterexample = the example above: Recall 
$L(A) = ABCDEFG$. However, $L(C) = CDGEF$, where $G$ comes before $F$!

![[linearizations.png]]

#### C3 Linearization
Assume we have a class $C$ with superclasses $B_1, \dots, B_n$ in the *local precedence order* $C(B_1 \cdot \dots \cdot B_n)$.

Define
$$L[C] = C \cdot \bigsqcup(L[B_1], \dots, L[B_n], B_1, \dots, B_n)$$

where $\bigsqcup$ is the *abstract inheritance join operator*, i.e. an operator which joins multiple inheritance relations, defined as follows:

$$\bigsqcup_i (L_i) = c \cdot \bigsqcup_i(L_i \setminus c), \text{ where } c = head(L_k), k = \min \{ k \mid \forall j: head(L_k) \notin tail(L_j) \}$$

If no such $k$ exists, the operator fails.

##### C3 Linearization Example
Again, the example from above: A(B, C) B(F, G) C(D, E) D(G) E(F).

Work your way backwards:

- $L[G] = G$, $L[F] = F$, $L[E] = EF$, $L[D] = DG$
- $L[B] = B \bigsqcup \{F, G, FG\} = BF \bigsqcup \{G, G\} = BFG$
- $L[C] = C \bigsqcup \{DG, EF, D, G\} = CD \bigsqcup \{ G, EF, G \} = CDGEF$
- $L[A] = A \bigsqcup \{ BFG, CDGEF, B, C \} = AB \bigsqcup \{ FG, CDGEF, C \} = ABC \bigsqcup \{ FG, DGEF \} = ABCD \bigsqcup \{FG, GEF \} = \bot$
	- the linearization fails: $F$ is in the tail of $GEF$, $G$ is in the tail of $FG$

==> C3 Reports a *monotonicity violation* due to `A(B, C)`: we would not be able to guarantee compatible linearizations for children and parents.

##### C3 Linearization Adoption
*C3 linearization is used, for example, by Python 3, Raku, and Solidity*.

#### Linearization vs. Explicit Qualification
Qualification has the advantages of being
- more flexible
- and avoiding potentially awkward/unexpected linearization effects

Linearization has the advantages of
- not requiring switch/duplexer code to disambiguate between choices (QUESTION: What would be an example of that?)
- not requiring explicit naming of qualifiers
- given a *unique super reference*
- reduces the number of multi-dispatching conflicts

Some languages with automatic linearization are, as mentioned, [CLOS](https://en.wikipedia.org/wiki/Common_Lisp_Object_System), [Solidity](https://en.wikipedia.org/wiki/Solidity), Python 3, and [Raku (formerly Perl 6)](https://en.wikipedia.org/wiki/Raku_(programming_language)) .

Automatic Linearization, is a prerequisite for Mixins in the presence of Multi-Inheritance, since these usually require a static super type.