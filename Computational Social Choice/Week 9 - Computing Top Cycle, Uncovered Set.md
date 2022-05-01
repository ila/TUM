### Top Cycle Linear-Time Algorithm
Linear in the number of edges (i.e. quadratic in the number of alternatives).

- computing the *minimal dominant set containing a given $x$*:
	- start with $B = \{x\}$
	- iteratively: add all alternatives that dominate some alternative in $B$, until no more such alternatives exist

This gives a different characterization of dominant sets: they are all of the form $\overline{D}^\ast(x)$ for some $x\in A$ ($\overline{D}^\ast(x)$ are the alternatives that can reach $x$ on some path).

$$Dom(A, P_M) = \{ \overline{D}^\ast(x) \mid x \in A \}$$

So: $Dom(A, P_M)$ can be computed in $O(m^3)$ time.

To improve runtime: we want to find an alternative in linear time which we *know* is contained in the TC. Claim: this is the case for Copeland winners.

$$CO \subseteq TC$$

...and CO is computed in linear time (linear in the number of edges).

Proof that $CO \subseteq TC$: Assume some Copeland winner $a$ outside the TC dominates a set of alternatives $D(a)$, also outside the TC. But then the TC nodes all point to $a$ as well as to $D(a)$, i.e. have higher degree than $a$.

##### Interlude: alternative way to draw tournaments
Draw all nodes on a vertical line, only draw the edges from bottom to top.



### Transitive Closure
Idea: the problem we are always dealing with are cycles in the base relation. How about we take the reflexive transitive closure of $P_M$?

> **Theorem (Deb, 1977)** The maximal elements of the transitive closure $P_M^\ast$ of $P_M$ are equal to the top cycle:
> $$TC(A, P_M) = Max(P_M^\ast, A)$$

*Proof*. see notes; intuitively quite clear why this holds:
- $P_M^\ast$ adds edges in both directions along the cycles
- the top cycle elements then have no strict ingoing edges in $P_M^\ast$

This leads to another, alternative algorithm:
- find strongly connected components
- find source in the DAG of strongly connected components

### Top Cycle and Pareto Optimality
Now we come to the drawbacks. General problem: the TC can be very large.

Therefore, it fails to be Pareto-optimal on more than 2 alternatives!


## The Uncovered Set
### Covering Relation
In a tournament, $x$ *covers* $y$ if $D(y) \subseteq D(x)$ (Notation: $xCy$).

$C$ is a *transitive subrelation* of $P_M$.
- transitivity: obvious since $\subseteq$ is transitive
- subrelation of $P_M$: assume instead $y P_M x$. Then $x \in D(y)$, i.e. $x \in D(x)$, contradiction.

Equivalent definition using dominators:
$$xCy \Longleftrightarrow \overline{D}(x) \subseteq \overline{D}(y)$$

### Uncovered Set
> **Definition (Uncovered Set)**. The Uncovered Set consists of all *uncovered alternatives*:
> $$UC(A, P_M) = Max(C, A).$$

- is a Condorcet extension
- alternatively: define as Condorcet winners of all inclusion-maximal subtournaments that have one

##### Example
![[uc-examples.png]]

In the second example: we are doing better than the top cycle! Recall that TC selects all edges in this case, which makes it non-Pareto-optimal.

### Characterization of UC
> **Theorem (Characterization of UC; Moulin, 1986).** The uncovered set is the *finest majoritarian SCF* satisfyfing [[Week 2 - Choice Theory, Rationalizability, Consistency#Expansion consistency gamma| expansion consistency γ]].

*Proof*.
1. show that for any maj. and $\gamma$ SCF $S$, $UC \subseteq S$.
	- let $a \in UC(A)$, show $a \in S(A)$
	- we have $a \in UC(A)$ iff $\forall x \in \overline{D}(a) \exists  y \in D(a): y P_M x$
	- (full proof see notes)
2. show that UC satisfies maj. and $\gamma$
	- by contradiction: let $x \in UC(A) \cap UC(B)$, $x\notin UC(A \cup B)$. Then for some $y$: $y C x$. ==> $y \in A \cup B$.
	- (full proof see notes)

Comment: This is a prime example of where the declaration of oddity helps immensely. Without it we would need some very technical extra conditions.

Also, since $\beta^+$ implies $\gamma$, $UC \subseteq TC$.

### UC and Pareto Optimality
Also: UC satisfies PO.

> **Theorem**. UC satisfies PO.

*Proof*. Let $a P_i b$ for all $i$. Then if $c$ is covered by $b$, then $b P_M c$ and therefore $a P_M c$. Therefore $a C c$.

> **Theorem (Brandt & Geist, 2014)**. UC is the largest majoritarian SCF satisfying Pareto-optimality.

Therefore: UC the *only* majoritarian SCF that satisfies PO and $\gamma$.

### Uncovered Set Algorithm
- Naively: in $O(m^3)$
- We can do better. However if a linear $O(m^2)$ algorithm exists is open

##### Two-Step Principle for UC
First, an equivalent characterization of UC:
> **Theorem (Shepsle & Weingast, 1984)**: UC consists precisely of all alternatives that reach every other alternative in *at most two steps*, i.e.
> $$UC(A, P_M) = \{x \in A \mid D^2(x) = A \}$$
> In graph theory terms, vertices that reach any other vertex on a path of at most 2 are called *kings*.

*Proof*. Claim: this was already shown via the proof we saw above. [TODO think about this]

##### Matrix Multiplication algorithm for UC
```python
def UC(A, PM):
	M = matrix(lambda i, j: 1 if PM(i,j) else 0)
	U = M^2 + M + I
	B = [i for i in A if all(U[i,j] != 0 for j in A)]
	return B
```

Matrix multiplication, asymptotically, can be done in $O(m^{2.37286})$ (Alman et al. 2021). Strongly based on the Coppersmith and Winograd algorithm (1990).

A conjecture is: matrix multiplication is believed to be possible in $O(m^2)$ time.

![[tc-uc-overview.png]]