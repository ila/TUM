## Arrow's Impossibility

Recall: 
![[condorcet-vs-arrow-axioms.png]]

While the stronger axioms like anonymity might not be so important and be violated in very special cases without making things too unreasonable, the much weaker non-dictatorship is definitely undesirable.

Arrow's Impossibility Theorem: 
> **Theorem** (Kenneth Arrow, 1951; fixed in 1963).
> *There is no SCF that satisfies IIA, Pareto optimality, non-dictatorship, and transitive rationalizability if $m \geq 3$*.


### Version for *Social Welfare Functions*
*Social Welfare Function* (SWF): $g: R(U)^n \to R(U)$ (returns a *collective preference relation* instead of a set).

SWFs and SCFs are connected: $x g(R_N) y \Longleftrightarrow x \in f(R_n, \{x, y\})$ (since we assume the SCF is rationalizable).

QUESTION: are SWFs always transitive? ==> Yes! $R(U)$ is the set of *complete and transitive relations*.


##### Properties reformulated for SWFs
Denote the collective preference relation for a given $R_N$ as $R = g(R_N)$.

g satisfies
- *IIA* if for all $R_N, R'_N, x, y$: if $\forall i \in N: R_i \mid_{\{x, y\}}$, then $R\mid_{\{x, y\}} = R'\mid_{\{x, y\}}$
- *Pareto-optimality* if for all $R_N, x, y$: if for all $i$, $x P_i y$, then $x P y$
- is *dictatorial* if for some $i \in N$ and all $R_N, x, y$: $x P_i y$ implies $x P y$

> **Theorem** (Arrow, 1951; 1963).
> *Every SWF that satisfies IIA and Pareto-Optimality is dictatorial if $m\geq 3$.*

### Illustration of Arrow's Theorem
...for $m=3, n=2$, strict preferences. See slides. Especially IIA has a lot of power to fill the table quickly.


### Interlude: Computer-Aided Theorem Proving
Lin & Tang, 2008: induction-based proof (induction step for increasing alternatives and increasing voters).

Base case ($m=3, n=2$) proved by SAT solver (about $10^{28}$ SWFs for the base case).


### Proof of Arrow's Impossibility Theorem
Let $g$ be an SWF and $m \geq 3$.
- a group of voters $G \subseteq N$ is *decisive for a against b* ($a D_G b$) if for all $R_N$, ($\forall i\in G: a P_i b$) implies $a P b$
- a group $G$ is *decisive* if it is decisive for all pairs $a, b$.
- a group $G$ is *semidecisive* for a against b, denoted $a \tilde{D}_g b$ if for all $R_N$: ($\forall i \in G: a P_i b \wedge \forall j \notin G: b P_j a$) implies $a P b$
	- *decisiveness* is stronger than *semidecisiveness*.


**Lemma**  (Field Expansion Lemma): Let $g$ be an IIA and Pareto-optimal SWF, let $G \subseteq N$. Then if for some $a \neq b$, $a \tilde{D}_G b$, this implies that for all $x, y$: $x D_G y$. (If a group is decisive for some pair of alternatives, it is decisive for all pairs of alternatives.)

*Proof.*
Let $x \neq a, b$.

(1) $a D_G x$ holds: Consider the partially specified preference profile (actually, a whole class of preference profiles):

| G   | N \ G |
| --- | ----- |
| a   | b     |
| b   |       |
| x   |       |

Because of the semidecisiveness, $a P b$. Because of Pareto optimality, $b P x$. Because of transitivity, $a P x$. Because of IIA, $a D_G x$. Fixing of $b$ in above profile is valid because of IIA.

(2) $b D_G x$ holds:

| G   | N \ G |
| --- | ----- |
| b   |       |
| a   |       |
| x   | a     |

By (1), $a P x$. By PO, $b P a$. By transitivity, $b P x$. By IIA, $b D_G x$.

- Repeated application of (2) shows that $D_G$ is a complete relation. 
- $D_G$ is symmetric: If $x D_G y$, by (2), $y D_G z$. Then by (1), $y D_G x$.



Now let $G$ be some decisive group with $|G| \geq 2$ (always exists $G=N$ because of PO). Partition $G$ into two nonempty subgroups. Consider the preference profile:

| $G_1$ | $G_2$ | $N \setminus G$ |
| ----- | ----- | --------------- |
| a     | b     | c               |
| b     | c     | a               |
| c     | a     | b               |

- Since $b D_G c$, we get $b P c$.
- Case 1, $a P c$: by IIA, $a \tilde{D}_{G_1} c$. By the field expansion lemma, $G_1$ is decisive.
- Case 2, $c R a$: By transitivity, $b P a$. By IIA, $b \tilde{D}_{G_2} a$. By the field expansion lemma, $G_2$ is decisive.

Now, one of $G_1, G_2$ is decisive. Therefore repeatedly apply this, until there is a decisive group with *only one element*, i.e. a *dictator!*

##### Some additional comments about the proof
- The final part is called *Group Contraction Lemma*.
- The *Field Expansion Lemma* also holds for a quasi-transitive relation


### Undesirable Groups of Voters
Dictators: extreme example of high concentration of power. But this notion can be generalized to other undesirable groups.

- Dictator: Decisive group with one element
	- $x P_i y$ ==> $x P y$
- Weak dictator: Voter who can force an alternative into the choice set
	- $x P_i y$ ==> $x R y$
	- you can think of a weak dictator as a *vetoer* who can veto $y P x$
- Oligarchy: Decisive group of weak dictators
	- seems a bit odd, but apparently quite natural because three papers considered it independently shortly after Arrow's theorem
- Collegium: Non-empty intersection of all decisive groups
	- if the collegium is non-empty

Implications: Dictator ==> Oligarchy ==> Weak Dictator ==> (under PO) Collegium


### Overview over Arrovian Impossibility Results
| Anonymity2        | Neutrality | Pos. Resp.    | Rat              | $n \geq 3$ | Condorcet/May (1785; 1952)       |
| ----------------- | ---------- | ------------- | ---------------- | ---------- | -------------------------------- |
| No Dictator2      | IIA2       | Par.Opt.2     | Trans.Rat.       |            | Arrow (1951)                     |
| No Oligarchy2     | IIA2       | Par.Opt.2     | Quasi-Trans.Rat. |            | Gibbard (1969)                   |
| No Oligarchy2     | IIA2       | Pos.Resp.2    | Rat.             | $n \geq 4$ | Mas-Colell & Sonnenschein (1972) |
| No Weak Dictator2 | Neutrality | Monotonicity2 | Rat.             | $m \geq n$ | Blau & Deb (1977)                |
| No Collegium      | --         | Par.Opt.2     | Rat.             | $m \geq n$ | Brown/Banks (1975, 1995)         |
| No Weak Dictator2 | Neutrality | Par.Opt.2     | Rat.             | $m > n$    | Austen-Smith & Banks (1999)      |

QUESTION: Aren't there also boundary conditions on $m$ or $n$ for Arrow's/Gibbard's results?

### Ways to escape impossibility results
Four ways:
- consider restricted domain of preferences
	- e.g. approval voting, median voting
- replace consistency (rationalizability) with a variable-electorate condition
	- e.g. conditions similar to $\alpha, \beta, \gamma$ for sets of *voters* instead of alternatives
	- ==> basically scoring rules in some sense ([TODO... check this later])
- only require expansion consistency $\gamma$, not contraction consistency $\alpha$
	- top cycle, uncovered set, Banks set, tournament equilibrium set
- weaken both consistency conditions
	- *bipartisan set*, related to mixed strategies in game theory

