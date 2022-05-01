##### Example: MESI on two threads
(Assumption: Cache lines larger than the variables itself - this requires us to do a bit of extra work)

Notation: M$x$, E$x$, S$x$ for M/E/S with value $x$, respectively, and I.

Code [[Week 2 - Cache-Consistency I#Cache Coherence|as before]]:
```c
// Thread A:
void foo(void) {
	a = 1; 			   // A.1
	b = 1;             // A.2
}
// Thread B:
void bar(void) {
	while (b == 0) {}; // B.1
	assert (a == 1);   // B.2
}
```

Mesi execution:

![[mesi-example.png]]


QUESTION: Why read (i.e. wait for RAM) when overwriting it anyway? (In step A.1)

##### Example: Happened-Before Diagram on two threads
For the same execution as above:

![[mesi-example-happened-before.png]]


## Out-Of-Order Stores
Now: Let's introduce some optimizations and see how they impact the behavior!

Problem so far: *stores always stall*, as after a store there is a let of MESI communication going on and the CPU cannot continue computing in the meanwhile.

Extend our so-far machine model to allow for *store buffers* between CPUs and caches:
![[store-buffers.png]]
- stores are not executed immediately; the CPU may continue working while the store buffer handles stores
- local CPU already sees the values of its still-pending stores (youngest if several stores for the same address)
- stores are written from store buffer to cache either FIFO (--> [[#TSO Total Store Order|TSO]]) or unordered (--> [[#PSO Partial Store Order|PSO]])

### TSO: Total Store Order
Total store order formalizes caches with FIFO store buffers.

> **Total Store Order**
> 1. $\sqsubseteq$ is total *wrt. stores*: any $St_i[a], St_j[b]$ are $\sqsubseteq$-comparable
> 2. *Stores* in program order $\leq$ are embedded in $\sqsubseteq$: $$St_i[a] \leq St_i[b] \implies St_i[a] \sqsubseteq St_i[b]$$
> 3. Loads *preceding another operation wrt. $\leq$* are embedded in $\sqsubseteq$: $$Ld_i[a] \leq Op_i[b] \implies Ld_i[a] \sqsubseteq Op_i[b]$$
> 4. A loads value is determined by the latest store *as observed by the local CPU*: $val(Ld_i[a]) = val(St_j[a])$, where $$St_j[a] = \max_{\sqsubseteq} (\{ St_k[a] \mid St_k[a] \sqsubseteq Ld_i[a] \} \cup \{St_i[a] \mid St_i[a] \leq Ld_i[a]\} )$$
> (The value of $St_j[a]$, where $St_j[a]$ is the $\sqsubseteq$-latest store which occurs before the load in $\sqsubseteq$-order or in $\leq$-order; i.e. a local store which is still in the store buffer is seen anyway on the same CPU).

Importantly, TSO does not satisfy one property which SC does satisfy: In TSO,
$$St_i[a] \leq Ld_i[b] \centernot\implies St_i[a] \sqsubseteq Ld_i[b]$$

##### Example: Unexpected Result with TSO
Consider the two threads
```c
// Thread A:
a = 1;
printf("%d", b);
// Thread B:
b = 1;
printf("%d", a);
```

Under sequential consistency assumptions, one would expect either of the outputs "10", "01", or "11". However under TSO assumptions, "00" is also a possible outcome! For example, in this happened-before-realization:

![[happened-before-tso.png]]

##### TSO in Practice: x86 and `mfence`
The x86 architecture uses a TSO memory model. To deal with the missing SC property, i.e. $St_i[a] \leq Ld_i[b] \centernot\implies St_i[a] \sqsubseteq Ld_i[b]$, modern x86 CPUs provide the `mfence` instruction:

$$Op_i \leq mfence() \leq Op_i' \implies Op_i \sqsubseteq Op_i'$$

Therefore, a fence between stores and loads gives sequentially consistent CPU behavior (with the drawback of slowing down the CPU to store-buffer-less levels). These can be optionally used in cases where they are necessary.


### PSO: Partial Store Order
Partial store order formalizes caches with unordered store buffers.

> **Partial Store Order**
> 1. $\sqsubseteq$ is total *wrt. stores*: any $St_i[a], St_j[b]$ are $\sqsubseteq$-comparable
> 2. ==Fenced stores== in $\leq$-order are embedded in $\sqsubseteq$: $$St_i[a] \leq sfence() \leq St_i[b] \implies St_i[a] \sqsubseteq St_i[b]$$
> 3. *Stores ==to the same address==* in program order $\leq$ are embedded in $\sqsubseteq$: $$St_i[a] \leq St_i[a]' \implies St_i[a] \sqsubseteq St_i[a]'$$
> 4. Loads *preceding another operation wrt. $\leq$* are embedded in $\sqsubseteq$: $$Ld_i[a] \leq Op_i[b] \implies Ld_i[a] \sqsubseteq Op_i[b]$$
> 5. A loads value is determined by the latest store *as observed by the local CPU*: $val(Ld_i[a]) = val(St_j[a])$, where $$St_j[a] = \max_{\sqsubseteq} (\{ St_k[a] \mid St_k[a] \sqsubseteq Ld_i[a] \} \cup \{St_i[a] \mid St_i[a] \leq Ld_i[a]\} )$$
 
 In contrast to TSO, PSO weakens the store embedding property: stores wrt. $\leq$ are now only embedded into $\sqsubseteq$ if they are fenced or if they go to the same address.
 
 In particular, under PSO:
 $$St_i[a] \leq St_i[b] \centernot\implies St_i[a] \sqsubseteq St_i[b]$$

##### Example: Unexpected Result with PSO
PSO is now weak enough that our [[Week 2 - Cache-Consistency I|original example]] breaks:
```c
// Thread A
a = 1;
b = 1;
// Thread B
while (b == 0) {};
assert(a == 1);
```

The assert will be false in the following happened-before realization:

![[happened-before-pso.png]]

What happens is that a=1 is written to A's store buffer before b=1, but b=1 is written to the cache earlier and hence B sees the state (b=1, a=0). Not sequentially consistent!

##### PSO in Practice: `sfence()`
In general, overtaking of store messages may be desirable; however if the program assumes sequential consistency between different CPUs, it is rendered incorrect.

The `sfence()` operation can be used to insert a write barrier, which forces stores  currently in the store buffer to be completed before the next store can take place.

Adapted example where the assert cannot fail:
```c
// Thread A
a = 1;
sfence();
b = 1;
// Thread B
while (b == 0) {};
assert(a == 1);
```

## Out-of-Order Loads
A further way to weaken the memory model: *out-of-order Loads*.


### Invalidate Queues
Introduce *invalidate queues*: Incoming "invalidate" messages from other CPUs are not executed immediately, but put into the queue and deferred. MESI messages regarding a cache line are not sent out while there is an invalidate pending for this line, however local loads and stores on that line *are* executed. Therefore, *local loads may load an out-of-date value*.

##### Example: Unexpected Result with Invalidate Queues
With invalidate queues, even the improved original example with `sfence()` may fail:
```c
// Thread A
a = 1;
sfence();
b = 1;
// Thread B
while (b == 0) {};
assert(a == 1);
```

![[happened-before-invalidate-queues.png]]

##### Load Barriers for Invalidate Queues
To enforce sequentially consistent loading behavior with invalidate queues, one can insert *load barriers* `lfence()`. A load barrier forces all invalidates waiting in the queue to be carried out before the next load is performed.

In practice: each *store barrier* in one process should be matched with a *load barrier* in another process.

Inserting a load barrier fixes the example again:
```c
// Thread A
a = 1;
sfence();
b = 1;
// Thread B
while (b == 0) {};
lfence();
assert(a == 1);
```

![[happened-before-invalidate-queues-2.png]]

Inserting load barriers *before each load* gives sequentially consistent behavior.

### Load Buffers
Load buffers are somewhat of an alternative approach to invalidate queues, apparently.

Idea: *defer loads as well*. Namely, introduce a *load buffer* where pending loads are stored, and when the corresponding register is later accessed, the load may have been completed already (and otherwise is waited for). Motivation: For example, consider an expression where several values need to be loaded; by using the load buffer approach, some communication time is saved if the loads are not executed strictly one after another.

##### Load Barriers
Example (left out): Load buffers can lead to another kind of inconsistency. Namely, load accesses may compute "too late" and lead to loads "from the future".

Inserting load barriers *after each load* gives sequentially consistent behavior. Load barriers ensure that the next operation is only executed once all entries in the load buffer have completed.

## RMO (Relaxed Memory Order)
Relaxed memory order: formal model to account for the kind of architectures we have seen.

Watch out: The specific specification depends on the manufacturer, but often manufacturers don't provide such a formal specification (but rather a specification by example).

Here: specification by SPARC architecture.

> **Relaxed Memory Order**
> 1. `mfence`d memory accesses in program order $\leq$ are embedded into memory order $\sqsubseteq$: $$Op_i[a] \leq \texttt{mfence()} \leq Op_i[b] \Rightarrow Op_i[a] \sqsubseteq Op_i[b]$$
> 2. *Stores to same address* in $\leq$ are embedded into $\sqsubseteq$: $$Op_i[a] \leq St_i[a]' \Rightarrow Op_i[a] \sqsubseteq St_i[a]'$$
> 3. Operations *dependent on a load* are embedded into $\sqsubseteq$: $$Ld_i[a] \to Op_i[b] \Rightarrow Ld_i[a] \sqsubseteq Op_i[b]$$
> 4. The value of a load is *determined by the latest store* as observed by the local CPU: $$val(Ld_i[a]) = val(\max_{\sqsubseteq} \{ St_k[a] | St_k[a] \sqsubseteq Ld_i[a] \} \cup \{ St_i[a] \mid St_i[a] \leq Ld_i[a] \})$$


The notion of *dependence* $\to$ is detailed in the specification: 
- A dependence $Ld_i[a] \to St_i[b]$ holds if the load is the *latest load before the store* that has *the same target register that the store has as source register*.
- A dependence $Ld_i[a] \to St_i[b]$ holds if the a conditional branch that comes before the store depends on the load
- (doesn't apply here, but) a dependence $St_i[a] \to Ld_i[a]$ holds if $St_i[a] \leq Ld_i[a]$ (same address access)


## Dekker Algorithm on RMO systems
Two processes want to ensure mutually exclusive access to a critical section. They share three variables: `flag[0]` (P0 has interest in critical section), `flag[1]` (P1 has interest in critical section), `turn` (the process which may enter the section right now).

##### Dekker's algorithm (assuming sequential consistency)
Algorithm for P0:

```C
flag[0] = true;				// P0 wants to enter
while (flag[1] == true) { 	// While P1 also wants to enter:
	if (turn != 0) {		// If it's P1's turn:
		flag[0] = false;    // Signal "no interest", wait until it's P0's turn
		while(turn != 0) { /* busy wait */ }
		flag[0] = true;     // Take flag
	}
}

// critical section
{ /* ... (do the critical work) */ 

// After critical section: Let P1 work
turn = 1;
flag[0] = false;
```

(P1 symmetrically)

##### Dekker's algorithm in RMO
In RMO, the algorithm can fail to achieve the mutual exclusion. We need to insert `sfence()` and `lfence()` in some places:

- insert `lfence()` *before every load* of shared variables
- insert `sfence()` *after every store* of shared variables

```C
flag[0] = true;
sfence();
while (lfence(), flag[1] == true) {
	if (lfence(), turn != 0) {
		flag[0] = false;
		sfence();
		while(lfence(), turn != 0) { /* busy wait */ }
		flag[0] = true;
		sfence();
	}
}

// critical section
{ /* ... (do the critical work) */ }

// After critical section: Let P1 work
turn = 1;
sfence();
flag[0] = false; sfence();
```

The `lfence()` placement in front of loads indicates that the invalidate buffer version of RMO is used in this example.

## Conclusion
Memory barriers: lowest level of synchronization primitives.

- can be used for low-level protocol implementations
- hard to get right, best-suited for well-understood algorithms
- if store/invalidate buffers are a bottleneck, too many fences might be costly

Often better: (OS-offered) locks (better optimized, less low-level, easier to use).


### Memory Models and Compilers
E.g. standard program optimization:

```C
int x = 0;
for (int i = 0; i < 100; i++) {
	x = 1;
	printf("%d", x);
}
```

becomes (via *loop-invariant code motion* and *dead store elimination*)

```C
int x = 1;
for (int i = 0; i < 100; i++) {
	printf("%d", x);
}
```

But what if another process executes `x = 0`, and the intention was to always reset `x = 1` in the loop? -> inconsistent behavior!

The compiler should also depend on consistency guarantees --> demand for memory models on language model.

##### Avoiding compiler optimizations
Way 1: (sfence() keeps compiler from reordering across it)
```C
int x = 0;
for (int i = 0; i < 100; i++) {
	sfence();
	x = 1;
	printf("%d", x);
}
```

Way 2: (volatile keeps C compiler from reordering accesses to this address)
```C
volatile int x = 0;
for (int i = 0; i < 100; i++) {
	x = 1;
	printf("%d", x);
}
```

In Java, even further: barriers generated around accesses of `volatile` variables.