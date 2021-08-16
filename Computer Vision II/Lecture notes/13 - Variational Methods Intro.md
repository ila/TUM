# Chapter 9 - Introduction to Variational Methods
Motivation: Optimization-Based Methods let you *specify what you want* from a solution directly. This can be much better than "cooking up" an algorithm using some made-up heuristics.

Variational Methods are especially suited for *infinite-dimensional problems* and *spatially continuous representations*. Applications include
- image denoising / restoration
- image segmentation
- motion estimation / optical flow
- spatially dense multi-view construction
- tracking

They are called *variational* because some parameters are varied to decrease a cost function.

### Advantages of Variational Methods
Example why heuristic "cook recipe" methods are problematic: *Canny edge detector*. Modifying some step in the middle basically means that the rest also needs to be re-engineered.

In constrast, Variational Methods (or in general, optimization methods)
- allow mathematical analysis of the cost function (uniqueness/existence of solutions?)
- make all modeling assumptions transparent
- have fewer tuning parameters with clearer effects
- are easily fused together (just add together cost functions)


### Variational Image Denoising
Input: Noisy grayvalue input image $f: \Omega \to \mathbb{R}$ that is assumed to arise from some true image by adding noise. Goal: find denoised $u$ s.t.
- $u$ is as similar as possible as $f$
- $u$ should be *spatially smooth* (i.e. noise-free)

=> $E(u) = E_\text{data}(u, f) + E_\text{smoothness}(u)$

Define $E_\text{data} = ||u - f||_2^2$, $E_\text{smoothness}(u) = ||\nabla u||_2^2$ (both function norms, i.e. integrals over all image points $x$). Result:

$$E(u) = ||u - f||_2^2 + \lambda ||\nabla u||_2^2$$

Choosing $\lambda$ comes down to: how much do you trust your prior assumptions about the world, and how much do you trust your observations of the world.

Since the error functional is convex, we can get a unique solution. The descent methods from the next section will converge towards a smoothed image which is the minimizer.

![[lena-smoothed.png]]

To allow edges to be not smoothed too much: don't square the gradient penalty. Intuitively, little variations are still smoothed out, but "semantically important information" (e.g. outline of hat) survive.

![[lena-smoothed-L1.png]]

A powerful algorithm: Perona-Malik Algorithm (Perona & Malik, 1990) for discontinuity-preserving smoothing (smoothes image, but preserves details). This was PDE-based, but formulated as variational method in Rudin, Osher & Fatemi 1992.

### Minimizing Error *Functionals*

How to solve an error functional as seen above? Here $E$ assigns an error to a *function $u$* (the solution space has *infinite dimensions*, $E$ is a *functional*). How to minimize over infinite dimensions?

##### Euler-Lagrange equation

**Theorem** (necessary condition for minimizers of functionals): Assume a differentiable functional of the form $E(u) = \int \mathcal{L}(u, u')\,dx$. If $u$ is a minimizer, the associated *Euler-Lagrange* equation holds:
$$\frac{dE}{du} = \frac{\partial\mathcal{L}}{\partial u} - \frac{d}{dx}\frac{\partial\mathcal{L}}{\partial u'} = 0$$

General goal of variational methods: determine solutions for Euler-Lagrange equation. This is difficult for general non-convex functionals.

##### Descent methods
Descent methods can be used to solve the Euler-Lagrange equation. For example, gradient descent: Start at $u(x, t=0)$, then go in the direction of the negative gradient.

For the class of functionals from above, the gradient descent is given by the PDE
$$
 \begin{cases}
u(x,0) &= u_0(x) \\
 \frac{\partial u(x, t)}{\partial t} &= -\frac{dE}{du} = -\frac{\partial\mathcal{L}}{\partial u} + \frac{d}{dx}\frac{\partial\mathcal{L}}{\partial u'} = 0
 \end{cases}
$$

Just as an example: For the specific functional $\mathcal{L}(u, u') = \frac{1}{2}(u(x) - f(x))^2 + \frac{\lambda}{2}(u'(x))^2$, this translates to

$$\frac{\partial u}{\partial t} = (f - u) + \lambda \frac{\partial^2 u}{\partial x^2}$$

If the gradient descent converges, i.e. $\frac{\partial u}{\partial t} = 0$, we have found a solution to the Euler-Lagrange equation.

*Comment: Variational Methods and PDEs typically go hand in hand, i.e. people who work on variational methods often also work on PDEs*

