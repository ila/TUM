### Eigenvectors and Eigenvalues
The *spectrum* of a matrix $\sigma(A)$ is the set of its (right) eigenvalues.

In MATLAB:
```MATLAB
% Then A*V = V*D, D diagonal
[V, D] = eig(A);
```
For a real square matrix $A$, it holds:
* for each eigenvalue, there also is a corresponding left eigenvector: $\sigma(A) = \sigma(A^\top)$.
* eigenvectors to different eigenvalues are lin. indep.
* $\det(A)$ is the product of all eigenvalues including multiplicities, since $\sigma(A)$ are the roots of the characteristic polynomial $\det(\lambda I - A)$.
* Similar matrices have the same spectrum: $\sigma(PAP^{-1}) = \sigma(A)$
* Conjugates of eigenvalues are eigenvalues: $\sigma(A) = \overline{\sigma(A)}$ (remember: $A$ is real).

### Symmetric matrices
Easy: symmetric, PSD ($\succeq 0$), PD ($\succ 0$)
For a real symmetric matrix $S$, it holds:
* $S$ only has real eigenvalues
* eigenvectors to different eigenvalues are orthogonal
* there exist $n$ orthonormal eigenvectors of $S$ that form a basis of $\mathbb{R}^n$. If $V = (v_1, \dots, v_n) \in O(n)$ contains these eigenvectors and $\Lambda = \text{diag}(\lambda_1, \dots, \lambda_n)$ the eigenvalues, we have $S = V \Lambda V^\top$.
* If $S$ is PSD, then $\max_{|x|=1}\langle x, Sx \rangle$ is the largest eigenvalue and $\min_{|x|=1}\langle x, Sx \rangle$ is the smallest eigenvalue.


### Matrix norms
Let $A \in \mathbb{R}^{m \times n}$.
* *induced 2-norm* (operator norm):
$$||A||_2 = \max_{||x||_2 = 1}||Ax||_2 = \max_{||x||_2 = 1} \sqrt{\langle x, A^\top A x\rangle}$$

* *Frobenius norm*:
$$||A||_f = \sqrt{\sum_{i,j} a_{ij}^2} = \sqrt{\text{trace}(A^\top A)}$$

Diagonalizing $A^\top A$, we obtain:
$$||A||_2 = \sigma_1, \quad||A||_f = \sqrt{\sigma_1^2+\dots+\sigma_n^2}$$

### Skew-symmetric matrices
A real square matrix $A$ is *skew-symmetric* (*schiefsymmetrisch*), if $A^\top = -A$. Then it holds:
* All eigenvalues are imaginary: $\sigma(A) \subseteq i\mathbb{R}$
* $A$ can be written as $A = V \Lambda V^\top$, where $V \in O(n)$ and $\Lambda = \text{diag}(A_1,\dots,A_m,0,\dots,0)$ is a  block-diagonal matrix with blocks of the form $A_i = [0 ~ a_i; -a_i~ 0]$
	* Corollary: skew-symmetric matrices have even rank.

##### Hat operator
Important skew-symmetric matrix: Given $u \in \mathbb{R}^3$, define
$$\widehat{u} = \begin{pmatrix}
0 & -u_3 & u_2 \\ u_3 & 0 & -u_1 \\ -u_2 & u_1 & 0
 \end{pmatrix}$$
The hat operator models the cross product: $\widehat{u}v = u \times v$ for any $v \in \mathbb{R}^3$. The one-dim. kernel of $\widehat{u}$ is $\text{ker}(\widehat{u}) = \text{span}(u)$.

### Singular-Value Decomposition (SVD)
Generalization of eigenvalues/-vectors to non-square matrices. SVD computation numerically well-conditioned.

Let $A \in \mathbb{R}^{m \times n}$, $m \geq n$, $\text{rank}(A) = p$. Then there exist $U, V, \Sigma$ s.t. $U$ and $V$ have *orthonormal columns* and $\Sigma = \text{diag}(\sigma_1, \dots, \sigma_p)$, $\sigma_1 \geq \dots \geq \sigma_p$, and
$$A = U \Sigma V^\top.$$

This generalizes the eigendecomposition (which decomposes $A = V \Lambda V^\top$ with diagonal $\Lambda$ and orthogonal $V$).

In MATLAB:
```MATLAB
% Then A = U * S * V'
[U, S, V] = svd(A)
% Here, S is smaller and has only non-zero sing. val.
[U, S, V] = svd(A, 'econ')
```

##### Proof of SVD
Assume we have $A \in \mathbb{R}^{m \times n}$, $m \geq n$ and $\text{rank}(A) = p$. Then $A^\top A \in \mathbb{R}^{n \times n}$ is symmetric + PSD.

$A^\top A$ has $n$ non-negative eigenvalues, which we call $\sigma_1^2 \geq \dots \geq \sigma_n^2$, and orthonormal eigenvectors $v_1, \dots, v_n$. The $\sigma_i$ (squareroots of eigenvalues) will become our singular values. The first $p$ vectors $v_i$ will become the columns of $V$.

One can show: $\text{ker}(A^\top A) = \ker(A)$ and $\text{range}(A^\top A) = \text{range}(A^\top)$. Therefore the first $p$ eigenvectors $v_1, \dots, v_p$ span $\text{range}(A^\top)$ and the remaining $v_{p+1}, \dots, v_n$ span $\text{ker}(A)$.

Define $u_i = \tfrac{1}{\sigma_i} A v_i$ for $i \leq p$: These $u_i$ will become the columns of $U$. They are orthonormal, because using that the $v_i$ are eigvalues of $A^\top A$, we get:
$$\langle u_i, u_j \rangle = \tfrac{1}{\sigma_i \sigma_j} \langle v_i, A^\top A v_j \rangle = \delta_{ij}$$

By definition, $A v_i = \sigma_i u_i$ for $i \leq p$, and therefore $$A \tilde{V} := A (v_1, \dots, v_n) = (u_1, \dots, u_p, 0, \dots, 0) \text{diag}(\sigma_1, \dots, \sigma_p, 0, \dots, 0) = \tilde{U} \tilde{\Sigma}$$
Now get $\tilde{V}$ to the right side by multiplying with its transposed, and delete the unnecessary columns/diagonal entries in the three matrices to obtain
$$A = U \Sigma V^\top. \qquad \square$$

Note: the proof on the slides actually completes $u_1, \dots, u_p$ with vectors $u_{p+1}, \dots, u_m$ to form a basis (instead of zero). This does not matter for the proof, but maybe for the next section.

##### Geometric Interpretation of SVD
Let $A$ be a square matrix. If $Ax = y$, with the SVD we get
$$U^\top y = \Sigma V^\top x.$$
Interpretation: $U^\top y$ are the coordinates of $y$ wrt. the basis represented by $U$, and similarly $V^\top x$ are the coordinates of $x$ wrt. the basis represented by $V$. The coordinates are related, as one can be obtained from the other simply by scaling by $\Sigma$.

Further interpretation: *$A$ maps the unit sphere into an ellipsoid with semi-axes $\sigma_i u_i$*.

### Moore-Penrose pseudoinverse
The Moore-Penrose pseudoinverse generalizes the inverse of a matrix. If $A$ has the SVD $A = U \Sigma V^\top$, the pseudoinverse is defined as

$$A^\dagger = V \Sigma^{-1} U^\top $$

Comment: here I assume a *compact SVD*, i.e. $\Sigma$ is already shrinked s.t. all singular values are non-zero.

In MATLAB:

```MATLAB
X = pinv(A)
```

##### Properties of the pseudoinverse
It holds that
$$A A^\dagger A = A, ~~A^\dagger A A^\dagger = A^\dagger$$

A linear system $Ax = b$, $A \in \mathbb{R}^{m \times n}$ can have multiple or no solutions. Among the minimizers of $|Ax - b|^2$, the one with smallest norm is given by
$$A^\dagger b.$$

# Chapter 2 - Representing a Moving Scene

### Origins of 3D Reconstruction
3D reconstruction is a classical ill-posed problem, as its solutions are not unique (most extreme example: imagine a photograph was pinned in front of the camera).

Two type of transformations are needed:
* *Perspective projection*: account for image formation process (i.e. specifics of camera)
* *Rigid-body motion*: represent movement of camera between frames

Perspective projection was studied by acient Greeks (Euclids ~400 BC) and in the Renaissance (Brunelleschi & Alberti, 1435). This lead to *projective geometry* (Desargues 1648, Monge in the 18th century).

Multi-view geometry was first treated by Erwin Kruppa in 1913: *Two views of five points are sufficient to determine a) the motion between the views and b) the 3D structure up to finitely many solutions.* The eight-point algorithm was proposed bei Longuet-Higgins in 1981, further work along these lines followed (three views 1987-1995, factorization techniques 1992).

The joint estimation of a) camera motion and b) 3D structure is called *structure and motion* or *visual SLAM*.