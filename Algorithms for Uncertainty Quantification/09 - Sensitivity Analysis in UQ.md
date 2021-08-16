# 09 Global Sensitivity Analysis in UQ

##### Motivation
We want to know: 
- how sensitive is the stochastic output $Y(\omega)$ to $\omega$?
- how much does each dimension $\omega_i$ of the stochastic input contribute relatively? (Which uncertain parameter contributes the most?)
=> need "measure" of output uncertainty, and compute "sensitivities" of $Y$ to $\omega_i$


This is needed
- to make sure the model is robust (small input change in one $\omega_i$ should not turn the airplane upside-down)
- for stochastic dimensionality reduction: can make certain $\omega_i$ deterministic

### Local vs. Global Sensitivity Analysis
##### Local Sensitivity Analysis
Some input value is fixed, and we assume local perturbations around this value => use gradients. May be computationally expensive

Computationally expensive: function evaluations + gradient calculations at a lot of different points.

##### Global Sensitivity Analysis
Analysis should be valid for the whole input domain.

Needs some measure of uncertainty to quantify contribution of each input to output uncertainty: Here, we use variance as this measure.

### Variance-based Global Sensitivity Analysis
##### ANOVA deomposition
*ANOVA*: "Analysis of variance"

Assume for simplicity: $\omega_i$ are iid, let $\Gamma^d := \text{supp}(\Omega)$. (Note: identically distributed just simplifies the notation. Independence is crucial though: things get more complicated with non-independent variables.)

ANOVA decomposition: decompose $f(t, \omega)$ into sum of functions that each only depend on a subset of the $\omega_i$.

$$f(t, \omega) = f_0(t) + \sum_{i=1}^d f_i(t, \omega_i)
+ \sum_{1 \leq i < j \leq d} f_{ij}(t, \omega_i, \omega_j)
+ \dots + f_{12\dots d}(t, \omega_1, \dots, \omega_d)$$
This decomposition is obviously non-unique, but can be made unique by imposing an orthogonality constraint:

$$\forall \{i_1,\dots,i_n\} \neq \{i_1,\dots,i_m\}:
\int_{\Gamma^d} f_{i_1,\dots,i_n}(t, \omega_{i_1},\dots, \omega_{i_n}) f_{i_1,\dots,i_m}(t, \omega_{i_1},\dots, \omega_{i_m}) \, d\omega = 0$$

QUESTION: why is this  orthogonality assumption reasonable? I suppose we want to make the lower-dimensional part take "as much" of the function as possible, but I don't know how to formalize this and if this is what the orthogonality constraint does.

QUESTION: is the integral used here a probability integral, i.e. w.r.t. the distribution of $\omega$? (usually we included the densities in the notation in this case). => confirmed in Q&A: Yes, it is. A more correct notation would be $\int \dots \,dP(\omega)$.

##### Computation of ANOVA terms
Idea: to get ANOVA term $f_{\mathcal{I}}$ for some indices $\mathcal{I}$, integrate out the other indices and subtract the remaining unwanted terms that were not integrated out. Analogy: this is similar to Gram-Schmidt orthogonalization.

$$f_i(t, \omega_i) = \int_{\Gamma^{d-1}} f(t, \omega) \, d\omega_{\sim i} - f_0(t)$$
$$f_{ij}(t, \omega_i, \omega_j) = \int_{\Gamma^{d-2}} f(t, \omega) \, d\omega_{\sim ij} - f_i(t, \omega_i) - f_j(t, \omega_j) - f_0(t)$$
$$\vdots$$

Standard approach for evaluation: Monte Carlo => Expensive! We'll keep the theoretical approach, but without evaluating all those integrals. 


##### Total Variance using ANOVA
For the variance computation, we can exploit the orthogonality.

$E(f^2) - E(f)^2 = \int (f_0 + \dots + f_{\dots})^2 d \omega - E(f)^2$, where in the integral, all *mixed* terms  disappear due to the orthogonality.

Denote $$D_{i_1\dots i_s}(t) = \int_{\Gamma^s} f_{i_{1\dots s}} (t, \omega_{i_{1\dots s}})^2 \,d\omega_{ i_{1\dots s}}$$
Then
$$\text{Var}(f(t, \omega)) = \sum_{i=1}^d D_i(t)
+ \sum_{1 \leq i < j \leq d} D_{ij}(t) + \dots + D_{1\dots d}(t)$$

##### Sobol Indices
The *local* or *partial Sobol index* of an index set $\mathcal{I} \subseteq [d]$ is the ratio between $\mathcal{I}$'s contribution to the total variance and the total variance.

$$S_{i_1,\dots,i_s}(t) = \frac{D_{i_1,\dots,i_s}(t)}{\text{Var}(f(t, \omega))}$$

The *total Sobol index* of a single index $i$ sums all partial sobol indices that the index $i$ plays a role in.

$$S_i^T(t) = \sum_{\mathcal{I} \ni i} S_{\mathcal{I}}(t)$$

##### Computing Sobol Indices
Issue: we would need to compute the coefficients with MCS. We are trying to avoid this by PCE etc., so we of course want to avoid using MCS again "through the back door".

Instead: derive Sobol indices from PCE coefficients (both ANOVA and PCE come from orthogonal sums).

The terms $D_i(t)$ can actually be written in terms of the PCE coefficients $\hat{f}_{\mathbf{n}}$, $\mathbf{n} \in \mathbb{N}^d$:

$$D_i(t) = \sum_{n \in A_i} \hat{f}_{\mathbf{n}}(t)$$

where for first-order indices (i.e. computing $S_i^T$)

$$A_i = \{ \mathbf{n} \in \mathbb{N}^d \mid \forall j \neq i: \mathbf{n}_j = 0 \}$$

and for higher-order contributions (i.e. computing $S_i$)

$$A_i = \{ \mathbf{n} \in \mathbb{N}^d \mid \mathbf{n}_i > 0 \}$$

QUESTION: What does the distinction between "first-order indices" and "higher-order contributions" mean? $i$ seems to be a single index in both cases... Is there also a way to compute higher-order $D_{\mathcal{I}}$? This would also be needed to compute $\text{Var}$, and therefore $S_i(t)$.


### Sensitivity Analysis of Damped Linear Oscillator
5 uncertain parameters, full (7776 nodes) and sparse (2203 nodes) Gaussian grids.

![[dampedOsc-Sensitivity.png]]

Sensitivity analyses with both first-order and total Sobol indices, and both full and sparse grid, look very similar.

The output (at this specific time) is most sensitive to the initial velocity $y_1$ and the forcing frequency $\omega_O$, others are relatively small.

![[dampedOsc-Sensitivity-over-time.png]]

The previous plots were taken at the time $t=15$. However the plots show that at other points, the contribution of the stiffness becomes much more important.

### Literature
Chapters 14 and 15 in the R. C. Smith book (Local sensitivity analysis in chapter 14, global sensitivity analysis in chapter 15).