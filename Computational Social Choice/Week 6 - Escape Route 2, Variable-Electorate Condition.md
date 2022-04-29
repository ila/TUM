# Escape Route 2: Replace Consistency with Variable-Electorate Condition

### A Brief History Lesson: Borda vs. Condorcet
- Jean-Charles Chevalier de **Borda** (1733 - 1799)
	- scientist; adventurer
	- involved in standard-meter definition (1/1e7-th distance north pole - equator)

- Marie Jean Antoine Nicolas Caritat, Marquis de **Condorcet** (1743 - 1794)
	- early advocate for equal rights


### Family of Scoring Rules
Feasible set $A$ fixed.
- a *score vector* is a vector $s \in \mathbb{R}^{|A|}$
	- voter ranks alternative in position $i$ --> it recieves $s_i$ votes

Set $s^1,\dots,s^m$ be a collection of score vectors, $s^i$ of dimension $i$. Define the corresponding *scoring rule*:
$$f(R_N, A) = \arg\max s(x, A) ~~~\text{ where }~ s(x,A) = \sum_i s^{|A|}_{|\{y \in A: y R_i x\}|}$$

Examples:
- Borda's rule: $s^{|A|} = (|A|-1,|A|-2,\dots,0)$
- Plurality rule: $s^{|A|} = (1, 0, \dots, 0)$
- Anti-plurality rule: $s^{|A|} = (1, 0, \dots, 0)$

But since we said that $A$ is fixed, we don't really need a collection of score vectors.

##### Properties of Scoring Rules
- Scoring rules are *invariant* under componentwise *positive affine transformations* $x \mapsto ax+b, ~a>0$.
	- In particular, for any score vector $(s_1, s_2, s_3)$ we can find an equivalent score vector $(1, s, 0)$.
- Scoring rules can be efficiently computed
- A scoring rule satisfies monotonicity if $s_1 \geq \dots \geq s_{|A|}$: see next lemma
- A monotonic scoring rule is one that satisfies $s_1 > s_{|A|}$

> **Lemma (Monotonicity Characterization)**. Assume there are enough voters. A scoring rule is monotonic if and only if $s_1 \geq s_2 \geq \dots \geq s_m$.
> (the only if direction of the proof only works with $m=n$ voters)

*Proof (one direction)*. Assume that for some $k$, $s_{k+1} > s_k$. Consider a Condorcet cycle style profile on $m$ alternatives: every alternative obtains every position exactly once. Therefore all have the same score $s(x_i)$ and therefore the chosen set is $f(R_N) = A$. Now switch the positions of $a_k, a_{k+1}$ in the first preference profile. Then $f(R_N') = \{a_k\}$: i.e. $a_{k+1}$ dropped out even |}$:though it was reinforced.  


### Family of Condorcet Extensions
The scoring rules we saw don't offer that much variety. The *Condorcet extensions* family on the other hand is much more varied.

- an SCF $f$ is a *Condorcet extension* if it uniquely picks Condorcet winners whenever they exist: $f(R_N, A) = \{x \}$ if $x$ is a Condorcet winner in $A$ according to $R_N$.

#### Classification of SCFs based on their Level of Abstraction
(...Fishburn 1977)

The way that this classification works: Higher number ==> the SCF is allowed to depend on more information.

- **C1**: $f$ only depends on the majority relation $R_M$
	- degree counting in the majority graph: *Copeland's rule* (maximize degree)
- **C2**: $f$ only depends on $(n_{xy})_{x,y\in U}$ and $f$ is not C1
	- maximin: $f_{maximin}(R_N, A) = \arg\max_{x\in A} \min_{y \in A\setminus\{x\}} n_{xy}$
	- Borda's rule: $f_{Borda}(R_N, A) = \arg\max_{x\in A} \sum_{y\in A\setminus\{x\}} n_{xy}$
		- apparently Borda's rule is the only scoring rule that is C2
- **C3**: $f$ is neither C1 nor C2, i.e. it depends on the full preference profile.
	- Young's rule: picks *all alternatives* that can be made Condorcet winner *by removing as few voters as possible* (well-defined: removing all voters but one obviously yields a Condorcet winner)
		- Computing Young's rule is hard: namely, $\Theta_2^P$-complete ($P$ with logarithmically many $NP$ oracle queries)


The classification is usually applied to Condorcet extensions, but the "C" does not stand for Condorcet! It can also contain non Condorcet extensions

##### Examples: Copeland, Maximin, Young
![[c1-c2-c3-rules-examples.png]]
(red edges are majority edges)

- Copeland: a, b (those both have 2 outgoing edges each)
- Maximin: b, c, d
	- the smallest outgoing edge labels are: (1, 2, 2, 2)
- Borda: b, d (obvious)
- Young: d, c (not so obvious)
	- removing one voter is not enough: by declaration of oddity, removing one voter could only lead to a tie, not a strict Condorcet winner
	- remove one from first, one from second -> c wins
	- remove one from first, one from third -> d wins


### Scoring Rules <--> Condorcet Extensions
##### Borda's Rule is no Condorcet Extensions
| 3   | 2   |
| --- | --- |
| a   | b   |
| b   | c   |
| c   | a   |

Here a is the Condorcet winner, but b wins.

##### Scoring Rules are no Condorcet Extensions
Scoring rules are no Condorcet extensions in general if $m \geq 3$:

For monotonic rules:

|       | 4   | 3   | 2   | 2   |
| ----- | --- | --- | --- | --- |
| $s_1$ | c   | b   | a   | b   |
| $s_2$ | b   | a   | c   | c   |
| $s_3$ | a   | c   | b   | a   |

Wlog. assume $s_1=1, s_2=s, s_3=0$ due to the [[#Properties of Scoring Rules|positive affine transfomations property]].

Then $s(a) = 2+3s$, $s(b) = 5+4s$, $s(c) = 4+4s$. ==> $b$ wins. The Condorcet winner however is $c$.

For non-monotonic rules, make a case distinction. Assume $s_2 > s_1$:

|       | 1   | 1   | 1   |
| ----- | --- | --- | --- |
| $s_1$ | b   | b   | c   |
| $s_2$ | a   | c   | a   |
| $s_3$ | c   | a   | b   |

(Condorcet winner b, but either a or c wins in the scoring rule)

Assume $s_3 > s_2$:

|       | 1   | 1   | 1   |
| ----- | --- | --- | --- |
| $s_1$ | a   | b   | c   |
| $s_2$ | c   | c   | a   |
| $s_3$ | b   | a   | b   |

(Condorcet winner c, but b wins in the scoring rule)


### Properties of Borda's Rule
- Borda's rule picks alternative with *highest average rank*

Condorcet winner are never Borda losers:
> **Theorem (Smith, 1973)**. A Condorcet winner is *never the alternative with the lowest Borda score*. Borda's rule is the unique scoring rule for which this is the case.
> Also, a Condorcet loser never has the highest Borda score.

Borda's rule maximizes the probability of Condorcet winners across all scoring rules: 
> **Theorem (Gehrlein et al., 1978)**. When all preference profiles are equally likely, Borda's rule maximizes the probability over all scoring rules that a Condorcet winer is chosen if it exists.


### Unifying Borda & Condorcet
Can we unify the advantages of Borda's and Condorcet's Rules?

##### Black's Rule
Return the Condorcet winner if one exists, the Borda winner otherwise
- obviously a Condorcet extension
- but somewhat ad-hoc

##### Baldwin's Rule
(Recall first exercise sheet)

Runoff method based on Borda's rule
- delete alternatives with minimal Borda scores
- also a Condorcet extension

Another rule: delete alternatives with below-average Borda scores

## Variable Electorates
Now: define properties similar to the consistency condtions ($\alpha$, $\gamma$ etc.), but with respect to sets of voters ("electorate"), not sets of alternatives.

Reinforcement: the alternatives chosen simultaneously by two disjoint electorates are precisely the alternatives chosen by the union of the electorates.

> **Definition (Reinforcement)**. An SCF $f$ satisfies *reinforcement* if for all $A$, disjoint $N, N'$ and all $R_N \in R(U)^N$, $R_{N'} \in R(U)^{N'}$ that satisfy $f(R_N, A) \cap f(R_{N'}, A) \neq \emptyset$ the following holds:
> $$f(R_N, A) \cap f(R_{N'}, A) = f(R_N \cup R_{N'}, A)$$
> - When dealing with reinforcement, we do *not* assume an odd number of voters


This is the equivalent of $\alpha \wedge \gamma$ for variable electorates!

- $a \wedge \gamma: ~~~~~~~~~~~~~~ x \in f(R_N, A) \cap f(R_N, A') \Leftrightarrow x\in f(R_N, A \cup A')$
- Reinforcment: $x \in f(R_{N}, A) \cap f(R_{N'}, A) \Leftrightarrow x \in f(R_N \cup R_{N'}, A)$

### Characterization of Scoring Rules
We can compose scoring rules to break ties: An SCF $f$ is a *composed scoring rule* if there are scoring rules $f_1, \dots, f_k$ s.t. $f(R_N, A) = f_1(R_N, f_2(R_N, \dots(f_k(R_N, A))))$

> **Theorem (Smith 1973, Young 1975)**. A neutral and anonymous SCF is a composed scoring rule if and only if it satisfies reinforcement.

In other words: If you want reinforcement in your SCF, you need to use a scoring rule.

Proof: $\Rightarrow$ is easy, but $\Leftarrow$ isn't and is left out here.

Furthermore: 
- every non-trivial anon. + neutral SCF refines a non-trivial scoring rule
- the non-composed scoring rules can also be characterized, by adding a continuity axiom

**Reinforcement is the defining property of scoring rules!**

### Characterization of Borda's Rule
An SCF satisfies *cancellation* if for all $A$ and $R_N$: $\forall x, a \in A: n_{xy} = n_{yx} \implies f(R_N, A) = A$.

(Cancellation is a rather technical axiom only needed for the characterization).

> **Theorem (Young, 1974)**. Borda's rule is *the only SCF* satisfying neutrality, Pareto-optimality, reinforcement, and cancellation.

Proof: Exercise. Pareto-optimality is only required in 1-voter profiles.

### Young-Levenglick Theorem: Incompatibility of Borda/Condorcet principles

> **Theorem (Young and Levenglick, 1978)**. No Condorcet extension satisfies reinforcement when $m \geq 3$.

Condorcet extensions and reinforcement-satisfying SCFs are two disjoint sets:
![[condorcet-extensions-vs-reinforcement.png]]

This shows that **the rationales between Condorcet's and Borda's ideas are incompatible**.

*Proof.*
![[young-levenglick-theorem-proof.png]]

(note: the arrows in the graphs are labelled with $n_{ab}-n_{ba}$).
(another note: this proof was pointed out by a student of this course, the original one is more complicated)

##### A Condorcet Criticism
Above proof is seen by some as a criticism of Condorcet's paradigm: adding one "symmetric" profile to another one changes the Condorcet winner. Rebuttal: The added profile is not completely symmetric. It cycles clockwise, there is also another anticlockwise cycle. Also it only shows that Condorcet extensions and reinforcements are incompatible, so one could equally critizice the reinforcement property.

##### A Borda Criticism
In the following profile:

|     | 99  | 1   |
| --- | --- | --- |
| 100 | a   | b   |
| 99  | b   | c   |
| ... | ... | ... |
| 0   |     | a   |

==> $b$ wins just because of one voter who feels very strongly about placing $a$ at the bottom.

### Kemeny's Rule
Kemeny's rule

The two disjoint set above do overlap with exactly one neutral function when aggregating preference relations to sets of preference relations: Kemeny's rule.