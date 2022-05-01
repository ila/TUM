




### Fairness Conditions of SCFs
- *anonymous*: $f(R_N, A) = f(R'_N, A)$ if the voters in $R'$ are a permutation of the ones in $R$
- *neutral* if $\pi(f(R_N, A)) = f(R'_N, B)$ if $\pi: A \to B$ is a bijection that satisfies $x R_i y  \Longleftrightarrow \pi(x) R'_i \pi(y)$ for all $i$


As defined here: Neutrality => SCFs independent of preferences over alternatives that are not in the feasible set (let $A=B$, $\pi(x) = x$)

### Pareto Optimality
For given $R_N$ and $x, y \in U$, then $x$ *Pareto-dominates* $y$ if $x P_i y$ for all $i \in N$. A *Pareto-optimal* alternative is an alternative which is not Pareto dominated.

Warning: stronger notion of Pareto dominance than usual, as *everybody has to strictly prefer* here.

##### Pareto SCF
$f_{\text{Pareto}}(R_N, A)$: returns all Pareto-optimal alternatives in $A$.
	- Pareto SCF is anon. and neutral
	
An arbitrary SCF $f$ satisfies *Pareto-optimality* if $f(R_N, A) \subseteq f_{Pareto}(R_N, A)$ for all $R_N, A$
(if it never returns a Pareto-dominated alternative).

### Resoluteness
- *resolute*: $|f(R_N, A)| = 1$

In general: anonymity and neutrality forbids resoluteness. (Intuition: sometimes we cannot break ties without violating anonymity or neutrality).

Formalized counterexample:
![[counterexample-resolute-anon-neutr.png]]

Bonus question: for which combinations of $m = |U|$ and $n = |N|$ are there such SCFs? Answer (Moulin 1983): There is a SCF that is anonymous, neutral, Pareto-optimal and resolute iff $n$ cannot be divided by any $q$ with $2 \leq q \leq m$.


### Social Choice from Pairs
For now: restrict ourselves to two alternatives. Notation: $N_{ab}$ is the set of voters who prefer $a$ to $b$, $n_{ab} = N_{ab}$.

Still, several possible SCFS. Examples:
- $f_{\text{majority}}$
- $f_{\text{Pareto}}$: a if $n_{ba} = 0$, b if $n_{ab} = 0$, else $\{a, b\}$
- $f_{\text{silly}}$

=> symmetry conditions are not enough: silly SCF should be ruled out by further axioms

### Strategic Manipulation

Manipulation is undesirable:
- difficult/impossible to detect, and also perfectly legal ("My scheme is intended only for honest men" - Borda)
- possibility to spend resources on manipulation (e.g. information gathering/compute power) not evenly distributed
- Predictions/theoretical statements become very difficult (e.g. silly rule)


##### Definition: Strategic Manipulation
For now: make some (strong) simplifying assumptions:
- we only know preferences over singletons - but now formally write $\{x \} R_i \{y \} \Leftrightarrow x R_i y$
- every $i$ knows the *submitted preferences* of all other voters

**Definition** An SCF $f$ is *manipulable* by voter $i$ if there exist $R_N, R'_N$ and $A$ such that $R_j = R_j'$ for all $j \neq i$ and $f(R'_N, A) P_i f(R_N, A)$. It is *strategyproof* if it is *not manipulable by any voter*.

### Monotonicity
Let $R_N$ and $R'_N$ s.t. for some voter $i$ and some alternative $a$, for all $j \neq i$, $R_j = R'_j$ and for all $x, y \in U \setminus \{a \}$,
$$(x R_i y \Leftrightarrow x R'_i y), ~(a R_i y \Rightarrow a R'_i y), ~(a P_i y \Rightarrow a P'_i y)$$

(in other words: $a$ rises in voter $i$'s preferences. Only so complicated because we have non-strict preferences.)

- An SCF $f$ is *monotonic* if $a \in f(R_N, A)$ implies $a \in f(R'_N, A)$.
	- (in other words: if $a$ is chosen, then it is also chosen if reinforced.)
- An SCF $f$ is *positive responsive* if $a \in f(R_N A)$ and the restrictions of $R_i, R_i'$ on $A$ differ, then $\{a \} = f(R'_N, A)$
	- (in other words: if $a$ is chosen in the first place, it is even chosen uniquely if reinforced.) 


> **Theorem**: A *resolute* SCF on two alternatives is strategyproof iff it is monotonic.


### May's Theorem: Characterization of Majority Rule
> **Theorem** (May, 1952).
> *Majority rule is the only SCF on two alternatives that satisfies anonymity, neutrality, and positive responsiveness*.


Proof sketch for $n=3$:
![[mays-theorem-proof-sketch.png]]

- Anonymity: exploited by the fact that we are only counting the number of voters per alternative
- Neutrality: diagram must be symmetric about the middle axis

Proof: start at middle axis (both ab). Then follow adjacency and exploit positive responsiveness to fill in the rest.

(note: the diagram also nicely shows that there are *three ways to reinforce an alternative*, as already mentioned [[#Monotonicity|above]])

![[mays-theorem-proof-sketch-finished.png]]


Another way to look at May's theorem: out of all anonymous, neutral and monotonic SCFs, majority rule is the *most decisive*.

Note: majority rule is uncontroversial *for two alternatives only*.


### The Condorcet Paradox
- define *pairwise majority relation* $R_M$: $x R_M y \Leftrightarrow n_{xy} \geq n_{yx}$.

> **Theorem** (Condorcet 1785; May 1952).
> There is no anonymous, neutral and positive responsive SCF that is *rationalizable* if $m \geq 3$ and $n \geq 3$.

Consider the preference profile:

| 1   | 1   | 1   |
| --- | --- | --- |
| a   | b   | c   |
| b   | c   | a   |
| c   | a   | b   | 



Let $f$ be an SCF with the desired properties, $R$ its rationalizing relation. By [[Week 2 - Choice Theory, Rationalizability, Consistency#Rationalizability|Lemma 2]], $R = R_f$. By May's theorem: $R_f = R_M$. $R_M$ is cylic for this preference profile and therefore cannot rationalize $f$. Generalize the proof to $m, n > 3$ by adding indifferent voters and bottom-ranked alternatives.

Comment on the theorem: This is not strictly weaker than Arrow's theorem, actually not comparable, but most of the assumptions here are stronger, so the theorem "feels weaker".


##### Condorcet Winners
An alternative $x$ is a *Condorcet winner* in $A$ if $x P_M y$ for all $y \neq x$ - i.e. an alternative that wins against all alternatives in pairwise majority comparisons. They may not exist, but are of course unique if they exist.

*How likely is it that no Condorcet winner exists?* For $m=n=3$ and uniformly distributed strict preferences: $p = 1/18$ (about 6%). In the limit for fixed $m$, $n \to \infty$:

| m   | 1   | 2   | 3   | 4   | 5   | 10  | 15  | 20  | 40  | 
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| p   | 0%  | 0%  | 9%  | 18% | 25% | 49% | 61% | 68% | 81% |



### 2-Versions of Axioms
We will use weakenings of axioms that only hold for agendas of size two:
- *positive responsiveness₂*
- *responsiveness₂*
- *monotonicity₂*
- *Pareto optimality₂*

### From Condorcet to Arrow
An SCF *f* satisfies
- *independence of infeasible alternatives* (IIA) if $\forall A, R_N, R'_N, i$, $R_i \mid_A = R'_i\mid_A$ holds, then $f(R_N, A) = f(R'_N, A)$.
	- IIA is weaker than neutrality
- *Pareto optimality* if $\forall A, R_N, i$ and $x, y \in A$, $x P_i y$ holds, then $y \notin f(R_N, A)$
	- Pareto optimality₂ weaker than monotonicity₂ under the additional assumption: $\forall x, y \in U$, there is $R_N$ s.t $\forall R'_N$ with $R'_N \mid_{\{x,y\}} = R_N \mid_{\{x,y\}}$, $f(R'_N, \{x, y\}) = \{x \}$.

- *dictatorial* if there exists a voter $i$ such that $\forall A, R_n$, and $x \in A$: 
$$(\forall y \in A \setminus \{x\} x P_i y) \implies f(R_N, A) = \{x\}$$
(namely, if voter $i$ prefers $x$ the most, $x$ is uniquely chosen).

![[condorcet-vs-arrow-axioms.png]]


### Arrow's Impossibility Theorem
> **Theorem** (Kenneth Arrow, 1951; fixed in 1963).
> *There is no SCF that satisfies IIA, Pareto optimality, non-dictatorship, and transitive rationalizability if $m \geq 3$*.

Even a stronger version holds for IIA₂, Pareto-optimality₂ and non-dictatorship₂.