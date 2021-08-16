# 07 Stochastic Galerkin Method

Goal: compute PCE without relying on quadrature (<-> pseudo spectral uses quadrature). Comparison to pseudo-spectral: faster convergence - however more time-consuming to implement, requires access to model/code.

### Finite-Element Analogy
- weak-form formulation: instead of $Lu = f$ (L differential operator), write it at $\int_\Omega Lu v = \int_\Omega f v$ for test functions $v \in V$.
- space discretization
- assume solution u (e.g. unknown displacements) is weighted sum of base functions: $u(x) = \sum \hat{u}_n N_n(x)$
	- So approximate the infinte-dim. solution space with the finite-dim. space spanned by the $N_n$, "project" to it

Analogy: $u$ becomes stochastic output $f(t, \omega)$, $N_n$ ("local" shape functions) become "global" orth.pol. $\phi_n(\omega)$. Coeffs $\hat{u}_n$ become $\hat{f}_n(t)$.

### Steps of Stochastic Galerkin
1. Polynomial Chaos Expansion of uncertain inputs
2. write model solution $f$ as polyn. chaos expansion, order $N$
3. subst. the two expansions (inputs/model solution) into model equations
4. using orthogonality: get system with $N$ unknown coeffs. 
5. modified solver solves new ("coupled" ?) system
6. from coeffs -> reconstruct "statistical properties" (i.e. moments?)

### Example: Dampled Linear Oscillator in Stochastic Galerkin
![[dampedLinearOsc.png]]

**Step 1** (PCE of uncertain inputs)

Here, uncertainty for damping coefficient $c$: $C \sim U(a, b)$.  

- Transform $c(\omega) = \tfrac{a+b}{2} + \tfrac{b-a}{2}\omega$ with $\Omega \sim \mathcal{U}(-1, 1)$. (first fraction is $c_\mu$, second one is $c_\sigma$)
- polynomial chaos basis: Legendre polynomials $\phi_i(\omega)$ (fit to uniform).
- use a 2nd-order PCE: $c \approx c_\mu + c_\sigma \omega = c_\mu \phi_0(\omega) + c_\sigma \phi_1(\omega)$.


**Step 2** (PCE of model PDE functions)
Write modeled functions $x(t, \omega)$, $v(t, \omega)$ as PCEs (not much happening yet, just bring it to PCE representation).

- PCE for $x$: $$x(t, \omega) = \sum_{n=0}^{N-1} \hat{x}_n(t) \phi_n(\omega)$$
- PCE for $v$: $$v(t, \omega) = \sum_{n=0}^{N-1} \hat{v}_n(t) \phi_n(\omega)$$
 
From now drop $\omega, t$ from notation.

**Step 3 + 4** (substitute PCEs -> model equations, exploit orthogonality)

- Subst. PCE into initial conds.: $x(0) = x_0$ $\mapsto$ $\sum_n \hat{x}_n(0) \phi_n = x_0$.
	- Put inner product with $\phi_j$ around it => get $$\sum_n   \hat{x}_n(0) \langle \phi_n, \phi_j\rangle = x_0 \langle\phi_0, \phi_j\rangle,$$ and finally $\hat{x}_j(0) = \delta_{0j} x_0$ for all $0 \leq j \leq N-1$.

- Subst. PCE into ODE #1: $$(\tfrac{d}{dt} x = v) \mapsto \tfrac{d}{dt} \sum \hat{x}_n\phi_n = \sum \hat{v}_n\phi_n.$$
	- With inner product: $\tfrac{d}{dt} \hat{x}_j(t) = \hat{v}_j(t) ~\forall j$.

- Subst. PCE into ODE #2 (==more interesting==, and now with uncertain input $c$): $$\frac{d}{dt} \sum \hat{v}_n\phi_n = f \cos(\omega_O t) - c_\mu \phi_0 \big(\sum \hat{v}_n \phi_n\big) - c_\sigma \big(\hat{v}_n \phi_1 \phi_n\big) - k\sum \hat{x}_n \phi_n$$
   - with inner product with $\phi_j$ (pull out anything not depending on $\omega$): After simplifying, canceling zero $\delta_{ij}$s, we get
 
$$\frac{d}{dt} \hat{v}_j \gamma_j =  f \cos(\omega_O t) \delta_{0j} - c_\mu \hat{v}_j \gamma-j - c_\sigma \sum_n \hat{v}_n \langle\phi_1 \phi_n, \phi_j\rangle - k \hat{x}_j \gamma_j \quad \forall j.$$

Here the $\phi_n$ are coupled to the $\phi_j$ through the $\langle\phi_1 \phi_n, \phi_j\rangle$. This expression increases the complexity.


**Step 5**
We get a new IVP (initial value problem):

![[dampedOsc-IVP.png]]

From 2 coupled ODEs to 2N coupled ODEs (one for each $\hat{v}_j, \hat{x}_j$). Modified solver can solve this new problem.


### Stochastic Galerkin vs. Pseudo-Spectral Approach

Stochastic Galerkin computes coeffs from ODEs/PDEs, while pseudo-spectral computes them numerically via quadrature. Pseudo-spectral has error both from series truncation and from quadrature, Galerkin only from truncation.

Stochastic Galerkin is more accurate, but more complex. It requires modification of the model, while pseudo-spectral works with a black-box model.