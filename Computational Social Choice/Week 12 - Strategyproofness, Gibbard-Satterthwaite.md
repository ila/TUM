# Week 12 - Strategyproofness
Goal: [[Week 5 - Escape Route 1, Domain Restrictions#Transitivity Strategyproofness Participation|Resistance against]]
- misrepresentation of preferences (*strategyproofness*)
- strategic abstention (*participation*)

Plurality and Borda, actually any monotonic scoring rule, satisfy participation.

Resolute SCFs on two alternatives: strategyproofness <--> monotonicty

In any domain with $R_M$ acyclic, $Max(R_M, A)$ satisfies strategyproofness and participation. In particular, [[Week 5 - Escape Route 1, Domain Restrictions#Dichotomous Preferences and Approval Voting|approval voting]] and [[Week 5 - Escape Route 1, Domain Restrictions#Condorcet Winner on Single-Peaked Preferences|median voting]] satisfy these properties in their respective domain.

Our notion of strategy-proofness is most reasonable for resolute SCFs. We usually study non-resolute SCFs (in order to allow for anonymity and neutrality).

## Strong Monotonicity
> **Definition** (*Strong Monotonicity*)
> An SCF $f$ is *strongly monotonic* if for all $R_N, R'_N$ and $i \in N$, with $R_N = R'_N$ except $x P_i y$ and $y P'_i x$ for some $x, y$ with $x \notin f(R_N)$, implies $f(R'_N) = f(R_N)$.

In other words: $f$ is strongly monotonic if it is *invariant under the weakening of unchosen alternatives*.

Strong monotonicity implies monotonicity (compare with [[sheet11#Lemma 43 2|Sheet 11, Exercise 43]])

A *resolute* SCF is strongly monotonic if and only if for all $R_N, R'_N$ and $i \in N$ such that $R_j = R'_j$ for all $j\neq i$, it holds that $$f(R_N) = \{x\} \wedge \bigg(\forall z: x P_i z \implies x P'_i z\bigg) \implies f(R'_N) = \{x\}$$
In other words: For a resolute strongly monotonic SCF, we can move any alternative below the winning alternative around however we want, without changing the outcome.

Comment: $\{z \mid x P_i z\}$ is also called *lower contour set* (in economics).

> **Theorem** (Muller & Satterthwait; 1977).
> A resolute SCF is strategyproof if and only if it is strongly monotonic.

*Proof*. Have $R_N, R_N'$ that only differ for a single voter $i$, and $f$ resolute.

Definition of strategyproofness we use now: "For all $R_i, R'_i$, $\neg (f(R_N') ~P_i~ f(R_N))$". Slightly reformulated: $$f(R_N) = \{x\} \neq \{y\} = f(R'_N) \implies y P_i x \wedge x P'_i y \qquad\qquad\qquad\qquad [SP^\ast]$$

Strong monotonicity formulation:
$$\forall R_i, R_i': f(R_N) = \{x\} \wedge (\forall z: x P_i z \implies x P'_i z) \implies f(R'_N) = \{x\} \qquad [SMON]$$

Direction $SP^\ast \implies SMON$: Assume for contradiction that SMON, but $f(R_N') = \{y\} \neq \{x\}$. By $SP^\ast$, $x P_i y \wedge y P'_i x$.

Direction $SMON \implies SP^\ast$: Assume for contradiction that $f(R_N) = \{x\} \neq \{y\} = f(R'_N)$, but $y P_i x$. Define $R_i''$ where $y$ is moved to the top. Since nothing below $x$ was moved, by $SMON$, $x$ is still selected: $f(R_N'') = \{x\}$. On the other hand, we can start with $R_i'$  and go to $R_i''$ via SMON-preserving operations. Therefore $f(R_N') = \{y\}$, by SMON, $f(R_N'') = \{y\}$. Contradiction!


### Strong Monotonicity: Number of Alternatives
For two alternatives, monotonicity <==> strong monotonicity.

However,  for more than two alternatives and more than two voters, things get bad again.

> **Theorem**. 
> No resolute Condorcet extension satisfies strong monotonicity when $m, n \geq 3$.

*Proof*. 
Consider the preference profile $R_N$:

| 1   | 1   | 1   |
| --- | --- | --- |
| a   | b   | c   |
| b   | c   | a   |
| c   | a   | b   |

Without loss of generality, assume $f(R_N) = \{a\}$. Create $R_N'$:

| 1     | 1   | 1   |
| ----- | --- | --- |
| a     | b   | c   |
| ==c== | c   | b   |
| ==b== | a   | b   |

Then $f(R_N') = \{a\}$ by strong monotonicity. But then $f$ is not a Condorcet extension, which would require $f(R_N') = \{c\}$.

For $m > 3$, add in bottom-ranked alternatives. For $n > 3$, $n$ odd, we can add pairs of voters which cancel each other out; for $n$ even, a somewhat other construction is needed (skipped here).

## Gibbard-Satterthwaite Impossibility
> **Definition** (*non-imposing*).
> An SCF is *non-imposing* if it is "surjective" on the singletons, meaning that for any alternative $x$, $f(R_N) = \{x\}$ for some $R_N$.

This gives the following well-known impossibility result, independently discovered by A. Gibbard and M. A. Sattherwaite.

> **Theorem** (*Gibbard & Satterthwaite; 1973; 1975*)
> Every non-imposing, strategyproof, resolute SCF on at least 3 alternatives is [[Week 3 - Formalizing Social Choice, May's Theorem, Condorcet Paradox#From Condorcet to Arrow|dictatorial]].

##### All Gibbard-Satterthwaite conditions are necessary
- omit resoluteness: SCF where 1) if all voters agree on x: return x; 2) else return everything
- etc.

One can check that all the conditions are necessary.

#####  Proof of Gibbard-Sattherthwaite Impossibility
Proof here: by reduction to Arrow's impossibility theorem.

- given a non-imposing, strategyproof, resolute SCF $f$, construct an SCF $g$ that satisfies all Arrovian conditions
- show that dictatorship of $f$ implies dictatorship of $g$

![[gibbard-sattherthwaite-proof-outline.png]]

For $S \subseteq U$, $R_i \in R(U)$, let $R_i^S$ be the ranking obtained by moving all alternatives in $S$ to the top in $R_i$ and not changing their internal ordering. For profiles write $f(R_N^S) = f((R_1^S, \dots, R_n^S))$.

*Lemma*. $f$ satisfies Pareto-optimality.
Proof: assume $x P_i y$ for all $i$, but $f(R_N) = \{y\}$. By strong mon. $f(R_N^{\{x\}} = \{y\}$. There exists $R'_N$ such that $f(R'_N) = \{x\}$ because of non-imposition. By strong mon., $f(R_N'^{\{x\}}) = \{x\} = f(R_N^{\{x\}})$, a contradiction.

Because of PO, $f(R_N^S) \subseteq S$ for all $R_N$ and $S \neq \emptyset$.

Define $g(R_N, \{x, y\}) = f(R_N^{\{x,y\}})$. $g$ is well-defined (by PO of $f$) and the base relation $R_g$ is asymmetric (by resoluteness of $f$). We now check the [[Week 3 - Formalizing Social Choice, May's Theorem, Condorcet Paradox#Arrow's Impossibility Theorem|Arrovian conditions]]:

Transitivity of $R_g$:
- Without loss of generality, $f\left(R_N^{\{x,y,z\}}\right) = \{x\}$ and $f\left(R_N^\{y,z\}\right) = \{y\}$.
- By strong monotonicity, $f\left(R_N^{\{x,y\}}\right) = \{x\}$ and $f\left(R_N^{\{x,z\}}\right) = \{x\}$.
- therefore $x P_g y$, $y P_g z$, and $x P_g z$.

Pareto-Optimality PO${}_{2}$
- if $x P_i y$ for all $i$, then $g(R_N, \{x,y\}) = f(R_N^{\{x,y\}}) = \{x\}$

Independence of infeasible alternatives IIA${}_2$:
- If $R_{N \big| \{x,y\}} = R_{N \big| \{x,y\}}'$, by SMON,$$g(R_N, \{x,y\}) = f(R_N^{\{x,y\}}) = f(R_N'^{\{x,y\}}) = g(R_N', \{x,y\})$$

Since $R_g$ is transitive, we can define $g(R, A) = Max(R_g, A)$. $g$ is tr. rat. and satisfies PO${}_2$ and IIA${}_2$. *By Arrow's Theorem, $g$ is dictatorial*.

Let $i$ be the dictator fo $g$. Assume for contradiction that $i$ is *not* a dictator for $f$: Then for some $R_N$, $Max(R_i, A) = \{x\} \neq \{y\} = f(R_N)$. By strong monotonicity, $g(R_N, \{x,y\}) = f(R_N^{\{x,y\}}) = \{y\}$. Therefore $i$ is not a dictator for $g$: contradiction!



## The No-Show Paradox: Participation
Condorcet extensions are ruled out if one wants to satisfy participation, with enough numbers of alternatives and voters - Hervé Moulin showed in 1988:

> **Theorem** (*Moulin, 1988*).
> No resolute Condorcet extension satisfies [[Week 5 - Escape Route 1, Domain Restrictions#Transitivity Strategyproofness Participation|participation]] when $n \geq 25$, $m \geq 4$.

The numbers $n \geq 25$, $m \geq 4$ are *not* tight (but just an artifact of the proof). Examples:
- For $m \leq 3$, Kemeny's rule with appropiate tie-breaking satisfies participation.
- the bound of 25 was improved to 12. For $n \leq 11$, an SCF that satisfies the assumptions, found by a SAT solver, exists.

> **Theorem** (Brandt et al., 2016).
> No resolute Condorcet extension satisfies [[Week 5 - Escape Route 1, Domain Restrictions#Transitivity Strategyproofness Participation|participation]] when $n \geq 12$, $m \geq 4$.

*Proof*. ![[no-show-proof.png]]

## Outlook: Escape Routes from Strategyproofness Impossibilities
- restrict domain of preferences
- computational hardness of manipulation
	- use computational complexity as a barrier to manipulation
- irresolute SCFs
	- requires limited information about tie-breaking
- probabilistic SCFs
	- requires preferences over lotteries

