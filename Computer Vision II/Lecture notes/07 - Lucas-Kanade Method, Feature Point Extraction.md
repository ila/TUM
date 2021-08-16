### The Lucas-Kanade Method

#### Some Assumptions we make
* *Brightness Constancy Assumption* (also optical flow constraint): every moving point has constant brightness. Formally, $I(x(t), t) = \text{const.} ~\forall t$.
	* This is *almost never* fulfilled. But often approximately
	* the equivalent formulation $$\frac{d}{dt}I(x(t), t) = \nabla I^\top \frac{dx}{dt} + \frac{\partial I}{\partial t} = 0$$is also called the (differential) optical flow constraint
* *Constant Motion in a Neighborhood Assumption*: the velocity of movement is constant in a neighborhood $W(x)$ of a point $x$: $$\nabla I(x', t)^\top v + \frac{\partial I}{\partial t}(x', t) = 0 \quad \forall x' \in W(x)$$

#### Lukas-Kanade (1981) formulation
Since the two assumptions are not exactly fulfilled usually, the method minimizes the least-squares error instead:
$$E(v) = \int_{W(x)} \vert \nabla I(x', t)^\top v + I_t(x', t)\vert^2 dx'$$
Comment: this would be done differently today (not quadratic e.g.). $E$ (cost function) is also called *energy*.

##### Solution
We get $\frac{dE}{dv} = 2Mv + 2q = 0$, where $M = \int_{W(x)} \nabla I \nabla I^\top dx'$ and $q = \int_{W(x)} I_t \nabla I dx'$.
If $M$ is invertible, the solution is $v = - M^{-1} q$.

##### Alternatives
Affine motion: basically, same technique. The cost function becomes

$$E(v) = \int_{W(x)} \vert \nabla I(x', t)^\top S(x') p + I_t(x', t)\vert^2 dx'$$

and is minimized with the same technique as above.

##### Limitations (translational version)
*Aperture problem*: e.g. for constant intensity regions (where $\nabla I(x) = 0, I_t(x) = 0$ for all points). To get a unique solution $b$, the *structure tensor* $M(x)$ must be invertible:

$$M(x) = \int_{W(x)} \begin{pmatrix} I_x^2 & I_x I_y \\ I_x I_y & I_y^2 \end{pmatrix} \,dx'$$

If $M(x)$ is not invertible, but at least non-zero, at least the *normal motion* (motion in direction of the gradient) can be estimated.


#### Simple Feature Tracking with Lucas-Kanade
Assume: given $t$.

- for each $x \in \Omega$ compute the structure tensor $M(x)$
- for the points $x$ where $\det M(x) \geq 0$ (above treshold), compute the local velocity as $$b(x, t) = -M(x)^{-1} \begin{pmatrix} \int I_x I_t dx' \\ \int I_y I_t d x' \end{pmatrix}$$
- update points from $x$ to $x + b(x, t)$ and repeat for time $t + 1$.

Important point: a translation-only model works in small window, but on a larger window, or with a longer movement, we need a better model (e.g. affine).

### Robust Feature Point Extraction
Problem: unreliable to invert $M$ if it has a small determinant. Alternative: Förstner 1984, Harris & Stephens 1988 - *Harris corner detector*.
- use alternative structure tensor to detect good points
	- weight neighborhood by a Gaussian: $$M(x) = G_\sigma \nabla I \nabla I^\top = \int G_\sigma(x - x') \begin{pmatrix} I_x^2 & I_x I_y \\ I_x I_y & I_y^2 \end{pmatrix}(x') \,dx'$$
	- select points for which $\det(M) - \kappa \,\text{trace}(M)^2 > \theta$

![[harris-foerstner-detector.png]]

### Wide Baseline Matching
Problem: many points will have no correspondence in the second image. Wide baseline might be needed to counter *drift*, i.e. the accumulation of small errors (compute corredpondences again with larger distance in time).

- One needs to consider an affine model (translational is not good enough for wide baseline).
- To be more robust to illumination changes (typically greater in wide baseline): replace L2 error function by *normalized cross correlation*

##### Normalized Cross Correlation
The NCC for a given candidate transformation $h$ is
$$NCC(h) = \frac{
 \int_{W(x)} (I_1(x') - \bar{I}_1) (I_2(h(x')) - \bar{I}_2) \, dx'}
{
 \sqrt{\int_{W(x)} (I_1(x') - \bar{I}_1)^2 \, dx'  \int_{W(x)} (I_2(x') - \bar{I}_2)^2 \, dx'}}$$

where $\bar{I}_1, \bar{I}_2$ are average intensities of $W(x)$ ($\bar{I}_2$ depends on $h$). Subtracting averages leads to invariance wrt. additive intensity changes. Dividing by the intensity variances of the window leads to invariance to multiplicative changes.

Different interpretation: If we stack the normalized intensity values of a window in one vector, $v_i = \text{vec}(I_i - \bar{I}_i), i =1,2$, then $NCC(h) = \cos \angle (v_1, v_2)$.

##### Normalized Cross Correlation for affine transformation
Affine transformation $h(x) = Ax + d$: Find optimum $\arg\max_{A,d} NCC(A, d)$. Just insert the $h$ in the above formula to get the $NCC$. Efficiently finding optima is a challenge

##### Optical Flow Estimation with Deep Neural Networks
Deep NNs can also be used for correspondence estimation and have become more popular in recent years.