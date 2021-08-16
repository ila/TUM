## Some Concrete Direct SLAM Methods

### Stühmer, Gumhold, Cremers, DAGM 2010: Real-Time Dense Geometry from Handheld Camera
Suppose we have $n$ images, $g_i \in SE(3)$ is the i-th image rigid body motion, $I_i: \Omega \to \mathbb{R}$ the i-th image. Goal: compute depth map $h: \Omega \to \mathbb{R}$ that maps each pixel to its depth.

Solve optimization problem:
$$\min_h \sum_{i=2}^{n} \int_\Omega |I_1(x) - I_i(\pi g_i(h(x) x)) | \,dx + \lambda \int_\Omega |\nabla h(x)| \,dx$$

- For each image $i$ and each point $x$ in image 1, the intensity of $x$ in image 1 should be close to the intensity of the corresponding point in image i
- The total variation of depths should be small (depths should not wildly fluctuate) - achieved via the regularization term

Actual minimization achieved with similar strategies like in optical flow estimation (above integral looks similar) - not detailed here. Roughly:
- linearize above terms in h (via Taylor expansions) - only holds for small h
- coarse-to-fine linearization

**Comment:** the total variation regularization adds in a "soap film" effect, where unknown/unseen parts are filled in/"made up" for the reconstruction.

![[plant-reconstruction.png]]

### Steinbrücker, Sturm & Cremers (2011): Camera motion of RGB-D Camera
Use a similar cost function as [[12d - Examples of Direct SLAM papers from 2011 - 2014#Stühmer Gumhold Cremers DAGM 2010 Real-Time Dense Geometry from Handheld Camera|before]] (without the regularization and with L2 instead of L1), but assume the depth is known and optimize over the rigid body motion $\xi \in se(3)$.

$$E(\xi) = \int_\Omega \big(I_1(x) - I_2(\pi g_{\xi}(h(x) x))\big)^2 \,dx$$

Linearize this by using the Taylor expansion around some initial $\xi_0$:

$$E(\xi) \approx \int_\Omega \big(I_1(x) - I_2(\pi g_{\xi_0}(h(x) x)) - \nabla I_2^\top (\tfrac{d\pi}{d g_\xi})(\tfrac{dg_\xi}{d\xi}) \xi \big)^2 \,dx$$

=> Convex quadratic cost function, linear optimality condition $\tfrac{dE(\xi)}{d\xi} = A\xi + b = 0$ (Linear system with 6 equations).

The linearization is identical to Gauss-Newton (approximation of a Hessian by a PSD matrix).

**Comment:** The Taylor approximation only works well for small camera movements (which you typically have in practice). For too large movements, the baseline generalized iterated closest points (GICP) works better.

![[rgb-d-tracking.png]]


##### Kerl, Sturm, Cremers (IROS 2013): Extension
Additionally to color consistency, also ask for depth value consistency. Assume the vector $r_i = (r_{ci}, r_{zi})$ of color and geometric discrepancy for the i-th pixel follows a bivariate t-distribution, the max-likelihood pose estimate depending on the parameter $\nu$ from the t-distribution is

$$\min_xi \sum_i w_i r_i^\top \Sigma^{-1} r_i, ~
w_i = \frac{\nu + 1}{\nu + r_i^\top \Sigma^{-1} r_i}$$

=> non-linear weighted least squares problem

(t-distribution in a sense interpolates between a uniform and a Gaussian distribution)


##### Loop Closure/Global Consistency
What to do about errors that accumulate when walking around?
1. either bundle adjustment which produces globally consistent solution (expensive though, not online)
2. Loop closure: estimate a lot of camera motions $\hat{\xi}_{ij}$ for image pairs $(i, j)$. Then estimate a globally consistent trajectory $\{\xi_i\}_{i}$ that minimizes: $$\min_\xi \sum_{i,j} (\hat{\xi}_{ij} - \xi_i \circ \xi_j^{-1})^\top \Sigma_{ij}^{-1} (\hat{\xi}_{ij} - \xi_i \circ \xi_j^{-1})$$ Here $\Sigma_{ij}$ denotes the uncertainty of measurement $(i, j)$. This minimization can be solved by [[12c - Optimization Algorithms, Direct Visual SLAM#Levenberg-Marquardt Algorithm|Levenberg-Marquardt]]

![[loop-closure.png]]

In the loop closure approach, we don't store all camera positions for efficiency, but a lot of *keyframes*.


### Newcombe, Lovegrove & Davison (ICCV 2011): Dense Tracking and Mapping - *DTAM*
Combines the previous tasks of (a) reconstructing dense geometry, and (b) reconstructing camera motion (and actually one of the first papers to do this).

Chicken-egg-problem! (Since here, RGB instead of RGB-D camera)

##### Depth estimation
Depth estimation very similar to [[#Stühmer Gumhold Cremers DAGM 2010 Real-Time Dense Geometry from Handheld Camera|Stühmer et al.]], but regularization over total variation of inverse depth $u=1/h$ instead of depth $h$. This alleviates a bias such that farther-away structures which have less pixels don't have a difference surface smoothing strength.

The regularization terms is also made *adaptive* by weighting the integrand by $\rho(x) = \exp(-|\nabla I_\sigma(x)|^\alpha)$: This is small if a big gradient exists at $x$, which means that big changes are penalized less at points in the image where the color changes by a lot. The final regularization term is 

$$\lambda \int_\Omega \rho(x) |\nabla u| \,dx$$

This can, but needn't be a good idea (e.g. reduced smoothing on a zebra).

##### Camera tracking
Similar to [[#Steinbrücker Sturm Cremers 2011 Camera motion of RGB-D Camera|Steinbrücker et al.]] (not detailed here).

Then: find good initialization (initial estimate for camera motion), alternate

![[DTAM-car.png|300]]

### Engel, Sturm, Cremers (ICCV 2013): LSD-SLAM (Large-Scale Direct Monocular SLAM)
- not dense, but semi-dense: reconstruct everywhere where there is a usable gradient (~50% of points in practice)
- additional uncertainty propagation, similar to Kalman filter
- additional scale parameter in camera motions: Use Lie group of 3D similarity transformations $$\textit{Sim}(3) = \bigg\{ \begin{pmatrix} sR & T \\ 0 & 1 \end{pmatrix} \mid R \in SO(3), T \in \mathbb{R}^3, s \geq 0 \bigg\}$$

Minimize non-linear least squares problem
$$\min_{\xi \in \textit{sim}(3)} \sum_i w_i r_i^2(\xi)$$
Here $r_i$ is a color residuum, $w_i$ a weighting, similar to [[#Kerl Sturm Cremers IROS 2013 Extension|Kerl et al.]]; this is minimized by a weighted Gauss-Newton algorithm on $\textit{Sim}(3)$

![[LSD-SLAM.png]]