# 06 Polynomial Chaos Expansion
Notation: $\Omega, \omega, \rho$ for domain/inputs/densities.
Goal: good approximation of stochastic model that is cheap to evaluate - faster convergence than Monte Carlo.

gPCE = general Polynomial Chaos Expansion.

### Spectral Expansions
* Fourier series: $s(t) = \sum \hat{s}_n \sin(\dots t)$.
* Polynomial chaos expansion: $f(t, \omega) \approx \sum_{n=0}^N \hat{f}_n(t) \, \phi_n(\omega)$
	* choose $\phi_n$ as polynomials, compute coefficients $\hat{f}_n$, choose N and truncate after $N$ terms (go up to $N-1$).
	* => compute statistical properties of $f(t, \omega)$

* function-space inner product: $\langle p, q \rangle_{\rho} = \int p(\omega) q(\omega) \rho(\omega) \, d\omega$

### Choosing polynomials
The choice depends on the density $\rho$, since we want orthonormality.
E.g. $U([0,1])$: Legendre polynomials; $N(0, 1)$: Hermite polynomials.

### Stieltjes' Three-Term Recursion
All orthonormal polynomials satisfy: (this can't be true, why would we need the base cases?)
$$ \phi_{-1}(\omega) = 0,\quad \phi_{0}(\omega) = 1$$ $$ \phi_{n+1}(\omega) = (A_n \omega + B_n) \phi_n(\omega) - C_n \phi_{n-1}(\omega) $$
where $A_n, B_n, C_n$ computed recursively, depend on $\rho$. 

=> yields a *numerically stable* method to get orthonormal polynomials.
Other schemes e.g. Gram-Schmidt.

### Selecting N and K
Choose $N$ and $K$: $N$ is the number of terms in the PCE, $K$ is the number of quadrature terms used.

##### Polynomial exactness P(K)
For a given quadrature method, $P(K)$ is the maximum polynomial degree that can still be integrated exactly. Examples: 
- Gaussian quadrature: $P(K) = 2K-1$
- Clenshaw-Curtis: $P(K) = K$
- Leja: $P(K) = K$

##### Choosing N, K
The choice depends on $K$ as the number of quadrature nodes $x_i$ in the stochastic space corresponds to the number of model evaluations. So $K$ should be chosen such that we can afford $K$ model evaluations. On the other hand, $N$ can be small as the PCE coefficients decay exponentially.

Rule of thumb: $N = P(K)/2$.

### Pseudo-Spectral Approach for Computing Coefficients
The $n$-th coefficient can be computed by an inner product with the $n$-th basis polynomial:
$$\hat{f}_m(t) = \sum_n \hat{f}_n(t) \langle \phi_n(\omega),\, \phi_m(\omega) \rangle_\rho = \langle \sum \hat{f}_n(t) \phi_n(\omega),\, \phi_m(\omega) \rangle_\rho = \langle f(t, \omega),\, \phi_m(\omega) \rangle_\rho$$

So we have a formula for the coefficients. Evaluating the integral is a quadrature problem. Gaussian quadrature is optimal here (in one-dimensional setting).

Using quadrature to obtain the PCE coefficients is known as the *pseudo-spectral approach*.

##### Algorithm for coefficient computation
Generate $x_k, \omega_k$ and evaluate $f(t, x_k)$. Then compute $\hat{f}_n(t) = \sum_{k=0}^{K-1} f(t, x_k) \phi_n(x_k) \omega_k$ for all $n = 0, \dots, N-1$.

```python
# expect that f computes w |-> f(t, w) for t fixed
def compute_pce_coefficients(f, N, K, rho):
	# e.g. use cp.orth_ttr(..., normed=True)
	phis = create_orthogonal_polynomials(N, rho)
	# e.g. use cp.quad_gauss_legendre(...)
	xs, ws = create_quadrature_nodes_weights(K, rho)
	f_evaluations = []
	for k in range(K):
		f_evaluations.append(f(xs[k]))
	
	pce_coefficients = []
	for n in range(N):
		fn_hat = sum(fk * phis[n](xk) * wk for fk, xk, wk in zip(f_evaluations, xs, ws))
		pce_coefficients.append(fn_hat)
	
	return pce_coefficients
		
```

Other approaches include least squares, stochastic Galerkin, etc.


### Statistical Properties of approximation
* Expected value: $E(f(t, \omega)) = \dots = \hat{f}_0(t)$
	* follows from the fact that $E(\phi_n(\omega)) = \langle \phi_0, \phi_n \rangle_\rho = \delta_{0n}$
* Variance: $Var(f(t, \omega)) = \sum_{n \geq 1} \hat{f}_n^2(t)$

"Nice behavior, saves a lot of costs"


### PCE for damped linear oscillator
Assume $c \sim U(0.08, 0.12)$ and some target time $T$. Then PC-expand the RV $y(T, \omega)$, compute coefficients $\hat{y}_n(T) = \sum_{k=0}^{K-1} y(T, x_k) \phi_n(x_k) w_k$.

Result: 5 quadrature node PCE are enough to get a slightly better result than Monte Carlo with 100000 samples. 


### Multivariate case

$$f(t, \omega) \approx \sum_{\mathbf{n}} \hat{f}_{\mathbf{n}}(t) \phi_{\mathbf{n}}(\mathbf{\omega})$$ where $\mathbf{n}$ is a multi-index, usually with $n_1 + \dots + n_d \leq N$.