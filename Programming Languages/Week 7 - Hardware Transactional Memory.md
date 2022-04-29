# Hardware Transactional Memory

## Overview: Hardware Transactional Memory
Motivation: Maybe we can do perform transactions on hardware instead in software, and have them be more efficient! Rough idea:
- track read and write sets in hardware
- hook into the *cache* messaging functionalities that have to track accesses anyway:
	1. cache line in *[[Week 6 - Transactions#Implementation Techniques|read set]]* is *invalidated* ==> transaction aborts
		- because then someone else produced a newer version of a value that was read
	2. cache line in *[[Week 6 - Transactions#Implementation Techniques|write set]]* is *written back* ==> transaction aborts
		- because then someone else tried to access the value updated by the transaction

In principal, two approaches: Explicit and Implicit transactional memory.

##### Explicit Transactional Memory
Parallel to the operations in [[Week 6 - Transactions#Software Transactional Memory|software transactional memory]]: Each transactional access is explicitly marked as transactional, similar to `StartTx`, `ReadTx`, `WriteTx`, `CommitTx`.

Problem when composing code: Calling some non-transactional library function from the transaction may break the transaction guarantees, because the library function fails to use the correct transactional commands!

##### Implicit Transactional Memory
Mark only beginning and end of transaction - the hardware internally keeps track if it is currently in *transactional mode* or not, and interprets the usual instructions in the corresponding mode.

Hardware access, OS calls, page table changes etc. all abort a transaction, because the go around the cache!

Implicit TM provides strong isolation

### Example Implementations of Hardware TM
##### AMD Advanced Synchronization Facilities (ASF)
ASF was an AMD project which was never put into production.

Functioning: define *speculative region* in memory, which is treated transactionally. Instructions to explicitly transfer data between normal memory and the speculative region.

##### Intel TSX in Broadwell/Skywell architectures
TSX by Intel was put into production: However, soon correctness and security bugs appeared, which forced Intel to publish firmware updates and disable many features again. Today, only exists on server systems, not on desktop/mobile processors.

Functioning: [[#Implicit Transactional Memory]], tracks read/write sets using a *transaction bit* on cache lines. CPU state can be backed up to reserved space. Transactions may abort due to lack of resources.

Two Software Interfaces by Intel to TM, treated in detail in the next sections:
1. Restricted Transactional Memory (RTM)
2. Hardware Lock Elision (HLE)

## Restricted Transactional Memory
### Hardware Implementation of RTM
Only a few things have to be modified:
- a nesting counter $C$ and a backup register set is added to the CPU registers
	- $C$ counts how deeply nested the current transaction is
- to each cache line, a bit $T$ is added, marking it as part or not part of a transaction

Transactional instructions:
- `xbegin` increments $C$, if $C=0$ it backs up registers and flushes buffer
- `xabort` clears all $T$ flags, the store buffers, invalidates TM lines, sets $C=0$ and restores CPU state
- `xend` decrements $C$ and, if $C=0$ (i.e. outermost transaction), clears the $T$ flags and flushes the store buffer

Different behavior of normal instructions inside a transaction:
- read or write accesses to a cache line set $T = 1$
- a MESI *invalidate* for a cache line that has $T=1$ leads to `xabort`
- a MESI *read* for a *modified* cache line that has $T=1$ leads to `xabort`

### Software Interface to RTM
Instructions:
1. `xbegin`: 
	- on transaction start: skips to next instruction
	- address can be specified where to continue at abort
	- on abort, error code is stored in eax
2. `xend`: commits the transaction started by the most recent `xbegin`
3. `xabort`: aborts whole transaction
4. `xtest`: checks if processor is in transaction mode

There is a library function `_xbegin()` that wraps the `xbegin` instruction:
- returns a code that indicates whether the transaction successfully started
- **very important**: if the transaction later aborted, *execution jumps back to the return from `_xbegin()`, now with an error code!*

Therefore, transactional code should be structured like this:

```c
if(_xbegin() == _XBEGIN_STARTED) {
	// transaction code
	_xend();
} else {
	// non-transactional fall-back
	// is executed EITHER if transaction failed to start OR if it was aborted
}
```

### Considerations for the Fallback Part
One should make sure that the fallback part is free of races as well, and in particular *also isolated from the actual transaction*. We can use a technique with a mutex:

```c
if(_xbegin() == _XBEGIN_STARTED) {
	if (!mutex > 0) _xabort();
	// Do the transaction
	_xend();
} else {
	wait(mutex);
	// Do the fallback code
	signal(mutex);
}
```

The trick is: even though the transactional part does not actually *take the mutex*, the mutex is added to the transaction's *read set* and if someone else takes the mutex in the meanwhile, the transaction will abort.


##### Pattern for Transactional Mutexes
Idea: The pattern seen above can be generalized to allow transaction-based mutexes that fall back to regular mutexes in case of a race.

```c
void update() {
	lock(&mutex);
	// Do the critical section
	unlock(&mutex);
}
void lock(int *mutex) {
	if(_xbegin() == _XBEGIN_STARTED) {
		if (!*mutex > 0) _xabort();
		else return;
	} wait(mutex);
}
void unlock(int* mutex) {
	if (!*mutex > 0) signal(mutex);
	else _xend();
}
```


## Hardware Lock Elision
Idea: use the existing [[#Restricted Transactional Memory]] infrastructure to do an optimistic optimization of mutex locking, resembling the optimization shown in [[#Pattern for Transactional Mutexes]].

By default, actual acquisition of the lock is deferred, and just tracked with the HTM system; in case there is a conflict, fall back on actual locking. This is supported by some hardware additions. Legacy code is supported: locally, it looks as if the semaphore value *was* modified.

General functioning:
- prefix lock-acquiring instructions with `xacquire`
- prefix lock-releasing instructions with `xrelease`

The codes for these annotations are chosen such that they map to NOPs on older platforms, i.e. there just the usual locking takes place.

### Implementing Lock Elision
Addition: a buffer for elided locks that lives besides the store buffer.

1. `xacquire` ensures shared/exclusive cache line state with $T$ bit set, issues `xbegin` *and keeps the modified lock value in the elided lock buffer*
	- if the $T$ cache line is later invalidated, the transaction is aborted
	- the local CPU sees the modified value of the lock due to the elided lock buffer kicking in
2. The fallback path in case of a conflict leads to the actual locking instruction that was prefixed with `xacquire`: Fallback behavior = conventional locking
3. `xrelease` ends the transaction and clears the elided lock buffer

## Transactional Memory in Practice
GCC has support:
- translates accesses in `__transaction_atomic` regions into library calls to `libitm`
- `libitm` provides TM implementations: uses HTM instructions on TSX systems, otherwise resorts to STM

C++20 standardizes `synchronized`/`atomic_...` blocks

OpenJDK support increasingly introduced (introduced in the VM implementation)

Hardware lock elision support is limited.

## Outlook: Other principles for Concurrent Programming
1. non-blocking message passing (aka the actor model): Program consists of actors that send messages, actors have message queues. 
	- example: Erlang. Also, add-ons to existing languages that support such a model
	- also partly in Rust with channels?
2. blocking message passing (CSP, $\pi$-calculus, join-calculus)
	- examples: Occam, Occam-$\pi$, Go
3. (immediate) priority ceiling
	- *processes* and *resources*, processes have *priority*
	- resource has maximum ("ceiling") priority of all processes that may acquire it
	- process priority at runtime = maximum of priorities of held resources
	- process with maximum run-time priority executes 