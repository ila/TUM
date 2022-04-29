# 05b) Assignment Markets
Assignment markets: bidders interested in winning *one item only*, known as *unit demand assumption*. In other words: this is more or less the simplest version of a multi-object auction.

Primal-dual algorithms: not all bidder valuations required upfront

Assignment problems are related to network flow problems:

![[assignment-problems-network-flow.png]]

### Assignment Problem as Bipartite Matching
Matching: subset $M$ of edges s.t. every vertex has degree at most 1.

Maximum weight bipartite matching problem: Find matching in bipartite graph with maximum weight of edges.

LP formulation: $$\max \sum_{i,j} v_{ij} x_{ij} ~~~~s.t.~~ \sum_i x_{ij} = 1 \;\forall j,~~ \sum_j x_{ij} = 1\; \forall i,~~ x_{ij} \geq 0 \;\forall i,j$$

The constraints say that
- the welfare-maximizing allocation should be found...
- ...where every bidder is assigned exactly one item...
- ...and every item is exactly assigned once.

Luckily: constraint matrix [[04 - Combinatorial Auctions#Hoping for integer solutions of the relaxation|Totally Unimodular]] ==> **don't need integer constraint**.

#### Hall's Theorem (1935)
A bipartite graph $G$ has a *perfect matching* that saturates $A$ if and only if for all $S \subseteq A$, $|N(S)| \geq |S|$ ($N$ is the neighborhood).

Intuition: If there is a group of 3 bidders that are interested in only 2 goods, there is no solution. The more interesting part is probably that an allocation does exist if such a situation does not occur.


#### Total Unimodularity for Bipartite Graphs
We can write the *incidence matrix* as $A = [M_1; M_2]$. Each column has two ones, one in the $M_1$ block, one in the $M_2$ block.

![[bipartite-graph-incidence-matrix.png]]

Recall the sufficient conditions for total unimodularity: 
- the incidence matrix has entries in $\{0, 1\} \subseteq \{-1, 0, 1\}$
- each column has at most two non-zero coefficients
- the set of rows can be partitioned in two sets s.t. two entries of the same sign are not in the same block.

==> this is fulfilled in our case!



### Competitive Equilibrium Algorithm
#### Definition: Competitive Equilibrium
According to (Bichler, Market Design):
> **Definition** (*Competitive Equilibrium*). A price vector $P$ and a feasible allocation $x^\ast$ are in competitive equilibrium if the allocation $x^\ast$ maximizes the payoff of every bidder and the auctioneer revenue, given the prices $P$

Furthermore, in the setting of linear (item-level) prices, a CE is called *Walrasian equilibrium* (and the prices *Walrasian prices*).

According to the slides:
- buyers *maximize their utility* at these prices
- sellers *maximize their profits* at these prices
- the market is *budget-balance*: no surplus/subsidy required

QUESTION: How does this relate to the envy-freeness property one sometimes sees?

#### How to find CEs? (Leonard, 1983)
An allocation can be found via the primal (also see [[#Assignment Problem as Bipartite Matching|above]])
$$\max \sum_{i,j} v_{ij} x_{ij}~~~~s.t.~~\sum_i x_{ij} = 1 \;\forall j,~~ \sum_j x_{ij} = 1\; \forall i,~~ x_{ij} \geq 0 \;\forall i,j$$
The dual is given by
$$\min \sum_i \pi_i + \sum_j p_j ~~~~s.t. ~~\pi_i + p_j \geq v_{ij} \;\forall i,j$$
where the dual variables $pi_i$ represent bidder profits, and $p_j$ represent item prices. By primal complementary slackness: $$\pi_i + p_j > v_{ij} \Rightarrow x_{ij} = 0$$

However this dual does not necessarily minimize the prices $p_j$. This alternate LP, *Leonard's dual*, minimizes the prices while ensuring that the sum of profits and prices (the original dual's objective function) equal the primal solution $W^\ast$:

$$\min \sum_j p_j ~~~~s.t.~~ \pi_i + p_j \geq v_{ij} \;\forall i,j,~~\sum_i \pi_i + \sum_j p_j = W^\ast, \pi_i, p_j \geq 0$$

> Implemented in: `code/assignment-market-ce.sage`.

### DGS Auction (Demange et al. 1986)
Idea: have an *ascending auction* which ends in an CE.

The *demand set* $D_i(P)$ of a bidder at given prices includes all bundles (= items here?) that maximize the bidder's payoff. For example, if bidder 1 has payoffs (4, 4, 2, -1) at prices $P$, then $D_1(P) = \{1, 2\}$.

A set of items is *overdemanded* if the number of bidders *only demanding items in this set* is greater than the number of items: i.e. $X$ is overdemanded at prices $P$ if
$$|\{i \mid D_i(P) \subseteq X \}| > |X|.$$

An overdemanded set is *minimally overdemanded* if no subset is overdemanded.

Idea: if a set of items is overdemanded, the DGS algorithm raises the prices.

#### DGS Algorithm
Assumption: integer valuations, i.e. we can raise prices by 1 without overshooting.
- start with prices all 0 and collect the demand sets
- while some set is overdemanded:
	- raise the prices for all items in the minimally overdemanded set by 1.
	- collect the new demand sets

This algorithm computes the minimal price vector in the core if bidders bid straight-forwardly, and this strategy is an ex-post Nash equilibrium. 

Due to Leonard (1983): *in an assignment market, minimal price vector in the core = VCG price vector*. All this means that the algorithm is robust against deviations, even by coalitions.

One advantage: you do not have to disclose your full cost structure, it suffices that the auctioneer always knows which object(s) you prefer most at the current prices.

### Primal-Dual Algorithms and Auctions
Motivation: The [[#DGS Auction Demange et al 1986|DGS auction]] can be interpreted as a primal-dual algorithm.
- the auctioneer solves the primal problem (AP = assignment problem)
- the bidders solve the dual problem (DAP = dual of AP)

#### Primal-Dual Algorithms
General intuition: primal-dual algorithms solve a linear program by reducing it to simpler linear programs that are iteratively solved to obtain a solution to the actual LP. More details: see https://ocw.mit.edu/courses/mathematics/18-433-combinatorial-optimization-fall-2003/lecture-notes/l15.pdf

Rough outline:
- formulate the primal (P) and the dual (D)
- formulate restricted primal (RP) from complementary slackness conditions of (P), (D). If $x$ if (RP)-feasible, it is (P)-optimal.
- while no P-optimal solution $x$ is found:
	- formulate the restricted dual (DRP)
	- solve (DRP) and use solution to update (D) and re-compute (RP)

![[primal-dual-algorithm.png]]

#### Steps of a general primal-dual algorithm
Warning: this is for a minimization-primal which furthermore has equals constraints instead of $\geq$ constraints, i.e. $\min c^Tx ~~s.t.~Ax=b, x\geq 0$.

One iteration of a primal-dual algorithm goes like this:

1. Obtain a *feasible solution* $y$ for (D)
	- reminder: (D) is $\max b^\top y~~~s.t.~A^\top y \leq c$
2. Let $J = \{j \mid \sum_i a_{ij}y_i = c_j\}$
	- by [[05a) - Duality in Linear Programs#Complimentary Slackness|complimentary slackness]], we are looking for a (P) solution s.t. for all $j \notin J$, $x_j = 0$
	- $J$ is the set of indices for which $A_{\cdot j}^\top y = c_j$ holds with equality, i.e. it cannot be made greater without violating (D)'s constraints.
3. Formulate the restricted primal (RP) $$\text{(RP):} ~~~~~~\min\sum_i X_i~~~s.t.~ \sum_{j\in J} a_{ij}x_j + X_i = b_i\;\forall i, ~~X_i, x_j \geq 0, ~~\forall j\notin J: x_j = 0$$
	- in other words, the approximate the (P) equation $Ax=b$ by allowing some slack $(X_i)_i$, but with the non-$J$ variables $x_j = 0$.
	- the slack $X_i$ should be minimal
	- if $opt(RP) = 0$, there is no slack and $x$ is optimal. Otherwise, continue.
4. Formulate the restricted dual (DRP), i.e. the dual of (RP) $$\text{(DRP):} ~~~~~~
\max \sum_i b_i y_i ~~~s.t.~\forall j \in J: \sum_{i} a_{ij} y_i \leq 0, ~y_i \leq 1$$
	- Question: where does the $\leq 1$ constraint come from?
5. Solve (DRP) and obtain a solution $\overline{y}$.
6. Improve the solution to (D) by setting $y' = y + \varepsilon \overline{y}$ for some $\varepsilon$
	- this is possible for the $j \in J$, $A_{\cdot j}^\top \overline{y} \leq 0$ by (DRP)'s constraints, and hence $A_{\cdot j}^\top y + \varepsilon A_{\cdot j}^\top \overline{y} \leq c_j$
	- ...and for the $j \notin J$, $\epsilon$ must be chosen minimally s.t. the constraint is not violated. $\epsilon > 0$ is possible by construction of $J$.

Advantage: usually, the (DRP) program is much easier to solve.

#### Primal-Dual Algorithm for DGS
Generally: we *interpret* the DGS algorithm as a primal-dual algorithm.

First intuition:
- primal: "find the best allocation"
- dual: "given these prices, what are the demand sets?"
- restricted primal: "given these demand sets, what is the minimally overdemanded set?"
- dual of restricted primal: "if there is an overdemanded set, how should I raise prices?"


**Primal (P):**
$$\max \sum_i \sum_i v_{ij} x_{ij} \qquad s.t. \qquad  \forall i,j: x_{ij} \geq 0$$
$$\forall i: \sum_j x_{ij} = 1 \qquad\qquad\qquad (\pi_i)$$
$$\forall j: \sum_i x_{ij} = 1 \qquad\qquad\qquad (p_j)$$

**Dual (D):**
$$\min \sum_i \pi_i + \sum_j p_j \qquad s.t. \qquad \forall i, j: \pi_i, p_j \in \mathbb{R}$$
$$\forall i, j: \pi_i + p_j \geq v_{ij} \qquad\qquad\qquad (x_{ij})$$

Have a price vector $p$, initially $p=(0, \dots, 0)$, and initially compute $\pi$ such that $\pi_i$ is the maximum valuation of bidder $i$ for any item.

Now compute the set $J$ of index pairs $(i, j)$ where the $(x_{ij})$ constraint in (D) holds with equality, i.e. $\pi_i + p_j = v_{ij}$. Because of the primal complementary slackness, we can assume that all other dual variables are zero. We call the bidders that appear in $J$ *active*, and the items that appear in $J$ *demanded*.

**Restricted Primal (RP):**
$$\max -\sum_i s_i - \sum_j t_j \qquad s.t. \qquad \forall i,j: s_i, t_j, x_{ij} \geq 0$$
$$\forall i, i \text{ active}: \sum_j x_{ij} + s_i = 1 \qquad\qquad(\pi_i')$$
$$\forall j, j \text{ demanded}: \sum_i x_{ij} + t_j = 1 \qquad\qquad(\p_j')$$

The optimal objective function for RP (and DRP) gives the number of unsatisfied bidders.

**Dual of Restricted Primal (DRP):**
$$\min \sum_i \pi'_i + \sum_j p'_j \qquad s.t.$$
$$\forall (i, j) \in J: \pi_i' + p_j' \geq 0$$
$$\forall j \text{ demanded}: p_j' \geq -1$$
$$\forall i \text{ active}: \pi_i' \geq -1$$

From solving DRP, one gets price differences $p'$ and profit differences $\pi'$, which can be used to update $p$ and $\pi$. Then the next iteration can start. If DRP has a zero objective function value, the iteration terminates.


Comment: the slides have the constraints for $p_j'$ that $\forall j: \text{if } j \text{ minimally overdemanded}, p_j' \geq -1, \text{ else } p_j' \geq 0$. However I think it is the same notion of "demanded" as in RP. Also, in RP, $\forall (i, j) \notin J: x_{ij} = 0$. However bidders $i$ and items $j$ that do not appear in $J$ could be ignored or left in in the RP and DRP constraints; they are not part of the objective function anyway.

##### Implementation
One only needs to compute $J$ and the initial prices $\pi$, then one can directly go to DRP without bothering with P, D or RP.

> Implemented in: `code/primal-dual-dgs.sage`.

##### Complexity
One comment: we have to solve a lot of linear programs - the the RP and DRP problems are much easier to solve (for reasons that were not explained further).


### Competitive Equilibria of the Assignment Game and LP Duality
**Theorem** (Shapley & Shubik 1971). *The set of CEs of an assignment game is exactly the set of solutions of the LP dual of the corresponding assignment problem*.


##### Core of an Assignment Game
**Definition** (Core). The *core* of a market is a set of feasible allocations that cannot be improved upon by a sub-coalition of bidders with the auctioneer.

![[assignment-game-core.png]]

The core comprises the set of all CE, i.e. the set of stable solutions. $(p^\ast, \pi^\ast)$ is the low-price corner of the core: where sellers get their minimum gains. This minimum CE price is equivalent to the VCG solution (example see on slides).


### Summary
Very important point: ==Like in single-object markets, there is an ascending implementation of the VCG mechanism==, namely the DGS algorithm.

### Proof: DGS Auction ==> VCG Payments
Details see slides.

Important step: From $|T|>|S|$ and $|T_1| \leq |S_1|$, it follows that $|T-T_1|>|S-S_1$.

### Ascending Combinatorial Auctions with Linear Prices
Outlook:

**Theorem** (Gul and Stacchetti, 2000). A Walrasian (item-level) equilibrium price vector is guaranteed to exist if goods are substitutes, but *no ascending auction can always determine the VCG prices*.

(There are such prices, but ascending mechanisms cannot always find them).