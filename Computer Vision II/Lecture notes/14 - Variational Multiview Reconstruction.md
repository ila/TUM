# Chapter 10 - Variational Multiview Reconstruction

## Shape optimization
Shape optimization: find 3d shape that best corresponds to the given images. 

### Explicit shape representations
For example, *splines*: Parametric models (e.g. of curve or surfaces). Here, a linear combination of basis functions $\sum_i C_i B_i(s)$

- interpolating vs. approximating spline: must the control points be met?
- intuition: basis function specify how much impact control points have in certain regions

- for surfaces: open vs. closed surfaces (with/without boundaries) corresponds to cyclical/non-cyclical basis functions. Formula then $\sum_{ij} C_{ij} B_i(s) B_j(t)$

![[splines.png]]

### Implicit shape representations
In optimization: people have moved towards implicit representations.

Advantages:
- union/difference easy to compute
- not depending on reparametrization (e.g. in explicit polygons, you could renumber the vertices; here we get rid of these degrees of freedom)
- arbitrary topology (i.e. arbitrary number of holes)
- many shape optimization problems wrt. implicit representations lead to a convex cost function

Drawbacks: 
- more memory intensive
-  Updating an implicit representations over time is less efficient.

##### Indicator function
Implicit repr. of a closed surface $S$: $u(x) = 1$ if $x \in \text{int}(S)$, otherwise $u(x) = 0$.

##### Signed distance function
$\varphi: V \to \mathbb{R}$ s.t. $\varphi(x) = d(x, S)$ if $x \in \text{int}(S)$, otherwise $\varphi(x) = -d(x, S)$.

Example: Matlab function `bwdist`.


## Multiview Reconstruction as Shape Optimization
Goal again: several images; reconstruct geometry. Importantly, we assume that camera orientations are given.

Idea: For a voxel on the surface, projecting this into different images should give same color. => Assign each voxel $x \in V$ a value via the *photoconsistency function* $\rho(x) \in [0, 1]$. $\rho(x)$ is small if projected voxels have similar colors, large otherwise. (Actually, this measures non-photoconsistency)

Underlying assumptions:
- visibility in all images
- Lambertian (non-reflecting) surface
- textured surface

### Weighted minimal surface approach [Faugeras & Keriven 1998]
Cost function of a surface $S$:

$$\int_S \rho(S) \, ds$$

A good surface is a surface which has good photoconsistency.

Problem: global minimizer is $\emptyset$ (this has cost 0) => there is a *shrinking bias*! One "solution" is to just optimize locally (this probably eliminates $\emptyset$, but not the bias itself)

### Imposing silhouette consistency [Cremers & Kolev, PAMI 2011]
Trying to alleviate the bias problem of [[#Weighted minimal surface approach Faugeras Keriven 1998|previous approach]].

Idea: *impose our knowledge that there actually is an object; not the empty object*. Do that via a *silhouette* and impose that projections of the shape should match the silhouettes seen in the images.

$$\min_S \int_S \rho(s) \,ds ~ \text{ s.t.}~ \pi_i(S) = S_i$$

*Compute a photoconsistent surface that is also silhouette-consistent*.

##### Formulation wrt. indicator functions
Written wrt. the indicator function:

$$\min_{u: V \to \{0,1\}} \int_V \rho(x) |\nabla u(x)| \,dx$$

s.t.

$$\int_{R_{ij}} u(x) \,d R_{ij} \geq 1 \Leftrightarrow j \in S_i$$

First equation: rewrite above cost function (using a weighted total variation; apparently it's a well-known fact in optimization that it can be rewritten like this).

Second equation: The ray $R_{ij}$ should hit the object if and only if it intersects the silhouette in the image area.

![[silhouette-rays.png]]

##### Convexity
Problem: set of indicator functions $u: V \to \{0, 1\}$ is not convex.
 
Instead use $$\mathcal{D} = \bigg\{u: V \to [0,1] \bigg\lvert \int_{R_{ij}} u(x) \,d R_{ij} \geq 1 \Leftrightarrow j \in S_i \bigg\}$$

This is called the set of *silhouette-consistent configurations*. It is a convex set. Interpretation: $u(x) \in (0, 1)$ represents some uncertainty (soft constraint). Like this, we avoid a difficult combinatorial problem and use an easier continuous problem.

Thresholding: You get an energy $E_{\text{thresh}}$ from the relaxed problem $E_{\text{relaxed}}$. For the actual optimal binary solution $E_{\text{optim}}$, it holds that $$E_{\text{thresh}} \geq E_{\text{optim}} \geq E_{\text{relaxed}}$$

The paper shows: thresholding can be performed in a way s.t. silhouette consistency is preserved.

![[viking-silhouette-reconstruction.png]]

### Multi-view Texture Reconstruction [Goldlücke & Cremers, ICCV 2009]
Motivation: The things we saw above could also (and more precisely) be done with a laser scanner. What laser scanners *cannot* capture, but cameras can, are colors.

- Very simple approach: just backprojeçt from image to 3d shape to find colors. But: need multiple views to cover whole object
	-  => averaging multi-view values leads to blurring. 
	-  Stitching instead of averaging instead leads to seams.

Alternative: Variational approach [Goldlücke & Cremers, ICCV 2009]. Intuition: find a *sharp texture* s.t. after blurring and downsampling (i.e. what a real digital camera does), it matches the observations.

##### Cost function for textures
Solve:

$$\min_{T: S \to \mathbb{R}^3} \sum_i^n \int_{\Omega_i} \bigg(b \ast (T \circ \pi_i^{-1}) - \mathcal{I}\bigg)^2 \,dx
+ \lambda \int_S ||\nabla_s T|| \,ds$$

Regularization constant $\lambda$ is typically very small. $b$ represents a linear operator including blurring and downsampling ($\ast$ is a little misleading notation). The $\nabla$ is taken along the two degrees of freedom of the surface.

Advantage: this cost function is convex!

![[bunny-texture-reconstruction.png]]

Important: the color includes the shading effects.

##### Super-resolution textures
The blurring + downsampling can be undone, we get the actual sharp texture (*super-resolution textures*).

How can we hallucinate details that aren't there in the input image?
1. we know a bit how the degradation (downsample + blurring) works (we also have to know how exactly the camera blurs, else it won't work!)
2. we have many images => the more, the sharper we can get


### Space-Time Reconstruction from MV Video [Oswald & Cremers, 4DMOD 2013]

Another advantage of cameras vs. laser scanners: reconstruct actions over time filmed with multiple cameras.

![[rope-jumping-MV-video.png]]

Some interesting applications:
- video conferencing with full 3D model of speaker
- sports analysis
- free-viewpoint television

