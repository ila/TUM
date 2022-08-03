# 4. Dimensionality Reduction
%% #Lecture 15, 04.07. [[Foundations Section 4 printout.pdf]] %%
## 4.1 The Johnson-Lindenstrauss Lemma

> **Theorem 13.1** (*JL Lemma*)
> For $\epsilon \in (0, 1)$ and $n \in \mathbb N$, let $k \in \mathbb N$ such that $$k \geq C(\epsilon^{-2}/2 - \epsilon^3/3)^{-1} \ln n$$
> for some $\beta \geq 2$ (where do we use $\beta$? Does he mean $C$? #TODO)
> Then for any set $\mathcal P$ of $n$ points in $\mathbb R^d$, there is a map $\mathbb R^d \to \mathbb R^k$ such that
> $$\forall v,w \in \mathcal P: (1-\epsilon)||v-w||_2^2 \leq ||f(v) - f(w)||_2^2 \leq (1+\epsilon)||v-w||_2^2$$
> $f$ can be chosen as a linear map.

^9f9398

>**Theorem 13.2** (*Probabilistic JL Lemma*)
> Let $\epsilon, n, k$ and $\beta \geq 2$ be such that
> $$k \geq 2\beta(\epsilon^{-2}/2 - \epsilon^3/3)^{-1} \ln n$$
> ...or $k \geq 2 \beta \epsilon^{-2} \ln n$ for Gaussians.
> Then there is a matrix-valued random variable $\Phi$ with values in $\mathbb R^{k \times d}$ such that for any set of $n$ points $\mathcal P$ in $\mathbb R^d$, it holds that
> $$(1-\epsilon) ||v-w||_2^2 \leq ||\Phi v - \Phi w||_2^2 \leq (1+\epsilon) ||v-w||_w^2$$
> with probability $1-(n^{2-\beta} - n^{1-\beta})$.

^ecd655

Remark on probability: $\beta \gg 2$ leads to a favorable probability of success.

##### Some intuition on the numbers
Consider a point cloud of $n=10^7$ points of dimension $d=10^6$, we can project them to dimension $k = O(\epsilon^{-2} \log 10^6) = O(6 \epsilon^{-2})$, preserving the distances with a maximal distortion of $O(\epsilon)$. For example for $\epsilon = 0.1$, we get $k$ of order of a thousand (3-4 orders of magnitude in reduction!).

#### Towards proving the JL Lemma
##### JL for Gaussian random matrices
Consider $(\Phi_{ij})_{i,j}$ with $\Phi_{ij} \sim \mathcal N(0, 1)$. We aim to show that $\tfrac{1}{\sqrt{k}} \Phi$ is a JL embedding with high probability. We then proceed using the union bound.

> **Lemma 13.3**
> Let $x \in \mathbb R^d$, assume $\Phi \in \mathbb R^{k\times d}$ has entries sampled from a standard Guassian. Then
> $$P\left( \left| ||\tfrac{1}{\sqrt k} \Phi_x ||_2^2 - ||x||_2^2 \right| > \epsilon ||x||_2^2 \right) \leq 2\exp(-\tfrac{\epsilon^2-\epsilon^3k}{4})$$ or, equivalently, #TODO 

^06b03b

###### #Proof of [[#^06b03b|Lemma 13.3]]
Step 1: show that for for any $x$, $\mathbb E\left( ||\tfrac{1}{\sqrt k} \Phi_x ||_2^2 \right) = ||x||_2^2$.
$$\mathbb ||1/\sqrt{k} \Phi x||_2^2
= (1/k) \sum_{j=1}^k \mathbb E \left(\sum_{\ell=1}^d \Phi_{j\ell}x_\ell\right)\left(\sum_{\ell'=1}^d \Phi_{j\ell'}x_{\ell'}\right)$$
... where things cancel due to orthonormality in expectation (right?) and we get $\dots = ||x||_2^2$. Namely, $\mathbb E(\Phi_{j\ell} \Phi_{j\ell'}) = \delta_{\ell \ell'}$.

Step 2: we note that $||\tfrac{1}{\sqrt{k}} \Phi x||_2^2 = \tfrac{1}{k} \sum_{j=1}^k (\Phi x)_j^2$. By rotation invariance, $Z_j = \tfrac{(\Phi x)_j}{||x||_2} \sim \mathcal N(0,1)$.

Step 3: apply Markov's ineq. By Markov, we get 
$$P\left( \sum_{j=1}^k Z_j^2 > (1+\epsilon)k \right)
= P\left( \exp\left(\theta \sum_{j=1}^k Z_j^2\right) > \exp(\theta (1+\epsilon) k)  \right)$$
$$\leq e^{-(1+\epsilon) k \theta} \mathbb E\left(\exp\left(\theta \sum_{j=1}^k Z_j^2\right)\right)
= e^{-(1+\epsilon) k \theta}\prod_{j=1}^k \mathbb E\left( \exp(\theta Z_j^2) \right)$$
Compute the expectation as $$1/\sqrt{2\pi}\int \exp(\theta g^2 - g^2/2) \,dg = 1/\sqrt{2\pi} \int \exp(-g^2/2(1-2\theta))\,dg$$
$$1/\sqrt{2\pi}\int\exp(-t^2/2)\,dt \frac{1}{1-2\theta} = \frac{1}{1-2\theta}$$
(provided that $\theta \in (0, \tfrac{1}{2}]$). Hence the main computation continues as
$$\dots = e^{-(1+\epsilon) k \theta} \cdot \left(\tfrac{1}{1-2\theta}\right)^{k/2}$$
Step 4: choose $\theta$.  Choosing $\theta = \tfrac{\epsilon}{2(1+\epsilon)}$ minimizes the above expression and also is less than $1/2$. We have
$$P\left( \sum_{j=1}^k Z_j^2 > (1+\epsilon)k \right) \leq \left( (1+\epsilon)e^{-\epsilon} \right)^{\tfrac{k}{2}} \leq \exp(-\tfrac{k}{4}(\epsilon^2-\epsilon^3))$$
using the estimate $1+\epsilon \leq \exp(\epsilon-(\epsilon^2 - \epsilon^3/2))$ (which can be proved e.g. by applying $\log$ to both sides and a well-known power series).

In a similar way, one can prove the the other direction $$P\left( \sum_{j=1}^k Z_j^2 < (1-\epsilon)k \right) \leq \exp(-\tfrac{k}{4}(\epsilon^2-\epsilon^3))$$
Finally, step 5: combining the pieces.
$$P\left( ||\tfrac{1}{\sqrt k} \Phi x||_2^2 > (1+\epsilon)||x||_2^2 \right) = P\left(\sum_{j=1}^k Z_j^2 > (1+\epsilon)k \right) \leq \exp(-\tfrac{k}{4} (\epsilon^2-\epsilon^3)$$
and similarly, $$P\left( ||\tfrac{1}{\sqrt k} \Phi x||_2^2 < (1-\epsilon)||x||_2^2 \right) = P\left(\sum_{j=1}^k Z_j^2 < (1-\epsilon)k \right) \leq \exp(-\tfrac{k}{4} (\epsilon^2-\epsilon^3)$$
We conclude by combining the two cases with the union bound.

#Exam especially the part about rotation invariance could be relevant for the exam.

###### #Proof of the JL Lemma [[#^ecd655|Theorem 13.2]]
- Choose $f$ as a random linear function $f(x) = \tfrac{1}{\sqrt k} \Phi x$, where $\Phi \in \mathbb R^{k\times d}$ has entries sampled from $\mathcal N(0, 1)$.
- There are $\binom{n}{2}$ pairs $(v, w)$ of points from $\mathcal P$.
- Hence we can apply the union bound: the probability that some pair $(v, w) \in \mathcal P^2$ will fail the inequality $(1-\epsilon)||v-w||_2^2 \leq ||\Phi v - \Phi w||_2^2 \leq (1+\epsilon)||v-w||_2^2$ is given by the probability $$P\left(\exists v, w \in \mathcal P: \left| \tfrac{1}{k} ||\Phi v - \Phi w||_2^2 - ||v-w||_2^2 \right| \geq \epsilon ||v-w||_2^2\right)$$ $$\dots \leq 2 \binom{n}{2} e^{-(\epsilon^2 - \epsilon^3)\tfrac{k}{4}} = 2 \binom{n}{2} n^{-\beta(1-\epsilon)} = n^{2-\beta(1-\epsilon)} - n^{1-\beta(1-\epsilon)}$$ (where in the last steps, the $\log$ etc. from the theorem statement comes into play)
- Hence with probability $1-(n^{2-\beta(1-\epsilon)} - n^{1-\beta(1-\epsilon)})$ we have $(1-\epsilon) ||v-w||_2^2 \leq ||f(v) - f(w)||_2^2 \leq (1+\epsilon)||v-w||_2^2$ for all $v, w \in \mathcal P$.

### Nothing special about Gaussian matrices
If we choose the entries in $\Phi$ to be $U(\{-1, +1\})$-distributed, we get comparable results. Full lemma statement: see *Lemma 13.4* on the slides (not gone into detail here).

### Scalar Product Preservation
%% #Lecture 16, 05.07. [[Foundations Section 4 printout.pdf]] %%
Under the transformation of a JL random matrix, scalar products of unit vectors are approximately preserved.

> **Corollary 13.5** (*Scalar product preservation*)
> Let $u, v$ be two points in the real $d$-dim. unit ball $B(1)$ and $\Phi \in \mathbb R^{k\times d}$ a matrix with random entries that satisfies a JL-like inequality,
> $$P\left( (1-\epsilon)||x||_2^2 \leq ||\tfrac{1}{\sqrt k} \Phi x||_2^2 \leq (1+\epsilon) ||x||_2^2\right) \geq 1-2e^{-(\epsilon^2-\epsilon^3)k/4}$$
> (e.g. if $\Phi$ is a matrix with Gaussian random entries, or with entries $U(\{-1, +1\})$-distributed).
> Then with probability at least $1 - 4e^{-(\epsilon^2 - \epsilon^3)k/4}$, for $u, v \in \mathcal P$, $$|\langle u, v \rangle - \langle \Phi u, \Phi v \rangle | \leq \epsilon$$

^99bd43
###### #Proof of [[#^99bd43|Corollary 13.5]]
We have
$$4 \langle \Phi u, \Phi v\rangle = ||\Phi (u+v)||_2^2 - ||\Phi(u-v)||_2^2
\geq (1-\epsilon)||u+v||_2^2 - (1+\epsilon) ||u-v||_2^2$$$$\dots = 4 \langle u, v \rangle - 2 \epsilon (||u||_2^2 + ||v||_2^2)
\geq 4 \langle u, v \rangle - 4 \epsilon$$
and the other direction is anologous.