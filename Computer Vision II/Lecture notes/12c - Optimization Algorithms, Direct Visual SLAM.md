## Iterative optimization algorithms

### Newton Methods
Second-order methods: take second derivatives into account.

Some intution: Fitting a parabola at the point and go to its minimum => does work well in *convex* parts of the function, does not work well in *concave* part.

We could actually decide at each point whether to use a Newton-method step or a gradient-descent step (also see [[#Levenberg-Marquardt Algorithm]]).

![[newton-method-parabolas.png]]

Fit with a quadratic term:

$$E(x) \approx E(x_t) + g^\top (x-x_t) + \frac{1}{2} (x - x_t)^\top H (x - x_t)$$

Here $g = \frac{dE}{dx}(x_t)$ is the Jacobian, and $\frac{d^2 E}{d x^2} (x_t)$ is the Hessian. The optimality condition is $\frac{dE}{dx} = g + H(x - x_t) = 0$, which yields the iteration rule

$$x_{t+1} = x_t - H^{-1} g \qquad \text{(Newton method iteration rule)}$$

An additional step size $\gamma \in (0, 1]$ can be added (more conservative):

$$x_{t+1} = x_t - \gamma H^{-1} g$$

##### Convergence Properties
Usually converges in *fewer iterations* than usual gradient descent; around each optimum there is a local neighborhod where the Newton method converges quadratically for $\gamma = 1$, if $H$ is invertible and Lipschitz continuous.

- matrix inversion not trivial on GPUs (not trivially parallelizable)
- one alternative: solve optimality condition from above iteratively
- quasi-Newton methods: approximate $H$ or $H^{-1}$ with psd matrix



### Gauss-Newton Algorithm
In the Newton method, there are the gradient $g$ and Hessian $H$:

$$g_j = 2 \sum_i r_i \frac{\partial r_i}{\partial x_j}$$
$$H_{jk} = 2 \sum_i \bigg(\frac{\partial r_i}{\partial x_j}\frac{\partial r_i}{\partial x_k} + r_i \frac{\partial^2 r_i}{\partial x_j \partial x_k} \bigg)$$

Drop the second-order term in the Hessian for the approximation:

$$H_{jk} \approx 2\sum_i \frac{\partial r_i}{\partial x_j}\frac{\partial r_i}{\partial x_k} = 2 \sum_i J_{ij} J_{ik} = 2 J^\top J$$

This approximation is guaranteed to be positive definite. Also, $g = J^\top r$. This gives the Gauss-Newton update rule:

$$x_{t+1} = x_t + \Delta := x_t - (J^\top J)^{-1} J^\top r$$

- advantage: no second derivatives, positive definiteness guaranteed
- approximation valid if the first-order part dominates, i.e. the second-order term we dropped is much smaller in magnitude. In particular, if the function is linear or almost linear


### Damped Newton and Levenberg-Marquardt 
Intuition: mixes between gradient descent and Newton method.

##### Damped Newton Algorithm
Modify Newton update rule as follows:
$$x_{t+1} = x_t - (H + \lambda I_n)^{-1} g$$
- hybrid between Newton method and gradient descent: $\lambda = 0$ => pure Newton method. If $\lambda \to \infty$ => approaches pure gradient descent (with learning rate $\frac{1}{\lambda}$).

##### Levenberg-Marquardt Algorithm
Analogously, a damped version for Gauss-Newton (Levenberg 1944):

$$x_{t+1} = x_t + \Delta := x_t - (J^\top J + \lambda I_n)^{-1} J^\top r$$

A different variant (Marquardt 1963), which is more adaptive and avoids slow convergence in small-gradient directions (and also generally slow convergence if all gradients are small):

$$x_{t+1} = x_t + \Delta := x_t - (J^\top J + \lambda \, \text{diag}(J^\top J))^{-1} J^\top r$$


## Example Applications
(see slides + video)

# Chapter 8 - Direct Approaches to Visual SLAM
SLAM = Simultaneous Localization And Mapping

## Indirect vs. Direct methods
### Classical Multi-View Reconstruction Pipeline
The classical approach tackles structure + mostion estimation (i.e. *visual SLAM*) like this:

1. Extract feature points (e.g. corners)
2. Determine correspondence of feature points across the images
	- either via local tracking (optical flow approach)
	- or random sampling of possible partners based on feature descriptor
3. Estimate camera motion
	- eight-point algorithm and/or bundle adjustment
4. Compute dense reconstruction from camera motion
	- photometric stereo approaches

### Disadvantages of classical "indirect" approaches
- disregards any information not included in the selected feature points: all remaining information in the color
- lack robustness to errors in the point correspondence

### Toward Direct Approaches
Skip feature point extraction: reconstruct dense/semi-dense scene directly from input images.

- more robust to noise: exploit all available information
- provide semi-dense reconstruction: better than sparse point cloud by eight-point algorithm/bundle adjustment!
- even faster: no feature point extraction

![[feature-based-vs-direct.png]]

Idea: "find geometry of the scene in a way such that the colors in the images are consistent".