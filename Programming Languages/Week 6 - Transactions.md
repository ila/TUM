# Transactions

## Abstraction and Composition vs. Concurrency
Two software concepts:
- abstraction: using an object without relying on its internals
- composition: combining several objects without interference

"The ability to compose relies on the ability to abstract from details."

### Conflict Concurrency <--> Abstraction/Composition
Concurrency leads to problems with these principles. Example: a linked list that exposes `push()` and `forall()` and a set that provides `insert()` and internally relies on the linked list.

```python
def insert(self, k):
	if self.list.forall(lambda x: x != k)
		push(self.list, k)
```

- assume the list is already threadsafe and uses some mutex internally
- the set now also needs to introduce a mutex because it makes *two* calls to the list
- it should use the *same mutex* as the list to avoid other list operations to be called

QUESTION: How is this an issue, given that the list is an internal of the set, not exposed?

The example shows: While sequential algorithms can always be composed, thread-safe algorithms that are composed may not yield other thread-safe algorithms.


## Transactional Memory
Idea: convert `atomic` blocks --> code that ensures atomic execution
- check that the block runs without conflicts with another thread - if another thread interferes, undo the computation done so far and restart the transaction
- provide `retry` keyword to manually restart

More in later sections.


## Semantics of Transactions
We want the typical "ACID" properties:
- *atomicity*: transaction either runs completely or appears to not have run at all
- *consistency*: if a state is consistent (i.e. certain invariants holds) before execution, the state after the transaction was executed is consistent as well
- *isolation*: different transactions do not interfere with each other
- *durability*: effects of transactions are permanent
	- at least wrt. main memory!

> **Semantics of Transactions**.
> *The result of running concurrent transactions is identical to **one** execution of them in sequence (serializability).*

### Consistency during Transactions, Opacity
ACID does not state what happens before a transactions commits: e.g. a transaction running on an inconsistent state may yield inconsistent states repeatedly (*zombie transaction*).

> **Opacity**.
> *A Transactional Memory system provides **opacity** if failing transactions are serializable wrt. comitting transactions (i.e. still see a consistent view of memory)*

### Models of Isolation
##### Weak and Strong Isolation
- *strong isolation*: order between accesses to transactional and non-transactional memory retained
- *weak isolation*: guarantees only for transactional memory accesses (i.e. inside atomic blocks)

[TODO... Understand this better, find examples. What exactly is the problem in the code on the slides? Apparently, T2 can see intermediate states of T1. Likely the problem is that the value of x is visible before the transaction commits. The problem would be easier to see if x would be changed a second time in T1.]
 
```C
// Thread 1:
atomic {
	x = 42;
}

// Thread 2:
tmp = x;
```

##### SLA: Single-Lock Atomicity
> **Single-Lock Atomicity (SLA).**
> *In the **single-lock atomicity** model, the execution is as if all transaction acquire a single program-wide mutex.*

The SLA model has some disadvantages:
- weak progress guarantees: two completely unrelated transactions that would not interfere at all cannot be executed concurrently.
	- extreme example: one transaction blocks indefinitely, no other transaction can be executed anymore
- SLA is stronger than one would like it to be in practice: e.g. empty atomic blocks can act as barriers and enable "spurious communication"
	- in the following example, the transactions should be independent, but synchronize

```C
// Thread 1:
data = 1;
atomic {}
ready = 1;

// Thread 2:
atomic {
	int tmp = data;
	if (ready) { ... }
}
```

##### Transactional Sequential Consistency (TSC)
Goal: strong isolation should be implemented more flexibly.

> **Transactional Sequential Consistency (TSC).**
> In *transactional sequential consistency*, the accesses within each transaction are sequentially consistent.

TSC gives strong isolation, but allows some parallelism. Example:

![[tsc-example.png]]

TSC is not weaker than SLA, but stronger in some aspects, as accesses within a transaction may not be reordered! In practice, weakened versions of TLC are implemented where race-free reorderings are allowed.

QUESTION: What does this mean in detail? How does this ensure atomicity/isolation? --> Apparently, this definition is not so good: according to a later addendum, a better definition is (du)

> **Transactional Sequential Consistency** (due to https://www.cs.rochester.edu/u/scott/papers/2009_TRANSACT_SI.pdf)
> A system is transactionally sequentially con-  
sistent (TSC) if and only if the result of any realizable exe-  
cution of a program is equivalent to some sequentially con-  
sistent execution of the program in which a transaction’s ac-  
cesses occur contiguously in the global total order of ac-  
cesses

## Software Transactional Memory
Now: how could such a transaction be implemented, in principle? How are the fictional `atomic` blocks translated, and how are there properties guaranteed in the implementation?

### Translating `atomic` blocks
Convert transactional read/write accesses to `x` to calls `ReadTx(&x)`/`WriteTx(&x, val)`. Start transactions with `StartTx()` and commit them with `CommitTx()`.

```C
do {
	StartTx();
	// code with ReadTx and WriteTx
} while (!CommitTx());
```

##### Translation with a preprocessor
Such a TM translation can be done by preprocessing or manually. Several ways:
1. try to minimize the set of transactional memory accesses (requires good static analysis)
2. simpler: translate all global variable and heap access to transactional memory accesses (we do that here)
3. Manual translation for more fine-grained control

##### `retry` keyword
A `retry` keyword might be provided: aborts and restarts the transaction. The transaction can wait with restarting until one of the variables it already read (the *read set*) changes.

Similar: [[Week 4 - Atomic Execution#Condition Variables|Condition variables]] in monitors.

### Implementation Techniques
Software TMs allocate *transaction descriptors* that store details about the transaction.

- *Undo logs* vs. *redo logs*:
	- *undo log*: all writes that have to be *undone* if the commit fails
	- *redo log*: all writes that are postponed until the commit
- *Read set* and *write set*: 
	- Store the locations accessed (read from/written to) so far
- Read and write version
	- Timestamp (of logical clock) of when the value was accessed

One point made in the video: the system must decide on the granularity of memory locations. If it distinguishes every address as distinct memory location, the overhead of managing these locations might be too high. One alternative is to e.g. consider the set of adresses allocated by a single `malloc` as one location, as they are likely to belong to the same object in the program logic.

## TL2: Example of an STM implementation
*...from the Scalable Synchronization research group at Sun Microsystems Laboratories, as featured in the DISC 2006 article "Transactional Locking II".*

### Properties of TL2
The TL2 system
- ...provides *[[#Consistency during Transactions Opacity|opacity]]*
- ...uses *lazy versioning*: writes stored in redo log and performed on commit
- ...*validating conflict detection*: accessing a modified address --> abort

### Functioning of TL2
1. on starting the transaction: obtain the *read version*, a global counter (timestamp)
2. reads `ReadTx` during the transaction:
	- watch out that no access to newer versions is performed
		- if an object version is younger during a read, abort the transaction
		- also abort if the object is locked
	- if the read goes through, add the accessed memory address to the read set.
3. writes `WriteTx` during the transaction:
	- add/update the location in the *redo log*
4. committing `CommitTx` the transaction:
	1. pick up locks for all the objects
	2. increment the global version
	3. check the read objects for being up to date
	4. if everything goes through, write redo log entries to memory and update their version; otherwise, the transaction fails
	5. release all locks

##### Opacity in TL2
Opacity is guaranteed because transactions abort when read-accessing inconsistent values.

##### Locks and Preemption in TL2
Since writes need locks, deadlocks are still possible in principle. However
- transactions can be *preempted* --> can break deadlocks
- a lock order might be enforced since the lock accesses are generated automatically --> can prevent deadlocks

Locks still lead to the problem of contention on the global clock.

## Challenges when using Software Transactional Memory
1. Unnecessary/suboptimal abortion of transactions: E.g., a very big transaction aborts on the last line because another very short transactions just changed a single value. Especially easy to happen if the granularity of transactions is too large.
```C
// Thread 1
atomic {
	... // 100 lines of code 
	int r = ReadTx(&x);
}

// Thread 2
atomic {
	WriteTx(&x, 42);
}
```

2. Contention because of lock-based commits
3. Danger of live locks (A aborts B, B reruns and aborts A, A reruns and aborts B, etc.)


### Integration of Non-Memory Resources
Problems when accessing other resources than memory inside atomic blocks. E.g. storage management, condition variables, `volatile` variables, I/O.

The semantics should be as if [[#SLA Single-Lock Atomicity|SLA]] or [[#Transactional Sequential Consistency TSC|TSC]] semantics were implemented. There are some ways to handle this problem:

- "prohibit it": compiler should reject certain constructs
- "execute it": abort if actual IO happens (sometimes it might just go to a buffer, which possibly is no problem)
- "irrevocably execute it": to deal with operations that are not undoable: once such an operation is executed, enforce that its transaction must terminate by making all other transactions conflict.
- "integrate it": Rewrite resource accesses to be transactoinal (error logging; writing data to a file; etc...)

Currently: best to use TM for memory only, use irrevocable transactions for other resources.