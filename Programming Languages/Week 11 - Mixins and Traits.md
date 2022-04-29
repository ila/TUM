# Mixins and Traits
## Discussion: What do we want to achieve?
Our ultimate goal: enable some kind of code sharing to avoid unnecessary code duplication.
- in OO systems: code sharing usually enabled via *inheritance* (which comes in different flavours)
- all inheritance-based systems tackle *composition* and *decomposition* problems

##### Example: ShortLockedDoor
Example: in an Adventure game, we have `ShortDoors` (that only small persons can pass), and `LockedDoors` (that only persons with a key can pass). Sometimes later in the game, we want to introduce `ShortLockedDoors` that combine these two characteristics.

![[short-locked-door.png]]

- If multi-inheritance is available: trivial!
- Without MI: Aggregation + SI construction possible (quite involved)
	- have a `Doorlike` interface that provides both `canOpen()` and `canPass()` 
	- `Door`, `Short`, `Locked` implement `Doorlike`: for the methods that are not relevant to them, they just always return true
	- A `Door` has a list of `Doorlikes` and aggregates their results

Problems:
- `Door` must take care of chaining
- The `Doorlike` interface must anticipate all the different flavours of doors that are to come
- All the `Doorlike` implementing classes must return a default value for all types of door attributes not relevant to them

##### Example: Threadsafe Streams Wrapper
Example: we have a hierarchy of Stream classes, for example a `FileStream` and a `SocketStream` (both with `read`/`write` methods). We want to introduce a threadsafe version of streams `SynchRW` which adds some locking code around the `read`/`write` calls of a stream.

![[threadsafe-streams-wrapper.png]]

- Multi-Inheritance not directly applicable: many-to one instead of one-to many relation!
	- we want many streams to adapt the features of one wrapper class, not one wrapper class to adapt the features of many parents
- Aggregation solution: SynchRW has a `Stream` internally, which can then be specialized to `FileStream`, `SocketStream`, etc.
	- Undoes specialization, i.e. does not fit into the hierarchy: there is now no way to express that we want, e.g., a threadsafe `FileStream`
		- QUESTION: we could express this with a generic `<S: Stream>` parameter, right?
	- Only works here because there is a common ancestor `Stream` of `FileStream` and `SocketStream`

![[threadsafe-stream-wrappers-aggregation.png]]

- Duplication MI solution
	- Have `SynchRW` independently from the stream classes
	- introduce child classes `SynchedFileStream`, `SynchedSocketStream` that inherit from both their respective stream type and `SynchRW`
	- Problem: code reusage limited, a lot of duplication is necessary!o
		- `read`/`write` need to be re-implemented identically for every wrapper

![[threadsafe-stream-wrappers-MI-duplication.png]]

- SynchedRW as superclass solution
	- Place `SynchRW` as superclass of the two stream classes
	- Make the streams implement read/write with and without locking; let them have some internal flag that indicates whether this feature is activated
	- Have subclasses of the streams that simply activate said flag
	- Problem: dodgy design, since the `SynchRW` methods are way too high up in the hierarchy; also, thread-safety functionality must be anticipated when writing the Stream classes

![[threadsafe-stream-wrappers-activated-by-flag.png]]


### (De-)Composition problems as the key question
All the problems we saw are centered around
- relationships between subclasses/base classes
- how to set up a hierarchy of classes
- how to minimize code duplication

Key question: "How do I distribute functionality over a hierarchy".
==> *functional decomposition*


## Abstract Class Hierarchy Formalism
Assume we have the following sets:
- a countable set of *names* $\mathcal{N}$
- a countable set of *method bodies* $\mathbb{B}$
- a set of *classes* $\mathcal{C}$
- the "flat lattice" of *bindings* $\mathcal{B} = \mathbb{B} \cup \mathcal{C} \cup \{\top, \bot\}$
	- $\bot$ means "abstract" = no implementation
	- $\top$ means "in conflict" = more than one implementation
	- $\mathcal{B}$ is partially ordered by $\bot \sqsubseteq b \sqsubseteq \top$ (hence the name "flat lattice")

![[flat-lattice.png|300]]

> **Definition** *Class $\in \mathcal{C}$*.
> A map $c: \mathcal{N} \to \mathcal{B}$ is called a *class*.
> I.e. a class maps names to bindings.

QUESTION: apparently, $c$ only maps a subset of $\mathcal{N}$ to bindings, right? And $pre(c)$ denotes this subset?

> **Definition** *Interface/abstract/concrete classes*.
> A class $c$ is called 
> - *interface* if $c(n) = \bot$ for *all* $n \in pre(c)$
> - *abstract class* if $c(n) = \bot$ for *some*  $n \in pre(c)$
> - *concrete class* if $c(n) \notin \{\bot, \top\}$ for all $n \in pre(c)$

> **Definition** *Family of classes $\mathcal{C}$*.
> The set of all mappings from names to bindings is called the *family of classes* $\mathcal{C}$

### Binary operators on  $\mathcal{C}$
Several possibilities to compose two elements of $\mathcal{C}$:

##### Symmetric join $\sqcup$
In $(c_1 \sqcup~ c_2)$, a name $n$ is mapped to a binding if it defined in exactly one of the two classes, or if it is defined in both, and the definitions coincide. If it is defined differently in the two classes, the name is mapped to $\top$ = "in conflict".

$$
(c_1 \sqcup c_2)(n) = \begin{cases}
c_1(n) & \text{if $c_2(n) = \bot$ or $n \notin pre(c_2)$} \\
c_2(n) & \text{if $c_1(n) = \bot$ or $n \notin pre(c_1)$} \\
c_1(n) & \text{if $c_1(n) = c_2(n)$} \\
\top & \text{otherwise}
\end{cases}
$$

##### Asymmetric join $\Lsh\hspace{-0.27cm}\sqcup$
In $(c_1 \Lsh\hspace{-0.27cm}\sqcup c_2)$, the bound names of $c_1$ override any bound names of $c_2$. Only the names from $c_2$ which are not bound by $c_1$ are left intact.

$$(c_1 \Lsh\hspace{-0.27cm}\sqcup~ c_2) = \begin{cases}
c_1(n) & \text{if $n \in pre(c_1)$} \\
c_2(n) & \text{otherwise}
\end{cases}
$$

## Examples of Inheritance Models
### Smalltalk Inheritance
*[Smalltalk](https://en.wikipedia.org/wiki/Smalltalk) inheritance* is the archetype for how inheritance works in mainstream imperative languages like C# or Java.

Children's methods dominate parent's methods, and a `super` reference to the parent is established.

> **Definition**. *Smalltalk inheritance $\rhd$*.
> Define the operator binary operator $\rhd$ on $\mathcal{C}$ by
> $$c_1 \rhd c_2 = \{ \texttt{super} \mapsto c_2 \} \Lsh\hspace{-0.27cm}\sqcup~ (c_1 \Lsh\hspace{-0.27cm}\sqcup~ c_2)$$
> Here $c_1$ is the child class and $c_2$ is the parent class.

##### Example: LockedDoor in Smalltalk Inheritance
$$Door = \{canPass \mapsto \bot, canOpen \mapsto \top\}$$
$$LockedDoor = \{canOpen \mapsto 0x4204711\} \rhd Door$$
$$= \{super \mapsto Door, canOpen \mapsto 0x4204711, canPass \mapsto \bot\}$$

### Beta-style Inheritance
In *Beta-style inheritance*, child methods do *not* replace parent methods. Instead, parent methods dominate, and child methods need to be explicitly qualified with the `inner` keyword. Design goal: provide security from replacement of one method by another.

> **Definition**. *Beta-style inheritance $\lhd$*.
> Define the operator $\lhd$ by
> $$c_1 \lhd c_2 = \{\texttt{inner} \mapsto c_1\} \Lsh\hspace{-0.27cm}\sqcup~ (c_2 \Lsh\hspace{-0.27cm}\sqcup~ c_1)$$

##### Example of Beta-style inheritance
...in pseudo-Java syntax with an added `inner`, `extension` keywords:
```java
class Person {
	String name;
	public String toString() {
		return name + inner.toString();
	}
}

class Graduate extends Person {
	public extension String toString() { return ", Ph.D."; }
}
```


### CRTP - Curiously Recurring Template Pattern
CRTP (the *Curiously Recurring Template Pattern*) is a C++ pattern that allows a base class to call a child class method statically (*static polymorphism*), by making a base class defined via a template where the child class can be filled in.

```c++
template <class D>
struct Base_class
{ 
	void base()
	{
		// Allows to call non-static methods of D
		static_cast<D*>(this)->derived();
	}
	static void static_base()
	{
		// Allows to call static methods of D
		D::static_derived();
	}
}
struct Derived
{
	void derived();
	static void static_derived();
}
```

Disadvantages are that
- the base class is defined as a template
- some spots are left open in an algorithm's definition (really a problem?)
- most problematic: struggles with repeatedly overwritten methods

## Mixins
Now that we saw some different inheritance models, let's find out what we really want to solve problems like in the above examples.

##### Example: ShortLockedDoor with Mixins
Idealistic goal: "cut out" the functionality from `LockedDoor` and `ShortDoor` and insert it in `ShortLockedDoor : Door`, *without creating a multi-inheritance diamond*.

Idea: have *mixins* `Locked` and `Short`, which can be composed to create the `ShortLockedDoor`.

![[short-locked-door-mixins.png]]

Code (pseudo-java with added mixin functionality):
```java
class Door {
	boolean canOpen(Person p) { return true; };
	boolean canPass(Person p) { return p.size() < 210; };
}

// Can define mixins:
mixin Locked { 	boolean canOpen(Person p){ ... } }
mixin Short {  canPass(Person p){ ... } }

// Can create the short and locked doors:
class ShortDoor = Short(Door);
class LockedDoor = Locked(Door);

// Can compose mixins to create short locked doors:
mixin ShortLocked = Short o Locked;
class ShortLockedDoor = Short(Locked(Door));
class ShortLockedDoor2 = ShortLocked(Door);
```

##### Threadsafe Stream wrappers with Mixins
Have `SynchRW` as mixin.
![[threadsafe-stream-wrappers-mixin.png]]
- avoids `read`/`write` code duplication
- keeps specialization (i.e. class hierarchy)
- does not need multi-inheritance

### Abstract Mixin Formalism
> **Definition** *Mixin*.
> The mixin constructor $mixin: \mathcal{C} \to (\mathcal{C} \to \mathcal{C})$ is defined by
> $$mixin(c) = \lambda x. c \rhd x$$
> $mixin$ is a curried version of [[##Smalltalk Inheritance|⊳]]. Methods of the mixin class dominate over existing methods.

The mixin operator is a *unary higher-order type expression*, which in some languages can be created.

Mixin operators can be composed:
$$mixin(Short) \circ mixin(Locked) = \lambda x. Short \rhd (Locked \rhd x)$$

### Mixins on Implementation Level: Non-Static `super`
Imagine you have
```java
class ShortDoor = Short(Door);
class ShortLockedDoor = Short(Locked(Door));
```
Sometimes `Short` is "mixed into" `Door`, and sometimes into `Locked`; i.e. the `super` reference from `Short` certainly cannot be static.

Some dynamic `super` dispatching therefore has to be implemented for mixins to work.

### Simulating Mixins with C++ Multi-Inheritance
Idea how to simulate mixins in C++:
- have a template for the mixin
- the template takes a generic `Super` class parameter

##### Example of C++ mixin simulation
```c++
// Mixin that makes a stream threadsafe:
template <class Super>
class SynchRw : public Super
{
	public: 
	virtual int read()
	{
		acquireLock();
		int result = Super::read();
		releaseLock();
		return result
	}
	// ... rest of the implementation
};
// Mixin that makes a stream buffered:
template <class Super>
class BufStream: public Super { ... };

// Composed stream:
class BufSynchFileStream : public SynchRw<BufferedStream<FileStream>> {};
```

##### "True" Mixins vs. C++ Mixins
C++ mixins can be seen as a coding pattern; the C++ type system is not modular, 	hence the mixins have to stay source code.

With True Mixins, *super* is natively supported. Mixins are composable (but so they are in C++?)

Generally: mixins require a linearization, and the exact sequence of mixins matters.

### Native Mixins in Ruby
[Ruby](https://en.wikipedia.org/wiki/Ruby_(programming_language)) is a language that supports native mixins

##### Example: ShortLockedDoor with Mixins in Ruby
Some setup:
```ruby
class Person
	attr_accessor :size
	def initialize
		@size = 160
	end
	def hasKey
		true
	end
end
class Door
	def canOpen (p)
		true
	end
	def canPass(person)
		person.size < 210
	end
end
```

Mixins are called `module`s in Ruby. Define `Short` and `Locked` mixins:
```ruby
module Short
	def canPass(p)
		p.size < 160 and super(p)
	end
end
module Locked
	def canOpen(p)
		p.hasKey() and super(p)
	end
end
```

Mixins can be mixed into a class at class definition:
```ruby
class ShortLockedDoor < Door
	include Short
	include Locked
end
```

Composed Mixins can be created:
```ruby
module ShortLocked
	include Short
	include Locked
end
```

Objects can be extended with mixins even at runtime:
```ruby
# At-runtime extension with a mixin
d = Door.new
d.extend ShortLocked

# Alternatively:
d = ShortLockedDoor.new
```

## Discussion: Is Inheritance *The Principle* for Resuability?

### Drawbacks of Inheritance
##### Lack of Control
Inheritance may not give sufficient control over which code to share and which not to share.

Example: `PoliceDrone`.
![[police-drone-inheritance.png]]

We would probably want to share the *fuel* attribute - but rather not share the *equipment* field, otherwise we might want to `shoot` a photo and instead `shoot` the precision gun!

In multi-inheritance, common base classes are usually either shared or duplicated. Such a fine-grained specification as we would want here is not possible.

##### Inappropriate Hierarchies
Inheritance does not allow to *remove* parent methods.

Example: a `LinkedList` which offers `add` and `remove` is extended by a `Stack` which offers `push` and `pop`. The `add` and `remove` methods should not belong to the methods offered by `Stack` - they turn obsolete. Yet there is no way to remove them (compare with *Liskov Substitution principle*: we should be able to insert a `Stack` everywhere where a `List` is used).

(Note: maybe it's not a good idea to have `Stack` extend `LinkedList`?)

### Is Implementation Inheritance an *Anti Pattern*?
Example: In Java 8, `Properties` inherits `Hashtable` and therefore exposes `put` and `putAll`. However, this allows to insert non-`String` objects, which is not what the object should be doing.

The documentation says that *the use of `put` and `putAll` is strongly discouraged* and that *the `setProperty` method should be used instead*. However, there is just no way in Java to disallow this wrong usage statically!

Bottom line: *Implementation inheritance as a pattern for code reusage is often misused!*

## Traits
The idea behind traits: maybe problems originate from the coupling of *implementation* and *modeling*?
- interfaces seem to be hierarchical
- functionality seems to be modular

Separate: *object creation* <--> *hierarchy modeling* <--> *functionality composition*

1. use interfaces for hierarchical *signature propagation*
2. use *traits* as modules for assembling functionality
3. use classes as frames for entities which can create objects


### Abstract Traits Formalism
> **Definition** *Trait $\in \mathcal{T}$*.
> A class without attributes is called *trait*.

Note: for mixins, we also did not have attributes, but this is often done differently. For Traits, there are *explicitly* excluded.


- The *Trait sum* $+: \mathcal{T} \times \mathcal{T} \to \mathcal{T}$ is just the symmetric join: $$c_1 + c_2 = c_1 \sqcup c_2$$
- *Trait exclusion* $-: \mathcal{T} \times \mathcal{N} \to \mathcal{T}$ allows to exclude names from traits
$$(t-a)(n) = \begin{cases}undef & \text{if $a=n$}\\t(n) & \text{otherwise}\end{cases}$$
- *Aliasing*: $[\to]: \mathcal{T} \times \mathcal{N} \times \mathcal{N} \to \mathcal{T}$
allows to introduce an additional name for some binding.
$$t[a\to b](n) = \begin{cases}
t(n) & \text{if $n \neq q$} \\
t(b) & \text{if $n = a$}
\end{cases}$$
- Connecting traits to classes: at the last composition level with the asymmetric join. Connect trait $t$ to class $c$ via $c \Lsh\hspace{-0.27cm}\sqcup~ t$.

### Concepts and Principles of Traits
##### Trait Composition Principles
- all traits have same precedence (under +), due to $+ = \sqcup$. Disambiguation needs to be done explicitly with aliasing and exclusion. (*Flat ordering*)
- class methods take precedence over trait methods, i.e. classes can override the "default implementations" from traits (since $\Lsh\hspace{-0.27cm}\sqcup~$ is used) (*Flattening*)

Conflict treatment:
- via *aliasing* or *exclusion*
- or by just overriding the conflicting method in the final class

### Trait-like extensions in common languages
#### C#: Extension Methods
Idea behind extension methods: uncouple method definitions <--> class bodies (methods can be defined outside classes).

Extension methods allow to
- retrospectively/externally add methods to types
- provide default implementations of interface methods ("poor man's multiple inheritance")

Syntax for extension methods is:
- declare extension methods as static methods within a static class
- declare first parameter as of type to be extended, with a `this` preceding the type, e.g. `this Locked lockedDoor`
- the extension method can now be called in infix form

```c#
public interface Short {}
public interface Locked {}
public static class DoorExtensions {
	public static bool canOpen(this Locked locked, Person p) { /*... */ }
	public static bool canPass(this Short locked, Person p) { /*... */ }
}
public class ShortLockedDoor : Locked, Short {}

// Somewhere in Main():
d = new ShortLockedDoor();
d.canOpen(new Person()); // calls the method defined above
```

##### Extension Methods vs. Traits
C# extension methods *can* extend types externally, *but not really traits*:
- no flattening: classes cannot override extension methods
- extension methods cannot be used to implement interface methods
	- e.g. if `Locked` would specify a `bool canOpen(Person p)` method, this would not be considered as implemented


#### Java 8: Virtual Extension Methods
Java has so-called *virtual extension methods*, which are basically default implementations for interfaces:
```java
interface Door {
	boolean canOpen(Person p);
	boolean canPass(Person p);
}
interface Locked extends Door {
	default boolean canOpen(Person p) { /*...*/ }
}
interface Short extends Door {
	default boolean canPass(Person p) { /*...*/ }
}
public class ShortLockedDoor implements Short, Locked, Door {}
```

Note that the `Locked` and `Short` default implementations actually override the `Door`.

Drawback: does not override methods from *abstract* classes.

Implementation of virtual extension methods: An extra interface phase is added to the name resolution of `invokevirtual`.

### Traits as General Composition Mechanism
What we would actually want from traits: separate class generation <--> hierarchy specification <--> functional modeling via
1. modeling the hierarchical relations with interfaces
2. composing functionality with traits
3. in classes, adapt functionality to interfaces and add state via glue code

Goal: *simplified multiple inheritance without adverse effects*!

Important: if there is any *precedence* involved, what you have is probably more like mixins than traits (e.g. "Scala traits").

### "Real Traits" in Squeak
[Squeak](https://en.wikipedia.org/wiki/Squeak) is a Smalltalk implementation (dialect?) which is extended by a trait system.

##### Squeak basic syntax
Syntax:
```smalltalk
"Declare method `someMethod` with two parameters `param1` and `param2`"
someMethod: param1 and: param2

"Declare variables `someVar1` and `someVar2`"
| someVar1 someVar2 |

"Assignment"
someVar1 := expr

"Send the message `someMessage` with content `someContent` to `someObject`"
"Think of method call: someObject.someMessage(someContent)"
someObject someMessage:someContent

"Line terminator: Dot"
.

"Return statement"
^ expr
```

##### Example: Threadsafe Streams Wrapper in Squeak
```smalltalk
"Some stream base class"
Trait named: #TRStream uses: TPositionableStream
	on: aCollection
		self collection: aCollection.
		self setToStart.
	"`next` method that returns the next element (details unimportant)"
	next
		^ self atEnd
		ifTrue [nil]
	ifFalse: [self collection at: self nextPosition].

"`TSynch` trait which implements acquiring/releasing a semaphore"
Trait named: #TSynch uses: {}
	acquireLock
		self semaphore wait.
	releaseLock
		self semaphore signal.

"Combining the two traits:
	- alias the #next method as #readNext so we can redefine #next
	- use trait sum + to add the two traits
"
Trait named: #TSyncRStream uses: TSynch+(TRStream@(#readNext -> #next))
	"Define a new `next` method which wraps reading with the lock"
	next
		| read |
		self.acquireLock.
		read := self readNext.
		self releaseLock.
		^ read.
```

## Traits vs. Mixins vs. Class Inheritance
Type expressions involved:
- Mixins: Second-order type operators + linearization
- Traits: Finegrained flat-ordered module composition
- Class inheritance: Defines (local) partial order on precedence of types wrt. MRO
- Combination of principles, e.g. in extension methods, templates, etc.

##### Traits and Mixins: Differences
- Traits are applied *in parallel*, mixins *sequentially*
	- and therefore also: trait composition is *unordered* and avoids linearization (and therefore e.g. unexpected/strange overloading/overriding)
- Traits *never contain attributes*: avoiding state conflicts
- Traits cause *glue code to be concentrated* in single classes

### Summary: Mixins and Traits
Mixins = "low-effort alternative to multi-inheritance"
Mixins lift type expressions to second-order type expressions
- "just open up your type expressions to lambda expressions, and you already have mixins"

Traits:
- fine-grained control of composition of functionality
- in native trait languages: separation of composition of functionality <--> specification of interfaces