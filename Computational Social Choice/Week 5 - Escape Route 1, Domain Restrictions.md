Now we will explore "Escape Routes" from Impossibility.

# Escape Route 1: Domain Restrictions
Goal: restrict the *set of possible preference relations*.

An SCF satisfies some property *in domain $D$* if it satisfies it for all $R_N \in D(U)^n$ where $D(U) \subseteq R(U)$.

- Example (that we saw before): the linear preferences $D_{LIN}(U) = \{ R \in R(U): \forall x,y \in U: x P y \vee y P x\}$.
	- Interesting note: Arrow's impossibility theorem still holds, but the two versions for strict and non-strict preferences are independent in the sense that none can be directly proven from the other. (Reason: someone might be a dictator in a smaller domain and not in a wider one).

### Transitivity, Strategyproofness, Participation
Recall the majority relation from [[Week 3 - Formalizing Social Choice, May's Theorem, Condorcet Paradox#The Condorcet Paradox]].
- an SCF $f$ can be *manipulated by strategic abstention* if for some $R_N, A$ and $i$: $$f(R_{n-i}, A) P_i f(R_N, A)$$

QUESTION: how does (Set $P_i$ Set) work? Probably works only for singleton sets.

- an SCF satisfies *participation* if it cannot be manipulated by strategic abstention.

The SCF $R_M \mapsto \max(R_M, A)$ is a well-defined SCF within domain $D$ if $R_M$ is acyclic in $D$ (Lecture 2, Lemma 1).

> **Theorem**.
> If $R_M$ is *transitive* in $D$, then $\max(R_M, A)$ satisfies Strategy-Proofness and Participation.

Proof: (intuition: show that you cannot manipulate from one Condorcet winner to another). Manipulating cannot improve the majority relation winner. Strategic abstention also doesn't work. [TODO... actual proof]

## Dichotomous Preferences and Approval Voting 
Dichotomous Preferences: only two indifference classes. There are *some alternatives that you like*, and *some alternatives that you dislike*. Formally:

$$D_{DI}(U) = \{ R \in R(U) \mid \forall x,y,z \in U: x P y \Rightarrow zIx \vee zIy \}$$

> **Theorem** (Inada, 1964).
> $R_M$ is transitive in domain $D_{DI}$.

Proof. For $x \in U$, define $n(x) = |\bigcup_{y\in U} N_{xy} |$ (the number of voters who (weakly) prefer $x$ to any alternative; Recall the $N_{ab}$ and $n_{ab}$ notations from [[Week 3 - Formalizing Social Choice, May's Theorem, Condorcet Paradox#Social Choice from Pairs]]).

We get, where the last step uses dichotomousness:
$$x R_M y \Leftrightarrow n_{xy} \geq n_{yx} \Leftrightarrow n(x) \geq n(y)$$

This equivalence directly implies transitivity by embedding $x, y$ in the transitively ordered set $\mathbb{N}$.

In the domain $D_{DI}$, $\max(R_M, A)$ is called *approval voting*: The alternatives with the highest number of approvals win.

Important note: not only the submitted preferences should be dichotomous, but the intrinsic preferences of the voters.

QUESTION: do the strategyproofness guarantees break down if intrinsic preferences are non-dichotomous?


## Simplifying Assumptions: "Declaration of Oddity"
==For the remainder of the course, we assume:==
- all $R_i$ are strict (anti-symmetric), and thus linear orders
- the number of voters $n$ is odd

Taken together, these imply that there are *no ties* in the majority relation.

That is: from now on, $R(U)$ is the set of *anti-symmetric, transitive and complete relations* (i.e. the linear orders).

Impact of these assumptions
- some results hold witout these restrictions (but might be harder to prove)
- some results hold in weaker forms
- some results have been generalized with additional axioms
- some results have *not* been generalized in a satisfactory way

## Single-Peaked Preferences
Assumption: there is a natural linear order of the alternatives, and voters have one single peak as most-preferred alternative, and the preference decreases as we move away from this peak.

**Definition**. $R_N$ is *single-peaked* wrt. some linear order $>$ over $U$ if for all $x,y,z \in U$ and voters $i$, $$(x > y > z) \vee (z > y > x) \Rightarrow (x P_i y \Rightarrow y P_i z)$$

QUESTION: I have doubts that a peak might be "skipped over" with this. Look at it again to be sure it is correct.

QUESTION: How many single-peaked preference profiles are there, for a peak in a given position?

The domain of single-peaked preferences wrt. $>$ is denoted $D^{>}_{SP}$.

Note: this concept can be generalized to multiple dimensions in principle, but all the nice results break down in this case.

##### Example applications
- left/right political spectrum
- location of desirable facilities on road
- temperature for joint thermostat
- grading system
- tax rate
- size of public park
- etc.

### Transitive $R_M$ on Single-Peaked Preferences
> **Theorem** (Black 1948, Arrow 1951).
> On single-peaked preferences, $R_M$ is transitive

Proof.
Show: $x P_M y$ and $y P_M z$ implies $x P_M z$ (transitivity).

Case 1: x > y > z (wlog., or z > y > x).
Because of single-peakedness, $N_{xy} \subseteq N_{yz}$. By transitivity of the individual preferences: $N_{xy} \subseteq N_{xz}$. Therefore $|N_{xz}| \geq |N_{xy}| > n/2$.

Case 2: z > x > y (wlog., or y > x > z).
Because of SP, $N_{zx} \subseteq N_{xy}$. Because of transitivity, $N_{zx} \subseteq N_{zy}$. Therefore and because a majority prefers y to z, $n/2 > |N_{zy}| \geq |N_zx|$, and finally $|N_{xz}| > n/2$.

Case 3: y > z > x (wlog., or x > z > y). (*this case is different and therefore interesting*).
$N_{yz} \subseteq N_{zx}$ by SP, $N_{yz} \subseteq N_{yx}$ by transitivity. Therefore $n/2 < |N_{yz}| \leq |N_{yx}|$. Therefore $|N_{xy}| < n/2$, which is a contradiction! This last case can never occur.


### Condorcet Winner on Single-Peaked Preferences
On single-peaked preferences, there is always a unique Condorcet winner. Namely, the Condorcet winner is the top choice of the median voter.

*Proof*: Let $x, y$ be adjacent alternatives, denote by $t_i$ the top choice of voter $i$. Then $x P_M y$ iff $|\{i : t_i \leq x\}| > |\{i : t_i \geq y\}$. Then by transitivity of $P_M$: $x$ is a Condorcet winner if $|\{i:t_i \leq x\}| > \frac{n}{2}$ and $|\{i : t_i \geq x\}| > \frac{n}{2}$.

The SCF $Max(R_M, A)$ is known as *median voting* in the domain $D_SP^{>}$.

Some more observations:
- knowing the top choices is enough to compute the median voter, i.e. the Condorcet winner
- even with single-peaked preferences, the plurality winner might be different from the Condorcet winner!

##### Strategy-Proofness of Median rule
Median voting satisfies *strategy proofness* and *participation* in the domain of single peaked preferences.

### Deciding Single-Peakedness of a Profile
Assume a preference profile is given and we want to decide whether there *exists a linear ordering of the alternatives* wrt. which it is single-peaked.

Observe that the last-ranked alternative of each voter must lie at the very left or the very right of the underlying order. For example, if there are more than two last-ranked alternatives accross all voters, the profile cannot be single-peaked.

This observation can be extended to an algorithm.

##### Single-Peaked Algorithm
Linear-time algorithm.

 1. set leftmost alternative to $z_l$ and rightmost one to $z_r$
 2. let $A = U$ be the set of alternatives that still need to be placed
 3. while $|A| \geq 2$: 
	 1. Let $l$ and $r$ be the current left-innermost and right-innermost alternative
	 2. Define $B$, $L$, $R$:
		 - $B = \{x \in A \mid \exists i: \forall y \in A: y R_i x\}$
		 - $L = \{ x \in B \mid \exists i : r P_i x P_i l \wedge \exists y \in A: y P_i x \}$
		 - $R = \{ x \in B \mid \exists i : l P_i x P_i r \wedge \exists y \in A: y P_i x \}$
	 3. If $|B| \leq 2$, $|L| \leq 1$ and $|R| \leq 1$ as well as $L \cap R = \emptyset$:
		 1. place the alternative in $L$ (if any) next to $l$
		 2. place the alternative in $R$ (if any) next to $r$
		 3. place the alternatives in $B \setminus (L \cup R)$ (if any) arbitrarily in empty slots next to $l$ and $r$
	 4. Else: $R_N$ is not single-peaked, terminate.
	 5. $A = A \setminus B$
 4. If $|A| = 1$, put $x \in A$ into the last remaining slot.


## Characterizing Domains: Value Restriction
Majority rule is transitive within
- the dichotomous preferences $D_{DI}$,
- all domains of single-peaked preferences $D_{SP}^{>}$, and
- all domains of single-caved preferences $D_{SC}$.

There are however other preference profiles on which $R_M$ is transitive. For example:

| 1   | 1   | 1   |
| --- | --- | --- |
| a   | c   | d   |
| b   | a   | a   |
| c   | b   | b   |
| d   | d   | c   |

We want a complete characterization of such domains (under the declaration of oddity, which rules out dichotomous preferences for $m>2$).

> **Definition** (value-restricted).
> A domain $D$ is *value-restricted* if for each $x, y, z \in U$, there is some alternative (say x) s.t.
> - $x$ is never the worst alternative ($\forall R \in D: x P y \vee x P z$)
> - OR $x$ is never the best alternative ($\forall R \in D: y P x \vee z P x$)
> - OR $x$ is never the middle alternative ($\forall R \in D: (x P y \wedge \dots$)

Some intuition: A domain is value-restricted if it does not contain the Condorcet cycle as a forbidden substructure.

|     |     |     |
| --- | --- | --- |
| x   | y   | z   |
| y   | z   | x   |
| z   | x   | y   |


> **Theorem** (Sen & Pattanaik, 1969).
> $R_M$ is transitive in domain $D$ if and only if $D$ is value-restricted. *(Proof: T exercise)*

Whether a domain is value-restricted can be checked in polynomial time (using the naive triple-checking algorithm).