# 04 - Combinatorial Auctions
*Complimentarities*: for example, I prefer one auctioned region more if I also get the adjacent regions.

## Some Integer Programming/Computational Complexity
Repetition: P/NP, SAT etc. - (see slides).

### Normal-Form Integer Program
*maximize* $c^T x$ (objective function)
*subject to* $Ax \leq b$ (functional constraints)
and $x \in \mathbb{N}_0$ (set constraints).

Binary programming is hard already: reduce SAT to it with binary variables $x_i, \neg x_i$, and constraints $x_i + \neg x_i = 1$ as well as constraints for each $(x_i \vee x_j \vee x_k)$ constraint, namely $x_1 + x_j + x_k \geq 1$.

### Solving IPs
#### LP Relaxations
Relax the $\mathbb{N}_0$ restriction to $\mathbb{R}_{\geq 0}$. Solve it (e.g. via the simplex method) and try to obtain a solution to the integer problem via rounding (which usually doesn't work so well, this is only a heuristic!).

#### Branch-and-Bound Algorithm
Start with LP relaxation problem -> get fractional solutions... -> introduce additional constraints (*cuts*).

Say we get on optimal $x_1 = 3.75$: introduce additional constraint $x_1 \leq 3$ in one problem and $x_1 \geq 4$ in another problem (branching). Continue until integer solution is found and no other branch promises a higher objective function value.

![[branch-and-bound.png]]

### Approximation Algorithms
Approximation algorithms: use heuristics that solve a problem possibly non-optimally, and prove approximation guarantees (i.e. how far the solution is from the optimum, in the worst case).

![[time-accuracy-tradeoff.png]]

## Philosophical and Ethical Foundations
##### Utilitarianism
"The moral worth of an action is determined solely by its contribution to overall utility". In other words: under an utilitarian view, the objective is to maximize the social welfare.

##### Economic Efficiency
Pareto: effiency = nobody can be made better off without hurting someone else. In a *Pareto improvement*, noone worsens and at least one person improves.

##### Adam Smith / Supply-Demand Curves
Adam Smith: "invisible hand" of the market.

Marshallian theory of supply and demand (example of partial equilibrium analysis).

Wikipedia: 
> In microeconomics, a consumer's *Marshallian demand function* (named after Alfred Marshall) is the quantity he/she demands of a particular good as a function of its price, his/her income, and the prices of other goods"

##### Leon Walras: General Equilibrium Theory
Leon Walras (1874): pioneered general equilibrium theory (general = considering multiple products and markets).

Wikipedia: 
> A Walrasian auction, introduced by Léon Walras, is a type of simultaneous auction where each agent calculates its demand for the good at every possible price and submits this to an auctioneer. The price is then set so that the total demand across all agents equals the total amount of the good. Thus, a Walrasian auction perfectly matches the supply and the demand.
> 
> Walras suggested that equilibrium would always be achieved through a process of tâtonnement (French for "trial and error"), a form of hill climbing. More recently, however, the Sonnenschein–Mantel–Debreu theorem proved that such a process would not necessarily reach a unique and stable equilibrium, even if the market is populated with perfectly rational agents.

##### Arrow-Debreu Theorem
> **Theorem** (*Arrow-Debreu-Theorem*): For continuous, monotonic, strictly concave utility functions, with divisible goods and separable utility functions for each good, market clearing prices exist.

Market-clearing means: no leftover supply or demand. In the words of the lecture, market-clearing prices mean: "The losers know why they lost, and the winners know why they won".

Proof: like NE proof, use Kakutani's FPT (non-constructive proof).

### Welfare Economics
- for market-like games with prices: *Walrasian equilibrium* defines an outcome $(x, P_{ask})$ with a feasible allocation $x$ and a vector of prices for each good $P_{ask}$.
	- in such an equilibrium: even if an agent prefers other goods, he can't afford them.

**First Fundamental Theorem of Welfare Economics**: Every Walrasian equilibrium is Pareto efficient.
=> analytical confirmation of "invisible hand"

**Second FT of Welfare Economics**: Every efficient allocation can be supported by some set of item prices.

##### Criticism
- "only a mathematical exercise" - assumptions too strong for real world (Georgescu-Roegen)


## Combinatorial Auctions
Bids are allowed on individual items and also bundles of items.

Objective:
- elicit information about preferences to achieve efficiency
- by mechanism design, incentivize honest reporting

Have $m$ non-identical, non-divisible items for sale, and $n$ players. Each player $i$ has a valuation $v_i(S)$ for *each subset of items* $S$. $v_i$ satisfies monotonicity and $v_i(\emptyset) = 0$.

![[combinatorial-auctions.png]]

### History of Combinatorial Auctions
- First proposal: Rassenti, Smith and Bulfin (1982). Combinatorial auction for allocating airport timeslots (still an open problem until today!).
- Spectrum auction design (in 1990s). Strong complementarities
	- since 2008: FCC uses combinatorial auctions

Recall the [[03 - Single-Object Auctions#Complementarities and Exposure Problem|exposure problem]] due to complementarities in SMRA: The goal of CAs is to provide a solution to such problems.

### Examples: Reverse Combinatorial Auction (Procurement Auction)
Example of a combinatorial procurement auction:
![[procurement-auction.png]]

Idea: the auctioneer (= buyer in an procument auction) wants to cover his demands at the lowest price.

Another example: Spectrum auctions.


### Symbols used in the following
- $K$: Set of $m$ items
- $I$: Set of $n$ bidders
- $S \subset K$: bundle of items
- $x_i(S) \in \{0, 1\}$: allocation of bundle to bidder $i$
- $\Gamma$: set of valid allocations
- $x = \bigcup_{S_i \cap S_j = \emptyset} x_i(S)$: allocation
- $v_i(S)$: valuation of bundle
- $b_i(S)$: bid price of bundle
- $p_i(S)$: ask price of bundle

### Design Goals
*Allocative efficiency* $E(X)$: ratio between outcome social welfare and optimal social welfare. $$E(X) = \frac{\sum_{S \subseteq K} \sum_i x_i(S) v_i(S)}{\sum_{S \subseteq K} \sum_i x^\ast_i(S) v_i(S)}$$

*Auctioneer revenue* $R(X)$: ratio between outcome total ask price and optimal social welfare. $$R(X) = \frac{\sum_{S \subseteq K} \sum_i x_i(S) p_i(S)}{\sum_{S \subseteq K} \sum_i x^\ast_i(S) p_i(S)}$$

*Bidder payoff* $u(S, P)$: $u(S, P) = v_i(S) - p_i(S)$

### Assumptions
- Independent private valuations
- Quasi-linear utilities: $u_i(S) = v_i(S) - p_i(S)$
- Free disposal (monotonicity): $S \subseteq T \Rightarrow v_i(S) \leq v_i(T)$.
- Zero auctioneer valuations: the seller values the item at zero (revenue = total payment)

### Design Challenges: Complexities
- Computational Complexity
	- Winner determination is NP-complete
- Valuation complexity
	- bidders need to valuate exponentially many bundles
- Strategic complexity
	- which of the bundles to bid on, which is the best strategy?
- Communication complexity
	- worst case, exponentially many *queries* required to the bidders


### Winner Determination Problem
*Winner determination problem* WDP: a linear program that solves the allocation. Also known as *combinatorial allocation problem* CAP.

$$\max_{x_i(S)} \sum_{S \subseteq K} \sum_{i \in I} x_i(S) v_i(S) \qquad \qquad s.t.\quad \forall i, S: x_i(S) \in \{0, 1\}$$
$$\forall i: \sum_{S \subseteq K} \sum_i x_i(S) \leq 1 \qquad \qquad\qquad (XOR)$$
$$\forall k \in K: \sum_{S \ni k} \sum_i x_i(S) \leq 1$$

What is optimized in the WDP is the auctioneer's revenue (= the social welfare if bidding is truthful).

#### NP Completeness of WDP
Belongs to NP (solution to the decision problem can simply be checked).

Hardness: reduction from *(weighted?) set packing* problem (From a collection $C$ of finite sets, is there a subcollection  $C'$ of disjoint sets with $| C' | > l$?)

From a Set Packing instance construct CAP problem: 
- each item in the sets in $C$ is a *good* in CAP
- one bidder for each set in $C$, and one more agent 0 (seller)
- valuations: $v_C(S) = 1$ if $S=C$ else $0$ (each bidder values their bundle as 1)
- social welfare equal to $C'$.

![[set-packing-CAP.png]]

#### Solving the WDP
Exact solution:
- solve via integer programming
- multi-unit variants: <=> multi-dimensional knapsack problem

Alternatives are to restrict the bidding language, or to approximate solutions in polynomial time.

See [[#Alternative solution approaches for WDP]].

### Fundamental Bidding Languages
- OR language: a bidder can win multiple of the packages they bidded on
	- price for a bundle = maximum of sum of bids over all disjoint sets of subsets of the bundle 
- XOR language: each bidder can win at most one of the packages they bidded on
	- price for a bundle = exactly what the bidder bid for this bundle
- OR*: include dummy items (as expressive as XOR, but can decrease complexity)
	- There are dummy items, which can be included in bids to express XOR constraints
	- pricing like OR rule, but factoring in the dummy items when checking for disjointness of bids

Design of bidding languages:
![[design-of-bidding-languages.png]]


### Alternative solution approaches for WDP
#### Restricting permissible packages
> **Theorem** (Rothkopf, 1998):
> 1. If bids can include up to 2 items: WDP has a polynomial-time solution (<=> maximum-weight matching)
> 2. If bids can include up to $l$ items, $l \geq 3$: WDP is NP-hard (the cardinality 3 version <=> max. 3-set packing on a hypergraph)

Hierachical package bidding (with OR language) is solvable in *linear time* - relevant for US FCC spectrum auctions!
- bidders can only bid on the (non-overlapping) packages mentioned in the hierarchy
- if a XOR language is used, the WDP is NP-hard nonetheless.

![[hierarchical-package-bidding.png]]

#### integer solutions of the relaxation
The relaxation admits integer solutions if the constraint matrix is *totally unimodular* (TU): i.e. the determinant of every square submatrix is 0, 1 or -1.

Example problems with TU constraint matrices:
- 0-1 matrices with consecutive 1s
- tree-structured bids
- assignment problem (relationship TU matrices <-> bipartite graphs: see [[05b) - Assignment Markets#Total Unimodularity for Bipartite Graphs|the next lecture]])

TU for auctions:
- when bids have the [[06 - Iterative Combinatorial Auctions#Gross Substitutes condition GS|gross substitutes]] property
- a number of other properties imply TU (Vries & Vohra, 2003)

Usually, does not hold for your garden variety auction!

#### Approximate solutions
There is a non-approximability result about the WDP:

> **Theorem** (Harstad 1999, Sandholm 2002): There is no poly-time algorithm that guarantees an approximate solution to WDP within a factor $(l^{1-\epsilon})$ from optimal, where $l$ is the number of submitted bids (both for OR and XOR).

#### Heuristic Approaches
Every linear integer programming solving heuristic can be applied to SPP (Greedy alg., genetic alg., probabilistic search, simulated annealing etc.)

=> can't say anything about optimality/incentive compatibility then!

#### Solver software: Exact solution in practice
Integer linear programming software is often quite efficient despite the NP completeness, and one can get solutions to reasonably-sized problems in a couple of seconds.

![[inter-linear-prog-solvers.png]]


### Occurrence of CAs in other scenarios
Example: markets with economies of scale (i.e. multi-unit CAs)
- some examples on slides

Some problems mentioned: in multi-unit CAs, the communication complexity blows up even worse.

Some remedies: 
- Compact Bid Languages for Procurement
- Supplier Quantity Selection Problem (SQS)
	- huge linear program on slides, but probably not very relevant

##### Example: TV Ad Markets
Allocate 170 slots per week. Bid language:
- bidder wants to win $n$ ads with duration $d$, in a subset of the slots (e.g. at a given time of day, like between 8 and 10pm)
- allocation rule: maximize revenue

=> A linear program can be formulated (see slides) which solves this.

## Mechanism Design for Combinatorial Auctions
Goal now again: incentivize bidders to be truthful.

Namely: from bundle bid inputs, output efficient allocation - but also set *payments* in a way to motivate truthful bidding.

### VCG Principles
- Bidders with highest overall reported valuation win
- Winners *pay what they bid*, but recieve a *Vickrey discount*
	- ...depending on optimal outcome without them and what other bidders get

Applied to Combinatorial Auctions:
$$p_i^{vcg} = v_i(x_i^\ast) - [w(I) - w(I_{-i})]$$
(your paying price is vour valuation *minus* your marginal contribution to welfare). This is just a slight reformulation/rearrangement of the general [[02 - Social Choice, Utility, Mechanism Design and Quasilinear Mechanisms#VCG mechanism|VCG mechanism]].

Example:

| Bidders | A   | B   | AB  |
| ------- | --- | --- | --- |
| 1       | 8*  | 7   | 12  |
| 2       | 6   | 8*  | 14  | 

With * : $p_1^{vcg} = 8 - (16 - 14) = 6$, but $p_2^{vcg} = 8 - (16 - 12) = 4$. Even if A, B were identical items, both bidders had a different payment => violates law of one price.

#### Uniqueness of VCG
The *generalized Vickrey Auction* is the only *strategy-proof, efficient* auction mechanism (Green and Laffont 1979, Holmstrom 1979).

=> very favorable: shrinks design space to direct revelation mechanisms; trivial strategic complexity for agents

"For interesting single-parameter settings, techniques other than VCG are known": QUESTION - what does this mean?

However, VCG has "many serious practical problems" (Rothkopf 2007).

### "The Lovely but Lonely Vickrey Auction"
Some issues with the VCG:
* Auctioneer must be trusted
* *Valuation complexity* (need complete information about all bundles)
* non-existence of dominant strategy, considering bid preparation costs
* winner determination is NP hard
* vulnerable to *collusion* (groups of bidders)
* *instable (non-core) outcomes*
	* e.g. auctioneer + one bidder as a group can have incentive to deviate
* low auctioneer revenues
* monotonicity problem: *adding bidders might reduce revenues*
* problems with budget-constrained bidders (i.e. non-quasi-linear utilities)
* in *sequences* of auctions: possibly not strategyproof


#### Non-Monotonicity of VCG
Problem: adding more bidders can decrease monotonicity. This also makes this susceptible to *shilling*: A single bidders that poses as two bidders may win all he wants, but at a lower price.

| Bidders | A   | B   | AB  |
| ------- | --- | --- | --- |
| 1       | 0   | 2*  | 2   |
| 2       | 0   | 0   | 1   |
| 3       | 2*  | 0   | 2   |

Adding bidder 3 reduces both the overall revenue as well as the payments of bidder 1.

#### Unstable outcome
Same example as above: Revenue is 0; also bidder 2 does not get anything, but willing to pay 1 for bundle => auctioneer and bidder 2 as a group would be willing to deviate! (*Not in the core*).

### Challenges/Outlook
- VCG is the unique dominant-strategy equilibrium mechanism, but has many problems
- Auction mechanisms we saw have high computational/strategic/valuation/communication complexity

=> make auction mechanisms simpler, in the best case without compromising on useful economic properties!

#### Outlook: Iterative combinatorial auctions?
- Better preference elicitation (don't need to submit everything at once, but incrementally)
- for single-item auctions: English auction more popular than Vickrey auction
- more transparent (learn about other bidders => better results in case of correlated values)

See [[06 - Iterative Combinatorial Auctions]].