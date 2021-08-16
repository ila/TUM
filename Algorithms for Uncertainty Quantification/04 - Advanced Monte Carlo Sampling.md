# 04 - Advanced Monte Carlo Sampling

### Variance reduction techniques
To reduce RMSE, we need to reduce the variance of the estimator.

##### Control variates
Assume we have an auxiliary function $g$ with known $E(g)$. The random variables $I_f^N$ and $I_g^N$ are the MC estimators for $\int_0^1 f(x) \,dx$ and $\int_0^1 g(x) \,dx$, respectively.

Then the control variate

$$I_{CV} = I_f^N + \alpha (E(g) - I_g^N)$$

is an unbiased estimator for $E(f)$ since the second term has zero mean. The optimal $\alpha$ is 

$$\alpha = \frac{\text{Cov}(I_f^N, I_g^N)}{\text{Var}(I_g^N)} = \frac{\sigma_f \rho_{fg}}{\sigma_g}$$

where $\rho_{fg} = \frac{\text{Cov}(f, g)}{\sigma_f, \sigma_g}$ is the Pearson correlation coefficient. With this optimal choice for $\alpha$, the variance of $I_{CV}$ is 

$$\text{Var}(I_{CV}) = (1 - \rho_{fg}^2) \frac{\text{Var}(f)}{N},$$

so if $f, g$ are (even negatively) correlated, this yields a variance reduction.

##### Importance sampling
Setting again: estimate an expected value (now more generally with respect to some density $p$) by sampling according to $p$ and estimating $I_f^N = (\sum f(x_i))/N$.

Assume we cannot sample from $p$ directly, but from another density $q$ s.t. $\{f\cdot p > 0\} \subseteq \{ q > 0\}$ . Intuitively, if we find an auxiliary $q$ that matches $p$ closely, that's better than if $q$ is just e.g. the uniform density. Define $w = p / q$: Then

$$\int_D f(x) p(x) \,dx = \int_D f(x) w(x) q(x) \,dx.$$

This motivates the *importance sampling* estimator:

$$I_{IS} = \frac{1}{N} \sum_i f(x_i) w(x_i), \qquad w(x_i) = p(x_i)/q(x_i)$$

The $w(x_i)$ are called *importance weights*. The variance  is given as

$$\text{Var}(I_{IS}) = \frac{1}{N} \bigg( \int_D \frac{(f(x)p(x))^2}{q(x)}\,dx - E(f)^2 \bigg)$$


### Alternative RNGs (Quasi Monte Carlo)
*Quasi Monte Carlo*: Instead of real random sequences, use pseudo-random sequences (behave/look somewhat like random sequences, but are deterministic). Examples:

- Fibonacci generators
- Latin hypercube sampling
- Sobol sequences
- Halton sequences

##### Koksma-Hlawa inequality/Low discrepancy sequences
We use low-discrepancy sequences. Define the *discrepancy* of a sequence up to term $N$ as

$$D_N = \sup_{A = [a, b] \subseteq [0, 1]} \left\lvert \frac{|A \cap \{x_1,\dots,x_n\}|}{N} - \text{vol}(A) \right\rvert$$

Let $V(f)$ be the *variation* of $f$. Then the *Koksma-Hlawka inequality* states that

$$|I - \hat{I}_f| \leq V(f) D_N \quad \text{(Koksma-Hlawka inequality)}$$

So the error can be reduced by reducing $D_N$, i.e. producing sequences that are "well-spaced" (even better than random?).

Now: error goes from $O(1/\sqrt{N})$ (usual MC) to $O(\log(N)^d / N)$

![[halton-sobol.png]]

##### Halton sequences
Given: prime $p$. The Halton sequence is defined iteratively as follows

- Break $[0, 1]$ in $p$ subintervals: list the partition points
- Break each subinterval into $p$ subintervals, list all new partition points in the order (new partition points that are first in their subinterval), (new partition points that are second in their interval), etc.
- repeat

![[halton-sequence.png]]

For multi-dimensional Halton sequence, take pointwise cartesian product of 1-dimensional Halton sequences with pairwise different primes $p_i$.

The Halton sequence mimics a uniform distribution, but can of course be transformed by the usual technique to mimic different distributions: $F_S^{-1}(U) \sim S$.


### Damped oscillator
Quasi MC can slightly improve the variance of the $y(t_0), t_0=15$ estimation for $c \sim \mathcal{U}(0.08, 0.12)$, but not much.

### Outlook: Multifidelity/Multilevel Monte Carlo

Multifidelity MC: Low-fidelity (i.e. inexpensive to compute, but inaccurate) models are used as control variates: e.g. simplify by interpolating, projecting to lower dimensions, simulate with simplified physics. A standard algorithm optimally distributes work among available models for a given computational budget.

Multilevel MC: special case for the low fidelity comes from having a coarser grid discretization.