### Lie Group SE(3) of Rigid-body Motions
Recall: SE(3) are transformations $x \mapsto Rx + T$, expressed in homog. coordinates as
$$SE(3) = \bigg\{\begin{pmatrix}R & T \\ 0 & 1\end{pmatrix} \;\bigg\lvert\; R \in SO(3)\bigg\}.$$

Consider a continuous family of rigid-body motions
$$g: \mathbb{R} \to SE(3), ~g(t) = \begin{pmatrix}R(t) & T(t) \\ 0 & 1\end{pmatrix}$$

Then the inverse of $g(t)$ is 

$$g^{-1}(t) = \begin{pmatrix}
	R^\top(t) & - R^\top(t) T \\
	0 & 1
 \end{pmatrix}$$

##### Linear differential equation for twists
We want to get a similar representation as differential equation as for $SO(3)$, i.e. write $g'(t) = \widehat{\xi}(t) g(t)$. To achieve this, consider $g'(t) g^{-1}(t)$ and later mulitply with $g(t)$:

$$g'(t) g^{-1}(t) = \begin{pmatrix}
	R'(t) R(t) & T'(t) - R'(t) R^\top T \\
	0 & 0
 \end{pmatrix}$$
As for $SO(3)$, the matrix $R'(t)R^\top$ is skew-symmetric. We call it $\widehat{w}(t)$, further define $v(t) = T'(t) - \widehat{w}(t) T(t)$, and write
$$g'(t) g^{-1}(t)
=: \begin{pmatrix}
	\widehat{w}(t) & T'(t) - \widehat{w}(t) T \\
	0 & 0
 \end{pmatrix} 
=: \begin{pmatrix}
	\widehat{w}(t) & v(t) \\
	0 & 0
 \end{pmatrix}
=: \widehat{\xi}(t) \in \mathbb{R}^{4\times 4}$$

Multiplying with $g(t)$ yields:

$$g'(t) = \widehat{x}(t) g(t)$$

The 4x4 matrix $\widehat{x}(t)$ is called a *twist*. We can view it as tangent along $g(t)$.

##### Lie algebra se(3)
The set of all twists form the tangent space at the identity of $SE(3)$:

$$se(3) = \bigg\{ \widehat{\xi} = 
 \begin{pmatrix} \widehat{w} & \widehat{v} \\ 0 & 0 \end{pmatrix}
 \;\big\lvert\;
 \widehat{w} \in so(3), v \in \mathbb{R}^3
 \bigg\}$$

We define a *hat operator* $\wedge$ and a *vee operator* $\vee$ as before:
$$\begin{pmatrix} v \\ w \end{pmatrix}^{\wedge} = \begin{pmatrix} \widehat{w} & \widehat{v} \\ 0 & 0 \end{pmatrix}  \in \mathbb{R}^{4\times 4} \qquad
 \begin{pmatrix} \widehat{w} & \widehat{v} \\ 0 & 0 \end{pmatrix}^{\vee} = \begin{pmatrix} v \\ w \end{pmatrix} \in \mathbb{R}^6$$

$\widehat{\xi} \in se(3)$ is called a *twist*, $\xi \mathbb{R}^6$ its *twist coordinates*. The vector $v$ represents the *linear velocity*, the vector $w$ the *angular velocity*.

#####  Exponential map $\exp: se(3) \to SE(3)$
Assume the motion has constant twist $\widehat{\xi}$. We get the linear differential equation system
$$
 \begin{cases}
g'(t) = \widehat{\xi} g(t) \\
g(0) = I
 \end{cases}
$$
which has the solution $g(t) = \exp(\hat{\xi}t)$.

So the exponential for $SE(3)$ is defined as

$$\exp: se(3) \to SE(3), \widehat{\xi} \to \exp(\widehat{\xi}),$$
where the $\widehat{\xi} \in se(3)$ are called *exponential coordinates* for $SE(3)$.

One can show that the exponential has the closed-form expression
$$\exp(\widehat{\xi}) = \begin{pmatrix}
	\exp(\widehat{w}) & \tfrac{1}{||w||^2} \big( I - \exp(\widehat{w}) \widehat{w} v + w w^\top v  \big) \\
	0 & 1
 \end{pmatrix}$$
(in case $w \neq 0$; otherwise $\exp(\widehat{\xi}) = [I ~ v; 0 ~ 1]$). Note: this is the analogon to the Rodrigues formula; in turn, this formula requires the Rodrigues formula for computing $\exp(\widehat{w})$.


##### Logarithm of SE(3)
To show that for any element of $SE(3)$ with rotation/translation $R, T$,  we can find a corresponding twist in $se(3)$, we use the above closed-form expression for $\exp(\widehat{\xi})$: Clearly, we can find $w$ as $\log(R)$. Then we need to solve $\tfrac{1}{||w||^2} \big( I - \exp(\widehat{w}) \widehat{w} v + w w^\top v  \big) = T$ for $v$ (not detailed in the lecture), which yields that the desired vector $v$ exists.


### Representing camera motion
Assume a point $\mathbf{X}_0$ in *world-coordinates*: this is mapped by the transformation $g(t)$ to a point $\mathbf{X}(t)$. Note: we follow the convention that points are moved by the transformation rather than the camera itself. In 3d coordinates, we define
$$\mathbf{X}(t) = R(t) \mathbf{X}_0 + T(t)$$
and in homogeneous coordinates:
$$\mathbf{X}(t) = g(t) \mathbf{X}_0.$$
We use the same notation for homogeneous and 3d representations (usually if a $g$ comes into play, we mean homogeneous coordinates).

##### Notation for concatenation
If we have one motion from $t_1$ to $t_2$ and another from $t_2$ to $t_3$, we can write $\mathbf{X}(t_3) = g(t_3, t_1) \mathbf{X}_0 = g(t_3, t_2) g(t_2, t_1) \mathbf{X}_0$.

Also, it holds that $g^{-1}(t_2, t_1) = g(t_1, t_2)$.

##### Rules of velocity transformation
We want to find the velocity of a point:
$$\mathbf{X}'(t) = g'(t) \mathbf{X}_0 = g'(t) g^{-1}(t) \mathbf{X}(t)$$
But $g'(t) g^{-1}(t)$ is simply a twist:
$$\widehat{V}(t) = g'(t) g^{-1}(t) = \begin{pmatrix}
	\widehat{w}(t) & v(t) \\
	0 & 0
 \end{pmatrix} \in se(3)$$

So $\mathbf{X}'(t) = \widehat{V}(t) \mathbf{X}(t)$ in homog. coordinates, or $\mathbf{X}'(t) = \widehat{w}(t) \mathbf{X}(t) + v(t)$. This clarifies why $w$ represents angular velocity and $v$ represents linear velocity.

##### Adjoint map: transfer between frames
Suppose in another frame, the view is displaced relative to our frame by $g_{xy}$, i.e. $\mathbf{Y}(t) = g_{xy} \mathbf{X}(t)$. The velocity in this frame is

$$mathbf{Y}'(t) = g_{xy} mathbf{X}'(t) = g_{xy} \widehat{V}(t) \mathbf{X}(t) = g_{xy} \widehat{V} g^{-1}_{xy} \mathbf{Y}(t)$$

In other words, the relative velocity of points observed from the other frame is represented by the twist $\widehat{V}_y = g_{xy} \widehat{V} g_{xy}^{-1} =: \text{ad}_{g_{xy}}(\widehat{V})$. Here we introduced the *adjoint map*:

$$\text{ad}_g: se(3) \to se(3), \widehat{\xi} \mapsto g \widehat{\xi} g^{-1}.$$


### Euler angles
*Euler angles* are a way to parametrize rotations, and an alternative to exponential parametrization. They are related, however, as we will see.

How to parametrize the space of rotations? We can choose a basis $(\widehat{w}_1, \widehat{w}_2, \widehat{w}_3)$ of $so(3)$ (skew-symm. matrices). Then we can parametrize any rotation in *Lie-Cartan coordinates of the first kind* $\alpha$ wrt. this basis as follows:
$$\alpha: (\alpha_1, \alpha_2, \alpha_3) \mapsto \exp(\alpha_1 \widehat{w}_1 + \alpha_2 \widehat{w}_2  + \alpha_3 \widehat{w}_3)$$

Alternatively, we can paremetrize it in *Lie-Cartan coordinates of the second kind* $\beta$ wrt. the basis:
$$\beta: (\beta_1, \beta_2, \beta_3) \mapsto \exp(\beta_1 \widehat{w}_1) \exp(\beta_2 \widehat{w}_2)\exp(\beta_3 \widehat{w}_3)$$

If we choose $w_1 = (0, 0, 1)^\top, w_2 = (0, 1, 0)^\top, w_3 = (1, 0, 0)^\top$, i.e. rotations around the z/y/x axes, the coordinates $\beta_i$ are called *Euler angles*.

This shows that the Euler angles are just a fairly random way, among infinitely many ways, to parametrize rotations. Advantage of the first-kind Lie-Cartan coordinates: allows to stay in the Lie algebra as long as possible, where the group operation (matrix addition instead of multiplication) is less expensive.

### Summary
![[chapter2-summary.png]]