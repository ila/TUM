# About these notes
These notes are my lecture notes on the Algorithms for Uncertainty Quantification course held in the summer term 2021 by Dr. Tobias Neckel. The notes are based on the course slides [^1]. Images are taken from the course slides.

The notes are written in [Obsidian markdown](https://obsidian.md/) and are best viewed in Obsidian.

[^1]: Tobias Neckel - Algorithms for Uncertainty Quantification, lecture slides, summer term 2021

I left out the first two lectures because these only contained an overview of UQ and a repetition of probability theory.

# 03 - Standard Monte Carlo Sampling

### Sampling methods in UQ
Sampling methods in general: from a statistical population, select a subset to estimate characteristics of the whole population. Up to specification: the population, the sampling method, the sampling size.

In UQ: Population = domain in $\mathbb{R}^n$.

### Monte Carlo sampling
Monte Carlo sampling: generate samples from a probability distribution via a pseudo-random number generator (*PRNG*) and perform determinstic computations using these samples.

##### Approximating $\pi$
Sample uniformly from square with sidelength $a$, for each point check if inside the circle; Get $A_{rec}/A_{circ} = \frac{a^2}{\pi(a/2)^2}$ and $\pi = 4 \frac{A_{circ}}{A_{rec}}$.

Example: after 10000 samples, we get $\pi \approx 3.1508$.

##### Formalization of Monte Carlo sampling
Assume $f: [0, 1] \to \mathbb{R}$ is a continuous function and want to evaluate $I = \int_0^1 f(x)\,dx$. But an antiderivative of $f$ is not available to us.

Notice that $I = E(f(x))$ with $x \sim \mathcal{U}(0, 1)$: So we approximate $E(f(x))$ via sampling as

$$I \approx \hat{I}_f = \frac{1}{N} \sum_{i=1}^N f(U_i), \qquad U_i \sim \mathcal{U}(0, 1)$$

### Error of Monte Carlo integration
$\hat{I}_f$ is a random variable! The error, i.e. deviation from the real integral, can be expressed by the *root mean squared error* (*RMSE*)

$$RMSE = \sqrt{E((I - \hat{I}_f)^2)}$$

The RMSE is exactly the standard deviation of the estimator $\hat{I}_f$, and related to the standard deviation of $f(U), U \sim \mathcal{U}(0,1)$ via

$$RMSE = \frac{\sigma_f}{\sqrt{N}} \approx \frac{\hat{\sigma}_f}{\sqrt{N}},
\quad \hat{\sigma}_f^2 = \frac{1}{N-1} \sum_{i=1}^N (f(U_i) - \hat{I}_f)^2$$

### Monte Carlo sampling in UQ
Goal in UQ: feed input distribution through model to get output distribution, from output distribution compute quantity of interest.

MC sampling can help with this: sample from the input distribution, compute expectation/variance from the outputs via MC integration. 

$$E(y) = \bar{Y} = \frac{1}{N} \sum y_i$$
$$Var(y) = \bar{S}^2 = \frac{1}{N-1} \sum (y_i - \bar{Y})^2$$

### Advantages and disadvantages of Monte Carlo methods

MC methods are easy, robust to the distribution/model used, "embarrassingly parallel" and have a convergence rate independent of the input dimension.

However the convergence rate is $O(1/\sqrt{N})$, which is very slow.

### Example: Damped oscillator with MC

![[damped-linear-osc.png]]

Via MC: find expected value of $y(t_0), t_0 = 15s$.