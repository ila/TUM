# Function Dispatching

The simplest model of dispatching is: each function name corresponds to exactly one function.

Example in C (ANSI C89): to dispatch, just use the function name to find the method, and check if the parameters match.

```c
void fun(int i) {}
void bar(int i, double j) {}

int main() {
	fun(1);
	bar(1, 1.2);
}
```

## Overloading Function Names
One step further: allow multiple methods with the same name, but a different signature.

##### C11: Workaround for missing overloading
C11 still does not have overloading, but has a workaround: *Generic Selection*, which can be used e.g. in Macros. Depending on the static type of a variable, one of several expressions is evaluated. Syntax with `_Generic(...)`

Example:
```c
int main() {
	printf(_Generic((1.2), signed int: "%d", float: "%f"), 1.2)
}
```

##### Java Overloading
In Java: can overload methods.

Example:
```java
class D {
	public int f(int i) {}
	public double f(double d) {}
}
```

Overloading also works with inheritance, i.e. a child class can overload methods of the base class.

```java
class D {
	public int f(int i) {}
}

class D extends B {
	public double f(double d) {}
}
```

##### C++ Overloading
Overloading per se works the same way as in Java.

Overloading with inheritance has one caveat to note: The method from the base class must be specified with `using MyParent::my_method`, else it is possibly treated as an *override* (e.g. in the case of doubles/ints where implicit casts are performed).

```c++
class B { public:
	int f(int i) {}	
};
class D : public B { public:
 	// If this is not present, f(double d) *overrides* f(int i)
	using B::f;
	double f(double d) {};
};
```

##### Overloading Ambiguities
Ambiguities may arise with overloading:

```java
class D {
	public int f(int i, double j) {}
	public int f(double i, int j) {}
}

// ... in main:
(new D()).f(2, 2); // this fails!
```

Error message from java: "error: reference to f is ambiguous". Reason: there is not clear *most specific variant*.

### Overloading with Static Dispatching
##### Overloading Theory
Three concepts involved:
- *function call expressions* $f(e_1, \dots, e_n)$
- *call signatures* $t_0, \dots, t_n$
- *function definitions* $t_0 f(t_1 p_1, \dots, t_n p_n)$

The call signature consists of the function name, the static parameter types and the return type. Function call expressions determine the signature and dispatch to function definitions. Function definitions are applicable to signatures.

(QUESTION: in the following, I think we define applicability of signatures to signatures, right?)

> **Definition** (*Applicability*).
> $f$ is applicable to $f'$, denoted $f \leq f'$ ($\leq$ is the subtype relation), if
> $$T_0 \leq T_0' \wedge T_i \geq T_i' \forall i > 0$$
> where the signatures are $T_0~f(T_1,\dots,T_n)$ and $T_0'~f'(T_1',\dots,T_n')$

![[overloading-theory.png]]

##### Javac implementation of overloading
Javac defines
- a function `isApplicable(MemberDefinition m, Type args[])`
	- checks applicability of methods to an argument list, using the `isMoreSpecific` method to check $\leq$
- a function `isMoreSpecific(MemberDefinition more, MemberDefinition less)`
	- for member functions: checks that the classes that the methods are defined in are in $\leq$ relation and applicability of the arguments of one to the other method
- a function `matchMethod(Environment env, ClassDefinition accessor, Identifier methodName, Type[] argumentTypes)`
	- finds the most specific method that matches a signature
	- checks all methods in the class and takes note of those that match
	- then checks if there is a unique most specific one (otherwise: ambiguity exception)

![[is-applicable.png]]

![[is-more-specific.png]]

![[match-method.png]]

## Overriding methods
Recall: in OOP, procedures can strongly associate with objects (i.e. *instance methods* and *receivers*). Convention: associate first parameter <--> receiver of the method, bind it to `this` and have some extra syntax like `receiverParam.myMethod(secondParam, thirdParam)`.

Also, in OOP, a subtype should take responsibility for method calls with it as receiver. For example, it should ensure that a method call leaves its internal state consistent.

This leads to the *overriding* principle: if a subtype implements a method that exists in the supertype, the subtype method overrides this method, i.e. is always called instead of the supertype method. In particular, this also holds when an object statically is of the supertype, but dynamically is of the subtype.

```java
class Integral { void incBy(int delta) {...} }
class Natural extends Integral { void incBy(int i) {...} }

// ...somewhere in the code
Integral i = new Natural(42);
i.incBy(43); // this calls the Integral::incBy method because of overriding
```

### Dynamic Single-Dispatching
##### Dynamic dispatching with overriding theory
The [[#Overloading Theory]] from above is slightly augmented: there is a *specializer* involved additionally which specializes a candidate function definition and does special dispatching based on the `this` parameter. During compiletime a more symbolic signature is generated instead of an address, which at runtime is matched to the most specific function.

![[overriding-theory.png]]

#### Java Implementation of Dynamic Dispatching
##### Dynamic Dispatching in Bytecode
As before, *match method* is used to generate the *statically most specific signature*. In other words: a signature is generated which matches the static types of the variables and actually exists somewhere as a method. (I think) this signature consists of the name, return type and parameters of the function, but not the `this` type.

In the bytecode, an invocation consists of a call to `invokevirtual` with the computed signature.

##### Dynamic dispatching in the Hotspot VM
The Hotspot VM gets the statically computed signature, and dynamically resolves the method to call.

Multiple methods involved:
- `resolve_method` does some general checks and calls `lookup_method` with the *dynamic* type of the receiver
- `lookup_method` traverses the superclass chain and calls `find_method` until a match is found
- `find_method` searches for a method that matches the signature in a specific class

`find_method` 
- finds all method of the right name available in the given class (via a binary search)
- searches all these methods and looks for an *exactly matching signature*
	- this is important: There are *no checks for "most specialized" etc*. done here!

The last point is what can lead to confusing behaviour, as we will see in the examples.

### Examples: Dynamic Dispatching in Java
##### Overriding `equals` for a Set
Unexpected behavior when putting numbers in a set:
```java
class Natural {
	// ...
	public boolean equals(Natural n) { return n.number == number }
}
// ... somewhere in the code
Set<Natural> set = new HashSet<>();
set.add(new Natural(0));
set.add(new Natural(0));
System.out.println(set); // [0, 0]
```
The `equals` implementation cannot be used by `HashSet` because it specializes the parameter! In other words: the statically generated signature (`Object.equals(Object)`) cannot be matched to this signature.

##### Visitor pattern with Single Dispatching
Recall the visitor pattern:
```java
abstract class Exp {
	public abstract int accept(EvalVisitor v);
}

class Const extends Exp {
	public int value;
	// ...
	public int accept(EvalVisitor v) { return v.visit(this); }
}

class Sum extends Exp {
	public Exp left, right;
	// ...
	public int accept(EvalVisitor v) { return v.visit(this); }
}

class EvalVisitor {
	int visit(Const c) { return c.value; }
	int visit(Sum s) { 
		return s.left.accept(this) + s.right.accept(this);
	}
	int visit(Exp e) { return e.accept(this); }
}
// ... somewhere in the code:
Exp e = new Sum(new Const(1), new Const(1));
System.out.println(new EvalVisitor().visit(e));
```

Why are the completely boilerplate `accept` methods needed? Because of Java's Single-Dispatching!

What if we would write:
```java
int visit(Sum s) { 
	return visit(s.left) + visit(s.right);
}
// no default accept
// int visit(Exp e) { return e.accept(this); }
```

Then there is an error "error: no suitable method found for visit(Exp)". Since `s.left` has static type Exp, `visit(s.left)` can only be dispatched to `visit(Exp)` at runtime (no specialization in the parameter). With the `accept` method however, dynamic dispatching is possible (specialization in the `this` parameter is possible).

The path taken in the visitor pattern: (simplified pseudo-correct notation)
- `visit(e: Exp)`
- `accept(e: Exp)`
- dynamically dispatched to `accept(e: Sum)`
- `visit(e: Sum)`
- `accept(e.left: Exp)`
	- dynamically dispatched to `accept(e.left: Const)`
	- `visit(e.left: Const)`
- `accept(e.right: Exp)`
	- dynamically dispatched to `accept(e.right: Const)`
	- `visit(e.right: Const)`

##### Mini-Quiz about Single Dispatching
```java
class A {
	public void m1(A a) { System.out.println("m1(A) in A"); }
	public void m1()    { m1(new B()); }
	public void m2(A a) { System.out.println("m2(A) in A"); }
	public void m2()    { m2(this); }
}

class B extends A {
	public void m1(B b) { System.out.println("m1(B) in B"); }
	public void m2(A a) { System.out.println("m2(A) in B"); }
	public void m3()    { super.m1(this); }
}
```

1. `A a = new B(); a.m1(new B());`
2. `(new B()).m1(new B());`
3. `(new B()).m2();`
4. `(new B()).m1();`
5. `(new B()).m3();`

Answers:
1. "m1(A) in A"
	- statically, signature is A.m1(B) which does not exist, so is converted to A.m1(A)
	- dynamically, B does not have a method matching m1(A), so A.m1(A) is called
2. "m1(B) in B"
	- statically, signature is B.m1(B); this exists and also matches dynamically
3. "m2(A) in B"
	- statically, signature is B.m2() which does not exist, so is converted to A.m2()
	- dynamically, A.m2() is called
		- inside m2: statically, signature is A.m2(A)
		- dynamically, the receiver is specialized to B, and ==B has a method matching m2(A)==. So B.m2(A) is called.
4. "m1(A) in A"
	- A.m1() is called
		- inside m1: statically, signature is A.m1(B) which does not exist, so is converted to A.m1(A)
		- dynamically, the receiver is specialized to B, but ==B has no method m1(A)==. So A.m1(A) is called
5. "m1(A) in A"
	- B.m3() is called
		- inside m3: statically, signature is A.m1(B) which does not exist, so is converted to A.m1(A)
		- dynamically, the receiver still is A and A.m1(B) is called.


## Multi-Dispatching
The [[#Overriding equals for a Set|examples]] [[#Visitor pattern with Single Dispatching|have]] [[#Mini-Quiz about Single Dispatching|shown]] that single-dispatching isn't that great. Can we get anything better?

### Workarounds for Multi-Dispatching in Java
How can we solve the `equals()` problem within the Java-Single-Dispatching framework with some workaround?

##### Multi-Dispatch via Introspection
The usual approach for `equals`: Keep your signatures general, perform the specialization manually via type introspection.
```java
// ...
public boolean equals(Object n) {
	if (!(n instanceof Natural)) return false;
	return ((Natural)n).number == number;
}
// ...
```

Problems: this burdens the programmer with type saftey issues, and requires type introspection in the language.

##### Multi-Dispatch via Generics
Another idea: introduce an `Equalizable` interface and implement a set that is aware of this interface.
```java
interface Equalizable<T> {
	boolean equals(T other);
}
class Natural implements Equalizable<Natural> {
	// ...
	public boolean equals(Natural n) { return n.number == number; }
}
// ... somewhere in the code:
EqualizableAwareSet<Natural> set = new EqualizableAwareHashSet<>();
```
Really not great: we need a new set implementation! Also, this only works for *one* overloaded version in the super hierarchy. (QUESTION: why?)

##### Double Dispatching
A bit similar to the [[#Visitor pattern with Single Dispatching|Visitor Pattern example]], rely on multiple single-dispatching calls to achieve what you want.
- `equals` calls a `doubleDispatch` method on the other object
- the current value is captured in an anonymous class, to which the other value is compared to later if it is a `Natural`

```java
abstract class EqualsDispatcher {
	boolean dispatch(Natural n) { return false };
	boolean dispatch(Object o) { return false };
}
class Natural {
	// ...
	public boolean doubleDispatch(EqualsDispatcher ed) {
		return ed.dispatch(this);
	}
	public boolean equals(Object n) {
		return n.doubleDispatch(
			// Create an anonymous class for the actual comparison
			new EqualsDispatcher() {
				boolean dispatch(Natural nat) {
					return nat.number == number;
				}
			}	
		)
	}
}
```
Problems: class hierarchies need to be known for the Dispatcher interface; more problematically, `Object` needs to offer `doubleDispatch` as well (i.e. this only works for more specific baseclasses than `Object` where such a method can be coded in). Also, it looks very complicated and over-engineered.

### Multi-Dispatching Theory
Same as the [[#Overloading Theory|model for overriding]], except that specialization works on all parameters.

![[multi-dispatching-theory.png]]

Here, the dispatcher *dynamically* selects the *most specific concrete method*.
	- not statically, but at runtime
	
### Requirements for Implementing Multi-Dispatching
Implications on
- **Type checking**:
	- e.g. for static type safety, would need to check if there is a unique most specific method for all visible type *tuples*
	- when ambiguities arise at runtime (through MI/interfaces) there must be new runtime errors for these ambiguities (and it would be easy to overlook that something like this could occur!)
- **Performance**: the performance might suffer, especially with a high number of parameters
	- However usually, the penalty only occurs where multi-dispatching is used, and not elsewhere

### Multi-Dispatching in Multi-Java
*MultiJava* is a Java extension that supports multi-dispatching annotations.

Back to our [[#Overriding equals for a Set|equals()]] example:

```java
class Natural {
	public boolean equals(Object@Natural n) { return n.number == number; }
}
```

This works cleanly now!

(Note: the Multi-Java project seems to be abandoned, and was not updated since 2006.)

##### Multi-Java: Behind the Scenes
Looking at the bytecode, this more or less translates to a runtime-`instanceof` check, i.e. similar to the [[#Multi-Dispatch via Introspection|introspection workaround]].


## Languages with Native Multi-Dispatching
### Multi-Dispatch in Raku
*Raku* (a Perl dialect, formerly known as *Perl 6*) has builtin multi-dispatching functionality. For methods that are marked as `multi`, multi-dispatch is used.

```perl
# type Cool is supertype of Int and Str
multi fun(Cool $one, Cool $two) { say "Dispatch base" }
multi fun(Cool $one, Str $two)  { say "Dispatch 1" }
multi fun(Str $one, Cool $two) { say "Dispatch base" }

my Cool $foo;
my Cool $bar;

# This gives: "Dispatch 1"
$foo = 1;
$bar = "bla";
fun($foo, $bar)

# This gives: "ambiguous call to fun(Str, Str)"
# (both the 2nd and 3rd signatures match, there is no most specific one)
$foo = "bla";
fun($foo, $bar);
```

### Multi-Dispatch in Clojure
*Clojure* (a Lisp dialect) also has native multi-dispatching.

##### Clojure basic syntax
(copied from the slides, probably not important)
- Prefix notation
- () – Brackets for lists
- :: – Userdefined keyword constructor ::keyword
- [] – Vector constructor
- fn – Creates a lambda expression (`fn [x y] (+ x y)`)
- derive – Generates hierarchical relationships (`derive ::child ::parent`)
- ==defmulti – Creates new generic method== (`defmulti name dispatch-fn`)
- defmethod – Creates new concrete method (`defmethod name dispatch-val &fn-tail`)

##### Clojure Multi-Dispatching Principles
Clojure allows more creative multi-dispatching as it allows to specify a dispatch function whose result determines the concrete variant used.

Example:
```clojure
(derive ::child ::parent)

(defmulti fun (fn [a b] [a b]))
(defmethod fun [::child ::child ] [a b] "child equals")
(defmethod fun [::parent ::parent] [a b] "parent equals")

(pr (fun ::child ::child))
```
What happens here?	

- `(derive ::child ::parent)` establishes a hierarchical relationship
- `(defmulti fun (fn [a b] [a b]))` means
	-	when `fun` is called with parameters a, b, then the value `[a, b]` should be used to determined which implementation to dispatch to
-	`(defmethod fun [::child ::child ] [a b] "child equals")` means
	-	when the dispatching function returns `[::child ::child]`, the lambda expression `[a b] "child equals"` should be used


##### Elaborate Clojure Multi-Dispatch Example
```clojure
; salary yields poor, rich or average label
(defn salary [amount]
	(cond (< amount 600)   ::poor
		  (>= amount 5000) ::rich
	      :else            ::average))

(defrecord UniPerson [name wage])

; the multi method print dispatches to different variants
; 	depending on the value of the salary function
(defmulti print (fn [person] (salary (:wage person)) ))
(defmethod print ::poor    [person] (str "HiWi  " (:name person)))
(defmethod print ::average [person] (str "Dr.   " (:name person)))
(defmethod print ::rich    [person] (str "Prof. " (:name person)))

(pr (print (UniPerson. "Petter" 2000)))  ; Dr. Petter
(pr (print (UniPerson. "Stefan" 200)))   ; Hiwi Stefan
(pr (print (UniPerson. "Seidl" 16000)))  ; Prof. Seidl
```
Takeaway: dispatching does not necessarily need to be tied to typing, there are also more creative approaches.

### Summary: Multiple Dispatching
Pros:
- Generalizes the established technique, leads out of the weird single-dispatching story
- eliminates boilerplate code
- compatible with modular compilation/type checking

Cons:
- hard to combine with the "first parameter dispatching" which naturally comes from OOP
- some runtime overhead; new runtime exceptions
- notion of *most specific method* ambiguous

Some other languages which support multi-dispatching: *Dylan* and *Scala*.