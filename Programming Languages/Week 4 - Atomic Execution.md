## Introduction
Now: more high-level programming

### Atomic Executions
- Concurrent program: multiple resource-sharing threads (resources like memory locations or memory-mapped I/O).
- Invariants must be retained wrt. to the resources, which may be temporarily, locally broken during updates

A sequence of operations that temporally breaks invariants should be *atomic*: The invariant *never seems to be broken*.

Definition: a computation forms an *atomic execution* if its effect can only be *observerd* as a single transformation on the memory.

##### Relationship to Memory Barriers
Barriers are not enough: these act on *single* memory locations.

![[atomic-operation.png]]

Use *barriers* to implement *automata that implement mutual exclusion*.


## Wait-Free Algorithms
"Wait-Free": implementable without arbitrarily long waits.

### Wait-Free Updates
Some examples that are not atomic:

```C
// example 1
i++;

// example 2
j = i;
i = i+k;

// example 3
int tmp = i;
i = j;
j = tmp;
```

Reason: the load/stores, even in the simple `i++` case, might be interleaved with a store from another processor.

They *can be made atomic* by (e.g. on x86) locking the cache bus for an address. Examples:

```nasm
; example 1
lock inc [addr_i]

; example 2
mov eax,reg_k
lock xadd [addr_i], eax
mov reg_j, eax

; example 3
lock xchg [addr_i], regj
```

### Example: Wait-Free Bumper-Pointer Allocation
Simple garbage collector: system manages a contiguous chunk of memory that grows if more is needed; bumper pointer points to one of the edges of memory. Problem: multiple threads -> might have concurrent `alloc` calls.

```C
char heap[1<<20];
char* firstFree = &heap[0];

char* alloc(int size) {
	char* start;
	asm("lock; xadd %0, %1" :"=r"(start),"=m"(firstFree):
		"0"(size),"m"(firstFree) :"memory");
	
	if (start+size > sizeof(heap)) garbage_collect();
	return start;
}
```

### Our Syntax for Atomic Statements
Instead of fiddling with inline assembly, we refer to examples 1-3 above and use the made-up keyword `atomic` to represent that actually, atomic assembly statements should be inserted.

```C
// example 1 (atomic-increment)
atomic {
	i++;
}

// example 2 (fetch-and-add)
atomic {
	j = i;
	i = i+k;
}

// example 3 (atomic-exchange)
atomic {
	int tmp = i;
	i = j;
	j = tmp;
}

// example 4 (set-and-test)
atomic {
	// set flag b to 0, return its previous state
	r = b;
	b = 0;
}

// example 5 (set-and-test)
atomic {
	// set flag b to 1, return its previous state
	r = b;
	b = 1;
}

// example 6 (compare-and-swap)
atomic {
	// Update i to j if i's old value is equal to k
	r = (k==i);
	if (r) i = j;
}

```



## Lock-Free Algorithms
If not wait-free, maybe lock-free?

### Compare-and-Swap for Lock-Free Algorithms
The atomic *compare-and-swap* is commonly used like this:
```C
do {
	k = i; // using memory barriers
	j = f(k);
}
// if i=k still holds, update i to j, else repeat
while (!atomic_swap(i, j, k));
```

(Assumption: $i = k$ implies that $i$ was not updated.)

##### General Recipe
1. Group variables for which invariant must hold and read their $n$ bytes atomically.
2. Compute a new value (using a pure function)
3. Perform compare-and-swap on these $n$ bytes

### Summary: Lock- and Wait-Free Algorithms
Severely limited: e.g. wait-free restricted to semantics of a single atomic operation. For lock-free algorithms, the $n$ bytes of the invariant-abiding data must fit into the space provided by the compare-and-swap mechanism.

Also, the set of atomic operations varies by architecture.

Idea: use these low-level primitives as building blocks for *locks*.



## Locks: Semaphores and Mutexes
A *lock* is a datastructure that can be acquired and released, ensures mutual exclusion, blocks other threads if they try to acquire it while already being locked. They are used to protect critical sections.

### (Counting) Semaphores
A semaphore stores an integer `s` with a `signal` and a `wait` operation.

- `signal` increments the integer by one (releases the resource) atomically
- `wait` busy-waits for the the integer being greater than zero, in one atomic operation doing the check and if it passes, decrementing the integer (acquires the resource).

Special case: $s=1$ => binary semaphore.

##### Implementation without busy waiting
Instead of performing busy-waiting, use the scheduling provided by the OS.

```C
void wait(int *s) {
	bool avail;
	do {
		atomic {
			avail = *s>0;
			if (avail) (*s)--;
		}
		if (!avail) de_schedule(s);
	} while (!avail);
}
```

Here `de_schedule()` inserts the current thread into an OS-managed queue of threads that will be woken up once `*s` becomes non-zero. (Implementation by *monitoring writes* to s: `FUTEX_WAIT`)

The `signal` side has to call a `wake` function:

```C
void signal(int *s) {
	atomic { *s = *s + 1; }
	wake(s);
}
```

##### Real-World Optimizations
- `wait()` might start off by busy-waiting for a few iterations before refraining to the schedule mechanism (good for locks with high throughput)


##### Semaphore with single core
... reduces to 
```C
if (*s) (*s)--; /* critical section */ (*s)++;
```
(apparently even with multiple threads, but only a single core, such a fine-grained atomicity is not required.)

## Monitors
Monitors are automatic- re-entrant mutexes.
- locking procedure bodys is a pattern worth generalizing
- problematic: e.g. for recursive calls where the mutex blocks re-entry

**Monitors**: A procedure associated with a monitor acquires a lock on entry and releases it on exit. If the lock is already taken *by the current thread*, execution may proceed (i.e. no problems with recursive calls).

Still a problem: releasing lock again after last exit of function body by current thread.

### Basic Monitor Implementation
This implementation stores
- a semaphore `count'
- the id `tid` of the occupying thread

```C
typedef struct monitor mon_t;
struct monitor { int tid; int count; };
void monitor_init(mon_t* m) { memset(m, 0, sizeof(mon_t)); }
```

##### monitor_enter
- if already in thread's possession: increment the count
- otherwise more complicated: if monitor available (`tid == 0`), we take the monitor and increase the count. Otherwise, `de_schedule` waiting for the `tid` to change.

```C
void monitor_enter(mon_t *m) {
	bool mine = false;
	while (!mine) {
		mine = thread_id() == m->tid;
		if (mine)
			m->count++;
		else atomic {
			if (m->tid == 0) {
				m->tid = thread_id();
				mine = true;
				m->count = 1;
			}
		};
		if (!mine) de_schedule(&m->tid);
	}
}
```

##### monitor_leave
- Decrement the count
- if count = 0 (i.e. monitor released by current thread), reset the thread ID and call `wake`

```C
void monitor_leave(mon_t *m) {
	m->count--;
	if (m->count == 0) {
		atomic {
			m->tid = 0;
		}
		wake(&m->tid);
	}
}
```



## Condition Variables
With the constructions we have so far, there can still be efficiency problems: Say, a thread waiting for a queue to be filled, and busy-waiting, repeatedly calling `pop()`, until it can obtain an element. This way it produces contention on the lock (a producer might not even be able to `push()` into the queue).

### Monitor with Condition Variables
```C
struct monitor { int tid; int count; int cond1; int cond2; ... };
```

with functions
- `wait`: Waiting for a condition to become true, *temporarily releases* monitor + blocks
	- when signalled: re-acquire monitor (now likely to succeed) and return
- `signal`: signal waiting threads that they may proceed
	- *signal-and-urgent-wait*: signalling thread blocks until signalled thread has released the monitor
	- *signal-and-continue*: signalling thread continues with its work



### Signal-and-Urgent-Wait Semantics
![[signal-urgent-wait.png|500]]

- one queue for each condition
- one enter queue and one suspended queue
- signalled thread can immediately enter the monitor


### Signal-and-Continue Semantics
(here, we say `notify` instead of `signal`)

![[signal-and-continue.png|500]]

- Call to `wait` for a -> go into queue a.q
- Call to `notify` for a -> first thread from a.q goes into e


### Implementation (Simplified)
Here: only signal-and-continue for a single condition variable.

```C
void cond_wait(mon_t *m) {
	assert(m->tid == thread_id()) // assert: we own the monitor (sometimes the compiler can do this)
	int old_count = m->count;
	atomic { m->tid = 0; }
	wait(&m->cond);
	bool next_to_enter;
	do {
		atomic {
			next_to_enter = (m->tid == 0);
			if (next_to_enter) {
				m->tid = thread_id();
				m->count = old_count;
			}
		}
		if (!next_to_enter) de_schedule(&m->tid);
	} while (!next_to_enter);
}
```

- make sure the monitor is acquired, save the (own) count
- release the monitor and wait on the condition variable
- try to re-acquire the monitor, if unsuccessful `de_schedule` and repeat
- if successful: re-acquire monitor and reset count.

```C
void cond_notify(mon_t *m) {
	// wake up other threads
	signal(&m->cond);	
}
```

### Notification Semantics
WIth signal-and-continue semantics, often two function exists:
- `notify` (wake up exactly one thread waiting on the condition variable)
- `notifyAll` (wake up all threads waiting on the condition variable)

Watch out: often `notify` actually has some "notify some" semantics which apparently simplifies the implementation. A thread cannot assume that it is the only one woken up  -> better recheck the condition.


### Java and C# Monitors with Single Condition Variable
Monitors with a single cond. var. are built into Java and C#:

```Java
class C {
	public synchronized void f() {}
}
```

is equivalent to

```Java
class C {
	public void f() {
		monitor_enter(this);
		monitor_leave(this);
	}
}
```

and `Object` contains variables

```
private int mon_var;
private int mon_count;
private int cond_var;
protected void monitor_enter();
protected void monitor_leave();
```

The condition variable can be used within a `synchronized` method.
