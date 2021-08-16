# 12 Inverse UQ
### Motivation for Bayesian Inversion
We always assumed we know the distribution of stochastic input parameters $\theta$. How do we get this distribution?

![[bayesian-inversion-setup.png]]

=> goal: some kind of Bayesian updating based on output data to compute posterior distribution of input parameter.

### Observational Data
Observations are usually *sparse* (think e.g. of earthquakes).

A observation $y$ is a random variable realization given as
$$y = f(\theta^{\text{true}}, \xi) + \eta$$
with independent noise $\eta \sim N(0, \Sigma)$ and an input vector $\xi$.

Idea now: we can't find the actual parameter $\theta^{\text{true}}$ (ill-posed problem), but we want to narrow down the uncertainty about it and model it as the input distribution.

### Bayes' Theorem for conditional densities
Let $x_1, x_2$ be RVs with joint pdf $\text{pdf}$ and marginal pdfs $\text{mpdf}_1, \text{mpdf}_2$.

Then a conditional distribution pdf is given by

$$\text{cpdf}_{1|2}(x_1|x_2 = b) = \frac{\text{pdf}(x_1, b)}{\text{mpdf}_2(b)}$$

Then Bayes' Theorem is


$$\text{cpdf}_{1|2}(\,\cdot\,| x_2 = b) = \frac{\text{cpdf}_{2|1}(b|x_1 = \,\cdot\,)\, \text{mpdf}_1(x_1)}{\text{mpdf}_2(b)}$$

With terms

![[bayes-theorem.png]]
* prior: prior knowledge about $x_1$
* likelihood: pdf of observed data given $x_1$
* posterior: distribution of $x_1$ knowing the data $x_2 = b$
* evidence: normalising constant

##### Bayesian Thm. formulation for Inversion
$$\pi^y(\theta) = \frac{L_y(\theta) \pi_0(\theta)}{Z(y)}$$
where $\pi_0(\theta)$ is the prior about $\theta$, $L_y(\theta)$ the likelihood of the data $y$ under $\theta$, $\pi^y(\theta)$ the posterior of $\theta$ given $y$, and $Z(y)$ the evidence.

##### Likelihood function under Gaussian noise
Due to noise model: $y - f(\theta^{\text{true}}) \sim N(0, \Sigma)$. Model $L_y(\theta)$ via *data misfit* $\Phi(\theta, y)$, s.t. it essentially represents a Gaussian density.

$$L_y(\theta) := \exp(-\Phi(\theta, y))$$
$$\Phi(\theta, y) := \tfrac{1}{2}||\Sigma_\eta^{-1/2}(y - f(\theta))||_2^2 = \tfrac{1}{2}||\eta||_{\Sigma_\eta^{-1}}$$


### Computing Bayesian Inversion
Approaches:
- sampling-based: sample from posterior $\pi^y$
- deterministic: quadrature rule to approximate $\pi^y$

#### MCMC (Markow-Chain Monte Carlo)
Sampling-based methods. Most promiment: Metropolis-Hastings algorithm.

Base idea of MCMC: construct *ergodic Markov chain* $(\theta_n)_n$ which is stationary wrt. $\pi^y$. Then samples $\theta_n \sim \pi^y$ for large $n$.

Markov property means: we can compute the next sample based only on the previous sample.

##### Metropolis-Hastings Algorithm
Intuition: draw sample from easy distribution (*proposal density* $q(\cdot | \theta^{n-1}))$). Correct sample s.t. it is distributed more like the posterior $\pi_y$. Assess posterior via Bayes theorem.

```python
# L evaluates L_y(theta) (likelihood)
# pi0 evaluates pi0(theta) (prior)
# q (proposal distribution) must support
#	- evaluating q(theta1 | theta2)
#	- sampling from q( . | theta2)
def metropolisHastings(L, pi0, q, M):
	theta[0] = 0 # or something more clever
	for i in range(1, M+1):
		# draw sample from (simple) proposal distribution
		thetaNew = q(.|theta[i-1]).sample()
		
		# compute acceptance probability
		alpha = acceptProb(theta[i-1], thetaNew, L, pi0, q)
		
		# update theta with prob. alpha
		theta[i] = thetaNew if random() < alpha else theta[i-1]

# t1: old theta. t2: new theta.
def acceptProb(t1, t2, L, pi0, q):
	posterior_t1 = L(t1) * pi0(t1)
	posterior_t2 = L(t2) * pi0(t2)
	
	# big <=> new theta explains data better
	posteriorRatio = posterior_t2 / posterior_t1
	
	# big <=> easier to get from t2 to t1 
	# than the other way around
	proposalLikelihoodRatio = q(t1 | t2) / q(t2 | t1)
	
	return min(1, proposalLikelihoodRatio * posteriorRatio)
```

 
Choice of $q$: Gaussian kernel
$$q(m, p) = \tfrac{1}{\sqrt{2\pi \gamma^2}} \exp(- \tfrac{1}{2 \gamma^2} ||m-p||^2 )$$

Then we get the *Random Walk Metropolis-Hastings algorithm*. The parameter $\gamma$ is the variance of $\theta' \sim q( \cdot | \theta_{i-1})$.

##### Effective sample size of MCMC methods
MCMC output samples are correlated; we can think of it as needing more samples to obtain the same accuracy in estimating $E(f)$ via Monte-Carlo as we would need for independent samples.

The *effective sample size* (ESS) $M^\ast$ is defined s.t. 

$$\text{Var}(\tfrac{1}{M}\sum_i^M f(\theta_i)) = \frac{\text{Var}(f)}{M^\ast}$$

is fulfilled. Intuition: shows how many independent samples we "effectively get" from the $M$ MCMC samples.

![[effective-sample-size.png]]

### MCMC Demos

##### First example (Thanh)
Full tutorial: http://users.oden.utexas.edu/~tanbui/teaching/Bayesian/BayesianMCMC.m

Note on this example: we know a lot of things beforehand here, so it's somewhat of a toy example.

![[metropolis-hastings-demo1.png]]

##### Interactive visualization example
Interactive visualizations under http://chifeng.scripts.mit.edu/stuff/mcmc-demo/ (e.g. "banana" distribution).

QUESTION: If we are in a point we don't easily get out of for a long time, isn't there a bias in the final density towards such points?


##### 1D Groundwater flow with uncertain source
More realistic demos where we actually incorporate data.

A PDE system is given, solved by FEM. 

### Variants of Bayesian Inversion
- Application variants:
	- *enhance* input pdf with data (keeping stochastic info)
	- classical calibration (throwing away stochastic info)
- Which uncertainties to assess
	- classical model parameters
	- additional sources of uncertainty
- Technical variants
	- different likelihood functions
	- different algorithms to assess posterior (sampling vs. deterministic)

##### Example: additional sources of uncertainty
Not just the model parameter uncertainty; but also uncertainty in 
- input data
- calibration data

=> incorporate all sources into one Bayesian inversion problem.

### Literature
- Smith book, Chapter 8
- Tan Bui-Thanh: *A Gentle Tutorial on Statistical Inversion using the Bayesian Paradigm*, 2012
- Andrew Stuart: *Inverse problems: A Bayesian perspective*, Review paper, 2010
- Jari Kaipio and Erkki Somersalo: *Statistical and Computational Inverse Problems*. In-depth book, 2005.

Further sources:
- SIAM summer school on inverse problems: http://g2s3.com/labs
- Course *Bayesian Strategies for Inverse Problems* (Mechanical Engineering @ TUM)
- Machine Learning courses which cover Bayesian strategies