# Computational Social Choice

### Common Voting rules
#### Plurality
Whoever is ranked first by more voters than any other candidate wins.

*Usage:* most democratic elections

#### Borda's Rule
The $i$-th ranked alternative of each voter gets $m-i$ points (the bottom alternative gets zero points). The points from each voter are summed together, and the alternative with the greatest total score wins.

*Usage:* in Slovenia for elections, in academic institutions, in the Eurovision Song Contest.

#### Sequential majority comparisons (SMC)
There is a fixed sequence of comparisons of the alternatives, e.g. (((a vs. b) vs. c) vs. d). Majority comparisons are performed between the first two alternatives, then between the winner and the next alternatives, etc. The winner of a majority comparison between two alternatives is the alternative that is preferred by the majority of voters.

*Usage:* e.g. for the US congress *amendment procedure*.

#### Plurality with runoff
The two alternatives with the highest plurality scores (i.e. which are first-ranked by the most voters) face off in a final round with a majority comparison.

(Runoff = "Stichwahl")

*Usage:* In France, Brazil and Russia for elections.

#### Instant Runoff
Successively delete alternatives that are ranked first by the lowest number of voters (by deleting an alternative on top of some voter's profile, the next undeleted alternative below becomes the top alternative of this voter). Repeat until all alternatives that remain are first-ranked by the same number of voters: these are the winners.

*Usage:* Canada and UK (for elections?), Oscar nominations.



##### Example: "A Curious Preference Profile"
Consider the preference profile

| 5   | 4   | 3   | 2   |
| --- | --- | --- | --- |
| a   | e   | d   | b   |
| c   | b   | c   | d   |
| b   | c   | b   | e   |
| d   | d   | e   | c   |
| e   | a   | a   | a   |

Then
- by plurality, a wins
- by Borda's rule, b wins
- by SMC, c wins
- by instant-runoff, d wins
- by plurality with runoff, e wins


### Desirable Properties (Axioms)
Here are informal definitions of some axioms that will be defined more formally.
- anonymity - the voting rule treats voters equally
- neutrality - the voting rule treats alternatives equally
- monotonicity - a chosen alternative will still be chosen if it is ranked higher in some individual rankings (and nothing else changes)
- Pareto-optimality - no alternative is chosen if there is another alternative which all voters prefer to it

![[common-voting-rules-axioms.png]]

SMC fails neutrality (obviously), and Pareto-optimality (say, a Pareto-dominates c, but a loses against b and b loses against c).

Runoff rules fail monotonicity: Say, voters reinforce b, and because of that b faces off against c (against which it loses) instead of a (against which it would have won).

### Strategic Manipulation / Strategic Abstention
Manipulation: mis-represent preferences to obtain a better outcome. By *Gibbard-Satterthwaite impossibility theorem*: every reasonable single-winner voting rule is prone to manipulation!

Abstention: don't participate in the election to obtain a better outcome. Plurality and Borda's rule are resistant to strategic abstention.