# 09 - Matching with Ordinal Preferences
From the very beginning, we have almost exclusively focused on settings where money is involved. Now, we go back again to settings without money: *matching with ordinal preferences*.

### Motivation
General setting/common features of the following examples: two-sided market, and money cannot be used.

##### Example: National Residency Match Programm (NRMP)
Problem: match doctors with hospital residencies (basically hospital internships). Each student submits rank-order list of hospitals, and hospitals submit rank-order list of students.

Before NRMP was introduced, there was the problem that students had to contact competitive hospitals very early. NRMP was adopted in 1952, and slightly modified in 1990s.

In the 1980s, it was realized that the algorithm used was the *deferred acceptance algorithm* (DA) proposed by Gale and Shapley in 1962.

##### Example: Kidney Exchanges
Goal: be able to match compatible kidney donors/recipients, but without using money. Example: A couple might be considered an ethically acceptable pairing; furthermore, if a couple is not compatible, but there is another couple and the two couples are cross-compatible, or even a larger cycle, donating with respect to this cycle might also be considered acceptable.

Usually: use top trading cycles (TTC) algorithm discussed later.

##### Example: School choice
In the US: many cities changed from neighborhood assignment to school choice programs. Similar system exist in Europe, also for schools and kindergartens.

## Matchings and Deferred Acceptance Algorithm
### Marriage Matching Model
Sets $M$, $W$ that should be matched one-to-one. Each man $m$ has a strict preference over women $\succ_m$, and vice-versa; $w$ is *acceptable* to $m$ if $m$ prefers $w$ to being unmatched, i.e. $w \succ_m \circ$

A *matching* is a set of pairs $(m, w)$ s.t. each individual has one partner.

A matching is *stable* if
- every individual is matched with an acceptable partner
- there is no *blocking pair* $(m', w')$: a pair not in the matching where $m'$ and $w'$ would both prefer to be matched together than with their assigned partner

##### Marriage Matching examples
Examples: it is possible that there are several stable matching; e.g. one stable matching better for the men, one better for the women.

### The Deferred Acceptance Algorithm
Iterative algorithm (man-proposing version):
1. in each round, each man proposes to the highest woman on his list
2. women each make a *tentative match* of their current options, and reject other offers
3. rejected men remove the rejecting women from their list
4. if there were rejections: repeat from 1.

An alternative version to this man-proposing version is the woman-proposing version, which is different.

> **Theorem**. The outcome of the DA algorithm is a stable one-to-one matching.

*Proof.* Suppose $(m, w)$ are matched, but $m$ prefers $w'$: Then at some point, $m$ proposed to $w'$ but was rejected; at that point, $w'$ preferred tentative match $m'$ to $m$. The final outcome for $w'$ must be at least as good as $m'$, i.e. certainly not $m$.

##### Aside: Roommate Problem
Similar to mariage problem where two people per room should be matched, but not bipartiteness: i.e. there can be cycles.

In this particular example, there is no stable matching!

### Why Stability?
#### Success of stable mechanisms
Many argue: stability leads to algorithms staying in use - if a market results in stable outcomes, there is no incentive for re-contracting.
Many stable mechanisms are still in use:
![[stable-mechanisms-example.png|500]]

#### Decentralized Markets
What if there is no clearinghouse? In other words, if the participants do not follow a fixed protocol? ==> Chaos ensues...

For example: 
- one woman $w$ may hold an offer for a long time before she rejects, but then they both don't have any other options anymore
- one man $m$ makes an exploding offer, before the corresponding woman could consider other options

#### An alternative: priority matching
Men and women submit preferences:
- algorithm matches all "priority 1" matches and takes them out of the market
- new priorities are assigned, the process iterates. When no "1-1" matches remain, look for "2-1" matches, etc.

QUESTION: probably the order in which the different priority pairs, e.g. "2-1, 1-2, 3-1, 2-2, 1-3" etc. are considered matters, right?

### Optimal Stable Matchings
Even stable matches may not be optimal for both the men and the women, as already [[##Marriage Matching examples|seen above]]: there might be other stable matchings which make some men, or some women, better off.

A stable matching is *man-optimal* if across all stable matchings, there is no man that has a more preferred partner in another stable matching. It is *woman-pessimal* if every woman gets her worst outcome accross all stable matchings.

> **Theorem**. The man-proposing DA algorithm results in a stable matching that is both *men-optimal* and *women-pessimal*.

*Proof*. By induction: no man is ever rejected by a woman who is possible for him. 
- if a woman $w$ rejects a man $m$, than $(m, w)$ cannot be in a stable matching

Takeway: DA algorithm is optimal for the proposing side.

#### Rural Hospital Theorem
What about unmatched partners? Does it matter to them who proposes first?

> **Theorem (Rural Hospital Theorem)**. The set of men and women who are *unmatched* is the same *in all stable matchings*.

### Non-Strategyproofness of DA Algorithm
Counterexample:
- $m_1: w_1 \succ w_2$
- $m_2: w_2 \succ w_1$
- $w_1: m_2 \succ m_1$
- $w_2: m_1 \succ m_2$

With truthful reporting: match $(m_1, w_1), (m_2, w_2)$. If $w_1$ instead reports that $m_1$ is *unacceptable*, the outcome is instead $(m_1, w_1), (m_2, w_1)$ which makes $w_1$ better off.

In fact, no strategy-proof stable matching mechanism exists:

> **Theorem**. There is no matching mechanism that is strategy-proof and always generates stable outcomes (with respect to the reported preferences)

Strategy-proofness always holds for only one side, the proposing side:

> **Theorem (Dubins and Freedman; Roth)**. The men-proposing deferred acceptance algorithm is strategy-proof for the men.


## Top Trading Cycles Algorithm
### Motivation: House allocation
In some matching markets, only one side has preferences: e.g. assigning students to campus housing.

*House allocation problem*: $N$ individuals and $N$ houses, where each individual has strict preference over houses. Each individual should be assigned a house.


### (Random) Serial Dictatorship
Algorithm: assign each student a priority (perhaps randomly); let student pick houses in order of their priority.

Outcomes can be very "unfair" ex post, however RSD has nice properties:

> **Theorem**. Serial dictatorship is Pareto-efficient (i.e. no mutually agreeable trades afterwards) *and* strategy-proof.

Proof for both properties is very simple.

### Top Trading Cycles algorithm (Gale; Shapley and Scarf)
(first mentioned by Gale, published by Shapley and Scarf)

Assume we start with an initial, but potentially non-efficient assignment: "everyone already owns a house, but might prefer another one".

Gale's TTC algorithm:
1. Build a directed graph:
	1.  each person points to their preferred house
	2. each house points to its owner
2. Remove all cycles from the graph, assigning people to the house they are pointing at, and delete the assigned houses
3. Re-draw the deleted edges, such that each person points to their preferred house from the still available ones
4. Repeat

#### TTC and the Core
> **Theorem (TTC = core)**. The outcome of the TTC mechanism is the *unique core assignment* in the housing market.

The core consists of all feasible unblocked assignments; an assignment $A$ is *blocked* by a coalition of agents if there is an assignment among the agents of this coalition from their initial endowments that all of them prefer to $A$.

*Proof*. By induction: Blocking coalition can't consist only of those matched in the first $n$ round. Uniqueness: the assignments in round $n$ are necessary - else those agents would block.

#### TTC and Incentives: Strategy-Proofness
> **Theorem**. The TTC mechanism is strategy-proof.

*Proof*. For any agent $j$ assigned at round $n$: no change in reports can give him a house that was assigned in earlier rounds; no house assigned in a later round makes him better off.

### Stability vs. Pareto Efficiency
Stability and Pareto Efficiency are two different goals: on the slides is an example of two matchings on the same preferences, one Pareto-efficient but not stable, the other stable but not Pareto-efficient!

In the example, the woman-proposing DAA is performed:
- afterwards, stable matching $(w_1, m_1), (w_2, m_2), (w_3, m_3)$.

Afterwards, a round of TTC is performed:
- Pareto-improvement *for the women* to $(w_1, m_2), (w_2, m_1), (w_3, m_3)$! However, no stability ($w_3$ and $m_1$ want to pair up).

The point is: there is a difference between stability and efficiency. Even more: there is a *tradeoff* between stability and efficiency.

QUESTION: is stability basically two-sided Pareto optimality?
- the Pareto improvement was only for one side, but no change where both sides are better off can be found.

### Example: TTC for Kidney Exchanges
Requirements: Blood type match

|             | donor 0 | donor A | donor B | donor AB |
| ----------- | ------- | ------- | ------- | -------- |
| receiver 0  | x       | -       | -       | -        |
| receiver A  | x       | x       | -       | -        |
| receiver B  | x       | -       | x       | -        |
| receiver AB | x       | x       | x       | x        |

...and tissue match.

Problem: patient <--> donor can not be paired up because they are incompatible.
- paired exchange: match two donor-patient pairs where Donor 1 is compatible with Patient 2 and Donor 2 is compatible with Patient 1
- list exchange: match one incompatible donor-patient pair with the waiting list
	- donor donates to patient at top of waiting list
	- patient of incompatible pair goes to top of waiting list

##### TTC for Kidney Exchanges
Roth, Sonmez + Unver (2004): this is essentially equivalent to the house allocation problem ==> TTC can be used to efficiently assign kidneys.

- first clearinghouse established in Boston in 2004
- often, kidney exchange programs today rely on integer programming to e.g. maximize the number of transplants

##### Kidney Exchanges in Europe
![[kidney-exchanges-europe.png|500]]

## Many-To-One Matching
Example: the [[#Example National Residency Match Programm NRMP|NRMP]], where hospitals actually want to hire *several* doctors. Model a hospital with $q$ positions as $q$ hospitals with one position each.

### Extended DAA (Many-To-One)
Algorithm (doctor-proposing):

1. Each doctor: propose to preferred hospital
2. Hospitals tentatively accept the $n$ best doctors that applied, reject the rest
3. Doctors that were rejected make new offers to their next-preferred hospital, respectively
4. Iterate until termination.

#### Many-To-One vs. Many-To-Many DAA
Many results carry over:
- at least one stable matches
- hospital-proposing DA ==> hospital-optimal stable matching
- [[#Rural Hospital Theorem Revisited|rural hospital theorem]] (see below)

But some do not:
- no stable mechanism is strategy-proof for the hospitals (probably because the original DA is not group-strategyproof?)

##### Rural Hospital Theorem, Revisited
We know: who proposes to whom makes a difference in the DA algorithm. How much can this change the outcome of a many-to-many matching?

Motivation: many rural hospitals stay unmatched when matching doctors to hospitals. Could another stable matching algorithm, something like a "rural hospital proposing algorithm", help? Answer: no.

The [[#Rural Hospital Theorem]] applies to this more general setting: *accross all stable matchings, all hospitals fill the same number of positions*.

#### Hospital Incentives (Manipulations) in Many-To-Many DAA
The hospital side can manipulate in many-to-many DAA, and misrepresent preferences to get better outcomes: there are examples for this both for the doctor-proposing and for the hospital-proposing version of the algorithm.

In the doctor-proposing version, a hospital with 3 applicants and 2 places may be able to get a 4th, more preferred doctor to apply by rejecting one applicant who will then kick the 4th applicant out of his preferred hospital. The rejection of the "right" applicant may only be possible with misrepresented preferences.

In the hospital-proposing version, it may happen that a hospital with two places kicks out another hospital out of his chosen doctor, which then goes on to take the second doctor of the first hospital. This may be avoided by mispresenting preferences to avoid the kicking-out assignment.

#### Example: School Choice
Abdulkadiroglu and Sonmez (2003): placement mechanisms in many cities, e.g. the *Boston mechanism*, are flawed. This lead to adoption of new mechanisms for many cities.

Soem school districts use DA today, there has also been discussion to use TTC.

### Comparing DA and TTC
![[comparison-ttc-da.png]]

## Summary
- DA is strategyproof for students, + stable, results in a student-optimal stable matching, but is not necessarily Pareto-efficient.
- TTC is strategyproof and Pareto-efficient for students, but not necessarily stable  (or fair!)
	- not stable = there can be justified envy!
	- the "fairest" of all efficient algorithms for 1-to-1 matchings (core solution!)
	- only minimal stability can be guaranteed: A student $s$ always gets her favorite course $c$ if she is among the top $q_c$ priorities of $c$.


What is much harder: assigning packages (e.g. say two doctors want to be assigned to the same hospital because they are a couple). Nice properties do not necessarily carry over to such more complex preferences.

Main takeaway: the whole field is about finding restrictions/assumptions that allow one to get nice properties.