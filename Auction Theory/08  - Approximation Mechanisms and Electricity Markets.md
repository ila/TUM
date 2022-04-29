# 08 - Approximation Mechanisms and Electricity Markets
## Application: Electricity Spot Markets
Types of electrity markets:
- forwards: trade some time before the time the electricity is needed, which secures you a particular price
- day-ahead trading: trade today the electricity that is needed tomorrow
	- cleared once a day
- intraday trading: trading on the same day the electricity is needed.

Job of TSO = Transmission System Operator: keep the balance s.t. a frequency is maintained and no blackout happens.

### EPEX spot market
EPEX = _**E**uropean **P**ower **E**xchange_

![[epex-countries.png]]

The EPEX market in particular works like this:

- day-ahead market:
	- auction at noon, 7 days a week, year-round
	- *uniform prices within each zone* (e.g. Germany)

- Intraday market:
	- continuous trading + price formation, 24/7
	- roughly like this: there are bids and asks, and as soon as an ask is below a bid, they are matched

- Bid language:
	- hourly bids
	- "all-or-nothing" block bids (i.e. XOR bundle bids)
		- motivation: e.g. on the supplier side, guarantee that you can use your gas turbine for a certain minimal amount since firing it up is costly
	- in some countries: multi-part bids (QUESTION: what is this?)

- Market Coupling
	- *EUPHEMIA* algorithm: matches energy markets in 25 countries

Amount of trading: 1530TWh average traded per day, 8.9 billion € gains from trade per day.

The EUPHEMIA Algorithm *must* produce a result within 17 minutes.

##### Uniform Zonal Prices
Europe uses large prices zones (e.g. Germany) which have a single wholesale price; this is in contrast to the US. By this, for these large zones, linear and anonymous prices are enforced.

Also, the simplistic "*copper plate assumption*" is made: assume that there is no problem in transporting electricity from A to B. In other words: with this assumption you do not model the electricity network ("trading as if there was no network"). Problem: with this assumption, the solution might not be feasible to be executed in the real world!

### Linear and Anonymous Prices
The European rules *try to find linear prices* (even if not welfare-maximizing/efficient; which we apparently can't even talk about if we don't model the network). Example where the European rules lead to paradoxically rejected bids:

|                              | Generator 1 | Generator 2 | Demander 1 | Demander 2 |
| ---------------------------- | ----------- | ----------- | ---------- | ---------- |
| **Limit price**     (EUR/MW) | 40          | 100         | 300        | 10         |
| **Quantity**         (MW)    | 12          | 13          | 10         | 14         |
| **Minimum Production** (MW)  | 11          | 0           | 0          | 0          |

Efficient solution: match $G_1, D_1$ and parts of $D_2$.

European solution: $G_1$ is paradoxically rejected: Once we match part of $D_2$, we need to go down with the price to an infeasible amount of 10€/MW.

### Locational Marginal Prices
E.g. in the US, New Zealand, Singapore, and many more countries: no uniform zonal prices, instead *nodal pricing* aka *locational marginal prices* (LMP). Takes into account the transmission network capacities for the optimization.

![[lmp-example.png|400]]

In the example, because G1 cannot provide for all the demand of node 2, G2 has to become active. G2 then sets the price (because we need linear prices).

Procedure:
- ISOs solve MIPs to compute the efficient dispatch considering transmission constraints
- compute linear + anonymous prices based on LP relaxations, using different heuristics
	- types of *[[07 - Combinatorial Clock Auction#Alternative open auction formats: Pseudo-Dual Linear prices|pseudo-dual linear prices]]*

##### Design Desiderata for LMP
- efficiency (welfare-maximizing)
- envy-freeness (each participant maximizes individual gains)
- individual rationality (no participant makes a loss)
- budget balance (market operator makes no loss or gain)
- market clearing: supply equals demand
	- (weaker than envy-freeness?) Apparently it just means that you have prices where it is clear who wins/loses.
- anonymous + linear prices (single price/item)
- anon. + lin. payments (payments non-discriminatory)

Note: prices and payments are distinguished here (recall the CCA, where this was the case as well).

![[design-desiderata.png]]

On a convex market: these design desiderata essentially characterize a *Walrasian equilibrium*.

On non-convex markets: must give up some design goals (e.g. budget balance)

Europe: give up *efficiency*.
	- recall the [[#Linear and Anonymous Prices|example above]]: no efficient outcome.

US: do not give up efficiency. Instead, different solutions by different ISOs:
- *lost opportunity cost payments*
	- subsidize the market and pay participants for their lost profits
- *make-whole payments*
	- pay a little less, just such that the expenses are covered
-  *penalties*
	-   incentivize participants to still to the efficient outcome by handing out penalties if they don't.

In other words - One way to achieve envy-freeness: pay *uplifts* in order to make everyone happy (i.e. subsidize the market). Also: make-whole payments (not quite achieves envy-freeness, just makes sure providers don't make a loss). Then still there is incentive to deviate, e.g. produce something else than wanted - counter this with *penalties* for deviating parties. Penalties work well here because the market is highly transparent.

### Price-Inelastic vs. Price-Sensitive Demand
##### Price-Inelastic Demand
Consumers just consume electricity whenever they need it, no matter the cost.

Example:

![[inelastic-electricity-demand.png]]

Note: because of the inelastic demand, IR (individual rationality) is always satisfied.
Also note: penalties are set, but only need to be paid if participants deviate.

- In the first allocation, linear + anon. payments and budget balance are violated.
- In the second allocation, all desiderata are satisfied instead of envy-freeness (which you can't get anyway with lin+anon. prices).
- In the third allocation, efficiency is violated (the European solution).

##### Price-Sensitive Demand
Some consumers are price-sensitive: they have a maximum for payments they are willing to make.

![[price-sensitive-electricity-demand.png]]

QUESTION: can't the buyers just set arbitrary prices, e.g. 0.01€/MWh? Is there some concept of a buyer's demand not being matched?

### Unit Commitment and Economic Dispatch (UCED) problem
#### UCED Linear Program
Mixed binary linear program to compute (in a simplified way) whether to commit electricity production.

- generator outputs $x_i$
- decisions to commit $u_i \in \{0, 1\}$
- marginal production costs $C_i$
- fixed costs $S_i$
- total demand $d$
- lower and upper production bounds $m_i, M_i$

$$\min_{x,u} \sum_i C_i x_i + S_i u_i$$
such that
- $\sum_i x_i = d$ (supply equals demand)
- $m_i u_i \leq x_i \leq M_i u_i$  (production capacities fulfilled)

Note: this is a severe simplification of what is really done. Basically, a toy model common in the literature.

> Implemented in: `code/uced.sage`.

#### IP Pricing
Use the same linear program as above, but with the $u_i$ fixed to be the optimal solutiosn determined previously; now, introduce dual variables $\lambda$ (market clearing price) and $v_i$ (uplift to generator $i$)

Note: this is a heuristic to determine prices in such a scenario. By fixing the $u_i$, the LP is no longer a mixed-integer linear program, which makes it possible to use the dual. IP prices correspond to marginal costs (i.e. "if demand grew by 1, given this allocation, how much would we have to charge for this demand?"). Important takeaway: fixed costs do not influence the IP prices, and are not covered!

Example (in a two-sided market scenario, where the LP does not apply):

![[two-sided-ip-pricing-example.png]]

#### Extended Locational Marginal Price (ELMP)
Just use the LP relaxation of the [[#UCED Linear Program]].

Empirically, ELMP uplifts tend to be a bit lower (but that does not always hold).

### Summary
Europe:
- market design enforces single linear and anon. prices, ignoring non-convexities in costs and transmission constraints.
- linear prices ==> existence of paradoxically rejected bids
- traded volumes typically infeasible wrt. the network structure ([[#Uniform Zonal Prices#copper plate assumption]]) ==> require action by TSO.

US:
- solve large MIP considering transmission constraints ==> spatially differentiated prices (LMPs)
- linearity + anon. of prices at network nodes **at odds with** non-convex operator costs
- ISO pays *uplifts* to compensate generators who are dispatched but cannot recover costs.

In both US and Europe: pricing is an ongoing debate.

## Approximation Mechanisms
Motivation: NP-complete allocation problem ==> would be nice to find a near-optimal solution with an approximation algorithm.

Question: *can we still get strong incentive properties - e.g. strategyproofness?*

### Approximation algorithm basics
#### Approximation Ratio
- $OPT(I)$: objective value of optimal solution of $I$
- $ALG(I)$: objective value of approximation algorithm output for $I$

For a *minimization problem*: approximation ratio $c$ means that $ALG(I) \leq c OPT(I)$ for any $I$.

For a *maximization problem*: appr. ratio $c$ means that $ALG(I) \geq \frac{1}{c} OPT(I)$

Class of problems with constant-factor approximations: $\mathbf{APX} \subseteq \mathbf{NP}$.

#### Types of Approximation Guarantess
1. absolute constant difference: $|OPT(I) - ALG(I)| \leq c$
2. constant-factor approximation: $\mathbf{APX}$ (see above)
3. polynomial-time approximation scheme $\mathbf{PTAS}$
	- approximation ratio $1+\epsilon$ for all $\epsilon > 0$, and a polynomially bounded running time for any fixed $\epsilon$

##### Example: Bin packing
Bin packing: given some items with weights, and a bin capacity, allocate the items to bins while using the minimum number of bins.

Bin packing does not have a PTAS, but an algorithm which is asymptotically 22% worse than optimal in the worst case, and which has $O(n \log n)$ runtime.

Aproximation algorithm: **First Fit Decreasing** (FFD)
1. Sort items in descending order
2. For each item: place it in the first bin that still has enough room.

Approximation ratio: $FFD \leq \frac{11}{9} OPT + 4$.

##### Example: Knapsack
Knapsack: Given a set of items with weights $w_i$ and values $v_i$, and a capacity $C$, find a subset of items that fit into the capacity and have maximal value.

Aproximation algorithm:
1. Sort items by $\frac{v_i}{w_i}$ in decreasing order
2. Add items until the capacity is reached

A slight modification (either take the first $k$ items, or the $k+1$-th item) guarantees an approximation ratio of $2$, i.e. the solution is at worst 50% worse than optimal.

### VCG: Computational vs. Strategic Complexity
VCG: simplicity for bidders. But: computing $\arg\max_a\sum_i v_i(a)$ is NP-hard!

#### Can VCG be approximated?
Idea: 
- approximate the computation of $a = \arg\max_x \sum_i v_i(x)$ (for the allocation)
- approximate the computation of $b = \arg\max_x \sum_{j \neq i} v_j(b)$ (for the payments)

However, this can go arbitrarily bad. For example, the prices may be higher than the actual valuations, or the prices may be less than zero (i.e. payments *to* the bidders).

No incentive to bid truthfully!

##### Example: Knapsack Auctions
Auction based on the [[#Example Knapsack|knapsack problem]]: Bidders own the items, and reveal the values $v_i$ of the items (value they have to be included in the knapsack). The bidder approximates the welfare-maximizing allocation and the payments using the above-mentioned heuristic.

A very simple example leads to payments for one bidder that exceed her utility.

![[knapsack-auction-counterexample.png|700]]

> Implemented in: `code/knapsack-vcg.sage`.

#### Challenges: approximation and Incentive compatibility
1. we need mechanisms that are both *computationally efficient* (not VCG!) and *truthful*
2. simply approximating VCG does not work: Nisan-Ronen 2000 showed that VCG does not work with any "reasonable" non-optimal algorithm.

Idea: restrict the possible valuations to get nicer results.

### Approximation Mechanisms for Single-Minded Bidders
A *single-minded bidder* has a bundle of items $S^\ast$ s.t. $v(S) = v^\ast$ for all $S \supseteq S^\ast$, and $v(S) = 0$ otherwise. Determining the optimal alocation with single-minded bidders is NP hard. However there are *truthful approximation algorithms* for this case.

#### Sufficient conditions for truthfulness with single-minded bidders
- *Exactness*: bidders get exactly their bundle, or nothing
- *Monotonicity*: if $j$ gets bundle $S$ for bid $v$, he would also 1) get a smaller bundle $S' \subseteq S$ for $v$, or 2) $S$ for a higher bid $v' \geq v$
- *Critical payments*: a winner pays exactly the critical value: i.e. the lowest value he could have declared and still be allocated the bundle
- *Participation*: losers pay nothing

#### Greedy approximation for CAs (Lehmann et al. 2002)
(for single-minded bidders).

- Phase 1: sort bids by some criterion of the form $\dfrac{b(S)}{|S|^l}, l\leq0$
- Phase 2: allow each bid which does not conflict with higher, granted bids, top-down.

Using $b(S)/\sqrt(|S|)$, i.e. $l=\frac{1}{2}$, approximates the optimum within a factor of $\sqrt{m}$, where $m$ is the number of items.

Payment scheme (these are critical payments):
- for any winning bundle $b_i$: Pay $|S_i|^l \frac{b_j}{|S_j|^l}$, where $b_j$ is the first bid forced out exclusively by the winning bid.

"X is forced out exclusively by Y" means that for all bundles that come above Y in the ordering, X is the only one that conflicts with Y.

![[single-minded-approximation-example.png|350]]

Advantages: Approximates optimum within a factor of $\sqrt{m}$ with the right criterion, runs in poly time and is truthful.

Disadvantages: revenues independent of VCG revenue (can be higher or lower), cannot be truthful for *multi-minded bidders* for any payment scheme, not group-strategyproof (like VCG).

> Implemented in: `code/lehmann-ca-approx.sage`.

#### Deferred Acceptance version
Instead of *greedily accepting bids*, rather *reject least attractive bids*.

1. Sort the bids by the same criterion as in the Lehmann algorithm
2. Delete the lowest bundle which has at least one over-demanded item

Payments: critical payments
- for each bundle: what is the highest-ranked other bundle which was kicked out due to this bundle being ranked higher? In other words, by how much could the price for the current bundle be lowered without losing?

QUESTION: not so clear how to compute those.

Example: (bundles with over-demanded items are yellow).

![[deferred-acceptance-ca-approximation.png]]

In this example, the algorithm allocates less. However, the slightly changed allocation rule leads to nice incentive properties: the algorithm is *strategyproof* and *weakly group-strategyproof* for single-minded bidders! 

Further properties:
- Strategically equivalent to clock auctions.
- $O(\sqrt{m \log m})$ approximation ratio for CA; Works very well in the average case (not so well in the worst case)

##### US FCC Incentive Auctions 2017
More or less, a practical implementation of the [[#Deferred Acceptance version|deferred acceptance]] algorithm. Background: auction held to sell back spectrum licenses from local TV station to telecoms that needed them more. 2000 stations with 130000 interference constraints (basically a coloring problem): Problem that is way too large to solve to optimality.

Paul Milgrom et al. came up with a scheme that allowed to perform this auction.

Interesting note: the single-mindedness assumption was not actually satisfied; some TV stations were able to manipulate quite impressively.

QUESTION: where to find more about these manipulations?


### Approximation Mechanisms for General Valuations
Problem: single-minded valuations are a very restrictive assumption. Can we achieve good properties with general valuations as well?

#### Maximal-In-Range Algorithms
Idea: restrict yourself to some subset of allocations that is easiser to compute with.

> **Definition (MIR)**. An algorithm is *maximal in range* (MIR) if for some subset of allocations $\mathcal{R}$, the range of the algorithm, the algorithm always outputs the allocation that maximizes the welfare in $\mathcal{R}$.

Desiderata: maximization over the range should be possible in polynomial time, but it should contain a good ratio of total welfare ==> tradeoff: runtime <--> quality.

For VCG-approximations where the optimal allocation problem is not solved to optimality (as [[#Can VCG be approximated|seen above]]), there is a theorem that such mechanisms need to be maximal-in-range to be truthful:

> **Theorem.** VCG-based mechanisms are truthful if and only if they are maximal in range (Nisan and Ronen, 2000).

### Randomized Mechanisms
Maximal-in-range had limited success. Maybe randomization helps? To allow for randomized mechanisms, one can relax truthfulness to only be required in expectation.

> **Definition (Incentive Compatibility in Expectation).** 
> A randomized mechanism is *incentive compatible in expectation* if truthtelling is a dominant strategy in the game induced by expectation:
> $$\forall i, v_i, v_{-i}, v_i': E(v_i(a) - p_i) \geq E(v_i(a') - p_i')$$
> The random variables $(a, p_i)$, $(a', p_i')$ denote the outcome and payment when $i$ bids $v_i$ and $v_i'$, respectively.

#### MIDR mechanisms (Maximal in Distributional Range)
The MIR assumption is modified to hold for distributions (MIDR):

> **Definition (Maximal in Distributional Range).**
> An algorithm is *maximal in **distributional** range* if there is a subset $\mathcal{R}$ of *distributions over allocations* (the range) such that the algorithm always outputs the random allocation that maximizes the *expected welfare* over all distributions in $\mathcal{R}$.

#### Lavi & Swamy: Truthful approximate mechanisms via LP
...due to Lavi and Swamy, 2005.

Idea: assume you have an intractable IP problem, and know that relaxing the integrality constraint only increases the solution by a factor of $\alpha$ in the worst case (integrality gap of $\alpha$). Then shrink the LP polytype by $\alpha$, select an optimal solution, and represent it as a convex combination of integer points. This convex combination is realized via randomization.

![[lavi-swamy.png]]

To get the convex combination coefficients, instead of solving the primal (many variables) solve the dual (many constraints, but few variables). In principle, this can be done in poly time with the ellipsoid method.

Huge downside: *the worst-case essentially becomes the average case!* The $\alpha$ ratio represents a worst case, and the average case is usually much better. However by scaling down the relaxation solution by $\alpha$, this worst case becomes the average case.

Takeaway: This is not really a practical mechanism,and wasn't ever used in practice; it's  more of a theoretical argument that shows the existence of an incentive-compatible-in-expectation, poly-time mechanism with certain worst-case guarantees.
