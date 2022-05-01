# Metaprogramming
Metaprogramming - guiding principle:

> "Let's write a program which writes a program"

In this category belong Compilers and Preprocessors, but also Reflection, the Metaobject Protocol, Macros and the most extreme form: Runtime Metaprogramming.

Some motivation: in [[Week 13 - Aspect-Oriented Programming in AspectJ#Aspect-Oriented Programming|Aspect-Oriented Programming]], we saw how program code is programatically refined (i.e. existing code "treated as data that is processed"). Now we go one step further.

## Codegeneration Tools
In compiler construction: tools that convert domain-specific languages to source code. This can be seen as the most simplistic way of metaprogramming.

### Codegeneration Example: `lex`.
Example: `lex`, which generates a C code representation of a finite automaton corresponding to some regular expressions.

```c
%{ #include <stdio.h>
%}
%%
[0-9]+	{	printf("integer: %s}n", yytext); }
.|\n	{	/* ignore */					 }
%%
int main(void) {
	yylex();
	return 0;
}
```

### Compiletime Codegeneration: C Macros
C macros are expanded by the *C Preprocessor* (CPP), which is just a simple string rewriting system.

```C
#define min(X,y) ((X < Y)? (X) : (Y))
x = min(5,x);
x = min(++x,y+5); /* ++x evaluated twice! */
```

Some drawbacks of C macros:
- parts of macros can bind to outside operators, if not properly parenthesized!
- unlike for function calls, side effects of parameter expressions might be recomputed several times
- no recursive behavior possible (rewriting stops)
- possibly duplicated identifiers (non-[[#Hygienic Macros in Lisp|hygienic]]), since identifiers are hard-coded in the macros

### Adding Language Constructs via Macro Hackery
With macros, one can in principle define new control structures, by exploiting the binding behavior of existing control structures. For example, one could syntax for the [[Week 6 - Transactions#Translating atomic blocks|ATOMIC block]]:

```c
ATOMIC (globallock) {
	/* user code */
}
// Should translate to
acquire(&lock);
{ /* user code */ }
release(&lock);
```

Macros don't allow something like this at first sight, due to their grammar; However, the following hack works. The key is to allow the usercode block that follows the macro to be executed after some preprended and before some appended code, with a combination of `if`/`while`/`break`/`goto`.

Preprending code:
```c
if (1) {
	/* prepended code */
	goto body;
} else body:
	{ /* usercode block */ }
```

Appending code:
```c
if (1)
	goto body;
else
	while (1)
		if (1) {
			/* appended code */
			break;
		}
	}
	else body:
	{ /* usercode block */ }
```

Both techniques combined:
```c
if (1) {
	/* prepended code */
	goto body;
} else
	while (1)
		if (1) {
			/* appended code */
			break;
		}
		else body:
		{ /* usercode block */ }
```

![[custom-control-structures-macros.png]]

##### Macro for custom control structures
```c
#define concat_( a, b) a##b
#define label(prefix, lnum) concat_(prefix,lnum)
#define ATOMIC (lock)	       \
if (1) {				       \
	acquire(&lock);	  	       \
	goto label(body,__LINE__); \
} else	                       \
	while (1)	               \
		if (1) {	           \
			release(&lock);    \
			break;	           \
		}	                   \
		else		           \
		label(body,__LINE__):
```

One trick: the labels for the goto are generated dynamically using the line number `__LINE__`. Therefore, not two atomic blocks may be placed in the same line.

## Homoicononic Metaprogramming
Homoiconic = "with the same representation". More formally: in a homoiconic language, the primary representation of programs *is also* a data structure in a primitive type of the language itself.

> data is code <--> code is data


### Reflection
*Type introspection* allows to examine types of objects at runtime. Java has type instrospection via `instanceof`:

```java
public boolean isNatural(Object o) {
	return (o instanceof Natural);
}
```

Furthermore, the Java Reflection API allows to analyze the class structure at runtime:

```java
static void fun(className) { // e.g. "Natural"
	// ... but the static type is of course Object
	Object incognito = Class.forName(className).newInstance();
	Class meta = incognito.getClass();
	Field[] fields = meta.getDeclaredFields();
	for (Field f : fields) {
		Class fieldClass = f.getType();
		Object fieldValue = f.get(incognito);
		if (fieldClass == boolean.class && Boolean.FALSE.equals(fieldValue)) {
			return true;
		}
	}
	return false;
}
```

### Metaobject Protocol (Lisp CLOS)
The Lisp CLOS *metaobject protocol* allows to manipulate the implementation of CLOS and change aspects of
- class, object, property and method creation
- causing inheritance relations
- creation of specializers (e.g. overwriting, multimethods)
- ...and many more aspects.

### Homoiconic Metaprogramming in Lisp
Common Lisp (Clisp) offers homoiconic metaprogramming, and especially allows elaborate macro programming!

##### Lisp language basics
*S-Expressions* are either atoms, or concatenate S-expressions $x, y$ in the form $(x \cdot y)$.
- Shortcut: $(x_1~x_2~x_3) = (x_1 \cdot (x_2 \cdot (x_3 \cdot ())))$

*Forms* are units that can be evaluated:
- numbers, keywords, empty lists, nil (fully evaluated)
- symbols (i.e. variables)
	- a symbol evaluates to the value it is bound to
- lists, and among these:
	- special forms (first element a certain keyword)
		- evaluates to a value
	- macro calls (first element a macro name)
		- is converted to another form
	- function calls (first element a function name)

Some *special forms*:
```lisp
(if test then else?)
(let [binding*] expr*)		 ; introduce a local binding
(eval-when (situation) form) ; evaluates the form in the situation
(progn form*)				 ; evaluates form and returns last value
(quote form)				 ; yields unevaluated form (quotes it)
(setq {var form}*)			 ; evaluates form and assigns it to variable var
```


##### Quoting in Lisp
Forms are directly interpreted, *unless they are quoted*! The syntax for quoting is `'` or `quote`, e.g. `(quote (let))` or `'(let)`.

Backquote-comma syntax to escape from quotes:
```lisp
`((+ 1 2) ,(+ 1 2))
; gives ((+ 1 2) 3)
```
QUESTION: how does the comma syntax work exactly?

#### Lisp  Macros
"Macros are configurable syntax/parse tree transformations". A guiding principle: Very few basic special forms, rest of language functionality is implemented in macros.

As an example, the for loop is implemented in a macro:
```lisp
(macroexpand
 	'(loop for y in '(1 2 3) do (print y))))
; this expands to multi-line list code that looks quite complicated
```

Example of how macros can be written, in this case a macro that converts an infix to a prefix expression:

```lisp
(defmacro infix (fpar spar tpar)
	"converting infix to prefix"
	`(,spar ,fpar ,tpar))

(infix 1 + 2)                ; evaluates to 3
(macroexpand '(infix 1 + 2)) ; evaluates to (+ 1 2)
```


##### Example: Factorial Macro
```lisp
(defmacro fac (n)
	if (= n 0)
		1
		`(* ,n (fac ,(- n 1)
))))
```

Example usage:

```lisp
(macroexpand '(fac 4))
; gives (* 4 (fac 3))
(macroexpand-all '(fac 4))
; gives (* 4 (* 3 (* 2 (* 1 1))))
```

Takeaway: Macros expand at compile-time!

##### Macros: Why Bother?
Macros = static AST transformations <--> Functions = runtime control flow manipulations.

Some more differences of macros <--> functions:
- macro parameters are uninterpreted and not necessarily valid expressions
- function parameters evaluate once


##### Hygienic Macros in Lisp
In Lisp: to avoid *shadowing* of variables, lisp offers automatically generated symbols.

```lisp
(defvar varia)
(defmacro mac (stufftodo) `(let ((varia 4711)) ,stufftodo))
(setf varia 42)
(mac (write varia))
```

### Homoiconnic Runtime-Metaprogramming
##### Example: Factorial with runtime metaprogramming
(Warning: convoluted example ahead)

```lisp
(defun fac (n)
	(let (
		  (lam `(lambda () (* ,@(loop for n from 1 below (+ n 1) by 1 collect n))))
		  (symb (gensym))
	     )
	  (compile symb lam)
	  (disassemble symb)
	  (funcall symb)
	)
)
```