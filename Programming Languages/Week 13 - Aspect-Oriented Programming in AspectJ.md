# Aspect-Oriented Programming
Basic idea: allow to add new functionality to existing modules.
- via extension-method-like code (*static crosscutting*)
- via hooks into certain points in the code (*dynamic crosscutting*)

Examples where this might be helpful: Security, Logging, Error Handling, input validatino, profiling. The core idea is to keep the main code clean and simple, and add in all the other functionality (*aspects*) via AOP crosscutting.

Another way to view it: traditionally, code focusses on sequential execution. In AOP, one focusses on *aspects of concerns* which themselves are decoupled. The aspects are *woven* into the program by a *weaver*.

![[aop-compiler-weaver.png]]

Here: we look at **AspectJ**, a superset of Java which allows to define aspects.

## Static Crosscutting
The simpler version of crosscutting, which resembles extension methods: Add new methods to existing types.

Example:
```java
class Expr {}
class Const extends Expr { /*...*/ }
class Add extends Expr { /*...*/ }

// Aspect for evaluating expressions
aspect ExprEval {
	abstract int Expr.eval();
	int Const.eval() { return val; }
	int Add.eval() {return l.eval() + r.eval(); }
}
```

This corresponds to the following plain Java code:
```java
abstract class Expr { abstract int eval(); }
class Const extends Expr {
	/*...*/ 
	public int eval() { return val; }
}
class Add extends Expr {
	/*...*/
	public int eval() { return l.eval() + r.eval(); }
}
```

## Dynamic Crosscutting
Dynamic crosscutting is the much more interesting and powerful part of AOP.

Idea: can define so-called *join points*, which correspond well-identifiable points in the byte code, that execution of new methods can hook into.

### Overview: Types of Join Points in JAspect
- method *calls* (corresponds to syntactical method calls)
- method *executions* (corresponds to actual executions of a method)
- field *get* and *set*
- exception handler execution
- class initialization (corresponds to static initializers being run)
- object initialization (corresponds to dynamic initializers being run)

### Pointcuts
> **Definition** (*Pointcut*).
> A *pointcut* is a set of join points, and optionally some specification of runtime values

A pointcut can combine multiple join points by boolean operations. Example:
```java
pointcut dfs(): execution (void Tree.dfs()) ||
			    exeuction (void Leaf.dfs());
```
The pointcut `dfs` in the example combines the joinpoints for a `Tree.dfs()` and a `Leaf.dfs()` execution.

### Advice
The callback code which should be executed at a joinpoint is called *advice*.

Advice code can be performed `before(...)` or `after(...)` a joinpoint, with additional syntax `after(...) returning (...)` and `after (...) throwing (...)`.

Example: before `C.foo(int)` is called, "About to call foo" should be printed.
```java
aspect Doubler {
	before(): call(int C.foo(int)) {
		System.out.println("About to call foo");
	}
}
```

Advice can bind certain parameters of the joinpoints, e.g. the method call parameters of a `call`: This is done via the `args(arglist)` syntax. The variables to bind to most be introduced in the `before(...)` or `after(...)` expression.
```java
aspect Doubler {
	before(int i): call(int C.foo(int)) && args(i) {
		i = i*2;
	}
}
```

Instead of variable names, arguments in `arglist` can also be typenames to match, `*` for all types, `..` to match several arguments.

##### Around advice
In addition to `before` and `after`, there is `around`: A special function `proceed` is introduced that signifies the point where the `around` advice hands off to the actual call, after which control flow returns to after the proceed call.

Example:
```java
aspect Doubler {
	int around(int i): call(int C.foo(int)) && args(i) {
		int newi = proceed(i*2);
		return newi/2;
	}
}
```

##### Advice Precedence
The order of execution of mutiple pieces of advice for the same join point is governed by some rules, but not fully specified for all cases.
- precedence of one `aspect` over another can be declared via `declare precedence: A, B`
- if $A$ is a subaspect of $B$, then advice from $A$ takes precedence
- for pieces of advice from the *same* aspect, the following rules apply:
	- if both are `after` advice, the one that appears *later* in the code has precedence
		- QUESTION: why is that? seems weird. QUESTION: is "both" correct?
	- otherwise, the one that appears *earlier* has precedence

For all other cases, there is no specified precedence.


### Types of Join Points in Detail
#### Method-related Designators
There are two method-related designators: `call(signature)` and `execution(signature)`. 

`signature` has the syntax:
```java
// method calls
ResultTypeName ReceiverTypeName.method_id(ParamTypeName, ...)
// constructor calls
NewObjectTypeName.new(ParamTypeName, ...)
```

##### Calls vs. Executions
Example usage:
```java
class MyClass {
	public String toString() { "bla" }
	public static voide main(String[] args) {
		MyClass c = new MyClass();
		System.out.println(c + c.toString());
	}
}

aspect CallAspect {
	pointcut calltostring() : call      (String MyClass.toString());
	pointcut exectostring() : execution (String MyClass.toString());
	before() : calltostring() || exectostring() {
		System.out.println("advice!");
	}
}
```

`call` and `execution` can differ in their behavior: In this example, one line from the call, but two lines from the execution are printed.

Output:
```
advice!
advice!
advice!
bla bla
```

##### Pointcuts that match multiple signatures
Once inheritance is involved, a `call` or `execution` specification might match multiple signatures. The behavior in this case is somewhat intricate.

Example:
```java
interface Q          { int m(int i); }
class P implements Q { int m(int s) {return 0;} }
class S extends P    { int m(int s) {return 0;} }
class T extends S    {}
class U extends T    { int m(int s) {return 0;} }

// ... somewhere in the code:
(new T()).m();
```

Aspect that matches on `call` for `m` on all 5 types, i.e. `int Q.m(int)`, `int P.m(int)`, etc: The advice for $Q$, $P$, $S$, $T$ is executed.

Aspect that matches on `execution` for `m` on all 5 types: The advice for $Q$, $P$, $S$ is executed, i.e. not for $T$ which does not implement its own variant of `m`.

Repeat the `execution` experiment, but now call `(new U()).m();`: The advice for $Q$, $P$, $S$, $T$, $U$ is executed!

General rules of thumb:
- `call` matches for all matching signatures on supertypes
- `execution` *should* match methods declared in the specified type, overridden in that type, or inherited by that type; weirdly, in the second example, it doesn't match `T`.

Takeaway: all of this is maybe a bit ill-specified.

#### Field-related Designators
There are two designators to hook into setting and getting of fields: `get(fieldqualifier)` and `set(fieldqualifier)`. Attention: this does *not* hook into the get/set methods, but rather in any use of the dot notation, `A.x = 1`.

The `fieldqualifier` has the syntax `FieldTypeName ObjectTypeName.field_id`. The argument of `set` can be bound by `args(arglist)`.

Example:
```java
aspect GuardedSetter {
	before(int newval): set(static int MyClass.x) && args(newval) {
		if (Math.abs(newval - MyClass.x) > 100) { /*...*/ }
	}
}
```

#### Type-based Designators
There are three type-related designators:
- `target(typeorid)`
	- matches any join point where `typeid` is as receiver
- `within(typepattern)`
	- matches any join point contained in a particular class body
- `withincode(methodpattern)`
	- matches any join point contained in a particular method body

#### Flow and State-Based Designators
More complicated flow-based and state-based designators exist:
- `cflow(arbitrary_pointcut)`
	- matches *any join point* that occurs *between the entry and exit* of each join point matched by `arbitrary_pointcut`
- `if(boolean_expression)`
	- matches *any join point* that *matches a boolean expression*

Example of `if` (a very inefficient way to match method calls):
```java
aspect GuardedSetter {
	before(): if(thisJoinPoint.getKind().equals(METHOD_CALL)) && within(MyClass) { /*...*/ }
}
```

### Implementation of Aspect Weaving
A lot of different methods are possible in principle to implement aspect weaving on the compiler level:
- via a preprocessor which transforms into valid Java sourcecode
- during compliation
- during runtime in the VM
- post-compile processor

The *post-compilation processor* method is the fashionable go-to method to date, and the others are not so relevant/vanishing. Here, the compiled byte code is modified to bring in the aspects.

##### Weaving example: setter
A setter advice is implemented by invoking the advice method before the `putfield` command is executed.
![[weaving-setter-example.png]]

##### Weaving example: method call
A call advice with an additional check is implemented by first performing an `instanceof` check, the invoking the advice method, then invoking the actual method. 
![[weaving-call-example.png]]

#### Weaving of more complicated constructs
##### Around/Proceed Weaving
Weaving of around/proceed is more complicated. An advice method is created that first performs the part before `proceed`, then calls the actual method, then the part after `proceed`.

##### Property-Based Croscutting Weaving
More complicated: property-based, like `cflow` and `if`. At each join point, one would need to match the conditions of each property-based pointcut.

Idea to optimize this: keep a separate stack of states (for each `cflow` pointcut?) that is only modified at pointcuts relevant to the `cflow`, and just checked for emptiness at each join point. In practice, even more optimizations take place.

#### Summary: Weaving implementation
Translation scheme summary:
- `before`/`after`: ranges from inlined code to distribution to several methods/closures
- Joinpoints that have matching advices may get explictly dispatching wrappers
- dynamic dispatching may require runtime tests to interpret certain joinpoint designators
- flow-sensitive pointcuts like `cflow` incur a runtime penalty, even if optimized

## Summary: Aspect Orientation
Pros of Aspect Orientation:
- concerns can be untangled
- late extension, across hierarchy boundaries, becomes possible
- another level of abstraction is provided by aspects

Cons:
- runtime overhead of weaving
- one loses transparency, since aspects can kick in without being apparent in the original code (especially: non-transparent interactions between aspects)
- IDE support needed, especially for debugging