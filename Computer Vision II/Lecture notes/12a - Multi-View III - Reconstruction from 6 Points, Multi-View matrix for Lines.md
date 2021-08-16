## Multi-View Reconstruction

Two approaches: 
1. cost-function based: maximize some objective function subject to the rank condition => non-lin. opt. problem: analogous to bundle adjustment
2. decouple structure and motion, like in the 8-point algorithm. Warning: not necessarily practical, since not necessarily optimal in the presence of noise + uncertainty (like the 8-point algorithm)

Approach 2 is called *factorization approach* (because it factors - i.e. decouples - the problem)

### Factorization Approach for Point Features
Assume: $m$ images $x_1^j, \dots, x_m^j$ each of points $p^j$, $j \in [n]$.
 
 Rank constraint => columns of $M_{p^j}$ dependent => (first column) + $\alpha^j$ (second column) = 0. As seen above, $\alpha^j = 1/\lambda_1^j$.
 
 ![[Pasted-image-20210608164310.png|500]]
 
 This equation is linear in the camera motion parameters $R_i, T_i$, and can be written as:
 
 $$P_i \begin{pmatrix} R_i^s \\ T_i \end{pmatrix}
 = \begin{pmatrix} 
x_1^1{}^\top \otimes \widehat{x_i^1} & \alpha^1 \widehat{x_i^1} \\
x_1^2{}^\top \otimes \widehat{x_i^2} & \alpha^2 \widehat{x_i^2} \\
 \vdots & \vdots \\
x_1^n{}^\top \otimes \widehat{x_i^n} & \alpha^n \widehat{x_i^n}  \end{pmatrix}
 \begin{pmatrix} R_i^s \\ T_i \end{pmatrix}
= 0 \in \mathbb{R}^{3n}
$$

Here simply things were re-arranged, the $R$ and $T$ matrices stacked in one long vector.
One can show: $P_i \in \mathbb{R}^{3n \times 12}$ has rank 11, if more than 6 points (in general position) are given! (Intuition behind 6: 3n rows for n images, but only 2 out of three are lin. indep.)

=> one-dim. null space => projection matrix $\Pi_i = (R_i, T_i)$ given up to scalar factor!

In practice: use > 6 points, compute solution via SVD.

Like 8-point algorithm: not optimal in the presence of noise and uncertainty.

##### Decoupling compared to 8-point algorithm
Difference from 8-point algorithm: structure and motion not fully decoupled, since the 1/depth parameters $\alpha$ are needed to construct $P_i$. However, structure and motion can be iteratively estimated by estimating motion from a structure estimate, and vice versa, until convergence. Advantage: each step has a closed-form solution. This could be initialized by an 8-point algorithm reconstruction and further improve on it using the multi-view information.

Least-squares solution to find $\alpha_j$ from $R_i, T_i$: 

$$\alpha^j = - \frac{\sum_{i=2}^m (\widehat{x_i^j} T_i)^\top \widehat{x_i^j} R_i x_1^j}{\sum_{i=2}^m || \widehat{x_i^j} T_i || ^2}$$

Another interesting point: Estimating $Pi_i = (R_i, T_i)$ only requires two frames 1 and $j$, but estimating $\alpha$ requires all frames.

QUESTION: Don't we get $\alpha_j$ from $M_{p_j}$? (No... we need $R, T$ to get $M_{p_j}$).


### The Multi-View Matrix for Lines
Recall:  $\ell_i^\top \Pi_i X_0 = \ell_i^\top \Pi_i V = 0$ for the coimages $\ell_i$ of a line $L$ with base $X_0$, direction $V$; we constructed the multi-view matrix for lines:
$$W_l = [\ell_1^\top \Pi_1 ; \dots; \ell_m^\top \Pi_m] \in \mathbb{R}^{m \times 4}$$

The rank constraint is that $W_l$ should have rank at most 2, since $W_l X_0 = W_l V = 0$.  Goal: find more compact representaion; assume $\Pi_1 = (I, 0)$, i.e. first camera is in world coordinates.

Trick: multiply $W_l$ by 4x5 matrix $D_l$ s.t. last four columns of first row become zero, but keep rank the same.

![[matrices-WlDl-lines.png]]

Now since the first column must be lin. indep. because of the zeros in the first row, and the matrix has rank at most 1, the submatrix starting $(W_l D_l)[2:, 2:]$ must have rank 1. This submatrix is called the *multi-view matrix for lines*.

$$M_l = \begin{pmatrix}
 \ell_2^\top R_2 \widehat{\ell_1} & \ell_2^\top T_2 \\
 \vdots & \vdots \\
 \ell_m^\top R_m \widehat{\ell_1} & \ell_m^\top T_m
 \end{pmatrix} \in \mathbb{R}^{(m-1) \times 4}$$

The previous rank-2-constraint can be characterized by a rank-1-constraint on $M_l$: A meaningful preimage of $m$ observed lines can only exist if

$$\text{rank}(M_l) \leq 1.$$

In other words: all rows and all columns must be linearly dependent.


##### Trilinear Constraints for a Line (from the Rows)
Since rows of $M_l$ are lin. dep., we have for all $i, j$: $\ell_i^\top R_i \widehat{\ell_1} \sim \ell_j^\top R_j \widehat{\ell_1}$. This states that the three vectors $R_i^\top \ell_j$, $R_j^\top \ell_j$, $\ell_1$ are coplanar. So $R_i^\top \ell_i$ is orthogonal to the cross product of $R_j^\top \ell_j$ and $\ell_1$, which leads to:

$$\ell_i^\top R_i \widehat{\ell_1} R_j^\top \ell_j = 0$$

Note: this constraint only contains the rotations, not the translations! (Observing lines allows us to directly put constraints on the rotation alone.)

By the same rank-deficiency lemma from before, we get that the linear dependency of the i-th and j-th row is equivalent to

$$\ell_j^\top T_j \ell_i^\top R_i \widehat{\ell_1} - \ell_i^\top T_i \ell_j^\top R_j \widehat{\ell_1} = 0$$

This relates the first, i-th and j-th images.

Both trilinear constraints are equivalent to the rank constraint if $\ell_i^\top T_i \neq 0$.

##### Generality of three-line constraints
Any multiview constraint on lines can be reduced to constraints which involve only three lines at a time. (Argument via 2x2 minors of matrix: see slides)


### Characterization of Unique Preimages for Lines
**Lemma:** *Given three camera frames with distinct optical centers and $\ell_1, \ell_2, \ell_3 \in \mathbb{R}^3$ represent three images lines, then their preimage $L$ is uniquely determined if*
$$
 \ell_i^\top T_{ji} \ell_k^\top R_{ki} \widehat{\ell_i} - \ell_k^\top T_{ki} \ell_j^\top R_{ji} \widehat{\ell_i} = 0
 \quad \forall i, j, k = 1, 2, 3,
$$
*except for one degenerate case: The only degenerate case is that in which the preimages of all $\ell_i$ are the same plane.*

Note: this constraint combines the two previous trilinear constraints.

Equivalent formulation using the rank constraint:

**Theorem:** *Given $m$ vectors $\ell_i$ representing images of lines w.r.t. $m$ camera frames, they correspond to the same line in space if the rank of $M_l$ relative to any of the camera frames is 1. If its rank is 0 (i.e. $M_l=0$, the line is determined up to a plane on which then all the camera centers must lie.*


## Summary of Multi-View Chapter
|       | (Pre)image                   | coimage                   | Jointly                   |
|-------|------------------------------|---------------------------|---------------------------|
| Point | $\text{rank}(N_p) \leq m+3$  | $\text{rank}(W_p) \leq 3$ | $\text{rank}(M_p) \leq 1$ |
| Line  | $\text{rank}(N_l) \leq 2m+2$ | $\text{rank}(W_l) \leq 2$ | $\text{rank}(M_l) \leq 1$ |

The rank constraints guarantee the existence of unique preimages in non-degenerate cases. 