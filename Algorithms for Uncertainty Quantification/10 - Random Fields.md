# 10 Random Fields
### Random Variables vs. Random fields
Random field = continuum of random variables (e.g. one for each point in time). The random variables in the random field are usually not independent.

**Definition:**
An $X$-valued *random field* is a collection of random variables $\{X_t \mid t \in \mathcal{T}\}$, indexed by a topological space $\mathcal{T}$.

*Stochastic processes* are random fields where $\mathcal{T}$ represents time.

### Covariance functions
The covariance function measures covariance between different indices of the random field:
$$C(t, s) := \text{Cov}(X_t, X_s)$$

Recall that $\text{Cov}(X, Y) = E(XY) - E(X)E(Y)$, and the covariance *matrix* is symmetric and PSD.

We can define alternative symmetric + PSD covariance functions. Note the similarity to kernel functions. Examples:
- Exponential covariance: $C(t, s) := \exp(-d(X_t, X_s)/V)$
- Squared exponential covariance: $C(t, s) := \exp(-d^2(X_t, X_s)/(2V^2))$
Here $V$ is a constant (the *length scale*).

QUESTION: what does the comment "shows how long RVs are correlated" mean?

### Brownian Motion
Physical scenario: particles in a fluid undergo constant, small random fluctuations due to atomic collisions.

*Wiener process*: mathematical formalization of Brownian motion. Formally: stochastic process $\{W_t \mid t \geq 0 \}$ s.t.
- $W_0$ = 0
- $W_t$ continuous in $t$
- increments $W_1-W_0, W_2-W_1$ etc. are stochastically independent
- $W_{t+u} - W_t ~ \mathcal{N}(0, u)$
- $C(t, s) = \min(t, s)$

### Approximating random fields
random fields are infinite-dimensional => need approximation!

Approximate with finitely many dimensions s.t. result is accurate + cheap to compute. Methods:
- direct sampling based on Cholesky decomposition
- circulant embedding
- Karhunen-Loève expansion

Remark: approximation tailored to specific cases, not generic.

### Karhunen-Loève expansion
Assume $Y_t(\omega)$ is a random field, and the means $m(Y_t(\omega))$ and the covariances $C(t, s)$ are available. We can write the random field as infinite sum as follows, called the KL expansion:

$$Y_t(\omega) = m(Y_t(\omega)) + \sum_{n=1}^\infty \sqrt{\lambda_n} \psi_n(t) \zeta_n(\omega)$$

Here $\lambda_n, \psi_n(t)$ are the eigenpairs of $C(t, s)$. The $\zeta_n(\omega)$ are uncorrelated random variables. To get an approximation, we can truncate the sum

QUESTION: what is $s$?

- designed for Gaussian processes (but not restricted to)
- bi-orthogonal, since the $\psi_n$ are orthogonal, and the $\zeta_n$ are as well.
- KL expansion is optimal wrt. mean-squared error
- for a Gaussian process, $\zeta_n$ are iid. Gaussian RVs.

##### Finding Eigenpairs: Spectral representation of $C(t, s)$
Assume $C(t, s)$ is continuous: Mercer's theorem yields
$$C(t, s) = \sum_{n \geq 1} \lambda_n \psi_n(t) \psi_n(s)$$
To find the $\lambda_n, \psi_n(t)$: solve *2nd kind Fredhold integral equation*
$$
\int_{\mathcal{T}} C(t, s) \psi_n(t) \,dt = \lambda_n \psi_n(s)
$$

Often no analytical solution => solve numerically.

##### Numerically solve Fredhold integral equation
We need to solve the eigenvalue problem, i.e. solve the Fredhold integral equation.
- quadrature-based approaches (e.g. *Nyström method*)
- expansion-based approaches
	- approximate eigenfunctions as linear combinations of basis functions
	- minimize error using e.g. the Galerkin method

##### Analytical solution of Wiener process, C = min
Assume a Wiener process, $t \in [0, 1]$, $C(t, s) = \min(t, s)$.

We need to solve $\int_0^1 \min(t, s) \psi_n(t) \, dt = \lambda_n \psi_n(s)$. Analytical solution:

$$\lambda_n = \frac{1}{(n + 0.5)^2 \pi^2}$$
$$\psi_n(t) = \sqrt{2} \sin((n + 0.5)\pi t)$$
$$\zeta_n \sim \mathcal{N}(0, 1)$$
$$W_t = m(W_t(\theta)) + \sum_{n=1}^\infty \sqrt{\lambda_n} \psi_n(t) \zeta_n(\theta)
= \sqrt{2} \sum_{n = 1}^\infty \frac{\sin((n + 0.5)\pi t)}{(n + 0.5)\pi} \zeta_n$$


##### Pros and Cons of KL expansion
**Pro**:
- separate stochastic/deterministic part (like PCE)
- bi-orthogonal; in particular, based on uncorrelated RVs, i.e. the approximation is a decorrelated process
- optimal approximation wrt. MSE

**Con**:
- assumes Gaussian underlying process
- number of expansion terms = number of stochastic dimensions: So high accuracy corresponds to high dimensionality
- for most covariance functions: no analytical solution
- eigenpair computation (can be) expensive


### Literature
R. Ghanem, P. Spanos. *Stochastic Finite Elements: A Spectral Approach* (Springer 1991).