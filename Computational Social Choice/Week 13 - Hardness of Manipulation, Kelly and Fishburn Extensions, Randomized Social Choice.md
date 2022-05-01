## Hardness of Manipulation
Idea: use NP-hardness as a shield against manipulation.

Finding a beneficial manipluation for the following SCFs is NP-hard:
- second-order copeland
	- due to Bartholdi et al., 1989
- instant runoff
	- due to Bartholdi and Orlin, 1991
- Baldwin's rule (Borda score with iterated deletions)
	- Narodytska et al., 2011

Those are the most important results. Many more similar result that use some kinds of weighted voting, or coalitional manipulation.

Some limitations to these approaches:
- the hardness is *in the number of candidates*, and usually the number of candidates is not so large
- often the NP hardness comes from a few hard instances, but practical instances are sometimes much easier to manipulate - then: no barrier against manipulation
	- random manipulations work well
	- there is a large class of distributions/restricted domains that are easy to manipulate


## Kelly's Extension
Idea to deal with irresoluteness: we need to have a tie breaking mechanism.

Strong assumption for Kelly's Extension: *a single alternative is eventually chosen, but the voters do not know anything about how the tie-breaking mechanism works*.

Under this assumption, the *preferences over choice sets* are given by *Kelly's preference extension* $R^K \subseteq F(U) \times F(U)$:

$$X R^K Y \Leftrightarrow \forall x \in X, y \in Y: x R y$$

(many sets are of course not comparable by this preference extension; $R^K$ is incomplete).

For the strict part, it holds that

$$X P^K Y \Leftrightarrow \forall x \in X, y \in Y: (x R Y) \wedge \exists x \in X, y \in Y: (x P y)$$

Watch out: even if $a P b P c$, $\{a, b\}$ and $\{a, b, c\}$ are incomparable!

Warning: By this definition, $R^K$ is not reflexive. It does not matter for our purposes because we are only concerned with the strict part.

### Kelly Strategyproofness
An SCF is $R^K$-strategyproof if there are no $R_N, R'_N , i$ s.t. $R_j =R'_J$ for all $j \neq i$ and $f(R'_N) P_i^K f(R_N)$.

Most $SCFs$ *violate* $R^K$-strategyproofness: e.g. plurality, Borda's rule, Copeland's rule, Nanson's rule.

> **Theorem** (Brandt, 2015). Every strongly monotonic SCF is $R^K$-strategyproof.

(Compare the [[Week 12 - Strategyproofness, Gibbard-Satterthwaite#Strong Monotonicity|Muller-Satterthwait Theorem]] for the resolute case; However observe that this result only goes in one direction!)

> **Thereom**. If an SCF satisfies monotonicity, $\hat{\alpha}$ and IIA, it satisfies strong monotonicity.

*Proof*. See notes.

> **Corollary** (Kelly strategyproofness of $BC$ and $UC$)
> -  The [[Week 11 - Escape Route 4, Weaken Both Consistency Conditions; Bipartisan Set#The Bipartisan Set|Bipartisan Set]] $BP$ is $R^K$-strategyproof
> - The [[Week 8 - Computing Kemeny, Escape Route 3, Top Cycle#The Top Cycle|Top Cycle]] $TC$ is $R^K$-strategyproof

Comment: The [[Week 9 - Computing Top Cycle, Uncovered Set#Uncovered Set|Uncovered Set]] $UC$ also satisfies $R^K$-strategyproofness, even though it does not satisfy $\hat{\alpha}$.
	- (because $BP \subseteq UC$ and $BP = UC$ when $BP$ returns a singleton)
	
Open question: *Is $BA$ $R^K$-strategyproof?*

### Kelly Participation
An SCF satisfies *$R^K$-participation* if for no $R_N, i \in N$: $f(R_{N-i}) P_i^K f(R_N)$.

> **Theorem**. Every majoritarian $R^K$-strategyproof SCF also satisfies $R^K$-participation.

*Proof*. 
Proof by contrapositive: simulate abstention by having a voter cancel out his own preferences. Show: No $R^K$-participation => not $R^K$-strategyproof. "Double" a preference profile where all voters are there twice, add preferences $R_i$ and $\overline{R_i}$ (= $R_i$ inverted) to it: by majoritarianness, it holds that
$$f(2R_{N_i} + R_i + \overline{R_i})
= f(R_{N-i}) P_i^K f(R_N)
= f(2 R_N)
= f(2 R_N + R_i + R_i)$$
(i.e. only one voter changed, and could manipulate by it).

Comment: this argument works for any extension, not just Kelly's extension.

By this theorem, $TC$, $UC$, $BP$ all also satisfy $R^K$-participation.

### Kelly Strategyproofness: Negative Results
There are some impossibility results concerning $R^K$-strategyproofness. These are not very strong, since they impose strong conditions.

> **Theorem** (Barberà 1977; Kelly 1977).
> Every non-imposing $R^K$-strategyproof, quasi-transitively rationalizable SCF is weakly dictatorial when $m \geq 3$

Comment: the conditions for the theorem are quite restrictive; every Pareto-optimal, quasi-transitively rationalizable SCF that satisfies IIA is weakly dictatorial (see Exercise 16).

> **Theorem** (Barberà 1977).
> Every Pareto-optimal, $R^K$-strategyproof, positive responsive SCF is dictatorial when $m \geq 4$.

Comment: positive responsiveness requires a high degree of decisiveness; of the SCFs we studied, only Borda's rule and Black's rule satisfy it.


## Fishburn's Extension
Alternative assumption: The tiebreaking is performed with respect to some unknown, but consistent ordering. (Whereas in [[#Kelly's Extension]], the tiebreaking did not have to satisfy any amount of consistency).

The preferences over choice sets given by *Fishburn's preference extension $R^F \subseteq F(U) \times F(U)$*:

$$X R^F Y \Leftrightarrow (\forall x \in X \setminus Y, y \in Y: x R y) \wedge (\forall x \in X, y \in Y \setminus X: x R y)$$

- Fishburn's extension is incomplete as well: e.g. $\{a,c\}$, $\{b\}$ are incomparable
- if $a P b P c$, then $\{a, b\} P^F \{a, b, c\} P^F \{b, c\}$ (unlike in Kelly's extension): more sets are comparable under $P^F$ than under $P^K$.

### Fishburn Strategyproofness and Participation
Fishburn Strategyproofness and Participation are defined equivalently as [[#Kelly Strategyproofness]].

Fishburn Strategyproofness is *stronger* than Kelly Strategyproofness.

An SCF satisfies set non-imposition if for every $X \subseteq A$, there is some $R_N$ such that $f(R_N) = X$.

> **Theorem** (Brandt & Lederer, 2021).
> $TC$ is the *only majoritarian SCF* satisfying $R^F$-strategyproofness and set non-imposition.
>
> Furthermore, $TC$ is the finest majoritarian $R^F$-strategyproof SCF.

There are a few other $R^F$-strategyproof SCfs, e.g. $f_{\text{Pareto}}$.

### Fishburn Strategyproofness: Negative Results
The following impossibilities were shown using SAT solvers:

> **Theorem** (Brandl et al. 2015).
> There is no Pareto-optimal majoritarian SCF that satisfies $R^F$-participation if $m \geq 5$.

> **Theorem** (Brandt et al. 2018)
> There is no Pareto-optimal and $R^F$-strategyproof anonymous SCF if $m \geq 5$ *and preferences are weak*.

Comment how one can use SAT solvers for something like this: The SAT solver gives the induction base case, and there is a very easy induction step (e.g. add more indifferent voters) which allows to go all $m \geq 5$.


## Probabilistic SCFs
A *social decision scheme* (SDS) $f$ maps from preference profiles to lotteries over alternatives. Formally: 

> An SDS is a function $f: R(U)^N \to [0, 1]^U$ such that for all $R_N$ and $p = f(R_N)$, $\sum_{x \in U} p(x) = 1$.

Idea: voters have an underlying utility function, but they only submit preferences; we argue about the possible utility representations that a voter might have.

An SDS is *manipulable* if a voter can increase their *expected utility* by misrepresenting their preferences. Formally:

> An SDS $f$ is *manipulable* if for some $R_N, R'_N, i \in N$ and $u: U \to \mathbb{R}$ such that $R_j = R_j'$ for all $j \neq i$,
> $$\forall x, y \in U: (u(x) \geq u(y) \Leftrightarrow x R_i y),$$
> and
> $$f(R_N) = p, ~~ f(R'_N) = p', ~~\sum_{x\in U}p'(x) u(x) > \sum_{x \in U} p(x) u(x).$$


> **Theorem**. Every SDS that puts probability 1 on a Condorcet winner can be manipulated when $m, n \geq 3$.

*Proof*. Similar to a [[Week 12 - Strategyproofness, Gibbard-Satterthwaite#Strong Monotonicity Number of Alternatives|proof from last week]].

Consider the preference profile $R_N$:

| 1   | 1   | 1   |
| --- | --- | --- |
| a   | b   | c   |
| b   | c   | a   |
| c   | a   | b   |

Let $f(R_N) = p$, Without loss of generality, assume $p(a) > 0$. Create $R_N'$:

| 1     | 1   | 1   |
| ----- | --- | --- |
| a     | b   | c   |
| ==c== | c   | b   |
| ==b== | a   | b   |

Then $f(R_N') = p'$ and $p'(c) = 1$ (Condorcet winner!). If we choose the utility for $a$ large enough (sufficiently much larger than $c$), the first voter has an incentive to mis-represent her true preferences ($R_N'$) and instead give $R_N$.

### Random Dictatorship
*Random dictatorships* are SDSs of the following form:
- pick a voter at random (the *dictator*)
- choose the favorite alternative of the dictator.

In a uniform random dictatorship, the voters are picked uniformly at random. Uniform random dictatorships are anonymous!

An SDS is *non-imposing* if its image contains all (degenerate) lotteries that yield any given alternative with probability 1.

> **Theorem** (Gibbard, 1977).
> Every non-imposing, non-manipulable SDS is a random dictatorship when $m \geq 3$.

(This is kind of a negative result - it's from the 1970's, after all - but is it really a negative result? Other than dictatorships, random dictarships can be useful in some settings!)

Non-imposition (in the probabilistic context) seems a rather strong property: think of SDS that never put 1 on any alternative, but at most $1 - \epsilon$. There is another result in this context

> **Theorem** (Barberà 1979).
> There are probabilistic variant of Borda's rule and Copeland's rule that are non-manipulable (but violate non-imposition).
> - in the probabilistic Borda's rule, every alternative gets probability proportional to its Borda score


### Maximal Lotteries
Proposed independently by Germain Kreweras and Peter C. Fishburn.

Let $(M_{x,y})_{x,y\in A}$ be the *majority margin matrix*: $M_{x,y} = n_{xy} - n_{yx}$. This is a skew-symmetric matrix.

A lottery $p$ is *maximal* if $p^\top M \geq 0$.

![[maximal-lottery.png]]


- A maximal lottery essentially picks a "randomized Condorcet winner" (Kreweras 1965; Fishburn 1984); if there is a Condorcet winner, it is assigned probability 1.
- It can be interpreted as a [[Week 6 - Escape Route 2, Variable-Electorate Condition#Classification of SCFs based on their Level of Abstraction|C2]] version of optimal distributions (as used for [[Week 11 - Escape Route 4, Weaken Both Consistency Conditions; Bipartisan Set#The Bipartisan Set|the bipartisan set]] $BP$).
- No other lottery $q$ is preferred by an expected majority: $p^\top M q \geq 0$

A *unique maximal lottery* always exists, and can be efficiently computed via linear programming.

![[on-paradoxes-afflicting-voting-procedures.png]]

(for lotteries, these properties are extended more or less naturally; for some, there is more than one possible reasonable extension).


#### Maximal Lotteries vs. Impossibility Theorems
Maximal lotteries
- can be characterized using reinforcement, Condorcet consistency and Cloning consistency (Brandl et al. 2016)
	- resolving the [[Week 6 - Escape Route 2, Variable-Electorate Condition#Young-Levenglick Theorem Incompatibility of Borda Condorcet principles|Dilemma of social choice]]!
- Can be characterized using participation and Condorcet consistency (Brandl et al. 2018)
- can be characterized using IIA and Pareto optimality (Brandl et al. 2020)
	- resolving Arrow's impossibility!

Takeaway: with randomization, several social choice impossibilities can be resolved!