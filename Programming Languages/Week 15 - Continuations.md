# From Gotos to Continuations
## Control Flow in C

In `C`, the simplest "irregular" way to change the control flow is via `goto`s:

```c
for (int i=0;i<15;i++)
	for (int j=0;j<15;j++)
		if (a[i] == a[j]) goto exit;
exit:
	return;
```

This is similar to `break` or `continue`, but a little more "irregular" and frowned upon. Still, `goto`s are somewhat restricted as they only allow to jump around within the same method (*scoped procedure-locally*).

### Stack-Backward Control Flow: `longjmp`

C also allows jumps accross procedure boundaries, in the sense of moving from child procedure calls to ancestor procedure calls, i.e. in the stack-upward direction: The methods for this are `setjmp` and `longjmp`


##### `longjmp` and exception handling
With these two commands we can mimick something similar to exception handling in higher-level languages. However this is not "proper" exception handling: For example, heap objects might be leaked since there is no mechanism to free them.

In other languages where this is properly implemented, e.g. C++: *stack unwinding*, and using tables of exceptions a method can catch and a cleanup table appended to the method body by the compiler. 

In detail: if an exception occurs, the runtime first checks if some method up the call hierarchy can catch it; if yes, detailed unwinding starts, if not, the program terminates immediately.

If cleanup is necessary, a so-called *personality routine* uses the cleanup table for the current method and runs destructors for allocated objects.

##### Example of `longjmp`/`setjmp`
```c
#include<setjmp.h>
#include<stdio.h>

int fun(jmp_buf *error_handler, int number) {
	printf(" fun num=%d |", num);
	if (number > 0)
	// Call is made, but the value will never be returned
		return fun(error_handler, number-1);
	longjmp(*error_handler, 2);
}

void main() {
	int x = 4;
	jmp_buf context;
	// 0 in the initial run, and 2 after each longjump
	int r = setjmp(context);
	x -= r;
	printf("In main: x = %d\n", x);
	if (x > 0) fun(&context, 5);
}
```

Output: 
```
In main: x = 4
 fun num=5 | fun num=4 | fun num=3 | fun num=2 | fun num=1 | fun num=0 |
In main: x = 2
 fun num=5 | fun num=4 | fun num=3 | fun num=2 | fun num=1 | fun num=0 |
In main: x = 0
```

The second argument of `longjmp` is the return value which then the `setjmp` command returns.

### "Same-Level" Control Flow: `swapcontext`
Other than the jumps restricted to the current stack we just saw, C also supports more flexible switching between "siblings":

- create a fresh context with `getcontext()`
- make context point to a particular function with `makecontext()`
- swap into the created context, thereby starting execution of the other function, with `swapcontext`
	- takes `ucontext_t *oucp` where it stores the current context, and `ucontext_t *ucp` which it activates
	- returns -1 on error, otherwise returns 0 as soon as `oucp` is activated again

Some drawbacks:
- a stack for the contexts has to be manually created, and its size be known
- scheduling of termination depends on definition of a successor context
- the functions are a bit fake: The behavior is not much different from having one big function body and `goto`-ing around within it.


##### Example: `swapcontext`
![[swapcontext-example.png]]
- main creates contexts for the other two functions
- main swaps to f2
- f2 swaps to f1
- f1 swaps to f2, which continues and returns to f1
- f1 continues and returns to main


## Coroutines
Couroutines: technique implemented in several languages in some form (there is no "hard definition" of what Coroutines are, exactly).

### Stackless Coroutines in EcmaScript 6+
EcmaScript 6+ implements *generators*:
- `yield` returns a value, but later execution can restart from that point
- generator function bodies marked with a `*`, namely with `function*() {...}`

```js
var genFn = function*(){
	var i = 0;
	while (i < 10) {
		yield i++;
	}
};
var gen = genFn();
do {
	var result = gen.next();
	console.log(result.value);
} while (!result.done);
```

The coroutine lives besides the normal function call stack (*stackless coroutine*).

Only the function body can yield for a generator, i.e. we cannot simply call another function which then yields for this generator as well. This is good, as another function call would go on the stack, and yielding from there would mean that the function on the stack could be overwritten.

![[stackless-coroutines.png|400]]

### Stackful Coroutines in Lua
Lua is more liberal with its coroutines: `yield` can be called from any called function as well. In consequence, coroutines need their own stack.

```lua
function func1 ()
  coroutine.yield(1)
end

function func2 ()
  func1()
  coroutine.yield(2)
end

coro = coroutine.create(func2)
coroutine.resume(coro) -- yields value 1
coroutine.resume(coro) -- yields value 2
```

![[stackful-coroutines.png]]


## Continuations in Haskell
Haskell allows to change the "control flow" very freely via so-called *continuations*.

### Continuation Passing Style
In *continuation passing style* (CPS), functions $f :: a \to b$ are transformed to functions $f' :: a \to ((b \to c) \to c)$, where $f'~x~cont$ computes $cont~(f~x)$, given a *continuation* $cont :: b \to c$.

$f'~x$, with a continuation not yet specified, can be seen as a *suspended computation*.

Additionally to count as a CPS-style function, $f'$ should internally rely only on CPS-style functions.


##### Example: Pythagoras in CPS
Direct style:
```haskell
square x = x * x
add x y = x + y
pythagoras x y = add (square x) (square y)
```

Continuation Passing Style:
```haskell
square_cps x = \k -> k ((*) x x)
add_cps x y = \k -> k ((+) x y)
pyth_cps x y = \k ->
	square_cps x (\x2 ->
		square_cps y (\y2 ->
			add_cps x2 y2 k))
```

("After we obtain $x^2$, then after we obtain $y^2$, then after we add $x^2 + y^2$, we perform $k$ with the result").

##### Example: CPS and Higher-Order Functions
Consider a function `trip` that applies a function $f$ thrice:
```haskell
trip f x = f (f (f x))
```

In CPS, we get:
```haskell
trip_cps :: (a -> ((a -> r) -> r)) -> a ((a -> r) -> r)
trip_cps f_cps x = \k -> f_cps x (\fx -> 
	f_cps fx (\ffx ->
		f_cps ffx k ))
```

### Tail Call Optimization
Interlude: "Is this even remotely efficient?"

Functional compilers can make use of *tail call optimization* to make patterns with repeated function calls more efficient. Idea: If a function call is the last thing a procedure does, recycle the stack frame.

![[tail-call-optimization.png]]

Something like 

```haskell
f 0 = 0
f n = n + f (n-1)
```

boils down to more or less a loop, which is of course quite efficient!

### Composing Continuations
Define a type 
```haskell
newtype Cont r a = -- ... 
-- roughly like this: Cont { runCont :: (a -> r) -> r }
-- but it seems like the actual definition is more complicated
```

(such a type is defined in `Control.Monad.Cont`).

The operations are $cont~f$ which wraps a function $f :: (a \to r) \to r$ into a suspended computation $Cont~r~a$, and $runCont~c~k$ which executes the suspended computation $c::Cont~r~a$ with the continuation $k :: a \to r$ and returns $r$.

One could now define a composition function to chain execution of continuations, like this:
```haskell
compose' s f = cont (\k -> runCont s (\x -> runCont (f x) k))

-- could be used like this:
square x p = p (x * x)
runCont (compose' (square 3) square) id
```

In reality, this is implemented as a monadic bind! `Cont r` is instantiated as a `Monad`.

##### Interlude: Monad
Monads, i.e. instance of the `Monad` typeclass, define at least a function `return` and a function `>>=`:
```haskell
class Monad m where
	return :: a -> m a
	(>>=)  :: m a -> (a -> m b) -> m b
```

Monads allow *do-notation*, a syntactic sugar to allow an imperative style:
```haskell
mothersPaternalGrandfather s = do
	m <- mother s
	gf = father m
	father gf

-- or, desugared:
mothersPaternalGrandfather s =
	mother s >>= (\m -> 
	father m >>= (\gf -> 
	father gf))
```

#### `Cont` as Monad
Implementation as a monad:
```haskell
instance Monad (Cont r) where
	return x = cont (\k -> k x)
	s >>= f  = cont (\k -> runCont s (\x -> runCont (f x) k))
```

##### Example: CPS Pythagoras in `Cont` Monad
```haskell
add_cont x y = return (x+y)
square_cont x = return (x*x)
pythagoras_cont x y = do
	x2 <- square_cont x
	y2 <- square_cont y
	add_cont x2 y2
```

### Call with Current Continuation
The function `callCC` (*call with current continuation*) calls a function with the current continuation as its argument: This allows to escape from a computation.

```haskell
callCC :: ((a -> Cont r b) -> Cont r a) -> Cont r a
callCC f = cont (\h -> runCont (f (\a -> cont (\_ -> ha a_)) h ))
```

Idea: *we ignore the current computation stream and reconnect to the original continuation point of callCC!*.

##### Simple `callCC` examples
```haskell
import Control.Monad.Cont
main = runCont (do
	z <- callCC (\c -> do
		a <- (c 3)
		-- this part is skipped:
		return (a + 3)
	)
	return (z + 1)
	) print
```

Here, the continuation $c$ is used as an escape hatch: instead of returning $a+3$, 3 is returned and assigned to $z$, the end result being 4.

##### While loops with `callCC`
We can simulate while loops: (some Monad hackery is needed to combine the `Cont` and `IO` Monad, hence )

```haskell
import Control.Monad.Class
import Control.Monad.Cont

main = flip runContT return $ do
	lift $ putStrLn "A"
	(k, num) <- callCC (\c -> let f x = c (f, x)
							  in return (f, 0))
	lift $ putStrLn "B"
	lift $ putStrLn "C"
	if num < 5
		then k (num+1) >> return ()
		else lift $ print num
```

This is basically a loop of the form
```c
print("A");
int num = 0;
do {
	print("B");
	print("C");
} while(num++ < 5);
print(num);
```

What happens in detail:
- `k` is assigned the function `f` that calls the continuation with itself and its argument as output
- hence when `k` is called, we jump to the program point where callCC is called, and the argument of `k` is stored in `num+1`

The calling site of `callCC` marks the point of program continuation!

### Implementing Continuations
Continuations pose some implementation difficulties:
- they can escape the current context (function frame): calling a continuation restarts execution at the original `callCC` site/function frame
- `callCC` continuations are multi-shot in principle: they may return to the same site multiple times

Traditional stack-based frame management is not good enough here, since old function frames are overwritten! Something more flexible, maybe more like a graph, is needed.


### Outlook: More Techniques
We saw just a glimpse of what can be done with continuations. Further possibilities are
- Delimited/Partial Continuations
- Y Combinator
- SKI Calculus