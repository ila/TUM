
%% Lecture 1, 02.05. %%

Motivation: to understand phenomena in deep learning, mathematical concepts needed are *linear algebra*, *nonlinear optimization*, *probability and statistics*.

Exam: both tasks similar to the exercise sheets, as well as questions about theoretical results/contents.

### Matrices
Notation:
- scalar field $\mathbb{K} \in \{ \mathbb{R}, \mathbb{C}\}$
- $\mathbb{K}^{I\times J}$ for matrices indexed by finite sets $I, J$
	- cleaner way to allow submatrices
- Transposed $A^T$ and Hermitian transposed $A^H$
- $i$-th row $A^{(i)}$, $j$-th column $A_{(j)} = (A^T)^{(j)}$
- standard unit vectors $e^{(i)}$
- $range(A) = \{ Ax \mid x \}$ is the span of the columns
- $\langle x, y \rangle = y^H x$
- MM by inner products: $(Ax)_i = \langle A^{(i)}, \overline{x} \rangle$ and $(AB)_{i\ell} = \langle A^{(i)}, \overline{B_{(\ell)}} \rangle$

*Orthogonal* matrix <=> orthonormal columns <=> $A^H A = I$. If $A$ square: *unitary*, $AA^H = I$ as well.

*Diagonal* matrix (not necessarily square): $A_{ij} = 0$ for all $i \neq j$ (supposing one index set is a subset of the other).

##### Matrix Rank
Notation: $\mathcal{R}_k$ are the $I \times J$ matrices with rank *up to $k$*.

$$r = rank(A) = dim ~range(A) = dim~range(A^H)$$
and
- $r$ is minimal with the property $A= \sum_i^r a_i b_i^H$ where $a_i \in \mathbb{K}^I$, $a_j \in \mathbb{K}^J$
	- e.g. $(1~1; 1~2) = (1;1)(1;1)^H + (0;1)(0;1)^H$, but there are no $a, b$ s.t. $(1~1; 1~2) = a b^H$
- $r$ is maximal with the property that there exists an invertible $r \times r$ submatrix of $A$.
- $r$ = # of positive singular values

Rank is the same whether considered over the complex or real numbers.

### Norms
Norms are continuous by reverse $\triangle$-ineq: $|\,||v||-||w||\,| \leq ||v-w||$.

Euclidean norm on $\mathbb{K}^I$: $||x||_2 = \sqrt{\sum_{i \in I} |x_i|^2}$ (note the absolute value, which is required in the complex numbers!)

### Hilbert spaces
#### Pre-Hilbert spaces
A normed space is pre-Hilbert if $||v|| = \sqrt{\langle v, v \rangle}$.

Cauchy-Schwartz: $|\langle v, w \rangle | \leq ||v||\,||w||$. 

#### Completeness
Cauchy sequence: $(a_k)_k$ s.t. $\forall \varepsilon > 0: \exists N: \forall m, n > N: |a_m - a_n| < \epsilon$.

A Hilbert space is a complete pre-Hilbert space. 

### Convexity and Projections

- Given a closed convex set $C$ in a Hilbert spacd $V$, define the *projection* of any vector as $$P_C(v) = \arg\min_{w\in C} ||v-w||$$ (this projection is actually unique; equivalently: fulfill the inequality $Re \langle z-P_C(v), v - P_C(v) \rangle \leq 0$)
- Projections onto subspaces computed via *Pythagoras-Fourier Theorem*: 

> 
> Let $\{w_\nu\}_\nu$ be a countable orthonormal basis for $W$. Then $P_W(v) = \sum_\nu \langle v, w_\nu \rangle w_\nu$ for all $v \in V$.
> Moreover, one has the Pythagoras-Fourier Theorem:
> $||P_W(v)||^2 = \sum_\nu |\langle v, w_\nu \rangle |^2$ for all $v \in V$ ^b8bb8e

Special case: $W=V, P_V = I$. This yields $v = \sum_\nu \langle v, w_\nu \rangle w_\nu$ and $||v||^2 = \sum_\nu |\langle v, w_\nu \rangle|^2$.


### Trace
The *trace* of a matrix is $tr(A) = \sum_i A_{ii}$.

- $tr(AB) = tr(BA)$ if $A \in \mathbb{K}^{I \times J}, B \in \mathbb{K}^{J \times I}$
- $tr(ABC) = tr(BCA)$, more generally invariance under cyclic permutations
- $tr(A) = tr(U A U^H)$, i.e. invariance under unitary transformations (consequence of previous property)
- $tr(A) = \sum_i \lambda_i$, where $\lambda_i$ are the eigenvalues of $A$


### Matrix Norms
#### Frobenius Norm
"essentially just the Euclidean norm for a matrix"

Also called: *Schur norm*, *Hilbert-Schmidt Norm*.

$$||A||_F := \sqrt{\sum_{i,j} |A_{ij}|^2}$$
Scalar product corresponding to $||\,\cdot\,||_F$: 
$$\langle A, B \rangle_F = \sum_i \sum_j A_{ij} \overline{B_{ij}} = tr(A B^H) = tr(B^H A)$$
In particular, $||A||_F^2 = tr(AA^H) = tr(A^HA)$.

#### Operator Norm
$$||A|| := ||A||_{X \to Y} := \sup_{z \neq 0} \frac{||Az||_Y}{||z||_X}$$
If $X, Y$ are Euclidean spaces (finite, equipped with $||\cdot||_2$) then $||\cdot||$ is called *spectral norm*, often also denoted $||A||_\infty$.


Both $||\cdot||_F$ and $||\cdot||_\infty$ are invariant under unitary transformations:
$$||A||_F = ||UAV^H||_F, \quad ||A||_\infty = ||UAV^H||_\infty$$
