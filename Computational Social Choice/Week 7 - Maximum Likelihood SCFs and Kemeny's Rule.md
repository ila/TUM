### Voting as Maximum Likelihood Estimation

> "Democracy is the recurrent suspicion that more than half the people are right more than half the time" (E. B. White, 1943)

Probabilistic model (due to Condorcet):
- assume there exists a "true" ("god-given") preference ranking
- Each voter selects a "true" pairwise comparison with probability "p \in (0.5, 1)": voters are imperfect, but right more often than wrong.


An SCF is a *maximum likelihood SCF* for a given $p$ if it yields all alternatives that are   most likely to be top-ranked in the true ranking. In other words, it yields the ranking(s) that maximize(s) the likelihood of observing the given votes, assuming the probabilistic model described above.

> **Theorem (Condorcet Jury Theorem, Condorcet 1785)**. For *two alternatives* and any $p$, majority rule is the maximum likelihood SCF.

Proof. see presentation. Not too complicated. $p_{ab}(R_N) > p_{ba}(R_N) \Leftrightarrow p^{n_{ab} - n_{ba}} (1-p)^{n_{ba} - n_{ab}} > 1$
<==> $\left(\dfrac{p}{1-p}\right)^{n_{ab} - n_{ba}} > 1$ which means $n_{ab} - n_{ba} > 0$.

"The more members you have in a jury, the more likely you are to get the right outcome."

> **Theorem (Young, 1988)**. If $p$ is sufficiently close to 0.5, the maximum likelihood SCF is *Borda's rule*.

Proof left out. Idea: Borda winners are alternatives that receive most "pairwise votes".

QUESTION: ==WHY "sufficiently close to 0.5"? Shouldn't p be close to 1 for this to work well?==

### Kemeny's Rule
A *social preference function* (SPF) - which is a set-valued version of a social welfare function - is a function
$$f: R(U)^N \to F(R(U))$$

Definition:

$$f_{Kemeny}(R_N) = \arg\max_{R \in R(U)} \sum_i |R \cap R_i|$$

i.e. all the $R$ such that the sum of all pairwise preference relations that it shares with any voter $i$ is maximized. Here $R(U)$ is the set of *strict* rankings (declaration of oddity)!

Related (shortly mentioned): Kendall-Tau Distance.

> **Theorem ((Condorcet 1785), Young 1988)**. Kemeny's rule is the maximum-likelihood SPF for any $p$.


Kemeny's rule can be interpreted as a "scoring rule on rankings".

![[kemeny-example.png]]


##### Kemeny's Rule and Majority Graphs
A Kemeny ranking is an *acyclic subgraph with maximum weight* in the majority graph: If cycles were allowed, $R_M$ would have maximal Kemeny score; with cycles not allowed, every Kemeny edge $(y,x)$ that does not coincide with the corresponding majority edge $(x, y)$ leads to a penalty of $n_{xy} - n_{yx}$.

The *majority graph* has weights $n_{xy} - n_{yx}$. If we find a set of edges with minimal accumulated weight whose removal breaks all cycles, the inversion of these edges yields a Kemeny ranking.

> **Lemma**. Let $G = (V, E)$ be a directed graph, $E' \subseteq E$. $G$ can be made acyclic by inverting a subset of edges in $E'$ if and only if $(V, E\setminus E')$ is acyclic.

(in other words: removing and inverting can both be used to break cycles)


#### Social choice axioms for SPFs
Adapt definitions of anonymity/neutrality to SPFs:
- SPF $f$ is anonymous if $f(R_N) = f(R'_N)$ for all $R_N, R_N'$ s.t. a permutation $\pi: N \to N$ exists with $R_i = R'_{\pi(i)}$.
- SPF $f$ is neutral if $\pi(f(R_N)) = f(R'_N)$ for all $R_N, R'_N$ such that a permutation $\pi: U \to U$ exists with $R_N = \pi(R_N')$

Note: the SPF version of neutrality does *not* imply IIA.

Furthermore, define *reinforcement* and *Condorcet consistency* for SPFs:
- SPF $f$ satisfies reinforcement if for all disjoint $N, N'$ and all $R_N$, $R_{N'}$ with $f(R_N) \cap f(R_{N'}) = \emptyset$, $f(R_N) \cap f(R_{N'}) = f(R_N \cup R_{N'})$
	- "seems easier to satisfy for SPFs than for SCFs".
- SPF $f$ satisfies *Condorcet consistency* if for all $R_N$ and $R \in f(R_N)$ and all $x, y$ that are adjacent in $R$, $x R y$ implies $x R_M y$.
	- ($x, y$ are adjacent in $R$ if there is no $z$ with $xPzPy$ or $yPzPx$)

Also define *Local Independence of Irrelevant Alternatives* (LIIA) and *Pareto optimality* for SPFs:
- SPF $f$ satisfies *LIIA* if for all $R_N, R_N'$, $R \in f(R_N)$, $R' \in f(R_N')$ and $x, y$ adjacent *in both $R$ and $R'$*, $$(\forall i : R_i|_{\{x,y\}} = R_i'|_{\{x,y\}} ) \implies R|_{\{x,y\}} = R'|_{\{x,y\}}$$
	- LIIA is weaker than Condorcet consistency
- SPF $f$ satisfies Pareto optimality if for all $R_N$, $x, y$ and $R \in f(R_N)$, $(\forall i: x P_i y) \implies x P y$

#### Young's Characterizations of Kemeny's Rule
*Young's first characterization*, using reinforcement and Condorcet consistency:
> **Theorem** (Young & Levenglick 1978).
> Kemeny's rule is the *only SPF* that satisfies neutrality, reinforcement and Condorcet consistency.

*Young's second characterization*, using LIIA and Pareto optimality (among others):

> **Theorem** (Young 1988).
> Kemeny's rule is the *only SPF* that satisfies anonymity, neutrality, Pareto optimality, reinforcement and LIIA.

In other words: weakening Condorcet consistency to LIIA requires adding anonymity and Pareto optimality to the mix.