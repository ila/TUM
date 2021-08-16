# Chapter 7 - Bundle Adjustment & Non-Linear Optimization

## Reconstruction under Noise
Linear approaches are usually prone to noise. We now assume that $\tilde{x}_1, \tilde{x}_2$ are noisy data points. Goal:
* find $R, T$ *as close as possible* to the truth
* such that we get a *consistent reconstruction*

### Bayesian Formulation
(seen before [[09 - Two views II - Structure Reconstruction, Robustness, 4-Point Algorithm#Bayesian approach|here]])

Maximum aposteriori estimate: involves modeling probability distributions on $SO(3) \times \mathbb{S}^2$. Instead just assume a uniform prior (=> maximum likelihood estimate).

### Bundle Adjustment
Assume zero-mean Gaussian noise => MLE leads to *Bundle adjustment*: Minimize *reprojection error*

$$E(R, T, X_1, \dots, X_n) = \sum_j^N |\tilde{x}^j_1 - \pi(X_j)|^2 + |\tilde{x}^j_2 - \pi(R, T, X_j)|^2$$

(two-image case). $\pi(R, T, X_j)$ denotes the projection $\pi(R X_j + T)$.

Generalization to $m$ images:

$$E\big(\{R_i, T_i\}_{i \in [m]}, \{X_j\}_{j \in [N]}\big) = 
 \sum_{i=1}^m \sum_{j=1}^N \theta_{ij} |\tilde{x}_i^j - \pi(R_i, T_i, X_j)|^2$$

Here $\theta_{ij}$ is 1 if point $j$ is visible in image $i$,  0 otherwise. Also $T_1 = 0, R_1 = I$.
These error functions are non-convex.

##### Reparametrizations
- represent $X_j$ as $\lambda_1^j x_1^j$, and $\pi(X_j)$ in first image as $x_1^j$
- constrained optimization, minimize cost function $E(\{x_i^j\}_j, R, T) = \sum_j^N \sum_i^2 ||x_i^j - \tilde{x}_i^j||^2$, subject to consistent geometry constraints: $x_2^j{}^\top \widehat{T} R x_1^j = 0, x_1^j{}^\top e_3 = 1, x_2^j{}^\top e_3 = 1, j \in [N]$.
	- $R$ and $T$ do not appear in $E$, only in the constraints!

##### Constrained vs. Unconstrained

Note: even the "unconstrained" versions are in a way constrained, since $R \in SO(3)$ (and usually $||T||=1$). But $R$ can be expressed via the Lie algebra: $R = \exp(\hat{\omega})$, where $\hat{\omega} \in so(3)$ is unconstrained.

##### Noise models
Quadratic cost functions stem from the Gaussian noise model. Assuming e.g. Poisson noise $P(x) \sim e^{-|x|/\lambda}$ leads to norm terms in the sum without square instead.

##### More comments on Bundle Adjustment
- "bundles" refers to bundles of light rays
- approach was first used in the 1950s in photogrammetry
- typically last step in reconstruction pipeline: First construct an initial solution (e.g. spectral methods), then apply bundle adjustment


## Nonlinear Optimization
The cost function from [[#Bundle Adjustment]] is called a *non-linear least square* cost function, because the "modeled 2d point" function $\pi(R_i, T_i, X_j)$ is non-linear.

Iterative algorithms tend to work well if the function is "not too far from linear". If the scene is somewhat far away, this increasingly tends to be the case. Iterative algorithms for nonlinear optimization are called *non-linear programming*.

### Gradient Descent
First-order method, compute local minimum by stepping in the direction of steepest decrease iteratively ("energy decreases the mose" = error function decreases the most).

Mathematical Setup: $E: \mathbb{R}^n \to \mathbb{R}$ is the cost function. The *gradient flow* for $E$ is the differential equation
$$
 \begin{cases}
x(0) = x_0, \\
 \frac{dx}{dt} = -\frac{dE}{dx}(x)
 \end{cases}$$

Then the *gradient descent* is simply the (Euler) discretization of this equation:

$$x_{k+1} = x_k - \epsilon \frac{dE}{dx}(x_k), \quad k=0, 1, 2, \dots$$

##### Comments on Gradient Descent 
- very broadly applicable, but more specialized algorithms have better asymptotic convergence rates
	- optimal convergence rates: e.g. Nesterov Momentum (Yurii Nesterov)
- many iterations for anisotropic cost functions
- More specialized techniques:
	- conjugate gradient method
	- Newton methods
	- BFGS method


### Least Squares and its Variants
Motivation of this section: clear up terminology.

*Linear* or *Ordinary Least Squares* is a method for estimating parameters $x$ in a linear regression model under zero-mean isotropic Gaussian noise:

$$a_i = b_i^\top x + \eta_i$$

where $b_i \in \mathbb{R}^d$ is the input vector, $a_i \in \mathbb{R}$ the scalar response, $\eta_i ~ N(0, \sigma^2 I)$. Ordinary least squares problem:

$$\min_x \sum_i (a_i - x^\top b_i)^2 = \min_x(a - Bx)^\top(a - Bx)$$

Historical note: Gauss invented the normal distribution when asking for which noise distribution the optimal estimator was the arithmetic mean.

##### Weighted least squares
Assume Gaussian noise with a diagonal $\Sigma$: This is called *weighted least squares*, and we minimize $\sum_i w_i (a_i - x^\top b_i)^2$, $w_i = 1/\sigma_i^2$.

The cost function from [[#Bundle Adjustment]] corresponds to weighted least squares because of the weights $\theta_{ij}$.

##### Generalized least squares
Assume general mean-centered Gaussian noise $N(0, \Sigma)$: this gives the *generalized least squares* problem

$$\min_x (a-Bx)^\top \Sigma^{-1} (a-Bx)$$

(i.e. minimize the Mahalanobis distance between $a$ and $Bx$). Closed-form solution:

$$\hat{x} = (B^\top \Sigma^{-1} B)^{-1} B^\top \Sigma^{-1} a$$

##### Least Squares with unknown $\Sigma$
There are iterative estimation algorithms: *feasible generalized least squares*, *iteratively reweighted least squares*. Watch out: this problem is non-convex! We usually only converge to local minima.

##### Iteratively Reweighted Least Squares
Assume there is a known weighting function $w_i(x)$ and a model $f_i(x)$ which replaces $b$. Then solve the minimization problem:

$$\min_x \sum_i w_i(x) |a_i - f_i(x)|^2$$

To solve it, iteratively solve 

$$x_{t+1} = \arg\min_x \sum_i w_i(x_t) |a_i - f_i(x)|^2$$

If $f_i$ is linear, i.e. $f_i(x) = x^\top b_i$, each subproblem is just a weighted least-squares problem with a closed-form solution.

##### Non-Linear Least Squares

Goal: fit observations $(a_i, b_i)$ with a non-linear model $a_i \approx f(b_i, x)$, and minimize $\min_x \sum_i r_i(x)^2$, $r_i(x) = a_i - f(b_i, x)$.
This is just the same problem as in [[#Iteratively Reweighted Least Squares]].

Optimality condition: 
$$\sum_i r_i \frac{\partial r_i}{\partial x_j} = 0, \quad j \in [d]$$

Solve this approximately via iterative algorithms, such as *Newton methods*, *Gauss-Newton*, or *Levenberg-Marquardt*.

