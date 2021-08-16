
### 3D space and rigid body motion
##### Euclidean space terminology
Definitions:
* $\mathbb{E}^3$ is identified with $\mathbb{R}^3$ and consists of points $\mathbf{X} = (X_1, X_2, X_3)^\top$
* *bound vector*: $v = \mathbf{X} - \mathbf{Y} \in \mathbb{R}^3$, taking its endpoints into account
* *free vector*: if we don't take the endpoints into account
* *curve length*: $l(\gamma) = \int_0^1 |\gamma'(s)|\,ds$ for $\gamma: [0, 1] \to \mathbb{R}^3$

##### Skew-symmetric matrices
* $so(3)$ denote the space of *skew-symmetric matrices*
* *cross product*: $u \times v = (u_2 v_3 - u_3 v_2, \,u_3v_1 - u_1v_3, \,u_1v_2 - u_2v_1)^\top$. This vector is orthogonal to both $u$ and $v$.
* The hat operator $\widehat{~}: \mathbb{R}^3 \to so(3)$ models the cross product and defines an isomorphism between 3-dim. vectors and skew-symmetric matrices. Its inverse is denoted $\vee: so(3) \to \mathbb{R}^3$.

##### Rigid-body motions
A *rigid-body motion* is a family $g_t: \mathbb{R}^3 \to \mathbb{R}^3$, $t \in [0, T]$ which preserves norms and cross products:
* $||g_t(v)|| = ||v||, ~\forall v$  (distance between moving points stays the same)
* $g_t(u) \times g_t(v) = g_t(u \times v), ~ \forall u, v$ (orientation stays the same)
In other words: each $g_t$'s rotation part is in $SO(3)$ ($O(3)$ because of the norm preservation, and then $SO(3)$ because the cross product preservation implies a determinant of $1$). So $g_t(x) = Rx + T$ for some $R \in SO(3)$, $T \in \mathbb{R}^3$.

They are also *inner product preserving*, as $\langle u, v \rangle = \tfrac{1}{4}(||u+v||^2 - ||u-v||^2)$ (the *polarization identity*). As a consequence, they are *volume-preserving* because the preserve the *triple product*: $\langle g_t(u), g_t(v) \times g_t(w) \rangle = \langle u, v \times w \rangle$.


##### Exponential coordinate reparametrization of rotations
Motivation: $SO(3)$ is actually only 3-dimensional, not 9-dimensional.

Assume a trajectory $R(t): \mathbb{R} \to SO(3)$. Its derivative $R'(t)$ is connected to $R(t)$ by a linear differential equation, which leads a matrix exponential solution for $R(t)$:

$$R(t) R^\top(t) = I 
 \Rightarrow R'(t)R^\top(T) + R(t)R'^\top(t) = 0
 \Rightarrow R'(t) R^\top(t) = -(R'(t)R^\top(t))^\top
$$
So $R'(t) R^\top(t)$, i.e. of the form $R'(t) R^\top(t) = \widehat{w}(t) \in so(3)$, and therefore $R'(t) = \widehat{w}(t) R(t)$.

Now assume an infinitesimal rotation: If $R(0) = I$ then $R(dt) \approx I + \widehat{w}(0) dt$. This means that an infinitesimal rotation can be approximated by an element of $so(3)$.

### Lie Group SO(3) of Rotations
**Definitions**
* A *Lie group* is a smooth manifold that is also a group, s.t. the group operations $+$ and ${}^{-1}$ are smooth maps.
* An *algebra over a field $K$* is a $K$-vector space that additionally has a multiplication on $V$ (non-commutative in general)
* *Lie bracket*: $[w, v] = wv - vw$

Formulated the above with this terminology:
* $SO(3)$ is a Lie group
* $so(3)$ is a Lie algebra, and tangent space at the identity of the Lie group $SO(3)$

The mapping from a Lie algebra to its associated Lie group is called *exponential map*. Its inverse is called *logarithm*.

##### Exponential map $\exp: so(3) \to SO(3)$
Assume a rotation with **constant $\widehat{w}$**.  The transformation $R(t)$ is then described by the linear differential system of equations
$$
 \begin{cases}
R'(t) = \widehat{w} R(t) \\
R(0) = I
 \end{cases}
$$
This has the solution
$$R(t) = \exp(\hat{w} t) = \sum_{n = 0}^\infty \frac{(\hat{w}t)^n}{n!}$$
This describes a *rotation around the axis $w \in \mathbb{R}^3$, $||w|| = 1$, by an angle $t$*.

So the matrix exponential defines a map $\exp: so(3) \to SO(3)$.

##### Logarithm of SO(3)
The inverse map (actually an inverse map, the inverse is non-unique) is denoted by $\log: SO(3) \to so(3)$.

The vector $w$ s.t. $\hat{w} = \log(R)$ is given by
$$
||w|| = \cos^{-1} \bigg(\tfrac{\text{trace}(R) - 1}{2}\bigg),\quad
 \frac{w}{||w||} = \frac{1}{2 \sin(||w||)} (r_{32} - r_{23}, ~r_{13} - r_{31}, ~r_{21} - r_{12})^\top
$$

The length of $||w||$ corresponds to the rotation angle, and the normalized $\frac{w}{||w||}$ to the rotation axis. The non-uniqueness can be seen from the fact that increasing the angle by multiples of $2\pi$ gives the same $R$.

##### Rodrigues' Formula
For skew-symmetric matrices $\hat{w} \in so(3)$, the matrix exponential can be computed by *Rodrigues formula*:

$$\exp(\hat{w}) = I + \frac{\hat{w}}{||w||} \sin(||w||)
+ \frac{\hat{w}^2}{||w||^2} \big(1 - \cos(||w||)\big)$$

**Proof:** Denote $t = ||w||$ and $v = w/t$. First, prove that $\hat{v}^2 = v v^\top - I$ and $\hat{v}^3 = -\hat{v}$. This allows to write $\hat{v}^n$ in closed form as
$$
 \hat{v}^n = \begin{cases}
(-1)^{k-1} \cdot \hat{v}^2, &n=2k \\
(-1)^{k} \cdot \hat{v}, &n=2k+1
 \end{cases}
$$
Plugging this into the exponential series yields:
$$
 \sum_{n = 0}^\infty \frac{(\hat{v}t)^n}{n!}
= I 
+ \sum_{n \geq 1, n=2k} \frac{(-1)^{k-1} t^n \hat{v}^2}{n!}
+ \sum_{n \geq 1, n=2k+1} \frac{(-1)^{k} t^n \hat{v}}{n!}
= I + \sin(t) \hat{v} + (1 - \cos(t)) \hat{v}^2
$$