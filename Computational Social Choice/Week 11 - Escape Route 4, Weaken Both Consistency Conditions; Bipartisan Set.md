# Escape Route 4 - Weaken Both Consistency Conditions

Recall [[Week 2 - Choice Theory, Rationalizability, Consistency#Contraction consistency alpha]] and [[Week 2 - Choice Theory, Rationalizability, Consistency#Expansion consistency gamma]].

Redefine $\alpha$ and $\gamma$ with sets: SCFs yield *sets of alternatives*, but rationality and consistency conditions defined in terms of *alternatives*.

$\alpha$ and $\gamma$: An alternative at the intersection of two feasible sets is chosen in both sets iff it is also chosen in the union of both sets.

### Set Rationalizability
$S$ is *set-rationalizable* if there is a relation $R \subseteq F(U) \times F(U)$ such that for all $A, X \in F(U)$:
$$X = S(A) \Longleftrightarrow X \in Max(R, F(A))$$

The base relation can be extended to sets:

$$X~R_S~Y \Longleftrightarrow X = S(X \cup Y)$$


### Set Consistency
Consistency conditions (as we've seen before):
**Let $x \in A \cap B$**.
- $\alpha$: if $x \in S(A \cup B)$, then $x \in S(A)$ and $x \in S(B)$
- $\gamma$: if $x \in S(A)$ and $x \in S(B)$, then $x \in S(A \cup B)$

New *Set Consistency Conditions*:
> **Definition**: Set Consistency Conditions $\hat{\alpha}$ and $\hat{\gamma}$.
> **Let $X \subseteq A \cap B$**.
> - $\hat{\alpha}$: if $X = S(A \cup B)$ then $X = S(A)$ and $X = S(B)$
> - $\hat{\gamma}$: $X = S(A)$ and $X = S(B)$ then $X = S(A \cup B)$

You can see: syntactically quite similar. However, the notions are actually quite different.

> **Lemma**.
> An SCF $S$ satifies $\hat{\alpha}$ iff for all V, W with $S(V) \subseteq W \subseteq V$, $S(V) = S(W)$.

### Stability
> **Theorem** (Brandt & Harrenstein, 2011).
> $S$ is set-rationalizable iff it satisfies $\hat{\alpha}$.

$S$ is *stable* if it satisfies $\hat{\alpha}$ and $\hat{\gamma}$.

$S$ is *quasi-transitively rationalizable* iff it satisfies $\alpha$, $\hat{\alpha}$, $\hat{\gamma}$.

![[stability-relationships.png]]



### Set-Rationalizable SCFs
All non-trivial monotonic scoring rules violate $\hat{\alpha}$: e.g. 

| 3   | 2   | 1   |
| --- | --- | --- |
| a   | b   | c   |
| c   | a   | b   |
| b   | c   | a   |

- $f(R, \{a, b, c\}) = \{a\}$
- $f(R, \{a, b\}) = \{a, b\}$

In fact, *almost every SCF* studied during this course violate $\hat{\alpha}$.
- instant-runoff
- plurality with runoff
- Baldwin's rule
- Black's rule
- Kemeny's rule
- maximin
- Young's rule
- Copeland's rule
- uncovered set
- Banks set

(If a losing alternative is removed, the winning set should not change).

However a handful of Condorcet extensions are set-rationalizable, and even stable. For instance, *the top cycle satisfies $\hat{\alpha}$ and $\hat{\gamma}$*.

## The Bipartisan Set
Let $(A, P_M)$ be a tournament and $p: A \to [0, 1]$ be a probability distribution over the alternatives. $p$ is *optimal* if 
$$u_p(x) := \sum_{y \in \overline{D}(x)} p(y) - \sum_{y \in D(x)} p(y) \geq 0 \text{~for all $x \in A$}$$

In other words: For every $x$, the alternative selected by $p$ is at least as likely to dominate $x$ as to be dominated by $x$. $u$ has that name referring to *utilities*.

> **Theorem** (Laffond et al. 1993; Fisher & Ryan 1995)
> Every tournament admits a *unique* optimal probability distribution.

The theorem is a consequence of the Minimax theorem (1928).

This can be related to game theory (roughly like this?):
- use the skew adjacency matrix of the majority relation (which has $+1$ and $-1$ for outgoing/incoming edges and 0s on the diagonal)
- use the matrix game defined by this matrix
- apply the min-max theorem to it (which is a special case of the Nash Equilibrium existence theorem)

*Proof*.
Two observations:
1. For every $p$, $\sum_{x\in A} p(x) u_p(x) = 0$:
$$\sum_{x\in A} p(x) u_p(x) = \sum_{x\in A} p(x) \left(\sum_{y\in \overline{D}(x)} p(y) - \sum_{y \in D(x)} p(y)\right)
= \sum_{x, y\in A} p(x) p(y) sign(n_{xy} - n_{yx}) = 0$$

2. If $p$ is *optimal*, then $\forall x \in A: p(x) > 0 \implies u_p(x) = 0$.

*Existence proof*: Base case $|A| = 1$ clear.
Consider $|A| > 1$. Let $p \in \arg\max_{p in \Delta(A)} \min_{x\in A} u_p(x)$ and assume for contradiction that $\min_{x\in A} u_p(x) =: v < 0$.

Pick some $z \in A$ s.t. $u_p(z) \geq 0$ (otherwise $u_p(x) = v < 0$ for all $x$ but $\sum_{x\in A} p(x) u_p(x) = 0$, contradiction).

By the IH find some $q \in [0,1]^A$ s.t. $q(z) = 0$ and $\forall x \neq z: u_q(x) \geq 0$.

Let $v = (1-\varepsilon) p + \varepsilon q$ for $\varepsilon in (0, 1]$, i.e. some convex combination of $p$ and $q$. Then for all $x \neq z$ (since $v < 0$),
$$u_v(x) \geq (1- \varepsilon)v + \varepsilon \cdot 0 > v.$$
Also,
$$u_v(z) = (1 - \varepsilon) u_p(z) + \varepsilon u_q(z) > v, \text{~given $\varepsilon$ is small enough}.$$

So we have found a lottery which performs better than $p$ did:
$$\min_{x\in A} u_r(x) > v$$
which contradicts (*).

*Uniqueness proof*: Assume $p \neq q$ are both optimal. Assume wlog. that $p, q$ have the same support. [TODO... otherwise?]

Let $v(x) = p(x) - q(x)$ for all $x$. Then
1. for all $x \in B$: $$u_r(x) = \sum_{y \in \overline{D}(x)}(p(y) - q(y)) - \sum_{y \in D(x)}(p(y) - q(y)) = u_p(x) - u_q(x) = 0.$$
2. $\sum_{x \in B} r(x) = 0$

(1) and (2) define a *homogeneous system of linear equations with integer coefficients*, which has a non-zero solution $r$. It also has a non-trivial solution $r^\ast$ in *integer* numbers.
(probably because if it has non-zero solutions, it is underdetermined, in which case we can multiply through any rational solution).

Assume wlog. $r^\ast(b)$ is odd for some $b \in B$ (otherwise divide all by some $2^k$).

Let $x \in B$:
$$0 = \sum_{x\in B} r^\ast(x) = r^\ast(x) + \sum_{y \in \overline{D}(x)} r^\ast(y) + \sum_{y \in D(x)} r^\ast(y)
= r^\ast(x) + 2\sum_{y \in \overline{D}(x)}r^\ast(y).$$

Therefore for all $x \in B$, $r^\ast(x)$ is even. Contradiction!


##### Bipartisan Set Example
![[bipartisan-set-example.png]]

### Properties of the Bipartisan Set

##### Some "Odd" properties
1. $p(x) > 0$ if and only if $u_p(x) = 0$
2. $|BP(A, P_M)|$ is odd
3. $p(x)$ is the *quotient of odd numbers* for all $x \in BP(A, P_M)$

*Proof*:
Let $p$ be optimal, i.e. $u_p(x) \geq 0$ for all $x \in A$, and $p(x) \in \mathbb{Q}$. Let $l$ be the lowest commond denominator of all $p(x) \neq 0$ and $p^\ast(x) = l\cdot p(x) \in \mathbb{N}_0$ for all $x \in A$. Then there is some $b \in A s.t. p^\ast(b)$ is odd.

$$\sum_{y \in A} p^\ast(y) = p^\ast(b) + \sum_{y \in \overline{D}(b)} p^\ast(y) + \sum_{y \in D(b)} p^\ast(y) = p^\ast(b) + 2\sum_{y \in \overline{D}(b)} p^\ast(y)$$
In the last term, we add an odd and an even number, which is odd. Therefore $\sum_{y \in A}p^\ast(y)$ is odd (this is the denominator of the fraction).

Let $x \in A$ s.t. $u_p(x) = 0$. Then $$\sum_{y\in A} p^\ast(y) = p^\ast(x) + 2\sum_{y \in \overline{D}} p^\ast(y):$$ i.e. an odd number is the sum of $p^\ast(x)$ and an even number. Hence $p^\ast(x)$ is odd (this is the numerator of the fraction), hence (1). This also shows that $p^\ast(x) > 0$, i.e. (2). Since $\sum_{y \in A}p^\ast(y)$ is odd, there must be an odd number of non-zero summands, which shows (3).

##### Relationship to other SCFs
1. $BP \subseteq UC$
	- therefore $BP$ is a Condorcet extension
2. Open question: can $BP$ and $BA$ return disjoint choice sets?
3. $BP$ is [[#Stability|stable]], i.e. it satisfies $\hat{\alpha}$ and $\hat{\gamma}$.
4. $BP$ satisfies *strong monotonicity*: this means it is invariant under the weakening of unchosen alternatives. (Also called "independence of unchosen alternatives").

### Characterization of the Bipartisan Set
Introduce a new notion of one SCF being *more discriminating* than another one.

> **Definition** (more discriminating).
> For two majoritarian SCFs $S$ and $S'$, $S$ is *more discriminating* than $S'$ if for some $m$, $S$ selects *fewer alternatives* than $S'$ *on average* (averaged over all labelled tournaments of size $m$).

> **Theorem** (Brandt et al. 2017)
> There is no more discriminating stable majoritarian SCF than $BP$.

In particular, no majoritarian refinement of $BP$ is stable; However $BP$ is not the *unique* stable majoritarian function, as there is no such unique function.

$BP$ can be characterized as the *unique most discriminating majoritarian SCF satisfying stability, monotonicity*, and two further axioms.


### Computing the Bipartisan Set
$BP$ can be computed in polynomial time by solving a linear feasibility program.

In game theoretic terms, we are just solving a symmetric zero-sum game.

The Bipartisan Set is P-complete: *it is among the hardest problems in P*. [TODO look up: how P hardness works exactly?]

