# Chapter 5 - Reconstruction from Two Views

### Assumptions
- two consecutive frames
- given: set of corresponding points
- static scene (no movements during camera motion)
- intrinsic camera parameters are known + fixed
	- e.g. focal lengths, radial distortion, ...

### Problem formulation
![[Pasted-image-20210528131527.png]]
two problems: estimate 3d coordinates of known points; estimate camera motion. Chicken-egg problem!
Approach:
1. estimate camera motion
2. reconstruct 3d coordinates (by *triangulation*)

### Epipolar Geometry
$o_1, o_2$: Camera centers
$P$ or $X$: 3d point that we know a correspondence of
$x_1, x_2$: projections of $P$ on the two image planes
$e_1, e_2$: *epipoles* (intersections of line $(o_1, o_2)$ with image planes)
$l_1, l_2$: *epipolar lines*: line between $x_i$ and $e_i$.

Also: *epipolar plane* associated with the 3d point $X$ (?)

One property: line $(o_2, x_1)$ passes through $l_2$.

### Parametric formulation / projection error cost function
Minimize *projection error* $E(R, T, X_1, \dots, X_N)$
with 6 params for camera motion $R, T$ and $3N$ (say 300) params for the 3d coordinates.
$$E(R, T, X_1, \dots, X_N) = \sum_j ||x_1^j - \pi(X_j)||^2 + ||x_2^j - \pi(R, T, X_j)||^2$$
(here we are in the 1-st camera coordinate system and rotate to get to the 2-nd camera coordinate system)

Hard to solve! Either
1. stick with this cost function and use some advanced optimization techniques
2. re-formulate problem, get rid of 3d coordinates, effectively a different cost function => epipolar constraint, 8-point alg.

### Epipolar Constraint
Projection $X \mapsto x_1$ is simply a projection with unknown depth $\lambda_1$: $\lambda_1 x_1 = X$.
For the second camera, we get $\lambda_2 x_2 = RX + T$.

Next: $\lambda_2 x_2 = R(\lambda_1 x_1) + T$
Multiplying with $\hat{T}$ (removes $T$) gives $\lambda_2 \hat{T} x_2 = \lambda_1 \hat{T} R x_1$.
The LHS is orthogonal to $x_2$: So projecting onto $x_2$, i.e. scalar product with $x_2$, yields

$$x_2^\top \hat{T} R x_1 = 0. \qquad\text{(Epipolar Constraint)}$$

Goal now: given enough epipolar constraints - one for each corresponding point pair - we'll get an estimation for $R$ and $T$?
$E = \hat{T} R$ is called the *essential matrix*.

##### Geometric interpretation
the constraint states that the vectors $o_1X$, $o_2o_1$, $o_2X$ form a plane; i.e. $o_1X$, $o_2X$ intersect (obvious if phrased like this, but the constraint expresses it in terms of $x_1, x_2, R, T$)

In 2nd-frame coords, $Rx_1 \sim o_1 X$, $T \sim o_2o_1$, $x_2 ~ o_2X$. Then volume = $x_2^\top (T \times R x_1) = 0$.

##### Properties of E
Essential space: $\mathcal{E} = \{ \hat{T} R | R \in SO(3), T \in \mathbb{R}^3 \}$.

**Theorem:** $0 \neq E \in \mathcal{E}$ iff SVD of E = $U \Sigma V^\top$, $\Sigma = \text{diag}(\sigma, \sigma, 0)$, $\sigma > 0$, $U, V \in SO(3)$.

**Theorem:** (Pose recovery from E)
There are 2 relative poses $(R, T)$ corresponding to some $E \in \mathcal{E}$:
$$(\hat{T}_{1,2}, R_{1,2}) = \bigg(U R_Z \big(\pm \tfrac{\pi}{2}\big) \Sigma U^\top, U R_Z\big(\pm \tfrac{\pi}{2}\big)^\top V^\top\bigg)$$

Only one solution is sensible (has positive depth)

### Basic Reconstruction Algorithm (Essential Matrix)
1. Recover essential matrix $E$ from epipolar constraints
2. Extract translation/rotation from $E$

$E$ recovered from several constraints will not be an essential matrix in general. We can either
- project the matrix we recover to $\mathcal{E}$ or
- optimize epipolar constraints in the essential space (more accurate, but more involved - nonlinear constrained opt.)

Using the first approach, this leads to:

### The 8-point algorithm
Write epipolar constraints as scalar product:
$$x_2^\top E x_1 = a^\top E^s = 0$$
where $a = x_1 \otimes x_2$
and for $n$ point pairs:
$$\chi E^s = 0, \chi = (a^1, \dots, a^n)^\top$$

So the flattened matrix $E$ lives in $\ker \chi$. For a unique solution (up to a scaling factor), we need $\text{rank}(\chi) = 8$.  => exactly 8 point pairs.

The scaling factor can never be figured out without additional information ("scale freedom" of images). Possible: just fix translation to be 1.

Important: a degenerate case is if all points lie on a line or on a plane.

Another non-uniqueness: if E is a solution, so is -E; each E produces two solutions.

##### Projection on Essential Space $\mathcal{E}$
Starting with arbitrary $F = U \text{diag}(\lambda_1, \lambda_2, \lambda_3) V^\top$, $\lambda_i$ descending, the essential matrix $E$ with minimum Frobenius distance from $F$ is
$$E = U \text{~diag}(\tfrac{\lambda_1 + \lambda_2}{2}, \tfrac{\lambda_1 + \lambda_2}{2}, 0) V^\top.$$
Even simpler: singular values (1, 1, 0), since scale doesn't matter.

##### 8-Point Algorithm Formulation
1. Approximate $E$
	1. Compute $\chi$
	2. Find $E^s$ that minimizes $||\chi E^s||$: This is the ninth column of $V_\chi$ in the SVD $\chi = U_\chi \Sigma_\chi V_\chi^\top$.
	3. Unstack $E^s$ into $E$.
2. Project $E$ onto $\mathcal{E}$
	1. Compute SVD of $E$
	2. Replace singular values with (1, 1, 0) (projection onto normalized essential space - this is ok since $E$ is only defined up to a scalar)
3. Recover R, T from essential matrix
	1. $R = U R_Z^\top(\pm \tfrac{\pi}{2}) V^\top$
	2. $\hat{T} = U R_Z(\pm \tfrac{\pi}{2}) \Sigma U^\top$
where $R_Z^\top(\pm \tfrac{\pi}{2}) = \pmatrix{& \pm 1 &  \\ \mp 1 & & \\ & & 1}$ (watch out: transposed!)

### We actually only need 5 points (Kruppa 1913)
$E$ is actually a five-dimensional space.
Example: with 7 points, $\ker \chi$ is (at least) 2-dim, spanned by $E_1, E_2$. Solve for $E$: find $E = E_1 + \alpha E_2$ s.t. $\det(E) = 0$ ($\det E = 0$ is one of the algebraic properties of $E$).

Kruppa 1913 proved: we only need five points to recover $(R, T)$; even less for degenerate (e.g. planar/circular) motion.


### Selecting solutions
Only one of the four solutions gives positive depth values to all points.

### Limitations / Extensions
If there is no translation, only rotation, $E = 0$. Then nothing can be recovered! Does not happen usually due to noise.

For "infinitesimal" view point changes: *continuous epipolar constraint*. Recovers linear and angular camera velocity instead of $(R, T)$.

Moving objects (independently): $(x_2^\top E_1 x_1) (x_2^T E_2 x_1) = 0$ for two essential matrices $E_1, E_2$ ("each part is either on the car or on the background").
- can be solved with enough points ("polynomial factorization techniques")