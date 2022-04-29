# 03 - Single-Object Auctions

### Why auctions?
Determine the right price to charge dynamically. Typically, when the selelr is unsure about the values of the sellers (otherwise, he could just offer to to the highest bidder at a high price).

One assumption is generally made in the theory: *common prior assumption*, i.e. all participants know about the probability distribution of valuations of the other participants.

Alternatives:
- list price - can be hard to set
- lottery - often low efficiency (person gets the good who doesn't want it that much), low revenue

Usually, high transaction cost when using auctions: use only when expected price is high, or setup cost is low.

### Auctions: formal view
Auctions are formalized as Bayesian games.

For now, assume risk neutrality and therefore utility functions $u_i = v_i(x) - p(B_i)$, where $B_i$ is the bid (QUESTION: shouldn't p also depend on other bids?)

"**Quasilinear utility: utility = valuation - payment**"

### Valuation methods
Several models for the valuation $v_i(x)$:

- *Independent private values* (IPV) paradigm: $v_i(\theta, x) = v_i(x)$
	- Bidders privately know their value for the object
	- Value of one bidder independent of knowing value to the others

- *Common values*
	- same value for all bidders, but unknown at the time of bidding
	- example: Rights to drill for oil, with unknown quantity of oil

- *Interdependent values*
	- Values of bidders are not independent
	- Example: object that can be sold again later; if the others have high values, that shows that the good is demanded, which justifies a higher price if it can be resold

Types of interdependencies:

![[interdependencies.png]]


### Standard Auction formats
- *First price sealed bid*: valuations told to the auctioneer in secret, highest bidder wins and pays the offered price
- *Second price sealed bid*: valuations told to the auctioneer in secret, highest bidder wins and pays the price offered by the second-highest bidder
- *Dutch auction*: start very high, get lower
- *English auction*: start low, get higher
- *Call market*: collect asks (?)/bids
- *Continuous double auction*

![[standard-auction-formats.png]]

#### English auctions
Usual English auction: auctioneer begins with low price and raises it (in small increments) as long as there are two or more active bidders. (Also common: bidders call out that they want to bid higher).

Formal model: (actually modeling Japanese/Clock auction)
- No active calling out - price rises and bidders indicate if they are still in
- auction ends when only a single bidder is still in, who wins the object at the price at which the last bidder dropped out (QUESTION: at which he himself dropped out, or at which the second-highest bidder dropped out?)

#### Dutch Auctions
Dutch auction (also *open descending price* auction): Descending counterpart to English auction.

- Auctioneer calls out very high price, at which nobody wants to buy the good
- Price is incrementally lowered, until one bidder expresses interest
- First bidder who expressed interest obtains the item at the price offered

Example: flower auctions in the Netherlands.


### Criteria for comparing auction formats
Main criteria: Efficiency and Revenue.
#### Efficiency and Revenue
...are two different goals the auctioneer can have:

**Efficiency**: overall utility is maximized.
- depends only the allocation

**Revenue**: Auctioneer's utility (payoff) is maximized.
- depends on the allocation and on pricing

#### Other criteria
Other criteria include
- simplicity of strategies
- susceptibility to collusion
- speed auf the auction (number of rounds)
- robustness against non-equilibrium bidding

### Strategy in Vickrey Auctions
In second-price auctions, the strategy is straightforward: Bidding anything else than your true valuation might make you worse off in some situations.

![[vickrey-strategy.png]]

Note: a *weakly dominant strategy* is a strategy that
- always performs at least as good as any other strategy, regardless of what the opponents do
- for some strategy profile of the opponents, it performs better than other strategies

### Equivalence of Auction formats
*Outcome equivalence*: To auction formats result in the same allocation and same price.

*Strategic equivalence*: For every strategy in one auction format, there exists an equivalent strategy in the other auction format, and vice-versa.

Strategic equivalence is stronger than outcome equivalence.

> **Theorem**. Under [[#Valuation methods|IPV]], the *English auction* and the *second-price auction* are *outcome-equivalent*.

This is because in the English auction, there is no reason to continue bidding after the price surpassed the own value, and the highest bidder wins when the second-highest bidder drops out.

IPV are needed because with interdependent values, information is revealed when bidders drop out in the English auction, which may influence the own value.

> **Theorem** (Vickrey, 1961). The *Dutch auction* and the *first-price auction* are *strategically equivalent*.

Strategies are mapped as follows: The strategy to bid a certain value in the FPSB auction is equivalent to the strategy of dropping out at that price from the Dutch auction.

## Auctioneer's View: Expected Revenue
How much the auctioneer earns (expects to earn) depends on the distribution of bidder values.

Example: values $X_1, \dots, X_n \sim U([0, 100])$, auctioneer gets $\max_i X_i$ (*order statistic*)

#### Order statistics
Let $X_1,\dots,X_n \sim F$ iid. with density $f$. Denote $Y_i$ the $i$-th highest value of the $(X_i)_i$ (i.e. $Y_1 \geq \dots \geq Y_n$).

Distribution of $Y_1$:  $$F_1(y) = F(y)^n, ~ f_1(y) = n F(y)^(n-1) f(y)$$

Distribution of 2nd-highest order statistic $Y_2$: $$F_2(y) = n F(y)^{n-1} - (n-1) F(y)^n,~ f_2(y) = n(n-1)F(y)^{n-2} f(y) - n(n-1) F(y)^{n-1} f(y)$$

Expected value of k-th order statistic on uniform distribution: $E(v_k(n)) = (n-k+1)/(n+1)$ (e.g. n=2: 33.3, 66.6)

Expected revenues in Vickrey auction: $E(v_2(n)) = (n-1)/(n+1)$. For example: with 8 bidders, expect $\frac{7}{9}$ of maximal revenue. In other words: As the number of bidders grows, the revenue approaches the maximum (the auctioneer gets all the surplus)!

## Bidder's View: Optimal Strategy
### 2 Players
Example: value of 20€, competing with one other bidder in first-price auction. Belief about other bidder's valuation: distributed as $U([0, 100])$.

Denote by $b(v_{other})$ the projection of how much the other bidder would bid at an actual valuation $v_{other}$.

Also denote by $v_{own}$ your own valuation. Choose own bid $B$ s.t. $E(u) = (v_{own} - B) * P(B is the highest bid)$ is maximized. Calculate $P(B\text{ is the highest bid}) = P(B > b(v_{other}))$.

Assume here that $b(v) = \beta v, ~\beta \in (0, 1)$. So $P(B > b(v)) = P(v \leq B/\beta) = (B / \beta)/100$. So choose $B$ to maximize $(20-B)(B/(100\beta))$, optimized by $B = 10$.

The opponent does the same calculation => we actually obtain a *BNE where everyone bids half of their value*. So everyone shades their bid by 50%.

#### Limitations: ONly Bayes-Nash Strategy
Strong assumptions needed to bid in first-price auction: about number of rivals, how much they bid, how their valuations are distributed, common prior etc. => Bayes-Nash Strategy as opposed to the weakly dominant strategy in a Vickrey auction!


#### 2-player Revenue Equivalence of First/Second price auctions
Expected revenue for two-player case: 

- First-price sealed-bid auction: Highest order statistic divided by two
	- $E(\frac{1}{2}v_1(2) = (2)/(2(2+1)) = \frac{1}{3}$
- Second-price auction
	- $E(v_2(2)) = (2-1)(2+1) = \frac{1}{3}$

### n players
I'm bidding against n+1 players in a first-price sealed bid (FPSB) auction.

Conjecture once more: others bid fraction $\beta$ of their value. Then $P(B\text{is the highest bid}) = (\frac{B}{100\beta})^{n-1}$.

Then $E(U) = (v-B)(\frac{B}{100\beta})^{n-1}$ is optimized by $B = v\frac{n-1}{n}$

=> I shade my bid less if there are more rivals. BNE: everybody bids $\frac{n-1}{n}$ of their value


### General version (arbitrary distributions and bid functions)
> **Theorem** *The symmetric BNE strategy in a FPSB auction is given by $\beta(v) = E(Y_1 \mid Y_1 < v)$ , where $Y_1$ is the highest of the $n-1$ values of the competing bidders.*

In other words: bid $E(\text{2nd highest value}|v\text{ is the highest value})$.


Derivation of the theorem: see slides. Solves an ordinary differential equation. For more complicated assumptions, we usually get *partial differential equations* that we cannot simply solve in closed form.

### n-Bidder Revenue Equivalence of First/Second price auctions
The [[#2-player Revenue Equivalence of First Second price auctions|2-player revenue equivalence]] shown above holds more generally:

> **Revenue Equivalence Theorem (RET)**:
> 
> All auction formats with 
> - one object allocated to the highest bidder
> - gives anyone the option of paying zero
> - bidders who know their value and have uncorrelated valuations drawn from the same distribution
> 
>  are revenue-equivalent.

Corollary: FPSB or Dutch auction have the same expected revenue as Second-Price sealed-bid or English auctions.

Assumptions for this result (a lot):
- risk-neutral bidders
	- in experiments: risk-averse bidders
- independent private values
- symmetric bidders
	- often, there are at least several classes of bidders
- payment is a function of bids alone
	- no participation fee!
- known number of bidders
	- not always valid: think ebay

#### Assumption violations
##### Non-Risk-Neutral bidders
Behavior in second-price/English auction does not change, but in first-price/Dutch does: Risk-averse bidders lead to higher, risk-seeking bidders to lower revenue on Dutch auctions.

##### Common Valuations
Common valuations violate the IPV assumption. Example: drilling rights to an oil field are sold, without knowing exactly how much oil there is. Then the ones who bid most are the ones with the most optimistic estimation, which might be an overestimation: *winner's curse*!

With common valuations, strategies change: in English auction, your valuation can be updated based on other agent's bids; in the second-price auction, bidding $v_i$ is no longer a dominant strategy.

Ordering in terms of revenue: English > second-price > first-price = Dutch.

##### Optimal Auctions
If the seller wants to increase the revenue, in light of the RET: Set reserve prices, that must be met for an allocation to take place!

Myerson (1981) determined how to optimally set the reserve prices, given the auctioneer's beliefs about the bidder's valuation distribution. Myerson solved this for single-object auctions - for multi-object auctions, *as of today, this is still an open problem!*

##### Relaxing Assumptions
Much work is being done on relaxing assumptions of the RET in different ways.

## Simultaneous Multi-Round Auctions (SMRA)
Basic idea: generalize the English auction because it usually performs quite well to multi-object auctions, like spectrum auctions.

- Simultaneous: All lots at the same time and all lots open until no more bids on any lot
- Series of discrete timed rounds
	- termination when no new bids in one round
- Ascending: only increments possible
- Activity rules: less or equal activity per bidder than in the last round

First used: by the US FCC to sell spectrum. Standing high bid for some block + some percentage becomes the new minimum bid on this block.

#### Information policy in SMRA
In Germany: all bids made public. This may enable signaling (e.g. encoding information like postal codes which you are interested in into the bid amount): Click-box bidding should migitate such signaling.

In Mexico: no public information, only prices visible to participants.

#### Signaling in SMRA
Typical way of strategizing:
- signal interest about desired package in first-round bids
- jump bids/withdrawals to indicate strong interests/possible ways to split the spectrum
- "punishments" to signal "owernship" and vulnerabilities


#### Demand reduction
The bidders would like to reduce their demand in a compatible way: For example, if 6 bidders are interested in 12 blocks, they will pay much more if they all try to get all blocks, and much less if each only goes for 2 of the blocks.

![[bidder-demand-reduction.png]]

##### Case study: demand reduction (Mannesmann/Telekom case)
1999 German GSM/1800 auction: Four bidders, the huge Telekom and Mannesmann and the smaller Viag and E+.

Mannesmann made an extremely high initial bid on all blocks: four blocks for 40 million each, 4 blocks for 36.36 million each.

This was essentially an offer to Telekom: Telekom got the first 4 blocks for 40.01 million each, Mannesmann the other 4 blocks for 40 million each, the other bidders dropped out at these high prices.

#### Complementarities and Exposure Problem
If bidders have complementarities, bidding in SMRA can be risky: For example, valuing AB at 2m, but each A and B alone have no value. Bidding 1m each and winning only one means a huge loss.

Another example: 14 licenses, 4 bidders each want 4; there will be at least one loser, say the weakest bidder can only get 2. If he bids aggressively on 4 licenses in the beginning, he may later still end up with only 2, but at a much higher price! Bidding selectively on 2 licenses might have been the better strategy.

Therefore a decision has to be made in the beginning to either bid agressively or selectively.

#### More strategic challenges in SMRA
Besides the exposure problem and signalling, there are more strategies commonly employed:

- Budget binding/Spiteful bidding: one weak bidder can raise prices for the others without any risk, if he has nothing to lose in the auction.
- Parking: bidding on uninteresting blocks in order to maintain activity points, and moving to true interests later
- Hold up: raise price on a block that another bidder is interested in

This is not what was promised to us by nice things like the Vickrey auction! The natural question is: are there better designs? => next lectures.