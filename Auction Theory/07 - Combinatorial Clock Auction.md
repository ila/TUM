# 07 - Combinatorial Clock Auction
Vague idea: two phase algorithm (easy round followed by more complicated round/or rather, ascending round followed by supplementary round).

QUESTION:  "*clock auction*" is another name for a [[##Standard Auction formats|dutch auction]], i.e. an auction where prices start very high and are lowered; why is the *combinatorial clock auction* called like that, when prices are going up?

Some more motivation: The [[##Formats with non-linear personalized prices|iBundle etc.]] ascending auctions we saw last week worked, but a) required many rounds and b) required non-linear personalized pricing, which is not so popular among bidders. Now, we use linear prices instead, and focus more on practical applicability than on theory (i.e. the formats proposed here are actually used, but do not have nice theoretical properties).

## Single-Stage Combinatorial Clock Auction (Porter et al. 2003)
XOR Bids, all bids remain active.

Functioning:
- While items are overdemanded:
	- Auctioneer announces a *linear price vector* (raise prices of overdemanded items)
	- bidders respond with their demand set
- If not item overdemanded ==> solve [[04 - Combinatorial Auctions#Winner Determination Problem|WDP]] considering *all bids submitted during the auction runtime*
	- if an active bidder is *displaced*, i.e. not assigned a bundle => rise prices of displaced items, go back to loop
	- otherwise, terminate

By raising the prices on the displaced items, i.e. the items the displaced bidder bid on, the algorithm is giving him a chance to submit a higher bid (so he can win the WDP), or to drop out (and the previous WDP solution can be implemented, then ok because the bidder is no longer displaced if he dropped out).

Also, an activity rule is used (e.g. monotonicity).

#### Strengths and Weaknesses of Single-Stage CCA
Strengths
- Relatively simple (definitely simpler than the two-stage CCA!)
- eliminates jump bidding + signaling
	- you can only bid what the auctioneer proposes, nothing else
- many applications: gas, energy
- experimentally: high levels of efficiency

Weaknesses
- only a heuristic! Simply a greedy algorithm known to work well in practice
	-	no equilibrium strategy known, no effiency guarantees

#### Truthful bidding?
There is no reason to bid thruthfully here. However, even if we assume bidders do bid truthfully for some reason -- we will not necessarily have a welfare-maximizing outcome, in fact it may be way off! *Often there may be some or even many unsold items!*

One example: (left linear ask prices, right bundle valuations)

|     | P(A) | P(B) | A      | B   | AB         |
| --- | ---- | ---- | ------ | --- | ---------- |
| v1  |      |      | 5      |     |            |
| v2  |      |      |        | 2   | 5          |
| v3  |      |      |        | 2   | 5          |
| t=1 | 1    | 1    | 1 (B1) |     | 2 (B2, B3) |
| t=2 | 2    | 2    | 2 (B1) |     | 4 (B2, B3) |
| t=3 | 3    | 3    | 3 (B1) |     |            |
| t=4 | 4    | 4    | 4 (B1) |     |            |
| t=5 | 5    | 5    | 5 (B1) |     |            |

end allocation: A -> B1, B -> unassigned. Efficiency: 5/7.  Problem: the auction never allowed B2 and B3 to voice their valuations for B, so they are not considered in the WDP.

One can construct more elaborate examples with arbitrarily bad efficiency if bidders bid truthfully.

One real-world way to circumvent such problems is to re-start the auction with the unsold items.

Another example, where not even that works, consists of many items A-Z; one person has a proportionally high valuation for A only (6), another a much higher, but proportionally lower valuation for the whole bundle A-Z (79). The first guy wins A, B-Z remain unsold.

#### Powerset bidding
Even stronger than truthful bidding: people tell you all bundles they would be interested in at the given prices, not just the one maximizing utility. They do this in order to gain an advantage and avoid the problems above.

However, there is another example where one bidder has a very high valuation on one item that is never discovered => again, an arbitrarily low efficiency.

QUESTION: does this have anything to do with the auction format?

#### Demand-reduction strategizing
Single-Stage CCA is also vulnerable to demand-reduction strategizing, similar to the [[03 - Single-Object Auctions#Simultaneous Multi-Round Auctions SMRA|SMRA]]. Example: see slide 10.


## Two-Stage Auction by Ausubel, Cramton, Milgrom 2006
Idea: have a *two-stage auction*, with the (vague) argument that the first phase, the *clock stage*, helps *price discovery*, but a second, *supplementary stage* allows to submit further bids to help avoid inefficiencies.


### Considerations for the second stage
Motivaton: would be nice to have "iBundle-like" outcomes. iBundle always results in core-stable prices, but only with BSM results in VCG prices. We want to use VCG here, on the other hand: this might mean we have non-core-stable outcomes.

Idea: what if we compute the core polytope, and choose a solution from it that is *VCG-closest*? This is core-stable, and at least hopefully "somewhat close" to incentive compatibility, since it is similar to VCG.

#### Vickrey-Nearest Core Prices
Example: VCG outcome not in the Core
![[core-vickrey.png]]

Winners: (B1, B2). VCG Prices: B1 pays 14 (bid of B4), B2 pays 11 (bid of B5). But then: B3 complains because 25 < 32.

Solution: pick some core price that is closest to the VCG prices. Rationale: winning bidders would have to shade their bids by quite much in order to influence the price you're paying, and by this they would risk losing.

Note: this is not necessarily so great, there are papers arguing against it as well. It's a very practical approach, but does not satisfy so nice theoretical properties anymore. One disadvantage: there is no dominant strategy anymore.

### Two-Stage CCA algorithm
1. Primary stage (*Clock phase*)
	1. one *linear anonymous price vector* is announced.
	2. each bidder: submits one indivisible bundle bid at these prices, subject to an activity rule.
	3. auctioneer raises prices for overdemanded items
	4. if no overdemanded items: terminate
2. Supplementary bids stage (*Sealed-bid phase*)
	1. All bids from stage 1 automatically included as XOR bids
	2. Bidders can submit arbitrarily many further XOR bids, subject to another activity rule.
	4. Payments: determined by bidder-optimal core-pricing rule (approximately), i.e. VCG-nearest.

Intuition: Stage 1 reduces value uncertainty; Stage 2 ensures high efficiency. Because of the activity rule, people should be motivated to bid truthfully in round 1. However, vague arguments and there is no consensus on if this is a good approach or not, and if these claims are justified or not.

##### Usage of Two-Stage CCA
- widely used (see Palacios-Huerta, Parkes, and Steinberg 2021)
- not all arguments for this are very strong or formal

### Activity Rules for the Clock Round
Reason we need activity rules: otherwise, bidders could ignore the first stage, and it couldn't be helpful for price discovery. Goal: force bidders to bid now and not later when they know more.

- In primary stage: simple point-based *monotonicity activity rule*, sometimes hybrid rule: combined with *revealed-preference rule*.
- in secondary stage: revealed-preference *relative cap* and *final cap* rules.

#### Monotonicity Rule
Monotonicity:
-	 in round t+1 a bidder can only bid on bundles requiring at most the number of eligibility points he used in round t.
	 
Usually, one uses eligibility points that do not directly correspond to the number of items: this allows to factor in prior knowledge about the worth of items, e.g. a spectrum licence for an urban region will have more eligibility points than one for a rural region.
	 
The monotonicity rule alone can be very restrictive. Example: one eligibility point per license X, Y, Z. In the beginning, X is very profitable; however later, the price for X rises and the bundle {Y, Z} is more profitable. But switching is not allowed anymore due to the activity rule!

#### Hybrid activity rule: Monotonicity + Revealed Preference
Add a *revealed preference rule* to the monotonicity rule: a bidder may bid on bundles that require more eligibility points *if it became relatively less expensive* than what he bid on before.

Formally: For the revaled preference rule to apply, the bids have to respect, for all rounds $s$, the revealed preference constraint
$$\sum_i x_{ti}(p_{ti}-p_{si}) \leq \sum_i x_{si} (p_{ti} - p_{si}),$$
where 
- $t$ is the current and $s$ a prior round,
- $x_{il}$ = quantity of item $i$ which the bidder bid on in round $l$, 
- $p_{il}$ = price of item $i$ in round $l$

More intuitive formulation (not from the lecture): say in round $t$, you want to bid on bundle $T$, and in round $s$ you bid on bundle $S$. Then $p_t(T) - p_s(T) \leq p_t(S) - p_s(S)$, or $\Delta_p T \leq \Delta_p S$.

Another interpretation (not from the lecture): We assume that the bidder bid truthfully, and are holding them to that standard - with this assumption, by choosing $S$ over $T$ in round $s$, the bidder **revealed** something about their valuations for $S$ and $T$. Namely, $v(S) - p_s(S) \geq v(T) - p_s(T)$. This restricts the possible valuations of $v(T)$: namely, $v(T) \leq v(S) - p_s(S) + p_s(T)$. If the bidder wants to violate this later, it is because he did not bid truthfully in round $s$ - so we are forbidding it! By not bidding truthfully, the bidder risks being restricted by the revealed preference rule later on.

##### Example for revealed preferences
![[revealed-preference-example.png]]

With the monotonicity rule, the bidder would not be allowed to switch to BC in round t. With the hybrid rule, she is, due to the revealed preference constraint being fulfilled (it must be fulfilled for all rounds!):

$p_t(B) + p_t(C) - (p_s(B) + p_s(C)) = 15 - 14 = 1 \leq = 4  = 5 - 1 = p_t(A) - p_s(A)$

In the interpretation outline above, we can decuce from the bid in round $s$ that if bidding was truthful, $v(BC) \in [0, v(A) + 13]$. The bid submitted now is compatible with this deduction, therefore the non-monotonic switch is allowed.

### Activity Rules for the Supplementary Round
For the [[#Activity Rules for the Clock Round|activity rules in the first round]] to be useful at all, we also need activity rules for the supplementary round which connect the two rounds (otherwise, everyone could just skip the first round).

The rules for the supplementary round are:
- bidders *can bid on any bundle*, but the *amount they can bid is limited*
- standing bidders, i.e. those who participated in the final clock round, can *arbitrarily increase their standing bids*, i.e. can place arbitrary bids on the bundle they bid on in the final round.
- all other bids are subject to the *relative cap rule* and the *final cap rule*, which both are essentially revealed preference rules.

The final cap rule is not always used, but it turned out it can prevent some manipulations which are otherwise possible. The details of this are intricate, and not covered in the course.

#### Relative Cap Rule
A bound for the possible price for a bundle $K$ subject to the relative cap rule is computed as follows:
- the *anchor round* is the last round when the bidder was eligible to bid on $K$
	- QUESTION: does this use only monotonicity, or the hybrid rule?
- the *anchor combination $K'$* is the bundle the bidder bid on the the anchor round
- the *anchor bid* is the *highest bid on $K'$ in any round* (including bids from the supplementary stage!)

Then according to the relative cap rule, a bid on $K$ by the bidder may be at most
$$(\text{Anchor bid}) + (\text{Price difference of $K$ and $K'$ in the anchor round})$$

#### Final Cap Rule
A bundle $K$ subject to the final cap rule is subject to the revealed preference constraint with respect to the final clock round.

If $t$ is the final round, $K'$ is the bundle bid on in the final round, and $b(X)$ denotes the bid for a bundle $X$, then the final cap rule is equivalent to

$$b(K) \leq b(K') - p_t(K') + p_t(K).$$

Note: if the anchor round of the relative cap rule is the final round, the final cap rule is automatically satisfied as well.

##### Example of supplementary stage activity rules
Example:
![[two-stage-cca-activity-rule-example.png]]

Note that for the relative cap rule, the highest bid on $K'$ is 700$ and not 360$, because this higher bid was submitted as supplementary bid.

### Consequences of the Activity Rules
##### Example: Bidder fails to misrepresent his valuation
For example: could a single bidder bid very low in the beginning, and then bid very high in the last phase, winning to everyones' surprise?

No, because of the activity rules. Example: 14 items supply. B4 drops out early of 2 items despite of a high valuation for 4 items. He then does not obtain the two other items he'd like in the end because he can only bid 520 on 4 items.

![[counterexample-supplementary-round.png|300]]

##### Example: Supplementary phase bids can become winning
The supplementary round is not useless: unlike in the previous example, new supplementary round bids can become winning!

Similar example, with changed bidder 4. After the clock round, there is an excess supply of 2. Both bidder 3 and bidder 4 submit additional bids on smaller bundles, which actually become winning. However, bidder 3 lost his standing bid from the clock round, and would have kept it if he didn't submit the new bid.

![[counterexample-supplementary-round-2.png|300]]

There are major problems! There is a counterexample that the designers were not aware of, and apparently Bichler et al found it? Something about revealing excess supply at the end of stage 1, or similar.

#### Theorems about supplementary round bidding
Two theorems with very important consequences:

> **Theorem 1.** If demand equals supply in the final primary bid round, a supplementary bid which exceeds the primary round bid cannot become losing.

In other words: if there are no unsold items, the standing bidders just need to increase their bids by some $\epsilon$ and can be sure to win. This is of course not at all incentive-compatible! One remedy for auctioneers is to keep the excess supply a secret.

> **Theorem 2.** If bundle $X$ is unallocated after the last primary bid round, a supplementary bid adding $p(X)$ to the primary bid of a standing bidder cannot become losing if all his supplementary bids contain the standing bid.

For example, in the [[#Example Supplementary phase bids can become winning|second example above]], bidder 3 could have increased his 50(4) bid to 70(4) (or even 60(4)?), he could have been sure to win these 4 items.

Note: this facts were not initially known to the regulators who used these rules. The takeaway is that even with a clever-looking auction format, minor details can still screw up the whole process.

### Spiteful bidding in Two-Stage CCA
Problem: You can sometimes safely submit losing bids that you will not have to pay, but that drive up payments for the others! This is called *spiteful bidding*. With [[#Vickrey-Nearest Core Prices|VCG-nearest prices]], spiteful bids can even decrease the own payments.

Example in the general setting: B1 drives up B2's price, and himself gets a lower price (an artifact of the Vickrey-nearest pricing).

![[spiteful-bidding-vickrey-nearest.png|450]]

##### The Swiss Case (2012)
Swiss LTE auction in 2012: Orange vs. Sunrise vs. Swisscom. Here: Sunrise paid *way more* (30% more) for a less attractive package.

This illustrates: the prices are personal prices! This is a general problem with VCG pricing. But here, you could speculate that maybe also spiteful bidding came into play, since it is so easy in the auction scheme used. It is known that later, in spectrum auctions, spiteful bids were very common to hurt the competitors.

Some more history: early in the UK when this was first used, only a little amount of bids was submitted. In a later auction in Austria, when all of these problems were known, hundreds of spiteful bids were submitted. Driving up the revenues, but leading to very expensive bids for the telecoms. In essence, everybody tried to raise prices for the competitors; partly, in order to not "look stupid" afterwards like Sunrise did.

### Core-Selecting Auctions and Revenue Monotonicity
Vickrey revenues are not monotonous; if a new bidder is added, revenues can go down (this was already discussed [[04 - Combinatorial Auctions#Example of Non-Monotonicity|previously]]).

The same is true for core-selecting auctions; these also do not satisfy monotonicity.

### Experimental Results
#### Laboratory Experiments
SMRA vs. CCA: In one experiment, SMRA significantly outperformed CCA (one should keep in mind that the bidders were not professionals).

Another CCA problem: Bidders will submit many many bids with XOR bidding, or actually, more realistically, have *many missing bids*.

Another efficiency loss: the cap severely caps bids of people that otherwise would have bid higher, if they fail to bid truthfully in the beginning.


##### Simple (compact) Bid Languages
Often: complementarities/synergies are *known* by auctioneer and bidders. Example: you know that synergies are often within spectrum bands, but not between them - the bid language can be simplified with that knowledge. With a simpler bid language, there are less missing bids.

In experiments, simpler bid languages clearly outperformed more complicated ones efficiency-wise.


#### Numerical Experiments
- Straightforward bidding yields high efficiency in non-linear personalized price based auctions (dVSV, iBundle(3))
- In linear price auctions (ALPSm, Clock): straightforward bidding a bit worse, but still looks ok
- heuristic bidding (basically, trembling-hand straightforward bidding) destroys efficiency completely in dVSV, but the others are robust against it

Comment: Revenue > 100% Vickrey comes from the fact that buyer submodularity is not satisfied.

##### Number of Auction Rounds in Experiments
![[experiments-number-auction-rounds.png]]

### Problems with Two-Stage CCA
Opinions are split on how successful this is: it *is* quite widely used. But yet, it has a number of issues.

See it as a lesson that designing such a mechanism is very hard (taking into account many factors when designing it might even make it worse than a simple approach like SMRA).

- truthful bidding: no equilibrium strategy
- missing bids problem with XOR bid language: only possible to reveal a small amount of your preferences due to combinatorial explosion
- if you bid inconsistently in clock phase, this restrict supplementary bids (really a disadvantage?)
- violation of law-of-one-price because of VCG/core-selecting payment rule.
- [[#Core-Selecting Auctions and Revenue Monotonicity|no revenue monotonicity]]
- spiteful bidding
	- sometimes bidders can drive up prices of others safely
- intransparency until end of auction: until the winner determination at the very end, nobody really knows what the outcome will be.


## Alternative open auction formats: Pseudo-Dual Linear prices
Rough idea: *pseudo-dual linear price* auction formats are an alternative to CCA. These somehow use relaxed LP dual prices (that overestimate the true prices, but often by not much).

To get these, use a restricted LP approximation to [[06 - Iterative Combinatorial Auctions#Computing Anonymous Linear ask prices CAP1|CAP1]] and minimize approximation errors.

Suggested by several authors in the past.

More details on slides (but not treated in a lot of detail).


## Summary of Open Combinatorial Auction Formats
[[06 - Iterative Combinatorial Auctions|Non-Linear price personalized CAs]]:
- very large number of auction rounds
- collect nearly all valuations
- straightforward bidding: ex-post equilibrium with BSM valuations; leads to effiency
- nice theoretical properties, but of limited practical use

[[07 - Combinatorial Clock Auction|Linear-price CAs]]:
- only a linear number of prices
- no equilibrium strategies known
- high efficiency in experiments
