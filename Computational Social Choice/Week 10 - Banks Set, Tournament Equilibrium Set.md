# Week 10

## Banks Set
A transitive subset of a tournament is a set of alternatives within which $P_M$ is transitive.

Let $Trans(A, P_M) = \{ B \subseteq A : B \text{ is transitive} \}$.

The *Banks Set* $BA$ consists of the maximal elements of all inclusion-maximal transitive subsets:
$$
BA(A, P_M) = \{ Max(P_M, B) \mid B \in Max(\supseteq, Trans(A, P_M)) \}
$$

Characterization: $x \in BA(A, P_M)$ if and only if there exists some $B \in Trans(A, P_M)$ such that $x \in Max(P_M, B)$ and $(\nexists a \in A: \forall b \in B: a P_M b)$. In other words: $x$ is the maximum of some transitive subset of the tournament, and this transitive subset cannot be extended from above.


##### Examples
The "only interesting 4-tournament up to isomorphism": $a \to c \to d \to b$, $a \to d$, $c \to b$.

![[only-interesting-4-tournament.png]]

Then 
- $TC(A, P_M) = \{a, b, c, d\}$
- $UC(A, P_M) = \{a, b, c\}$
- $BA(A, P_M) = \{a, b, c\}$
	- $a$ from $\{a, b, d\}$
	- $b$ from $\{a, b\}$
	- $c$ from $\{b, c, d\}$
	- $d$ could only be from $\{d, b\}$, but this can be extended from above by $c$

An example where $BA \neq UC$:

![[uc-neq-ba-example.png]]

Then
- $TC(A, P_M) = \{ a, b, c, d, e, f, g \}$
- $UC(A, P_M) = \{ a, b, c, d \}$
- $BA(A, P_M) = \{ a, b, c \}$
	- $a$ from $\{A, d, f, g\}$, $b$ and $c$ by symmetry
	- not $d$ ($d$ + two alternatives from the bottom can be extended by an alternative from the top)
	- not $e$, $f$ and $g$

We will soon see: $BA(A, P_M) \subseteq UC(A, P_M)$.

### Strong Retentiveness $\rho^+$
A choice function $S$ satisfies *strong retentiveness* $\rho^+$ if for all $A \in F(U)$ and $x \in A$ with $\overline{D}(x) \neq \emptyset$, $S(\overline{D}(x)) \subseteq S(A)$.

(In other words: the best elements from all dominator sets have to be chosen.)

> **Theorem** (majoritarian +  $\gamma$ ==> $\rho^+$)
> A majoritarian SCF that satisfies $\gamma$ also satisfies $\rho^+$.

*Proof*. Let $S$ be such an SCF, $a \in A$. Show: $S(\overline{D}(a)) \subseteq S(A)$.

Idea: find enough sub-tournaments where $x$ is chosen, use $\gamma$ to show that it is chosen from their union = the whole set.

Let $x \in S(\overline{D}(a))$. For all $y \in D(a): x \in S(\{a, x, y\})$.
- either $x \to a, x \to y, a \to y$, then $x$ is selected from $\{x, a\}$ and from $\{x, b\}$, hence also from their union
	- QUESTION: does this mean that $\gamma$ implies Condorcet extension?
- or $x \to a \to y \to x$: then from this 3-cycle, by neutrality (from majoritarianness), everything must be selected.

### $\rho^+$ Characterization of the Banks set

> **Theorem** (Brandt, 2011): The Banks set is the finest majoritarian SCF satisfying $\rho^+$.

*Proof*.

Lemma 1: If $S$ is majoritarian and satisfies $\rho^+$, then $BA \subseteq S$.
Let $x \in BA(A)$. To show: $x \in S(A)$.
There is some inclusion-max. trans. $B = \{ x = x_0, x_1, \dots, x_k \}$, $x_i P_M x_j \forall i < j$.

Define $C = \bigcap_{i=1}^k \overline{D}(x_i)$. Claim: $C = \{x \}$. (if any other $z$ were in $C$, then $B$ would not be inclusion-maximal).

Then
$$
S(A)
\overset{\rho^+}{\supseteq} S\bigg(\overline{D}(x_k)\bigg)
\overset{\rho^+}{\supseteq} S\bigg(\overline{D}(x_k) \cap \overline{D}(x_{k-1})\bigg)
\overset{\rho^+}{\supseteq} \dots
\overset{\rho^+}{\supseteq} S\bigg(\overline{D}(x_k) \cap \dots \cap \overline{D}(x_1)\bigg) 
= S(C) = \{x\}
$$ 

Lemma 2: show that BA satisfies major. and $\rho+$.
Let $a \in A$ and $x \in BA(\overline{D}(a))$. Show: $x \in BA(A)$.
Have some inclusion-max. $B \subseteq \overline{D}(a)$, with max. $\{x\}$.
[TODO see notes for the complete proof]

### Computing the Banks set
- some alternative in the Banks set can be found efficiently:
	- in linear time

- finding if *some specific alternative* is a Banks winner is *NP-complete*: computing the Banks set is NP hard.

##### Computing one alternative from the Banks set
```python
def banks_set_element(A, PM):
	B = []
	a = A[0]
	while True:
		B = B + [a]
		C = (interesection of dominators)
		if C = []:
			return a
		a = C[0]
```

Apparently by choosing $a$ from $A$ or from $C$ in some other way, we could in principle obtain any element from the Banks set; maybe a route of attack for a randomized algorithm?

##### Computing the Banks set is NP-complete
Computing the Banks set is NP-complete (Woeginger, 2003); It remains NP complete even for 5 voters (even though you can only get a subset of majority tournaments with 5 voters) (Bachmeier et al. 2013).

Proof sketch:
- reduction from CNF-SAT (conjuctive normal form SAT)
- a maximal transitive set with maximal element $d$ has to contain an element of every level below $d$
- A maximal transitive set with maximal element $d$ cannot contain upward edges
- for some formula $\phi$, construct a graph with some node $d$ where $d$ is in the Banks set iff $\phi$ is satisfiable

![[banks-set-np-reduction.png]]

Idea:
- for each clause, one grey area
- upward edges for literals that are negations of each other
- from each grey area, at least one element must be taken: otherwise one of the elements on top kick in and dominate $d$
- the $u_2, u_4$ elements make sure that no upward edges between the grey areas can be included, and hence no contradicting nodes can be chosen

The Bachmeier et al. paper shows that these kinds of graphs can always be realized with 5 voters.


### More on the Banks set
- the Banks set is a singleton $\{x\}$ iff $x$ is a Condorcet winner
	- ==> every trans. set without $x$ is eventually extended by $x$
	- <== Condorcet winner extends all trans. sets

- The same property holds for $UC$ and $TC$
	- ==> because $BA \subseteq UC \subseteq TC$
	- <== was shown earlier ([TODO... where?])

### Retentiveness $\rho$
Strong retentiveness $\rho^+$ can be further weakened to *retentiveness* $\rho$:

A choice function $S$ satisfies *retentiveness* $\rho$ if for all $A \in F(U)$ and $x \in S(A)$ with $\overline{D}(x) \neq \emptyset$, we have $S(\overline{D}(x)) \subseteq S(A)$.

## Tournament Equilibrium Set
Let $S$ be an arbitrary choice function.

A non-empty set of alternatives $B$ is $S$-retentive if $S(\overline{D}(x)) \subseteq B$ for all $x \in B$ with $\overline{D}(x) \neq \emptyset$.


Compare with the top cycle:
- in the TC, *no incoming edges allowed*
- with $S$-retentiveness, this notion is relaxed: incoming edges to $x$ are allowed as long as among the dominators of $x$, the "best" (i.e. chosen by $S$) options  are in $B$
	- "no alternative should be properly dominated"

$\mathring{S}$ is a new choice function that yields the *union of all inclusion-minimal $S$-retentive sets*.

$\mathring{S}$ satisfies retentiveness $\rho$.

Example: $\mathring{TRIV} = TC$.

Define the *tournament equilibrium set*:
> The *Tournament Equilibrium Set* (TEQ) of a tournament is defined as solution to $TEQ = \mathring{TEQ}$, i.e. the unique fixpoint of $(\mathring{\phantom{X}\;})$.

> **Theorem** (Schwartz, 1990):
> $TEQ \subseteq BA$ 

##### TEQ Example
![[teq-example.png]]

- $TEQ(\overline{D}(a)) = TEQ(\{c\}) = \{c\}$
- $TEQ(\overline{D}(b)) = TEQ(\{a,e\}) = \{a\}$
- $TEQ(\overline{D}(c)) = TEQ(\{b,d\}) = \{b\}$
- $TEQ(\overline{D}(d)) = TEQ(\{a,b\}) = \{a\}$
- $TEQ(\overline{D}(e)) = TEQ(\{a,c,d\}) = \{a,c,d\}$

Pick *the inclusion-minimal set which has no thick ingoing edge*.

### Properties of TEQ
 Computing TEQ is NP-hard (Brandt et al. 2010) and remains so even for 7 voters (Bachmeier et al. 2015)
	- best known upper bound: PSPACE
	
> **Theorem**
> The following are equivalent:
> - Every tournament contains a unique minimal TEQ-retentive set (Schwartz' conjecture, 1990)
> - TEQ is the unique finest maj. SCF satisfying $\rho$
> - TEQ satisfies monotonicity
> - TEQ satifies independence of unchosen alternatives
> - TEQ satisfies $\hat{\alpha}$ and $\hat{\gamma}$
> - TEQ is $R^K$-strategyproof

**Schwartz' conjecture is false!**
=> All the cool properties *do not hold*!

##### Disproving Schwartz' conjecture
The first proof was *highly non-constructive!*.

Proof: using the probabilistic method.

Smallest counterexample *of the type from the non-constructive proof* requires 10^136 alternatives.

The conjecture holds for up to 14 alternatives.

Counterexample with 24 alternatives constructed (Brandt et al. 2016)

"In principle, TEQ severely flawed; but not clear if there are any practical consequences"

Does this even cast some doubt on the axiomatic method?


## Summary: Expansion-Consistent SCFs

![[expansion-consistent-scfs.png]]