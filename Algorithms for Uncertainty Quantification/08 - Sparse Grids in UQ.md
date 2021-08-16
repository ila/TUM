# 08 Sparse Grids in UQ

**Motivation:** We usually have more than one uncertain parameter. gPCE needs quadrature, but quadrature in high-dimensional grids is expensive => sparse grids! 

### Recall: Multi-dim. gPCE
(also see [[06 - Polynomial Chaos Expansion, Pseudo-spectral Approach#Multivariate case|here]])

- multi-indices $\mathbf{n}, \mathbf{k} \in \mathbb{N}_0^d$
- multivariate base polynomials: $\phi_{\mathbf{n}}(\mathbf{\omega}) = \prod \phi_{n_i}(\omega_i)$
- multivariate PCE: $f(t, \mathbf{\omega}) = \sum_{\mathbf{n}} \hat{f}_{\mathbf{n}}(t) \phi_{\mathbf{n}}(\mathbf{\omega})$
- obtain coefficients via multi-variate spectral approach: $\hat{f}_{\mathbf{n}}(t) \approx \sum_{\mathbf{k}} f(t, \mathbf{x}_{\mathbf{k}}) \phi_{\mathbf{n}}(\mathbf{x}_\mathbf{k}) w_\mathbf{k}$
	- $\mathbf{x}_\mathbf{k}$,  $w_\mathbf{k}$ are the quadrature nodes and weights

To make things simpler, I don't write multivariate variables in bold face from now.

##### Multi-Indices in sums
In the sum for $f(t, \mathbf{\omega})$ (PCE expansion), we usually sum over all $n$ s.t. $0 \leq ||n||_1 \leq N$ => $P = \binom{d+N}{d}$ summation terms

In the sum for $\hat{f}_n(t)$ (pseudo-spectral quadrature), we usually sum over all $k$ s.t. $0 \leq ||k||_\infty \leq K-1$: In other words, we have a grid of $x_k$ points and take all of them into account.

The number of points therefore grows exponentially in $d$: $M = K^d$.

### Sparse Grids
Dilemma: We could explore the dimensions individually to defeat the curse of dimensionality - but if we explore each dimension separately, we lose the coupling effects!

Idea: weaken assumed coupling, find sparser grid.

##### Core Idea with continuous linear operators $\mathcal{U}$
Assume $\mathcal{U}^{(i)}, i \in [d]$, are continuous linear operators (e.g. integration, interpolation,...). $d$-dimensional version: $\mathcal{U}^{(\mathbf{d})} = \mathcal{U}^{(1)} \otimes \dots \otimes \mathcal{U}^{(d)}$.

* numerical approximation of operators: $\mathcal{U}_k^{(i)} \approx \mathcal{U}^{(i)}$, s.t. $||\mathcal{U}_k^{(i)} - \mathcal{U}^{(i)}|| \to 0$ as $k \to \infty$.

=> telescoping sum setup along $k$: write e.g.
$$
\mathcal{U}^{(i)}_2 = \mathcal{U}^{(i)}_0 + \big(\mathcal{U}^{(i)}_1 - \mathcal{U}^{(i)}_0\big) + \big(\mathcal{U}^{(i)}_2 - \mathcal{U}^{(i)}_1\big)
$$
or in general, as a series:
$$
\mathcal{U}^{(i)} = \sum_{k=0}^\infty \big(\mathcal{U}^{(i)}_k - \mathcal{U}^{(i)}_{k-1}\big), \qquad \mathcal{U}^{(i)}_{-1} := 0
$$
Define differences $\Delta_k^{(i)} = \big(\mathcal{U}^{(i)}_k - \mathcal{U}^{(i)}_{k-1}\big)$: then
$$
\mathcal{U}^{(i)} = \sum_{k=0}^\infty \Delta_k^{(i)}$$

In multiple dimensions:
$$
\mathcal{U}^{(\mathbf{d})} = \mathcal{U}^{(1)} \otimes \dots \otimes \mathcal{U}^{(d)} = \sum_{||k||_1=0}^\infty \Delta_{k_1}^{(1)} \otimes \dots \otimes \Delta_{k_d}^{(d)}
$$

##### Truncating the operator sum
Idea: only sum over multi-indices $k \in \mathcal{K}$,
$$\mathcal{K} = \{ k \in \mathbb{N}_0^d \mid ||k||_1 \leq L + d - 1  \}$$

Some intuition (same principle on the polynomial basis):

![[diagonal-cut.png|500]]

"Diagonal cut", where only terms below the diagonal are kept. For the polynomials, a higher-order quadrature degree is required above the diagonal, but the error contribution is typically low.

##### Variantes of sparse grids
The following choices can be made:
- choose operator (here: integration)
- choose type of diagonal cut (e.g. vary maximum level in different directions)
- choices of underlying 1D grid
	- nested vs. non-nested
	- stretched vs. regular
	- with vs. without boundary


### Sparse Grids Examples

##### Newton-Cotes nodes

- Hierarchical approach
- Combination technique

![[newton-cotes.png]]

Level 5 Newton-Cotes grid:

![[newton-cotes-2.png|300]]

*Hierarchical approach* (each "subspace" (?) only adds new nodes) vs. *combination technique* (each has the full grid, they are combined via an inclusion-exclusion scheme).

![[newton-cotes-combination.png|350]]

##### Clenshaw-Curtis nodes
Roots of Chebyshev polynomials
- boundary points: can be included or excluded

![[clenshaw-curtis.png]]



##### Nested Leja Points
Not exponential, but only linear growth when including more layers. But not good for quadrature (better for interpolation).

 ![[nested-leja.png]]
 
 ### Nested vs. Non-nested Sparse Grids
  Nested means: new nodes are added to the existing ones for the next level. Non-nested means: all points are new.
  
 
Formally:
- $G_{n-1} \subseteq G_n$ => grid is nested. Evaluating $\Delta_n^{(i)}$ requires only $G_n \setminus G_{n-1}$.
- otherwise: => grid is not nested. Evaluating $\Delta_n^{(i)}$ requires $G_n \cup G_{n-1}$


### Performance of Sparse Grids
The number of grid points depends on the dimension $d$ and the parameter choices.

Example (Clenshaw-Curtis points, $R_l$ points in each dimension): Grows approximately as $\sim R_l \log(R_l)^{d-1}$, compared to $R_l^d$ for the full grid.

![[sparse-grids-performance.png|400]]

Sparse grids useful and feasible in about the range $4 \leq d \leq 20$.

### Applications and Variants
##### Applications
- quadrature
- approximation/interpolation: classification problems, financial mathematics, simulation result visualization...
- PDE discretization
Always in the rule-of-thumb $4 \leq d \leq 20$ range.

##### Adaptivity in subspaces
- Dimension-adaptive SG: Instead of diagonal cut, adaptively use a few more subspaces in addition if it pays off
- spatially-adaptive SG: next grid level only locally around points

##### Sparse Grids in UQ
Mostly for quadrature: Use to obtain PCE expansion coefficients

Alternatively for so-called *stochastic collocation*: polynomial interpolation on space of uncertain $\omega$ with samples from the model; then query stochastical moments from this model.


### Damped Linear Oscillator with Sparse-Grid UQ
Assume more uncertain parameters. Gauss-Legendre sparse grid quadrature with about 3.5 times less nodes than full grid; very low relative error (about $10^{-10}$ for $E$, about $10^{-8}$ for $\text{Var}$).


### Literature
=> more infos: Chapter 11 in R.C. Smith book (UQ - Theory, Implementation, and Applications)