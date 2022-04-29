# 06 - Iterative Combinatorial Auctions
Recall: The [[05b) - Assignment Markets#Primal-Dual Algorithms and Auctions]|Primal-Dual Algorithm for an assignment market may be interpreted as an ascending auction]].  What about combinatorial auctions?

## Problems in ICA design
General procedure how we want to design an ICA:
1. collect bundle bids
2. compute winner
3. if some termination rule is fulfilled: stop
4. compute new ask prices, go to 1

We have to deal with
- calculating ask prices
- tie breaking
- preventing strategic bidding/signaling
- setting minimal bid increments, activity rules (keep bidding moving)

### Threshold Problem
![[threshold-problem.png]]
One bidder: 7 million for all. 6 bidders: 1 million each for a one-sixth part.

Problem: each small bidder would have to double their price to outbid the big bidder if acting individually.

*Ask prices* should solve this by guiding bidders how much more to bid.

### Types of Ask Prices
- *linear ask prices*: $\forall i, S: p_i(S) = \sum_{k\in S}p_i(k)$
- *anonymous ask prices*: $\forall i\neq j, S: p_i(S) = p_j(S)$

Combinations we consider:
- linear anonymous prices (I think: until now, connected to Walrasian equilibrium)
- non-lin. anon. prices
- non-lin. personalized prices

(rarely discussed: linear personalized prices)

We start with the usual [[04 - Combinatorial Auctions#Winner Determination Problem|WDP (winner determination problem) = CAP (combinatorial allocation problem)]], and call its relaxed version CAP1. Its dual DP1 represents *anonymous linear prices* $p(k)$ for items $k$. The relaxed version does not guarantee an integral solution: We formulate CAP2 and DP2 where prices are *non-linear, but still anonymous* ($p(S)$ for bundles $S$). Non-integral solutions are still possible: We introduce CAP3 and DP3, where prices are *non-linear and personalized* ($p_i(S)$ for bidders $i$, bundles $S$). In CAP3, integrality of solutions is guaranteed (at the cost of an exponentially increased number of variables in the primal, or constraints in the dual).

#### Computing *Anonymous Linear* ask prices: CAP1
Example: 

|     | A   | B   | AB  |
| --- | --- | --- | --- |
| B1  | 2   | 3   | 5   |
| B2  | 1   | 4   | 7   |

Ask prices should be lower or equal than winning bids, and higher than losing bids.

Ask prices: $p(AB) = p(A) + p(B) \leq 7$ (linear + B2 can afford AB) as well as $p(A) \geq 2$ and $p(B) \geq 4$ (noone can afford A or B individually). One possible solution: $p(A) = 2.5, p(B) = 4.5$.

In general: use *dual of the relaxed CAP*, i.e. DP1, the dual of CAP1. This corresponds to *non-linear, anonymous prices* $p(j)$ for items $j$.

CAP1:
$$\max \sum_S \sum_i x_i(S) v_i(S) ~~~s.t.~~~\forall i, S: x_i(S) \geq 0,$$
$$\forall i:~~ \sum_S x_i(S) \leq 1 \qquad\qquad\qquad(\pi_i)$$
$$\forall j:~~ \sum_{S \ni j} \sum_i x_i(S) \leq 1 \qquad\qquad(p(j))$$

DP1:
$$\min \sum_i \pi_i + \sum_j p(j) ~~~s.t.~~~\forall i, j: \pi_i, p(j) \geq 0,$$
$$\forall i, S:~~ \pi_i + \sum_{j \in S} p(j) \geq v_i(S) \qquad\qquad(x_i(S))$$

Some problem: before we just went to duality theory, as a recipe to get to prices. However this does not work here, because of an LP relaxation *overestimating the true prices* which are actually the solution of an integer programming problem (because the allocation must be integral). This is due to the*duality gap*.

Interesting note: in some electricity markets, these wrong prices from the LP relaxation are actually used.

#### Computing *Anonymous Bundle* prices: CAP2
From CAP1 to CAP2: Introduce partitions of items (*from linear to bundle prices*). Denote by $X$ a partition of items, e.g. $X = (AB, C)$. Introduce variables $\delta(X)$ that signify that partition $X$ is used.

CAP2:
$$\max \sum_S \sum_i x_i(S) v_i(S) ~~~s.t.~~~\forall i, S, X: x_i(S), \delta(X) \geq 0,$$
$$\forall i:~~ \sum_S x_i(S) \leq 1 \qquad\qquad\qquad\qquad(\pi_i)$$
$$\forall S:~~ \sum_i x_i(S) \leq \sum_{X \ni S} \delta(X) \qquad\qquad(p(S))$$
$$~~~\sum_X \delta(X) = 1 \qquad\qquad\qquad\qquad\qquad(\pi)$$

DP2:
$$\min \sum_i \pi_i + \pi ~~~s.t.~~~\forall i, S: \pi_i, p(S), \pi \geq 0,$$
$$\forall i, S:~~ \pi_i + p(S) \geq v_i(S) \qquad\qquad(x_i(S))$$
$$\forall X: ~~\pi - \sum_{S \in X} p(S) \geq 0\qquad\qquad~(\delta(X))$$

With bundle prices: Solution of the LP relaxation leads to an integral allocation solution in more cases than for CAP1, but still not always!

(note about slide 14: there are more partitions than just 3, ... is missing)

There can be situations where anonymous non-linear prices do not lead to integral allocations. Example:

|     | A   | B   | AB  |
| --- | --- | --- | --- |
| B1  | 0   | 0   | 3   |
| B2  | 2   | 2   | 2   |

Solution: assign half of AB to B1, half of A to B2, half of B to B2.

#### Computing *Personalized Bundle* prices: CAP3
One step further, from CAP2 to CAP3: Price *per person and package* $p_i(S)$. One variable per allocation ==> exponentially-growing LP size. Denote by $X$ an allocation, e.g. $X = (A2, BC3)$.

CAP3:
$$\max \sum_S \sum_i x_i(S) v_i(S) ~~~s.t.~~~\forall i, S, X: x_i(S), \delta(X) \geq 0,$$
$$\forall i:~~ \sum_S x_i(S) \leq 1 \qquad\qquad\qquad\qquad(\pi_i)$$
$$\forall (i, S):~~ x_i(S) \leq \sum_{X \ni (i, S)} \delta(X) \qquad\qquad(p_i(S))$$
$$~~~\sum_X \delta(X) = 1 \qquad\qquad\qquad\qquad\qquad(\pi)$$

DP3:
$$\min \sum_i \pi_i + \pi ~~~s.t.~~~\forall i, S: \pi_i, p_i(S), \pi \geq 0,$$
$$\forall i, S:~~ \pi_i + p_i(S) \geq v_i(S) \qquad\qquad(x_i(S))$$
$$\forall X: ~~\pi - \sum_{(i, S) \in X} p_i(S) \geq 0\qquad\qquad~(\delta(X))$$

There is *always* an integer optimal solution to CAP3. However there *may be* non-integer optimal solutions as well, that are convex combinations of integer optimal solutions (i.e. $\delta(X) > 0$ for multiple $X$). If the LP solver gives you such a solution, you can simply pick one $X$ that has positive $\delta(X)$, and use it as your allocation.

#### Overview: CAP1, CAP2, CAP3
![[CAP-123.png]]

Background: theory of *extended formulations* in LP where more constraints are added to make the duality gap smaller. The (ridiculously large) resulting LPs that we get if we add all constraints to obtain integer solutions may not be practical anymore.

General insight (using the next section): to *clear the market*, you cannot always just use linear and anonymous prices; in some cases, for a [[05b) - Assignment Markets#Competitive Equilibrium Algorithm|competitive equilibrium]] you need non-linear personalized prices.

For an efficient allocation, one trivial way to find non-linear CE prices is to use "pay as bid", i.e. each bidder pays their valuation for the bundle they are assigned in the allocation.

### Competitive Equilibria and CAP3
Recall what "competitive equilibrium" means, adapted to our situation: A competitive equilibrium $(X^\ast, p)$ is an allocation $X^\ast = (S_1^\ast, \dots, S_n^\ast)$ with a price vector $p$ that *maximizes the payoff of every bidder and the seller given the prices $p$*. Formally:
$$\forall i: \pi_i(S_i^\ast, p) = \max \{v_i(S) - p_i(S) \mid S\} \cup \{0\} ~~~\text{(bidder payoffs)}$$
$$~~~~\pi^s(X^\ast, p) = \max_{X \in \Gamma} \sum_i p_i(S_i) \qquad\qquad\qquad~~~~\text{(seller revenue)}$$

CE are not always possible with linear prices, or non-linear anonymous prices (as seen in the examples above).

Sidenote, a reinterpretation of complementary slackness: $x_i(S) (\pi_i - (v_i(S) - p_i(S))) = 0$ holds, so whenever $x_i(S) = 1$, $pi_i = v_i(S) - p_i(S)$. Likewise $\delta(X) (\pi^s - \sum_{(i,S)\in X} p_i(S)) = 0$, so whenever $\delta(X) = 1$, $\pi^s = \sum_{(i,S)\in X} p_i(S)$. This corresponds to the terms in the CE definition.


> **Theorem** (Bikhchandani and Ostroy, 2002). $(X^\ast, p^\ast)$ is a competitive equilibrium *if and only if* $X^\ast$ is an integral optimal solution to the primal and $p^\ast$ is an optimal solution to the dual for all bids.

Proof sketch:
- If CAP3 has an integral solution, this implies a CE by the compl. slackness interpretation we just saw.
- Assume we have a CE, show CAP3 is integral:
	- $W_{CAP3,LP} = W_{DCAP3}$ by strong duality.
	- $\dots \leq \sum_{i\in I} \pi_i^\ast + \pi^{S\ast}$ by feasibility of the optimal payoffs $(\pi_i^\ast, \pi^{S\ast})$
	- $\dots = \sum_{i\in I} (v_i(S^\ast) - p_i(S^\ast)) + \sum_{i\in I} p_i(S^\ast)$ (just by plugging in the profits/seller revenue)
	- $\dots = \sum_{i\in I} v_i(S^\ast)$ by cancelling out
	- $\dots \leq W_{CAP3,IP}$ since this efficient solution is bounded by the CAP3 IP solution
	- $\dots \leq W_{CAP3,LP}$ since LP relaxation relaxes constraints

Note: this theorem is quite a landmark theorem.

## Two-Sided Combinatorial Markets
Assume more than one seller. Some more problems arise, as the next example shows.

##### Example: non-existence of non-linear personalized prices in two-sided comb. markets
Consider this example where there are non-linear personalized prices (that lead to a CE) for 1 seller, but not for three. (If there are multiple buyers and sellers, the core can be empty).

![[two-sided-markets-empty-core-example.png]]

Details for this example: 3 sellers, one for $A$, $B$, $C$, each. If, say, all three sell to bidder 3 for $\frac{41}{3}$ each. Then bidder 1 can offer the $A$ and $B$ sellers more money. But this assignment, where the seller $C$ cannot sell, is not stable either: bidder 2 can offer $C$ a little bit and $A$ more than what bidder 1 offers. Such a deviation is always possible in this example.


### Two-Sided Markets LP formulation
Huge LP, analogous to CAP3, where multiple bidders, sellers and an auctioneer are involved.
- both bidders and sellers have valuations ($v_i$ and $v_j$)
- allocation variables: $x_i(S)$ for buyers, $y_i(Z)$ for sellers

![[two-sided-market-primal.png]]

In the dual:
- budget balance constraint: what bidders pay, what sellers get, and the auctioneer's revenue need to cancel out.

![[two-sided-market-dual.png]]

The auctioneer's revenue $\pi_a$ should be 0 if the market is budget-balanced - if $\pi_a < 0$ it means the market needs to be subsidized! The cases where this happens is when the core is empty. On the other hand, if we forced $\pi_a = 0$, in such situations we would not get an integer solution, which illustrates the impossibility of having core outcomes without subsidies in some scenarios.

A formalization: A CE happens if and only if the auctioneer has zero revenues and no subsidies.

> **Theorem**. The assignment $(X^\ast, Y^\ast)$ and prices $(P_i^\ast, P_j^\ast)$ form a CE if and only if $\pi_a^\ast = 0$.

### Core in Two-Sided Markets
Formal definition of the core in this context:
> **Definition** (*Core in Two-Sided Markets*).
> Let $\Pi_i = (\pi_i)$ and $\Pi_j = (\pi_j)$ be the payoff vectors of the buyers and sellers in the auction. Denote by $V(N)$ the *coalitional value function* (in our case, equal to the primal objective function value, i.e. the generated welfare).
> 
> Then $(\Pi_i, \Pi_j)$ *is in the core* if
> 
> 1. core efficiency: $\sum_{i\in I} \pi_i + \sum_{j\in J} \pi_j = V(N)$
> 2. core rationality: $\sum_{i \in C} \pi_i + \sum_{j \in C} \pi_j \geq V(C)$ for all $C \subseteq N = I \cup J$

We can characterize a non-empty core as the optimal dual solutions with zero auctioneer revenue.

> **Theorem** If the core of the auction is non-empty, then the set of optimal solutions of the Dual with $\pi_a = 0$ coincides with the core.

*Proof*. (see slides.)

## Iterative CAs, duality theory and CE
We focus again on single-sided auctions.

Big picture: Bikhchandani and Ostroy (2002) showed that an allocation $X^\ast$ is supported in CE by some prices $p$ if and only if $X^\ast$ is an efficient allocation (==in single-sided markets!==)

Therefore, ==we can design ICAs which converge a set of minimal CE prices!==
But: *are these prices VCG prices*, [[05b) - Assignment Markets#Competitive Equilibria of the Assignment Game and LP Duality|like in the assignment market case]]?

Now: we have to give up some of our design desiderata... "you don't get all of it". Especially hard to get: incentive compatibility. We will see: to get incentive compatibility, we will need some very strong assumptions on the valuations. Problem: these assumptions are so strong that they *more or less exclude complimentarities, i.e. the main reason why we're doing combinatorial auctions in the first place!* In other words, in environments where you *do want a complementarities*, you *don't get incentive compatibility*; or rather, you have a *clash between incentive compatibility and core stability*, you cannot have both.

### Bidder Submodularity (BSM)
*Bidder submodularity condition*: Bidders are more valuable (higher welfare $W$ addition) when added to a smaller coalition: Here $W(\text{set of bidders})$ denotes the welfare of the optimal allocation where only the bidders in the set are used.

$$W(M \cup \{i\}) - W(M) \geq W(M' \cup \{i\}) - W(M') ~~\forall M \subseteq M' \subseteq I, i \notin M'$$

Bidder complementarities (i.e. superadditive valuations) *violate* bidder submodularity. Example of non-BSM:

|     | A   | B   | AB  |
| --- | --- | --- | --- |
| V1  | 7   |     | 7   |
| V2  |     | 8   | 8   |
| V3  |     |     | 10  |

Here $V_3$ is more interested in AB than A and B individually. BSM is violated: $W(\{3, 1\}) - W(\{3\}) < W(\{2,3,1\}) - W(\{2,3\})$. 
- $W(\{3, 1\}) = W(\{3\}) = W(\{2,3\}) = 10$ (bidder 3 wins AB)
- $W(\{2,3,1\}) = 15$ (bidder 1 gets A, bidder 2 gets B)

In this example, there is no incentive compatibility: one bidder can lie and be better off. In this case, bidder 1: if he drops out early (say at a price of 2.5 for him), the best strategy for bidder 2 is to bid up to his true value; Bidder 1 and 2 as a coalition win the auction, but bidder 1 makes the greatest profit.

#### BSM implies incentive-compatibility in ascending auctions
There is no real theorem statement, but I assume it goes like this:
> **Theorem** (?)
> (Roughly:) If valuations satisfy bidder submodularity BSM, then in an ascending auction with non-linear personalized prices,
> 1. Straightforward bidding is an ex-post Nash equilibrium.
> 2. The ascending auction *converges to the VCG prices* if bidders follow this strategy.

In other words: *With BSM, ICAs are incentive compatible*.

*Proof*. See slide 31. Proof by contradiction, assuming some player $l$ at round $t$ has payoff $\pi_l^t$ lower than the Vickrey payoff $\overline{\pi}_l$. One insight: $\pi_l = W(I) - W(I \setminus \{l\})$. BSM is used to show $W(I) - W(I \setminus \{l\}) \leq W(C \cup \{l\}) - W(C)$.

Comment: BSM is sufficient, but not necessary. According to (Cramton et al. 2006, Combinatorial Auctions: Chapter 2), the very similar, but slightly weaker bidders-are-substitutes (BAS) condition is both necessary and sufficient.

##### Example of non-incentive compatibility in a non-BSM ascending auction
[[#Bidder Submodularity BSM|Recall]] this example of non-BSM valuations:

|     | A   | B   | AB  |
| --- | --- | --- | --- |
| V1  | 7   |     | 7   |
| V2  |     | 8   | 8   |
| V3  |     |     | 10  |

Bidder 1 can free-ride, i.e. mis-represent his preferences and still win! Imagine the following scenario: The first rounds go like this: (0, 0), (1, 1), (2, 2), (3, 3), (4, 4) with everyone still on board. At this point, bidder 1 drops out! Bidder 2 now knows: in order to beat bidder 3, he must bid up 7 (7 + 4 = 11 > 10). Bidder 2 and 3 now both have the dominant strategy to bid straight-forwardly, but in the end bidder 1 makes a great gain at the cost of bidder 2.

Note: this example assumes that the valuations are publicly known.

### Gross Substitutes condition (GS)
A requirement that is similar to BSM, but is defined solely on valuations: gross substitutes.

GS condition: "Increasing price of one item does not reduce the demand for others".

Formal definition (Bichler, Market Design, p.145): 

> **Definition** (*Demanded item at prices*)
> Let $P = (p(1), \dots, p(m))$ denote the prices on all items. Item $k$ is *demanded by bidder $i$* if there is some bundle $S \ni k$ s.t. $S = \arg\max_{S'}v_i(S') - \sum_{j\in S'} p(j)$.

Note: this definition, as well as the gross substitutes condition, assumes *linear prices*.

> **Definition** (*Gross Substitutes condition* (GS))
> The prices $P$ are satisfy *gross substitutes* if for any item $k$ that is demanded at $P$ and prices $P' > P$ where $p'(k) = p(k)$ (i.e. all prices are raised except for the $k$-th item), $k$ is still demanded at prices $P'$.

Some intuition: gross substitutes roughly means that one good can be substituted by another good. For example, if the price for Darjeeling tea rises, one would expect that Assam tea is still demanded (probably even more demanded). On the other hand, complementarities might change this: Assume product 1 and product 2 are only valuable together, then raising the price for product 2 might also kill the demand for product 1.

 *Gross Substitutes ensures that prices can be linear and anonymous* (= Walrasian).

### Subadditive Valuations (SUBADD)
Subadditive valutions mean that the values of items in a larger bundle is weakly sub-proportional to the values of items in a smaller bundle; in other words, adding $B$ to a bundle $A$ increases the joint value by not more than $v(B)$.

> **Definition** (*Subadditive valuations* (SUBADD))
> A valuation $v$ is called *subadditive* if  for all disjoint $A, B$, i.e. $A \cap B = \emptyset$,
> $$v(A) + v(B) \geq v(A \cup B)$$

### Unit Demand Valuations
Unit Demand valuations are valuations in having bundles of size more than one does not add any value.

> **Definition** (*Unit demand*)
> A valuation $v$ is called *unit-demand* if $v(s) = \max_{j \in S} v(\{j\})$ for all $S$.

Unit demand valuations correspond to [[05b) - Assignment Markets|Assignment Markets]].

### Hierarchy of valuation conditions
All the conditions we saw, [[#Bidder Submodularity BSM|BSM]], [[#Gross Substitutes condition GS|GS]], [[#Subadditive Valuations SUBADD|subadditive]] and [[#Unit Demand Valuations|unit demand]] valuations, are pretty restrictive. The following hierarchy holds:

- $\phantom{\subsetneq}$ unit demand valuations 
- $\subsetneq$ GS valuations
- $\subsetneq$ submodular valuations
- $\subsetneq$ SUBADD valuations 
- $\subsetneq$ general valuations.

Warning: "submodular" here apparently means something different than BSM, i.e. defined on the items, not the bidders. We did not go into that any further.

##### SUBADD and BSM are not necessarily GS
SUBADD and BSM do not imply GS, i.e. the subset relation is strict: An example is given below.

|                | A   | B   | C        | AB      | AC  | BC  | ABC |
| -------------- | --- | --- | -------- | ------- | --- | --- | --- |
| valuation $v$  | 4   | 4   | 4.25     | 7.5     | 7   | 7   | 9   |
| prices $p$     | 3   | 3   | 3        | 6       | 6   | 6   | 9   |
| utility at $p$ | 1   | 1   | 1.25     | **1.5** | 1   | 1   | 0   |
| prices $q$     | 3   | 4   | 3        | 7       | 6   | 7   | 10  |
| utility at $q$ | 1   | 0   | **1.25** | 0.5     | 1   | 0   | -1  |

- at prices $p = (3, 3, 3)$, $D(p) = AB$ is demanded
- at prices $q = (3, 4, 3)$, $D(q) = C$ is demanded
- reason: while there is no complementarity (actually, an anti-complementarity) between $A$ and $B$ (A, B both at 4, AB at 7.5), there is an even stronger anti-complementarity between $A$ and $C$ (A at 4, C at 4.25, but AC at 7). If this was not stronger, i.e. $AC$ at $4+4.25-0.5 = 7.75$, then in both cases $AC$ would be selected

Takeaway: anti-complementarities of different strengths can also destroy the GS property.

### What if we want Linear and anonymous prices?
Clear advantages of linear and anonymous prices:
- bidders achieve same price for same package
- simple to interpret, transparent
- only a linear, *not an exponential number of prices*

What are the necessary drawbacks? We get the following problems:
- *efficiency losses*!
- *paradoxically rejected bids*!

#### Examples: Efficiency losses/Paradoxically rejected bids
![[linear-anon-prices-efficiency-loss-paradox-rejection.png]]
##### Efficiency loss
Seller sells a package of two items for at least 30$ - one buyer would pay up to 10$ for one, the other up to 26$ for another.

- it would be efficient if they would trade: clearly 30$ < 26$ + 10$
- with linear anon. prices: buyer 2 cannot pay more than 10$ and therefore, no trade can be made.
- ==linear anonymous prices restrict us from choosing the efficient outcome!==

Real-world relevance: On real-world electricity markets, for example, such efficiency losses actually occur.

##### Paradoxically rejected bids
One seller sells 3 items for 60$ and another sells an item for 10$ - 3 buyers each value an item at 30$.
- welfare-maximizing outcome: three buyers buy from seller 1
- prices: at 20$ (or e.g. 25$ would also work)
- ask of seller 2 is *paradoxically rejected*: he would sell at 10$!

Real-world relevance: Again, in real-world electricity markets (European dayahead-markets, specifically), such rejections actually occur.

QUESTION: why does this come from linear anonymous prices? Wouldn't, say, the [[#Two-Sided Markets LP formulation|two-sided market CAP3]] have the same problem?


### No Ascending Auctions always finds VCG (even with GS)
> **Theorem** (*Gul-Stachetti, 2000*).
> A Walrasian equilibrium price vector is guaranteed to exist if [[#Gross Substitutes condition GS|goods are substitutes (GS)]], but *no ascending auction can always determine the VCG prices*.

## Combinatorial Auction Formats
### Formats with linear ask prices
[[07 - Combinatorial Clock Auction|Combinatorial Clock Auction]]: see next week.

### Formats with non-linear personalized prices
There are some combinatorial auction formats (not treated in detail):
- iBundle (Parkes 2000)
	- iBundle(2) has anon. prices, iBundle(3) has personalized prices
- Ascending Proxy Auction (Ausubel & Milgrom 2002)
	- like iBundle(3), but with proxy agents
- dVSV (de Vries, Schummer, Vohra 2007)
- Credit-Debit, iBEA (Mishra, Parkes 2007)
	- not purely ascending: discount at the end
- Deadness-Level auction (Adomavicius et al. 2005, Petrakis, Ziegler, Bichler 2013)
	- related to iBundle, with different price update rule

No details required. Some drawbacks of these formats: 
- very large number of auction rounds (therefore, proxy agents necessary).
- Losing bidders only learn they lost, but prices do not convey information about competition on particular items.
- Depending on the implementation, the proxy agent might need all information up front (making it similar to sealed-bid auctions).

## Summary
### Summary: Literature Overview
![[literature-ascending-sealed-bid.png]]

To summarize, we have
##### In assignment markets (= unit demand)
- sealed-bid auction [[05b) - Assignment Markets#How to find CEs Leonard 1983|by Leonard]] (1983)
- ascending auction: [[05b) - Assignment Markets#DGS Auction Demange et al 1986|DGS auction]] (1986)

##### With Gross Substitutes (GS)
- sealed-bid auction: linear prices exist due to [[#No Ascending Auctions always finds VCG even with GS|Gul-Stachetti]] (2000)
- ascending auction: no ascending auction exist that always finds linear prices, also due to [[#No Ascending Auctions always finds VCG even with GS|Gul-Stachetti]] (2000)

##### With Bidder Submodularity (BSM)
- sealed-bid auction: personalized non-linear price CE exists due to [[##Competitive Equilibria and CAP3|Bikhchandani & Ostroy]] (2002)
- ascending auction: VCG can be implemented by an ascending auciton with non-linear, personalized prices

##### With General valuations
Even in the sealed-bid auction, a core-selecting auction is worse than VCG, due to Goeree & Lieen (2009).

### Summary: CE prices and VCG prices
- *if minimal CE prices are equivalent to VCG prices, straightforward bidding is an ex-post equilibrium*
- (only?) for BSM valuations, *minimal CE prices **are** equivalent to VCG prices*

### Summary: Design Goals
Design goals:
1. Individual rationality: each bidder has (in expectation?) a non-negative payoff for participating
2. Efficiency: the highest-valued bid wins/social welfare is maximized
3. Strategy proofness, or incentive compatibility: Misreporting one's value does not give an advantage
4. Competitive equilibrium: No coalition of bidders with the auctioneer can form a mutually beneficial re-negotiation among themselves (with a single seller: <=> to the core property)
5. Budget balance

Then
- the VCG auction (QUESTION: which?) satisfies 1, 2, 3, but not 4
- iBundle(3) satisfies 1, 2, 4, but not 3 unless BSM holds.

