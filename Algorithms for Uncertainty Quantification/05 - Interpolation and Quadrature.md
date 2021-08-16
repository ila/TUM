# 05 - Aspects of Interpolation and Quadrature

## Polynomial interpolation

Interpolation: estimate unknown points of function of which some points are known. Here: polynomial interpolation.

Assume $f: [-1, 1] \to mathbb{R}$ for simplicity, $N \geq 1$. A grid is a set $G$ of $N+1$ notes $x_i$ in ascending order.

The unique polynomial $I_N^G$ of degree $N$ that satisfies
$$I_N^G(x_i) = f(x_i) ~\forall i$$
is called the *interpolating polynomial of $f$ on $G$*.

QUESTION: Slides say $f$ should be continuous; but ththat shouldn't be needed at all?

### Lagrange form of $I_N^G$
Define the $i$-th *Lagrange cardinal polynomial* $L_i^G$ as

$$L_i^G(x) = \prod_{j=0, j\neq i}^N \frac{x - x_j}{x_i - x_j}$$

with the property that $L_i^G(x_j) = \delta_{ij}$. Then the Lagrange form of the interpolant is

$$I_N^G f(x) = \sum_{i=0}^N f(x_i) L_i^G(x)$$

![[lagrange-polys.png]]

### Interpolation error
Assume that $f$ is "smooth enough" (has derivatives up to order $N+1$), let $bar{x} \in [x_0, x_N]$. Then there is some $\xi \in [x_0, x_N]$ such that

$$f(\bar{x}) - I_N^G f(\bar{x}) = \frac{D^{N+1}f^{(N+1)}(\xi)}{(N+1)!} \prod_{i=0}^N (\bar{x} - x_i)$$

##### Maximum error bound and Lebesgue constant
Let $I_N^\ast$ be the *best approximation polynomial at $N+1$ nodes*, i.e. the polynomial of minimum supremum norm difference to $f$ that intersects $f$ at $N+1$ points.

The *Lebesgue constant* relative to grid $G$ is

$$\Lambda_N(G) = \max_x \sum_{i=0}^N |L_i^G(x)|$$

Then a bound for the maximum interpolation error of $I_N^G f$ is 

$$||f - I_N^G f||_\infty \leq (1 + \Lambda_N(G)) ||f - I_N^\ast f||_\infty$$


$\Lambda_N(G)$ contains all the information about the effect of the grid choice.  For any grid, $\Lambda_N(G) > \frac{2}{\pi} \ln(N+1) - C, C > 0$. For a uniform grid, we have approximately $\Lambda_N(G) \approx \frac{2^{N+1}}{e N \ln N}$.

QUESTION: what is C?

### Runge phenomenon
Interpolation on uniform grid can lead to Runge phenomenon: Very large approximation errors near the boundaries.

![[runge.png]]

Remedies:
- use non-uniform grid
- use other basis functions than $L_i^G$, e.g. splines with local support


## Quadrature
Integrate black-box function without known antiderivative. Idea: approximate based on function evaluations, using rules that could give *exact* integrals for polynomials. In this lecture: *Gaussian quadrature*.

Usually, weighted sum of function evaluations. Classical quadrature: e.g. *trapezoidal sum*

$$h (\frac{f(x_0) + f(x_1)}{2} + \sum_{i=1}^{N-1} f(x_i)), ~ h = x_{i+1}-x_i = \text{const}.$$

### Orthogonal polynomials
Assume we have a domain $\mathbb{D}$ and weight function $w$, use $L_w^2(\mathbb{D})$ as set of functions that are square-integrable over $\mathbb{D}$ w.r.t. weight function $w$: i.e. $\int_\mathbb{D} f(x)^2 w(x) \,dx < \infty$.

Define the inner product on $L^2_w(\mathbb{D})$ as $$\langle f, g \rangle_w = \int_{\mathbb{D}} f(x) g(x) w(x) \, d(x).$$

Polynomials $p_i, p_j$ of degree $i, j$, respectively, are *orthogonal* if $\langle p_i, p_j \rangle = k \delta_{ij}$ and *orthonormal* if $k=1$.

##### Legendre polynomials
Used for a uniform weight $w(x) = c$. 
$$p_0(x) = 1, p_1(x) = x, p_2(x) = \frac{1}{2}(3 x^2 - 1), p_3(x) = \frac{1}{2} (5x^3 - 3x)$$

Different $p_i, p_j$ are orthogonal, and $\langle p_i, p_i\rangle_{w(x)=1/2} = \frac{2}{2i+1}$

##### Hermite polynomials
Used for Gaussian weight $w(x) = \frac{1}{\sqrt{2\pi}}\exp(-x^2/2)$.
$$p_0(x)=1, p_1(x) = x, p_2(x) = x^2-1, p_3(x) = x^3-3x$$
and $\langle p_i, p_i \rangle_w = i!$.

To keep in mind: there are different conventions regarding the normalization, some authors ("physicists") drop the normalization constant in $w(x)$. Then the polynomials have a little different coefficients.

### Gaussian Quadrature
Let $(p_i)_i$ be a family of orthogonal polynomials wrt. $w$. 
- Define a quadrature grid $G = (x_i)_i$ by the $N+1$ roots of $p_{N+1}$
- For each $i$, define a weighting factor $$w_i = \int_{\mathbb{D}} L_i^G(x) w(x) \,dx$$

The Gaussian quadrature is given by
$$\int_\mathbb{D} f(x) w(x) \,d(x) = \sum_{i=0}^N w_i f(x_i) + \epsilon$$

Gauss-Legendre nodes (roots of Legendre polynomials):
![[gauss-legendre-nodes.png]]

Gauss-Hermite nodes (roots of Hermite polynomials):
![[gauss-hermite-nodes.png]]