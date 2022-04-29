# Cache Consistency (I)
Motivation: consider the following code executed in two threads.
```c
// Thread A:
void foo(void) {
	a = 1;
	b = 1;
}
// Thread B:
void bar(void) {
	while (b == 0) {};
	assert (a == 1);
}
```

Intuitively, the assertion should never fail. In practice, it might fail, given enough iterations, due to *cache consistency* issues! Therefore we need to define a memory model.

## Memory Models
Problem: Memory interactions on the hardware level behave differently if multiple concurrent thread with shared data are involved, including deferred communication of updates.

Memory models trade off freedom of implementation against guarantees about race-related properties.

### Strict Consistency
Strict consistency is the following "idealistic" model:

> **Strict Consistency**
> Independently of which process reads or writes, the value from the *most recent write* to a location is observable by reads *immediately after the write occurs*

Very nice, but too strong to be realistic:
- would need a concept of absolute global time
- physically not possible

### Happened-Before Relations
"Abandoning absolute time": just require *some* pairs of events to happen in a certain order.

- A process $P$ is seen as a series of events $p_1, p_2, \dots$
- Event $p_i$ *happened before* $p_{i+1}$
- If $p_i$ in $P$ sends a message to $Q$, there is an event $q_j$ that receives this message, and $p_i$ *happened before* $q_j$

> **Happened-Before Relation**
> $p$ *happened before* $q$, i.e. $p \to q$, according to the above rules.
> The relation $\to$ is transitive, and furthermore irreflexive, asymmetric, and partial. Therefore it is a *strict partial order*.

> **Concurrent Events**
> Two events $p, q$ are *concurrent* if $p \not\to q$ and $q \not\to p$, i.e. they are $\to$-incomparable.

##### Example: Happened-Before
Consider the example [[#Cache Consistency I|from above]]: Require that
- Thread A stores to a before it stores to b
- Thread B sees b=1 before execute `assert`

But still, several outcomes are possible:

![[happened-before-first-example.png]]


### Event Ordering
A *logical clock* assigns a *logical global time stamp* $C(p)$ to each event $p$.

$C$ satisfies the *clock condition* if for any $p, q$, $p \to q$, $C(p) < C(q)$.

For a distributed system, this means that
- if $p_i, p_j$ are events of $P$, $p_i \to p_j$, then $C(p_i) < C(p_j)$
- if $p$ is sending and $q$ receiving of a message, then $C(p) < C(q)$

If $C$ satisfies the clock condition and has unique timestamps, it induces a total order that embeds the strict partial order $\to$. The set of such $C$ corresponds to the set of sequential executions that one can observe from the system.


## Models for Memory Coherence
Weakening the model: introduce *caches*.
- each process has a cache attached to it, that is quicker to access than main memory and is thought to store data that is accessed often

Each cache line is modeled as one process. (It seems like each variable is drawn as one line). QUESTION: Is that what is meant?

Obviously, a naive implementation of caches creates *races*, and therefore incoherence.

![[cache-lines.png]]

We see the following operations:
- $St[a]$ stores a value to $a$ (from the process registers to the cache, apparently).
- $Ld[a]$ loads a value to $a$
- $Op[a]$ represents any operation on $a$

To denote the accessing CPU, use an index $i$: i.e. $St_i[a], Ld_i[a], Op_i[a]$.

### Cache Coherence
Introduce the notion of a *memory order* $\sqsubseteq$: This orders the memory accesses for each adress $a$. Furthermore, the *program order* $\leq$ is specified by the control flow of the program for each CPU.

> **Cache Coherence**
> 1. Operations in program order $\leq$ are embedded in $\sqsubseteq$: $$Op_i[a] \leq Op_i[a]' \implies Op_i[a] \sqsubseteq Op_i[a]'$$
> 2. For each address $a$, $\sqsubseteq$ is a total order on all accesses to $a$
> 3. A load's value is determined by the latest store wrt. $\leq$ and $\sqsubseteq$:$$val(Ld_i[a]) = val(St_j[a]),\text{ where } St_j[a] = \max_{\sqsubseteq} \{ St_k[a] \mid St_k[a] \sqsubseteq Ld_i[a] \}$$

QUESTION: Doesn't $\leq$ depend on the actual CPU? Wouldn't $\leq_i$ be a more appropriate notation? ==> It appears as if $\leq = \bigcup_i (\leq_i)$.

##### Example: Coherent Cache
Consider this realization of the [[#Cache Consistency I|above example]]:
![[cache-coherency-example.png]]
- A's cache write the value of b to memory before it writes the value of a
- B first sees `b == 1`, then  `a == 0`, and the assertion fails!

However, the example *is* cache-coherent
- for memory location $a$, the read by $B$ comes $\sqsubseteq$-before the write by $A$
- for memory location $b$, $\sqsubseteq$ first has a read by $B$, then a store by $A$, then a read by $B$
- $\leq$-embedding only needs to hold for pairs of accesses to the *same address*

==> Cache Coherence is too weak!

### Sequential Consistency

Sequential Consistency (Lamport, 1978): The result of any execution is the same as if the memory operations where executed in some sequential order, in which the operations of each individual processor appear in the right order.

To show a system is not sequentially consistent, find two executions (total orders $\sqsubseteq$) which both embed $\leq$, but which give different results.

Formally:

> **Sequential Consistency**
> 1. Memory operations in $\leq$-order are embedded in the total order $\sqsubseteq$: $$Op_i[a] \leq Op_i[b]' \implies Op_i[a] \sqsubseteq Op_i[b]'$$
> 2. A load's value is determined by the latest store wrt. $\sqsubseteq$:$$val(Ld_i[a]) = val(St_j[a]),\text{ where } St_j[a] = \max_{\sqsubseteq} \{ St_k[a] \mid St_k[a] \sqsubseteq Ld_i[a] \}$$

Crucial difference to the [[#Cache Coherence|Cache Coherence definition]]: The embedding covers all memory locations at once, instead of each one on its own!

To achieve sequential consistency, caches and memory need to collaborate in a *Cache Coherence Protocol*.

##### Benefits of Sequential Consistency
Sequential consistency covers *all interleavings* that are due to timing variations.

For simple hardware architectures, it is a realistic model: In particular, suitable for concurrent processors that acquire *exclusive memory access*. If processors use caches, they can still be made to maintain sequential consistency.

However for more elaborate hardware (with out-of-order stores): Complex cacheline management optimizations determine what other processors see. In this context, expecting sequential consistency is not realistic.


## MESI Cache Coherence Protocol
The MESI protocol is a distributed system protocol to allow processors to keep track of the latest copy of a value.

Each cache line is attached a state machine:
![[mesi.png]]

- **I**nvalid: cache line is ready for re-use
- **S**hared: other caches have an *identical copy* of this cache line
- **E**xclusive: the content is in this cache only; can be overwritten without caring about other caches
- **M**odified: the content is exclusive *and* has been modified.

To keep the state of cache lines consistent, *messages* are being sent over a message bus.

(slightly simplified) messages of MESI:
- R (read = load from an address) and RR (read response = data for requested address)
- I (invalidate = ask others to evict a cache line) and IA (invalidate ack. =  indicates that a line has been evicted)
- RI: Read + Invalidate ("read with intend to modify"), asks for the contents of a cache line *and* asks to invalidate it afterwards
- WB: Writeback, special form of RR or IA. When in state **M**, an R is received, WB is issued which notifies main memory about the modifications. The same happens if an **I** is received.

![[mesi-messages.png]]
(black = sent; red = received)

Some interesting cases:
- if an **I** cache line requests data from memory, it sends out R, receives RR, and goes in the **E** state
- If an **E** or **S** cache receives an I message, it sends back IA and transitions to **I**
- If an **M** receives R, it transitions to **S**, issuing a WB
- If an **M** receives I, it transitions to **I**, issuing a WB
- **E** transitions into **M** when the CPU writes to a **E** cacheline; no messages need to be issued
- **S** transitions into **M** when an *atomic read-modify-write* is executed on a read-only item; then an I must be issued

An interesting detail: If R messages are sent out, this possibly is responded to quickly by the other CPUs which have the value in their cache; otherwise, it needs to be loaded from RAM, and the response takes longer.