# 02 - Shapes: Alignment, Descriptors, Similarity

### 3D Shape Alignment (*Registration*)
Often needed: e.g. in 
- 3D scanning (combine different scanned parts)
- SLAM = Simultaneous Localization + Mapping (essentially, robot navigation)
- protein structure alignment

Given: two shapes $A, B$ with overlap; register together by rigid transform s.t. distance is minimized: $\min_T \delta(A, T(B))$ for some distance measure $\delta$.

Challenges: find both *point correspondences* and *transformation*.

#### Rigid 3D Alignment: Procrustes

Goal: find best alignment, given correspondences $(x_i)_i, (y_i)_i$ (even for different shapes).

![[chair-alignment.png]]

Namely, find $(R, t)$ that minimizes $\sum_i ||R x_i + t - y_i||_2^2$. Solved as *orthogonal Procrustes problem* (1966).

Assume a coordinate system centered at the mean of the $(x_i)_i$: then the minimization term becomes

$$\min_{R, t} \sum_i ||t-y_i||_2^2 - 2\sum_i \langle R x_i, y_i \rangle$$

The first sum is minimized by $t = (\sum y_i)/N =: \bar{y}$. To minimize the second sum, define the mean-centered $X = (x_0 - \bar{x}, \dots, x_n - \bar{x})^\top$, $Y = (y_0 - \bar{y}, \dots, y_n - \bar{y})^\top$ and compute the SVD of $XY^\top$: $XY^\top = UDV^\top$.

Now replace the diagonal matrix $D$ by $S$: either by $S = I$ if $\det(U)\det(V)=1$, or $S = \text{diag}(1, \dots, 1, -1)$ otherwise. Then the minimizer $R$ is given by

$$R = USV^\top.$$

Question: We had problems with this formula in Exercise 1, and had to slightly change it based on the original paper. Is it valid as written here or not?

#### Obtaining point correspondences
##### Iterative Closest Points [Besl and McKay ’92]
Iterative algorithm:
- assume that the currenty closest points correspond
- align these correspondences (Procrustes)
- recompute closest points, repeat

Converges if the initialization is good enough. Optional steps: weight correspondences (by quality?), reject outlier correspondences before aligning.

![[iterative-closest-points.png]]

Runtime: $O(N_A \cdot N_B)$ to find closest points naively, $O(N_A)$ to compute optimal alignment and update. So $O(K N_A N_B)$ overall runtime (where $K$ is the number of iterations, $N_A$ and $N_B$ the number of points in shape $A$ and shape $B$).

Better runtime with data structures like kd-trees.

Improved correspondence selection: minimize not pairwise distance, but distance to tangent plane of the surface. This can make the point correspondences more evenly distributed. No closed-form solution anymore, but faster in practice. (No details on how to compute this).

![[icp-tangent-plane.png]]

##### Global Registration: Finding ICP Initialization
General strategy: 
1. find a good initialization
2. refine with ICP

Approaches to find an initialization:

###### Exhaustive search
Just try out "all possible transforms" (or probably, a sufficiently dense subset of all transforms). Of course, this is extremely slow.

###### Normalization with PCA
Center shapes, use PCA and align such that the principal directions match up. Works well in some cases, but can also go wrong - problems are:
- inconsistent orientation of principal directions (e.g. two cars, for one the principal direction points towards the front, for the other towards the back)
- unstable axes (e.g. cup with handle => principal direction is not the expected top-bottom axis through the cup)
- partial similarity (chair with back vs. barstool without)

###### Random sampling (RANSAC)
RANSAC: pick random pairs of points, estimate alignment (details a bit unclear).

###### Matching by invariant features
Identify feature points like corners that describe local geometry ("invariant" because they should be invariant under the transformation). Align these feature points.

![[feature-points.png]]


### Shape Descriptors
Needed for the feature point matching approach: feature descriptors that can capture the information to answer "are these points similar?".

##### Spin Images [Johnson and Hebert ’99]
To describe a point, create a *spin image* associated with its neighborhood. Neighborhood point contributions are parametrized by a) their distance to the tangent and b) their distance to the normal.

![[spin-image.png]]

##### Point Feature Histograms [Rusu et al. ’09]
Find neighbors $(q_i)_i$ of point $p$, compute histograms based on distances, normal, curvature etc.


##### Global Shape Similarity and Global Shape Descriptors
Capture models by high-dimensional shape descriptors, compare these descriptors with some similarity measure.

###### Shape Histograms
Histograms that capture how much surface area resides withing concentric shells of different radii.

Can be made a local shape descriptor restricting the shell radii.

### Non-Rigid shape matching
Goal: find correspondences that preserve the *geodesic distance* on the shapes. In other words: even if the actual shape changes, pathes along the surface of corresponding points should stay the same.

![[nonrigid-elephant.png]]

One way to compute something like this: *near isometries preserve local structure*, so use descriptors of local regions and establish mappings between these. A problem: how to choose the scale of a local region?

#### Intrinsic similarity measures
##### Gromov-Hausdorff distance
The *Hausdorff distance* between two point sets is the *maximum of all minimum distances* $\max_p \min_{q} d(p, q)$.

The *Gromov-Hausdorff distance* is the infimum of Hausdorff distances over all mappings/correspondences.


##### Heat kernel signature [Sun et al. ’09]
Heat kernel $k_t(x, y)$: amount of heat transfered from $x$ to $y$ in time $t$. Advantage: invariant under isometric deformations, works at multiple scales. Difficult to use in real-world scenarios with partial/noisy data, though.

![[heat-kernel.png]]

### Shape Search
Find shapes similar to given shape in shape search engine. Approaches: bag of geometric words, i.e. decompose shape into some parts.

Retrieve similar shapes through embedding in descriptor space (also see [[05 - Shape Generation#Joint Embedding for Retrieval|Joint Embedding for Retrieval]] and [[05 - Shape Generation#Joint embedding of 3D scans and CAD objects Dahnert et al ‘19|Joint embedding of 3D scans and CAD objects]] in Lecture 5).