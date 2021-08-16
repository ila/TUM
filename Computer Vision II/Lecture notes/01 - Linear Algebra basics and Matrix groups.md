# Chapter 1 - Mathematical Background: Linear Algebra

### Linear Algebra Basics
Easy: vector spaces, linear independence, basis, vector space dimension, basis transform, inner product, norm, metric, Hilbert space. linear transformations, matrix ring $\mathcal{M}(m, n)$, groups

* *Kronecker product*: $$A \otimes B = \begin{pmatrix}
a_{11}b & \dots & a_{1n}B \\
 \vdots & \ddots & \vdots \\
a_{m1}B & \dots & a_{mn}B
 \end{pmatrix}$$
* *Stack* $A^{s}$ of matrix $A$: stack the $n$ column vectors to a $mn$-dim. vector
	* For example, $u^\top A v = (v \otimes u)^\top A^s$

### Matrix groups
An arbitrary group $G$ has a *matrix representation* if there is an injective group homomorphism into some $GL(n)$. 

##### Important Matrix groups
* *General linear group*: $GL(n)$ are all invertible square matrices of size $n$
* *Special linear group*: $SL(n) = \{A \in GL(n) \mid \det(A) = 1\}$
* *Affine group*: $$A(n) = \bigg\{\begin{pmatrix}A & b \\ 0 & 1\end{pmatrix} \mid A \in GL(n)\bigg\}$$
	* Affine transformations $L: \mathbb{R}^n \to \mathbb{R}^n$ are of the form $L(x) = Ax + b$
	* $A(n)$ represents affine transformations in homogeneous coordinates
* *Orthogonal group*: set of all orthogonal matrices, i.e. $O(n) = \{R \in GL(n) \mid R^\top R = I \}$
	* orthogonal matrices have determinant $\pm 1$
* *Special orthogonal group*: $SO(n) = O(n) \cap SL(n)$ is the subgroup of $O(n)$ with positive determinant
	* $SO(3)$ are the 3-dim. **rotation matrices**
* *Euclidean group*: $$E(n) = \bigg\{\begin{pmatrix}R & T \\ 0 & 1\end{pmatrix} \mid R \in O(n)\bigg\}$$
	* Affine transformations $L(x) = Rx+T$ where $R$ is orthogonal
* *Special Euclidean group*: $SE(n)$ is the subgroup of $E(n)$ where $R \in SO(n)$
	* $SE(3)$ are the 3-dim. **rigid-body motions**

##### Relationships between groups
![[matrix-groups-summary.png]]


### Kernel and Rank
Easy: definitions of range/span, null/ker, rank.

The kernel of $A$ is given by the subspace of vectors which are orthogonal to all rows of $A$. In MATLAB:
```MATLAB
Z = null(A)
```

##### Rank equations
Assume $A \in \mathbb{R}^{m \times n}$. Then:
* $\text{rank}(A) = n - \text{dim}(\text{ker}(A))$
* $\text{rank}(A) \leq \min(m, n)$
*  $\text{rank}(A)$ is the highest order of a non-zero minor of $A$
	*  minor of order $k$ is the determinant of a $k\times k$ submatrix of $A$
*  *Sylvester's inequality*: If $B \in \mathbb{R}^{n \times k}$, then
$$\text{rank}(A) + \text{rank}(B) - n \leq \text{rank}(AB) \leq \min(\text{rank}(A), \text{rank}(B))$$
This inequality describes that if $A$ is $a$ dimensions away from its full rank and $B$ is $b$ dimensions away, $AB$  can be between $a+b$ and $\max(a, b)$ dimensions away from its full rank.
* Multiplying with invertible matrices does not change the rank

In MATLAB:
```MATLAB
d = rank(A)
```
