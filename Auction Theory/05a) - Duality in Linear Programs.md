# 05a) Duality in Linear Programs
General form of a linear program:
$$\max c^\top x ~~ s.t. ~Ax \leq b, ~~x \geq 0$$

Mixed Integer (linear) Program: some variables integral.

### Motivation for LP Duality

##### "Economical" motivation
Example: 3 machines, trying to find the right product mix to max. revenue (Primal problem). Now another company wants to rent the machines: what's the minimum price for this? (Dual problem). This is the "economic way" of approaching the primal-dual problems.

##### "Mathematical" motivation (from the lecture)
Associate a factor $y_i$ with each i-th constraint. Add them together s.t. the left-hand-side is greater or equal than the objective function. 


Example:
Obj. func. $5x_1 + 3x_2$, constraints $2x_2 \leq 12 \qquad 3x_1+2x_2\leq 18$ ==> pick $y_1=0,y_2=2$ to obtain $6x_1 + 4x_2 \leq 36$ ==> 36 is an upper bound on the objective function, and $6 x_1 + 4x_2 \geq 5x_1 + 3x_2$.

Now: interpret the factors $y_i$ as new variables and pose the dual problem to find the tightest upper bound (now a minimization problem).

##### Own mathematical motivation
Inspired by [https://theory.stanford.edu/~trevisan/cs261/lecture06.pdf](https://theory.stanford.edu/~trevisan/cs261/lecture06.pdf).

Assume we have a maximization problem $\max c^\top x$ s.t. $Ax \leq b, x \geq 0$.
We want to find a non-neg. linear combination of the constraint inequalities (excluding the $x \geq 0$ ones) whose left-hand side is an upper bound to the objective function $c^\top x$. Call the coefficients $y_i$. Then
$$c^\top x \leq \sum_i y_i (Ax)_i \leq \sum_i y_i b_i$$
With this, we can verify that every feasible solution $x$ has an objective function value of at most $\sum_i y_i b_i$.

In the next step, we want to make $\sum_i y_i b_i$ as small as possible: this gives the tightest upper bound achievable by this method ("weak duality"); it turns out this is actually the tightest bound overall ("strong duality").

We formulate this as a minimization problem: $\min \sum_i y_i b_i$, i.e. $\min b^\top y$, s.t. some constraints hold. The constraints should say that $c^\top x \leq \sum_i y_i (Ax)_i$. We have $\sum_i y_i (Ax)_i = y^\top Ax$. For $c^\top x \leq y^\top A x$, it is sufficient that $c^\top \leq y^\top A$. Transposed and rearranged, we get $A^\top y \geq c$.

So the dual of
$$\max c^\top x~~\text{s.t.}~~Ax \leq b, x \geq 0$$
is
$$\min b^\top y~~\text{s.t.}~~A^\top y \geq c, y \geq 0$$

##### Marginal Cost motivation
If we have a constraint $\dots \leq C$, and an associated dual variable which has the value 5, then changing the constraint to $\dots \leq (C+1)$, the objective function increases by 5.

### Relationship between Primal and Dual
- primal max. problem constraints: $Ax \leq b, x \geq 0$
- dual min. problem constraints: $A^\top y \geq c, y \geq 0$

Observation when looking at matrix/vector notation: just a few matrices/vectors are moved around/transposed to switch between primal/dual. Also a symmetry property holds: The dual of the dual is the primal.


##### Weak Duality
*Weak duality* holds:
$$c^\top x \leq (A^\top y)^\top x = y^\top (Ax) = (Ax)^\top y \leq b^\top y$$
The objective value of the primal problem is *always less or equal* to the objective value of the dual problem (in case the primal is a maximization problem).

##### Complimentary Slackness
- economic interpretation: if $y_1^\ast + 3 y_3^\ast > 5$ then $x_1^\ast = 0$; if $x_1^\ast > 0$ then $y_1^\ast + 3y_3^\ast = 5$. Equivalently: $(y_1^\ast + 3y_3^\ast - 5)x_1^\ast = 0$.

**Theorem?** (Complimentary Slackness conditions): If $x^\ast$ is an optimal solution for the primal, $y^\ast$ one for the dual, then $(A^\top y^\ast - c)^\top x^\ast = 0$ and $(Ax^\ast - b)^\top y^\ast = 0$.

In other words, for each $i$, one of $(A^\top y^\ast -c)_i$ and $x^\ast_i$ is zero; also, one of $(Ax^\ast - b)_i$ and $y^\ast_i$ is zero.

Intuition/motivation: complimentary slackness lets us check for a feasible solution if it is optimal.

##### Strong Duality
*Strong duality*: If $x^\ast$ is an optimal solution to the primal problem and $y^\ast$ is an optimal solution to the dual problem, then $c^\top x^\ast = b^\top y^\ast$.

*Proof*: by compl. slackn. cond. $(A^\top y^\ast)^\top x^\ast = c^\top x^\ast$ and $(Ax^\ast)^\top y^\ast = b^\top y^\ast$. This implies: $c^\top x^\ast = b^\top y^\ast$.


##### Example: Single-Item Auctions
The single-item auction (for a divisible good) can be re-written as LP (this is of course overkill, but just serves as an example here).

Primal: $\max \sum_i v_i x_i$ s.t. $\sum_i x_i \leq 1$, $x_i \leq 1 \forall i$, $x \geq 0$.

Dual: $\min \sum_i y_i$ s.t. $y_1 + y_{i+1} \geq v_i \forall i$, $y \geq 0$.

(The slides call $p := y_1$, $\pi_i := y_{i+1}$)

Idea: from the primal, we get the allocation; from the dual, we get the prices.

QUESTION: this point is not totally clear yet.


### The Simplex Algorithm (Dantzig 1947)
Idea: start at corner of feasible area, go along corners until optimum is found. In worst case, exponential running time (but this rarely happens)

### Primal-Dual Algorithms
(not said much about, but apparently there is a link to auctions)