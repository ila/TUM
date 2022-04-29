## Deadlocks
Deadlock: Two processes are waiting for the respective other to finish. More generally, a set of processes are cyclically waiting for each other to finish.

Example:

```Java
class Foo {
	public Foo other = null;
	public synchronized void bar() {
		other.bar();
	}
}

// in main:
Foo a = new Foo(), b = new Foo();
a.other = b; b.other = a;
// fictional syntax for "execute in parallel": ||
a.bar() || b.bar();
```

### Four Condition for Deadlocks
Deadlocks occur <=> following four conditions hold:
1. *mutual exclusion*: exclusive access to resources required
2. *wait for*: waiting for resources while holding on to others
3. *no preemption*: not possible to take resources away from processes
4. *circular wait*: processes wait in a cycle

### Treatment of Deadlocks
- ignoring
- detecting (OS checks for a cycle; but needs ability to *preempt* to resolve)
- preventing (just design your programs to have no deadlocks)
- avoiding (additional information allows OS to schedule threads in a way s.t. no deadlocks occur)

==> *Prevention* is the only feasible way on standards OSs.

## Lockset Analysis: Deadlock Prevention through Partial Order
Idea: break the circular wait condition by partially ordering locks.
- denote by $\mathcal{L}$ the set of all locks
- for a program point $p$, denote by $\lambda(p) \subseteq \mathcal{L}$ the *lock set at p*: The set of all locks that may be in the acquired state at $p$
- for locks $l, l'$ define $l \lhd l'$ iff $l \in \lambda(p)$ and the statement at $p$ is a statement that acquires lock $l'$
	- here: `wait(l')` or `monitor_enter(l')`
- define the *lock order* $\prec$ as transitive closure of $\lhd$


### Freedom of Deadlocks with $\prec$
Partition $\mathcal{L} = \mathcal{L}_S \cup \mathcal{L}_M$ into semaphore (mutex) locks and monitor locks. Now deadlock-freedom is related to irreflexivity of $\prec$.

**Theorem** (freedom of deadlocks, semaphores only). *If $\mathcal{L} = \mathcal{L}_S$ and for all $a \in \mathcal{L}_S: a \not\prec a$, the program is free of deadlocks.*

More generally for monitors:

**Theorem** (freedom of deadlocks, both semaphores and monitors). *The program is free of deadlocks if  $$\forall a \in \mathcal{L}_S: a \not\prec a$$
and $$\forall a \in \mathcal{L}_M, b \in \mathcal{L}: a \prec b \wedge b \prec a \Rightarrow a = b.$$*

##### Summarizing instances of locks
It might be not statically possible to track all locks because there might be an unknown instances of locks (runtime-dependent). Lower-precision approximation: just summarize multiple instances of a class of locks into one *summarized lock/monitor*. Now if $\tilde{a} \not\prec \tilde{a}$ holds for all summarized locks, we are still fine. However if $\tilde{a} \prec \tilde{a}$, the program could be deadlock-free anyway (lower precision).

### Deadlock Prevention Tactics
- keep an array of locks, only lock in increasing array index sequence
- or: if $l \in \lambda(p)$ and $l' \prec l$ is to be acquired, release $l$ first, acquire $l'$, reacquire $l$. This is quite inefficient.

## Practical Example of Locksets/Lockset Order
Example here: lock instances whose object they lock on are created *on the same line* are summarized into one lock in $\mathcal{L}$.

![[lockset-example.png]]

- line 6: $\lambda(6) = \emptyset$. he synchronized bar calls
- line 8: `this` might be lock objects $l_0$ or $l_1$. $\lambda(8) = \emptyset$.
- line 9, monitor is being entered: $\lambda(9) = \{l_0, l_1\}$ because of that
-  line 12: $\lambda(12) = \{l_0, l_1\}$
-  line 8 again: $\lambda(8) = \{l_0, l_1\}$
- line 9: $l_0 \lhd l_1$ and $l_1 \lhd l_0$.


## Summary: Locks
### Atomic Execution <==> Locks
The `atomic` blocks we introduced cannot be easily translated to locks. Some difficulties:
- an atomic block nested inside another atomic block should have no additional effect

It is non-standard in programming languages to have `atomic` blocks that automatically create locks, for a few reasons:
- use one global lock or several locks?
- some pairs of atomic blocks that access different variables would not require mutual exclusion
- locking on variables dangerous due to danger of deadlocks if one atomic block has another variable access order than another
- decrease in performance with many unnecessary locks


### Concurrency Constructs in C/C++/Java/C#
![[concurrency-constructs-languages.png]]

### Classification of Concurrent Algorithms
- **Wait-free**
	- never block, deadlock or starve and always succeed
	- very limited expressiveness
- **Lock-free**
	- never deadlock or starve, ==may starve and may fail==
	- more expressive, but invariant must fit in little memory (Intel: 8 byte)
- **Locking**
	- ==may deadlock==
	- can guard arbitrary code; several locks allow more fine-grained concurrency
	- can use semaphore (not re-entrant) or monitors (re-entrant)

