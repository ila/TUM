# 02 - Social Choice, Utility, Mechanism Design and Quasilinear Mechanisms

Overview of related fields:

- Game theory
- Social Choice Theory
- Mechanism Design


## Social Choice Theory
Voting rule: n voters, m alternatives, preference profile for each voter

- Plurality rule
- Borda rule
- **Condorcet criterion**: candidate that wins all pairwise majority comparisons (does not always exist)
	- Condorcet paradox: cycles can occur!
- Majority criterion (isn't this = plurality?)
- Consistency: if alternative chosen, it it also chosen from all subsets in which it is contained
	- Plurality voting is not consistent!
	- Example: what happens if a candidate dies one day after the election - is the election still valid?


### May's Theorem
"2 Alternatives are easy"

### Social Choice vs. Social Welfare functions
- $O$: finite set of alternatives/outcomes
- $L^n$: set of preference orderings of a set $I$ of n voters
- *Social choice function* over $I$ and $O$: $C: L^n \to O$
	- produces one alternative (different from the definition in CSC!)
- *Social welfare function* over $I$ and $O$: $W: L^n \to L$
	- produces preference ranking

### Arrow's Impossibility Theorem (1950)
Some principles for a social welfare function:
- Pareto efficiency
	- If everybody prefers a to b, a should be preferred to b
- Non-dictatorship (not one voter s.t. its preferences are always copied)
- Independence of irrelevant alternatives (IIA)
	- if a is preferred to b, and then the votes are changed s.t. the relative ordering between a and b stays intact in each vote, then a should still be preferred to b.


*Arrow's Impossibility Theorem*: Assume 3 alternatives. A social welfare function which is Pareto efficient and satisfies IIA is dictatorial.

##### Proof sketch
Start with the situation where everybody prefers A and C to B. (by Pareto efficiency: B ranked last). Now go through to the voters in turn and for each, move B to the top of their preference list, until B is the preferred outcome overall.

Show: there is a pivotal voter who causes the swing, using IAA

Show then: the pivotal voter $i$ dictates society's decision between $A$ and $C$ (local dictator over this set)

(kinda unclear to me... Why does this dictator argument work in general, not just for the particular preference profile we constructed?)

### Social Choice Impossibility results
... (see slides)

### Manipulability
All known voting rules are (sometimes) manipulable!

##### Gibbard-Satterthwaite Impossibility Theorem
Assume at least 3 alternatives. No rule is simultanesouly
- onto (every alternative can win, in principle)
- non-dictatorial,
- non-manipulable (i.e. dominant-strategy truthful)

### Circumventing Impossibilities
- Relaxing properties
- randomization
	- random serial dictatorship is strategy-proof!
- limiting the domain (e.g. only two candidates; single peaked votes; allow for monetary transfers)


## Utility Functions
Ordinal vs. cardinal measures.

Ordinal: advantage of comparability, but disadvantage that it cannot express intensity. In market environments, cardinal utilities are often reasonable.

### Uncertainty
Lotteries, etc.

St. Petersburg Paradox: resolved by *diminishing marginal utility*, i.e. $u'(o) > 0$ and $u''(o) < 0$. Referred to as *Bernoulli utility function*.

Simplest example: $U(o) = ln(o)$.

### Axioms of Expected Utility Theory
Axioms for *Von-Neumann-Morgenstern utility functions*:

A.1 $\succcurlyeq$ is complete

A.2 $\succcurlyeq$ is transitive

A.3 $\succcurlyeq$ is continuous: if $p \succcurlyeq q \succcurlyeq r$, there is an $\alpha$ s.t. $\alpha p + (1-\alpha)r \sim q$

A.4 $\succcurlyeq$ is independent: if $p \succ q$, then for all $\alpha \in (0, 1]$: $\alpha p + (1-\alpha)r \succ \alpha q + (1-\alpha)r$.

$\succcurlyeq$ satisfies the Axioms A1-A4 if and only if there is a utility function $U(o)$ s.t. for all $p, q \in \Delta(O)$, $p \succcurlyeq q \Leftrightarrow U(p) \geq U(q)$.



## Mechanism Design
Goal: we'd like to use the private information of each agent to implement a social choice function, and would like to avoid them lying about it.

Example: "outcome 1 gives me 100000000 utility and everything else 0" even if they only slightly prefer outcome 1


### Bayesian Mechanism Design
Bayesian game: $(I, A, \Theta, F, u)$ with
- *agents* $I$, 
- *action set* $A = A_1 \times \dots \times A_n$,
-  *type space* $\Theta = \Theta_1 \times \dots \times \Theta_n$,
-   common *type prior* $F: \Theta \to [0, 1]$,
-    *utilities* $u = (u_1, \dots, u_n)$, $u_i: A \times \Theta \to \mathbb{R}$.

For mechanisms: actions not equivalent to outcomes anymore! Introduce
- $O$ is the set of *outcomes*,
- $u_i$ now has signature $u_i: O \times \Theta \to \mathbb{R}$

Define the Bayesian game that is being played as $(I, O, \Theta, F, u)$, i.e. with $O$ as action space.

A *mechanism* in such a game is a pair $(A, M)$ where $A$ are the actions and
- $M: A \to \Delta(O)$

In other words: the players choose their actions, which the center of the mechanism translates to a distribution over the outcomes. These determine the utilities.

##### Implementation of social choice functions
The mechanism $M$ *implements* a social choice function if there is an equilibrium induced by $M$ that matches the outcome of the social choice function. Formally:

- $M$ *implements $C$ in a dominant strategy* if for any $u$, the game $(I, O, \Theta, F, u)$ has an equilibrium in dominant strategies and in any such equilibrium $a^\ast$, $M(a^\ast) = C(u)$
- $M$ *implements $C$ in a Bayesian Nash equilibrium* if the Bayesian game $(I, O, \Theta, F, u)$ has a Bayesian Nash equilibrium such that for each $\theta$ action profile $a$ that can arise in the equilibrium given $\theta$, $M(a) = C(u(\cdot, \theta))$

In other words: the choice function selects an outcome according to the preference of the agents, i.e. $u$. 


### Design Goals: Incentive Compatibility
- *Incentive compatibility* (*truthfulness*): No incentive to lie about one's type
- *Dominant-strategy incentive compatible* (i.e. *strategyproof*) mechanism: $$
\forall i, \theta_1,\dots,\theta_n, \theta_i':~ u_i(o(\theta_1,\dots,\theta_i,\dots,\theta_n), \theta_i) \geq u_i(o(\theta_1,\dots,\theta_i',\dots,\theta_n), \theta_i)$$




### Direct Revelation Mechanisms
Observation: there is no need for arbitrarily complicated mechanisms, we can focus on *direct-revelation mechanism* where every bidders reports their type truthfully. => Revelation principle (Gibbard 1973)

##### Revelation principle
"The agents don't have to lie, because the mechanism already lies for them"

![[direct-revelation.png]]


### Single-Peaked Preferences
Assumption: alternatives are ordered on a line, and each voter has a most preferred alternative, and prefers the other alternatives in order of the distance from their preferred alternative. 

Strategyproof mechanism: choose mediam mechanism. (Also Condorcet winner!)


## Quasilinear Mechanism Design

### Quasilinear utility functions
We separate non-monetary and monetary outcomes:
- Define *outcome* $o\in O$: o = (x, p) s.t. x is a non-monetary outcome and $p$ is an n-dimensional payment vector of payments to the individual agents
- agents can valuate the non-monetary outcomes: $v_i(x, \theta_i) \in \mathbb{R}$

A *utility function*  in this context is computed as the utility of the value of the non-monetary outcome minus the payment:
$$u_i(o, \theta_i) = U_i(v_i(x, \theta_i) - p_i)$$

A utility function is *quasi-linear* if it is "linear in the payment", (but apparently actually any monotonic $U_i$ means that $u_i$ is considered quasi-linear). Now we assume risk-neutrality, i.e. $u_i(o, \theta_i) = v_i(x, \theta_i) - p_i$.

##### Some properties of quasilinear utility functions (?)
- no budget constraints: amount of money the agent has does not influence utility
- no externalities: agents don't care how much others have to pay
- transferable utility: money can be used to transfer utility


### Interpersonal Utility Comparisons
"we assume *interpersonal utility comparisons*" (whatever that means) => allows for 
- *utilitarian social welfare function*: overall welfare = sum of individual utilities
- *Rawlesian social welfare function*: overall welfare = utility of least-well-off individual

Assumption for now: utilitarian.

### Mechanism Design Goals
#### Incentive Compatibility in Quasilinear, utilitarian setting
Compare with [[02 - Social Choice, Utility, Mechanism Design and Quasilinear Mechanisms#Design Goals Incentive Compatibility | this section]], but now with the quasi-linear utilities substituted.

- *Dominant-strategy incentive compatible* (i.e. *strategyproof*) mechanism: $$
\forall i, \theta_1,\dots,\theta_n, \theta_i':~ v_i(x(\theta_1,\dots,\theta_i,\dots,\theta_n), \theta_i) - p_i(\theta_1,\dots,\theta_i,\dots,\theta_n) \geq$$ $$v_i(x(\theta_1,\dots,\theta_i',\dots,\theta_n), \theta_i) - p_i(\theta_1,\dots,\theta_i',\dots,\theta_n) $$

- *Bayes-Nash incentive compatible* if telling the truth is a BNE:
$$\forall i, \theta_k, \theta_i': 
\sum_{\theta_{-i}} P(\theta_{-i}) (v_i(x(\theta_1,\dots,\theta_i,\dots,\theta_n), \theta_i) - p_i(\theta_1,\dots,\theta_i,\dots,\theta_n)) \geq
$$
$$
\sum_{\theta_{-i}} P(\theta_{-i}) (v_i(x(\theta_1,\dots,\theta_i',\dots,\theta_n), \theta_i) - p_i(\theta_1,\dots,\theta_i',\dots,\theta_n))
$$

#### Individual Rationality
Avoid selfish center: "all agents give me all their money" => agents would not participate.

In an individually rational mechanism, no participant can be made worse off if deciding to participate in the mechanism.

- *ex-post individually rational* mechanism: $$\forall i, \theta_1,\dots,\theta_n: ~ v_i(x(\theta_1,\dots,\theta_n), \theta_i) - p_i(\theta_1, \dots, \theta_n) \geq 0$$


### The VCG (Vickrey-Clarke-Groves) Mechanism
Goal: design mechanism which is 
- efficient (maximize utilitarian SWF)
- truthful revelation is an equilibrium
- individually rational
- budget balanced

i.e. get around [[#Gibbard-Satterthwaite Impossibility Theorem]] using quasi-linear utilites


#### VCG mechanism
(Notation slightly adjusted from the slides)

Mechanism where 
1. Each $i$ *reports type* $\theta_i'$ and has valuation $v_i'(x) = v_i(x, \theta_i')$ (max amount $i$ would pay the center for choice $x$).
2. Mechanism *chooses outcome* $x = \arg\max_x \sum_i v_i(x, \theta_i')$
3. Mechanism *determines payments* of each agent $i$:
	1. Pretend $i$ does not exist and choose $y = \arg\max \sum_{j\neq i} v_j(y, \theta_j')$
	2. $i$ must make payment $\sum_{j\neq i} v_j(y, \theta_j') - \sum_{j\neq i} v_j(x, \theta_j')$: The difference between the other agent's total welfare between $y$ and $x$, i.e. how much the existence of $i$ damages the other agents.

The i-th agent has utility $\sum_i v_i'(x, \theta_i') - \max_y \sum_{j \neq i} v_j' (y, \theta_j')$: their *marginal contribution to welfare*.

Some more explanation (taken from [Noam Nisan, Introduction to Mechanism Design (for Computer Scientists)]):
- the term $- \sum_{j\neq i} v_j(x, \theta_j')$ makes the mechanism incentive compatible, by aligning the i-th bidder's incentives with the goal of maximizing social welfare
- payments of $h_i(\theta'_{-i}) - \sum_{j\neq i} v_j(x, \theta_j')$ would be incentive compatible for any function $h_i$ that is independent of the i-th agent's reported types
- one wants to choose $h_i$ in such a way that it is *individually rational* (all players always get non-negative utility), and has *no positive transfers* (no player is ever paid money).
- these properties are provided by *Clarke's pivot rule*  $h_i(\theta'_{-i}) = \max_y \sum_{j \neq i} v_j(y, \theta'_j)$. Choosing this $h_i$ leads to VCG payments.

#### Vickrey Auction
The second-price Vickrey auction is a VCG mechanism: $v_i(x, \theta_i)$ is the value the item has if $i$ gets the item in $x$ and 0 otherwise. The bidder therefore gives the item to the highest-bidding agent, who has to pay the second-highest price ($y$ is that the second-highest bidder wins; so the second-highest price minus what the other agents get if $x$ is chosen, i.e. 0)

#### Strategyproofness of VCG Mechanism
The i-th agent can neither affect the choice of $y$, nor the terms $v_i'(x, \theta_i')$. Maximizing the utility therefore is equivalent to maximizing $v'_i(x, \theta_i)$, which is the case if $\theta_i' = \theta_i$.

=> Truth-telling is a dominant strategy

#### Grove mechanisms
Green and Laffont 1977, Holmstrom 1979: The general class of Groves mechanisms are the only mechanisms that implement *efficient allocation in dominant strategies with quasi-linear utility functions*.

#### Downsides of VCG
- trusted center required
- not always budget-balanced
- Collusion: Strategy-proof against single actors, but not against groups of actors


### Wilson Doctrine
R. Wilson (1987):
The deficiency of Bayesian game theory is that the common prior assumption is unrealistic. Common knowledge assumptions need to be weakened to approximate reality.