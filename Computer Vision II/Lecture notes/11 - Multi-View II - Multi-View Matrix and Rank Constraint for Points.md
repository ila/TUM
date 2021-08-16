### Modeling Multi-View with Point Features

Assume we have the world point $X$ represented by points $x_1, \dots, x_m$ in the $m$ images, with depths $\lambda_1, \dots, \lambda_m$. This is modeled by

$$\mathcal{I} \vec{\lambda} = \Pi X$$

or in more detail,

$$\mathcal{I} \vec{\lambda} = \begin{pmatrix} x_1 & & & \\ & x_2 & & \\ & & \ddots & \\ & & & x_m \end{pmatrix} \begin{pmatrix}\lambda_1 \\ \lambda_2 \\ \vdots \\ \lambda_m \end{pmatrix} = \begin{pmatrix} \Pi_1 \\ \Pi_2 \\ \vdots \\ \Pi_m \end{pmatrix} X = \Pi X$$

* $\vec{\lambda}$ is the *depth scale vector*
* $\Pi$ (3m x 4 matrix) is the *multiple-view projection matrix*, $\Pi_i = \Pi g_i$, and contains the i-th camera rotation as well as the projection
* $\mathcal{I}$ (3m x m matrix) is the *image matrix* and contains the "2d" (homogenous) coordinates of projections $x_i$

##### The Rank Constraint

Rewrite to obtain:

$$N_p u = 0, \qquad \text{ where } N_p := [\Pi, \mathcal{I}],  u :=[X; -\vec{\lambda}]$$

$N_p$ is a $3m \times (m+4)$ matrix. Since $u$ is in the null space of $N_p$, we get the **rank constraint**:

$$\text{rank}(N_p) \leq m+3$$

There exists a reconstruction iff this rank constraint holds. Compare with epipolar constraint: contraint is on matrix that only includes camera params and 2d coords, but **no 3d coords**.

##### Writing Rank Constraint more compactly
Define $\mathcal{I}^\bot$, where in $\mathcal{I}$, each $x_i$ is substituted by $\widehat{x_i}$ (a 3m x 3m matrix). It annihilates $\mathcal{I}$: $\mathcal{I}^\bot \mathcal{I} = 0$. By multiplying the above $\mathcal{I} \vec{\lambda} = \Pi X$ with it:

$$\mathcal{I}^\bot \Pi X = 0$$

There exists a reconstruction iff $W_p = \mathcal{I}^\bot \Pi$ does not have full rank:

$$\text{rank}(W_p) \leq 3.$$

Note: $W_p$ has the form

$$W_p = [\widehat{x_1} \Pi_1; \dots; \widehat{x_m} \Pi_m]$$

### Modeling Multi-View with Line Features
Intuition: from a line in two views only, we can't say anything about the camera motion, because any two line preimages intersect. We can get results from more views.

Saw already:  $\ell_i^\top \Pi_i X_0 = \ell_i^\top \Pi_i V = 0$ for the coimages $\ell_i$ of a line $L$ with base $X_0$, direction $V$.

Define matrix $W_l = [\ell_1^\top \Pi_1 ; \dots; \ell_m^\top \Pi_m]$ (m x 4 matrix). It maps both $X_0$ and $V$ to 0; these two vectors are linearly independent ($X_0[4] = 1, V[4] = 0$). We get a new rank constraint:

$$\text{rank}(W_l) \leq 2$$


### Rank Constraints: Geometric Interpretation

##### Points case
For points, we had $W_p X = 0$, $W_p = [\widehat{x_1} \Pi_1; \dots; \widehat{x_m} \Pi_m]$ (3m x 4 matrix). There are 2m lin. indep. rows, which can be interpreted as the normals of 2m planes, and $W_p X = 0$ expresses that $X$ is in the intersection of these planes. The $2m$ planes have a unique intersection iff $\text{rank}(W_p) = 3$.

![[fourPlanes-rank-constraint.png]]

##### Lines case
The constraint is only meaningful (i.e. actually a constraint) if $m > 2$. 


### Multiple-View Matrix of a Point
Goal: further compatify constraints.
We are in coordinate frame of first camera; i.e. $\Pi_1 = [I, 0], \Pi_2 = [R_2, T_2], \dots, \Pi_m = [R_m, T_m]$.

Define $D_p = \begin{pmatrix} \widehat{x_1} & x_1 & 0 \\ 0 & 0 & 1\end{pmatrix}$ (4 x 5 matrix, full rank). Multiply with $W_p$ (3m x 4 matrix) to get a 3m x 5 matrix, drop the first three rows and columns and call the submatrix $M_p$:

$$M_p = \begin{pmatrix}
	\widehat{x_2} R_2 x_1 & \widehat{x_2} T_2 \\
	\widehat{x_3} R_3 x_1 & \widehat{x_3} T_3 \\
	\vdots & \vdots \\
	\widehat{x_m} R_m x_1 & \widehat{x_m} T_m \\
 \end{pmatrix}$$
  
  $M_p$ is a 3(m-1) x 2 matrix. Now: $\text{rank}(W_p) \leq 3 \Leftrightarrow \text{rank}(M_p) \leq 1$, i.e. the two columns are linearly dependent (easy to check and work with!). Proof: 
  
  ![[matrix_WpDp.png]]
  
  $M_p$ is the **multiple-view matrix**. Summary: there exists a reconstruction iff the matrices $N_p, W_p, M_p$ satisfy:
  
  $$\text{rank}(M_p) = \text{rank}(W_p) - 2 = \text{rank}(N_p) - (m+2) \leq 1$$
  
  ##### Geometric interpretation of Multiple-View Matrix
  
  The rank constraint implies that the two columns of $M_p$ are linearly dependent. In fact, even $$\lambda_1 \widehat{x_i} R_i x_1 + \widehat{x_i} T_i = 0, i = 2, \dots, m:$$
  So the scaling factor is equal to the depth value $\lambda_1$.
  
  (Proof: from the projection equation we know $\lambda_i x_i = \lambda_1 R_i x_1 + T_i$, hence $\lambda_1 \widehat{x_i} R_i x_1 + \widehat{x_i} T_i = 0$.)
  
  ##### $M_p$ => Epipolar (bilinear) constraints
  Goal: if we consider only a pair of images, the epipolar constraint should emerge from $M_p$.
  
  Proof - *linear dependence of $\widehat{x_i} R_i x_1$ and $\widehat{x_i} T_i$ implies epipolar constraint $x_i^\top \hat{T}_i R_i x_1 = 0$*:
  
$\widehat{x_i} T_i$ and $\widehat{x_i} R_i x_1$ are each normals to planes spanned by $x_i, T_i$ and $x_i, R_ix_1$, respectively. Linear dependence of these normals implies: => $x_i, T_i, R_i x_1$ live in the same plane (*coplanar*). Therefore $x_i^\top \hat{T}_i R_i x_1 = x_i^\top \hat{T}_i R_i x_1 = 0$.

#####  $M_p$ <=> Trilinear constraints
**Theorem**: *a matrix $M = [a_1 b_1; \dots; a_n b_n]$ with $a_i, b_i \in \mathbb{R}^3$ is rank-deficient <=> $a_i b_j^\top - b_i a_j^\top = 0$ for all $i, j$.*

Applied to $M_p$, this yields the *trilinear constraints*:

$$\widehat{x_i} (T_i x_1^\top R_j^\top - R_i x_1 T_j^\top) \widehat{x_j} = 0, ~\forall i, j \in [n] \qquad \text{(trilinear constraints)}$$

Different than the epipolar constraints, the trilinear constraints actually characterize the rank constraint on $M_p$. Each constraint couples *three* images: one can show that constraints on pairs of images cannot capture all the information from $m$ images, but these trilinear constraints can.

Note: we can also obtain the epipolar constraints directly from the trilinear constraints in non-degenerate cases.

Question: what does the "3 x 3 = 9 scalar trilinear equations" part mean?

##### Uniqueness of the Preimage
This slide was skipped ("a little bit to technical")

##### Degenerate cases
If $\widehat{x_j} T_j = \widehat{x_j} R_j x_1 = 0$ for some view $j$, then the epipolar constraints cannot be obtained from the trilinear constraints; also the equivalence "trilinear constraints <=> rank constraint" does not hold in degenerate cases.
1. If between three images, each pair of epipolar constraints is fulfilled, they determine a unique preimage $p$ - except if all three lines $o_i x_i$ between optical center and image point lie in the same plane.
![[degeneracies-epipolar.png]]

2. If between three images, all three trilinear constraints hold (3 out of 9 are different considering symmetry), the determine a unique preimage $p$ - except if the three lines $o_i x_i$ are collinear.


In the example where all optical centers lie on a line, going from bilinear to trilinear constraints solves the problem.

##### Summary: Rank of $M_p$
* $M_p$ has rank 2 => no point correspondence; empty preimage.
* $M_p$ has rank 1 => point correspondence + *unique* preimage
* $M_p$ has rank 0 => point correspondence, but non-unique preimage



(Rest of the lecture on Multi-View Reconstruction: see notes for lecture 12a)