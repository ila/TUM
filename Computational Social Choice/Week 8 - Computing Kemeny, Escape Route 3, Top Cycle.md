## Computing Kemeny Rankings
Intuition beforehand: the whole problem smells like NP hardness.

> **Theorem (McGarvey, 1953)**. For every majority graph $G$ with weight 1 on every edge, there exists a preference profile with an odd number of voters that induces $G$.

*Proof.* Idea: for each edge, add two voters s.t. only this edge is induced, and all other edges remain unchanged. Do this for an edge $(a, b)$ by adding the following voters:

| 1     | 1     |
| ----- | ----- |
| **a** | z     |
| **b** | ...   |
| c     | c     |
| ...   | **a** |
| z     | **b** |

Do this for every edge of the majority graph to finish.

QUESTION: Where does "weight 1 on every edge" come into play? How do we ensure oddness of the number of voters? (right now they are even). 

Answer to the oddness question: just add one voter in the beginning, then flip all wrong edges.

Note: McGarvey's construction requires $O(m^2)$ voters, but due to Erdos and Moser: you only require $\Theta(m/log m)$ voters.

### NP-Completeness of Computing Kemeny Rankings
*Feedback Arc Set (FAS)* problem: "Is it possible to make a given directed graph *acyclic* by removing $k$ edges?".

> **Theorem (Karp, 1972)**. FAS is NP complete.

> **Theorem (Alon 2006; Charbit et al. 2007; Conitzer 2006)**. FAS is NP-complete even when restricted to *tournaments*, i.e. complete oriented graphs.

We introduce a Kemeny score decision problem: "Does there exists a ranking with Kemeny score at least $s$?"

> **Theorem (Bartholdi et al. 1989)**. The Kemeny ranking decision problem is NP-complete.

*Proof*. Reduce tournament-restricted FAS to the Kemeny problem. Assume we have a tournament-restricted FAS instance: a complete oriented graph $G$ and an integer $k$. Construct a preference profile with an odd number of voters corresponding to $G$ using McGarvey's theorem. 

The [[Week 7 - Maximum Likelihood SCFs and Kemeny's Rule#Kemeny's Rule and Majority Graphs|lemma from last week]] implies that $G$ can be made acyclic by removing $k$ edges <==> $R_N$ admits a ranking with Kemeny score at least 
$$s = \frac{m(m-1)}{2}\frac{n+1}{2} - k$$

In addition, we also show that finding a Kemeny ranking is also hard: Just consider the Turing reduction where we have a Kemeny-ranking-computing oracle. This trivially lets us solve the decision problem.

More advanced results:

> **Thereom (Dwork et al. 2001; Bachmeier et al. 2016)**. Finding a Kemeny ranking is NP-hard for even $n\geq 4$ and odd $n \geq 7$.

The question for $n=3$ and $n=5$ is open.

> **Theorem (Hemaspaandra et al. 2005; Fitzsimmons et al. 2021)**. Deciding whether a given ranking is a Kemeny ranking is coNP-complete and deciding whether a given alternative is a Kemeny winner is $\Theta_2^P$-complete (even worse than NP-completeness).

> **Thereom (Kann 1992)**. FAS is APX-hard, i.e. it cannot be approximated efficiently.

One positive result:

> **Theorem (Kenyon-Mathieu et al. 2007)**. There exists a *polynomial-time approximation scheme* (PTAS) for *weighted tournament* FAS.

# Escape Route 3 - Only Require Expansion Consistency
Recall the basics of [[Week 2 - Choice Theory, Rationalizability, Consistency|choice theory]]: $\alpha, \gamma, \beta^+$, furthermore rationalizability = $\alpha \wedge \gamma$ and trans. rat. = $\alpha \wedge \beta^+$.

Also recall the [[Week 4 - Arrow's Impossibility Theorem#Overview over Arrovian Impossibility Results|Arrovian impossibility results]].

- Sen (1977): all these impossibility proofs are statements about the base relation; every impossibility involving rationalizability can be turned into one involving only $\alpha$.
- *Strong Condorcet-May Impossibility*: No SCF satisfies anonymity$_2$, neutrality$_2$, positive responsiveness$_2$ and $\alpha$
- *Strong Mas-Colell/Sonnenschein impossibility*: Every SCF with IIA, positive responsiveness$_2$ and $\alpha$ admits a weak dictator.

### Dropping $\alpha$
Takeaway: $\alpha$ is the culprit, without $\alpha$ we can get better results! $\beta^+$ has no implications on the acyclicity of the base relation (example of cyclic CF satisfying $\beta^+$ see slides).

Contraction consistency: **devastating**, even in its weakest form <--> Expansion consistency: **much less harmful**, even in its strongest form.

![[weakening-to-beta-plus.png]]

## The Top Cycle
Due, among others, to John I. Good.

- a *dominant set* is a non-empty subset of alternatives $B$ such that for all $x \in B$ and $y \in A \setminus B$, $x P_M y$.

Denote the set of dominant sets by $Dom(A, P_M)$. 

The set $Dom(A, P_M)$ is ordered by $\subseteq$: If $X, Y \in Dom(A, P_M)$, $x \in X \setminus Y$ and $y \in Y \setminus X$, then $x P_M y$ and $y P_M x$ (contradiction).

The *minimal dominant set* is called the *Top Cycle* (TC).

$$TC(A, P_M) = \bigcap Dom(A, P_M)$$

The same construction is also nown as *GETCHA* or the *Smith Set*.

#### Axioms fulfilled by TC
TC is a Condorcet extension.

> **Theorem (Bordes, 1976)**. The top cycle is the *finest* SCF that satisfies anonymity, neutrality, positive responsiveness$_2$, and $\beta^+$.

An SCF $f$ is *finer* than $f'$ if $f(X) \subset f'(X)$ for all $X$. Some trivial SCFs also satisfy above axioms, but are coarser than the top cycle.

*Proof*. In two steps: every SCF that satisfies the axioms contains the top cycle; the top cycle itself satisfies the axioms.

Recall $\beta^+$:
![[recall-beta-plus.png]]

Step 1, let $f$ satisfy the axioms. Let $B = \{x, y\}$ with $x \in f(A)$ and $y \in A \setminus f(A)$. By $\beta^+$, $S(B) = \{x \}$ ($y$ cannot be contained because it is not in $S(A)$). By May's Theorem, $x P_M y$. Therefore $f(A)$ is a dominant set, and the top cycle is containted in it.

Step 2, show that $TC$ satisfies the axioms. [TODO... I think we only showed $\beta^+$]

##### Top Cycle examples
![[top-cycle-examples.png]]

#### Majoritarian SCFs
The top cycle only depends on majority rule (i.e. the base relation).
- an SCF is *binary* if for all $R_N, R_N'$: If $f$ agrees on pairs for $R_N$, $R_N'$, it agrees on all sets.

A *majoritarian SCF* is an SCF that satisfies anonymity, neutrality, positive responsiveness$_2$ and *binarity*.

Properties of majoritarian SCFs:
- majoritarian SCFs are a subset of the [[Week 6 - Escape Route 2, Variable-Electorate Condition#Classification of SCFs based on their Level of Abstraction|C1]] functions: additionally, have to be neutral + make pairwise choices according to majority rule
- three of [[Week 3 - Formalizing Social Choice, May's Theorem, Condorcet Paradox#Arrow's Impossibility Theorem|Arrow's conditions]] are satisfied by maj. SCFs: IIA, PO${}_2$, and non-dictatorship.

The strict part of majority rule $P_M$ for a given preference profile defines a *tournament* (= complete directed graph) on $A$, written $(A, P_M)$. We denote majoritarian SCFs as function of tournaments: e.g. $CO(A, P_M)$ and $TC(A, P_M)$.

> **Lemma**. Let $S$ be majoritarian and $a P_M b P_M c P_M a$. Then $S(\{a, b\}, P_M) = \{a \}$ and $S(\{a, b, c\}, P_M) = \{a, b, c\}$.

For odd numbers of alternatives, you can find quite symmetric tournaments (called *cyclones*) - has to do with the complete automorphism group.

Some more new notation:
- Dominion of $x$ $D(x) = \{y \in A \mid x P_M y\}$
- Dominator $\overline{D}(x) = \{y \in A \mid y PM x \}$

(In graph theory language: outgoing/incoming neighbors).

Furthermore, define inductively $D^{k}(x)$ and $\overline{D}^{k}(x)$ as the vertices that can be reached in $k$ steps, or can reach $x$ in $k$ steps (including $x$ itself). Finally, define $D^\ast(x) = D^{|N|}(x)$ and $\overline{D}^\ast(x) = \overline{D}^{|N|}(x)$ as the alternatives that can be reached from $x$/that can reach $x$.