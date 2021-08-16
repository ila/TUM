## Reconstructing 3D Structure from R, T

### Structure Reconstruction
After recovering R, T, we have for j-th point $X^j$:
$$\lambda_2^j x_2^j = \lambda_1^j R x_1^j + \gamma T$$
Scale params $\lambda_{1,2}^j, \gamma$ are unknown.

Eliminating $\lambda_2^j$ with $\widehat{x_2^j}$:
$$\lambda_1^j \widehat{x_2^j} R x_1^j + \gamma \widehat{x_2^j} T = 0$$

Leads to linear system with $n+1$ variables: $M \vec{\lambda} \approx 0$, $\vec{\lambda} = (\lambda_1^1, ..., \lambda_1^n, \gamma)^T \in \mathbb{R}^{n+1}$

Solve this via least-squares: solve $\min\limits_{||\vec{\lambda}||=1} ||M \vec{\lambda}||^2$. A.k.a find the eigenvector to the smallest eigenvalue.

### Issues with Noisy Inputs
The 8-point algorithm is not robust against noise: small perturbations can lead to large errors in the reconstruction.

Underlying reason: small changes can change eigenvalue structure.

### More robust approaches

##### Bayesian approach
Maximum aposteriori estimate:
$$\arg\max\limits_{x, R, T} P(x, R, T | \tilde{x}) = \arg\max\limits_{x, R, T} P(\tilde{x} | x, R, T) P(x, R, T)$$

Problem: hard to define distributions on $SO(3) \times \mathbb{S}^2$.

##### Constrained optimization
Minimize cost function:
$$\phi(x, R, T) = \sum_{j=1}^n \sum_{i=1}^1 ||\tilde{x}_i^j - x_i^j||^2$$
subject to constraints: $x_2^i{}^\top \hat{T} R x^j_1 = 0$, $x_1^i{}^\top e_3 = 1$, $x_2^j{}^\top e_3 = 1$ 

I.e. find points as close to the estimations as possible that fulfil the epipolar constraints.

##### Bundle Adjustment
An "unconstrained" version, which has the unknown depth params $\lambda_i$,:
$$\sum_{j=1}^n ||\tilde{x}^j_1 - \pi_1(X^j)||^2 + ||\tilde{x}^j_2 - \pi_2(X^j)||^2$$
(not really unconstrained, if $R$ has to be in $SO(3)$; could be made unconstrained via Lie algebra coordinates and applying $\exp$).

But this is essentially just a different parametrization of the above, and can also be expressed by a cost function.

![[Pasted-image-20210529173123.png]]

### Degenerate Configurations
i.e. ones where the 8-point alg. provides no unique solution: Here all 8 points lie on a so-called "critical" 2D surface. Can be described by quadratic equations => *quadratic surfaces*. Mostly pathological, except for one case: all points lie on a 2D plane.

Non-degenerate configurations are called *general configurations*.

### Four-Point Algorithm
To handle planes, we use this algorithm instead.

##### Planar Homographies
For a point $X$ on the plane with normal $N \in \mathbb{S}^2$, we have $N^\top X = d$ (d = distance of plane from the origin). Assume this holds for $X_1$ from frame 1; then for $X_2$ from frame 2:
$$X_2 = RX_1 + T = (R + \tfrac{1}{d} T N^\top) X_1 =: H X_1,$$
where $H \in \mathbb{R}^3$ is called *homography matrix*.

With 2D coords $x_1, x_2$: $\lambda x_2 = H \lambda_1 x_1$ (called planar homography). Multiplying with $\widehat{x_2}$ yields:

$$\widehat{x_2} H x_1 = 0 \quad \text{(planar epipolar (or homography) constraint)}$$

With $a := x_1 \otimes \widehat{x_2} \in \mathbb{R}^{9 \times 3}$, we get the equation
$$a^T H^s = 0$$

##### Four-Point Algorithm Formulation
1. Calculate $\chi = (a^1, \dots, a^n)^\top \in \mathbb{R}^{3n \times 9}$
2. Compute solution $H^s$ for equation $\chi H^s = 0$ via SVD
3. From $H = R + \tfrac{1}{d} T N^\top$, extract the motion parameters (more difficult!)
	1. we can obtain: $R$, $N$, and $T/d$. So again: scale ambiguity.

##### Relationships between $H$ and $E$
Numerous relations, in particular $E = \hat{T}H$ and $H^\top E + E^\top H = 0$.