# About these notes
These notes are my lecture notes on the Machine Learning for 3D Geometry course held in the summer term 2021 by Prof. Angela Dai. The notes are based on the course slides [^1]. Images are taken from the course slides and related research papers which are referenced in the notes.

The notes are written in [Obsidian markdown](https://obsidian.md/) and are best viewed in Obsidian.

[^1]: Angela Dai, Christian Diller, Yawar Siddiqui -- Machine Learning for 3D Geometry, lecture slides
# 01 - Geometric Foundations: Surface Representations
There are several ways to represent 3D shapes, each with their own advantages and disadvantages. Aspects to consider are the memory usage, how efficient operations are, and the constraints of the source data and target application.

### Voxel grids
Voxels = Pixels in 3D. Each Voxel stores an attribute like occupancy (boolean), distance from the object (SDF), colors, etc.

Advantages: arbitrary topologies, easy to query, easy to operate on neighbors.

Huge disadvantage: space requirement grows cubically. Sparse surfaces in occupancy grids lead to a lot of empty space (as the resolution grows, the ratio of occupied voxels goes to zero).

### Point clouds
Set of $(x, y, z)$ locations of points (optionally, additional attributes). Unordered by definition. Can be the result of raw scanning data capture.

Advantage: More efficiently represents sparse surfaces.

Disadvantage: No spatial structure; less efficient neighbor queries.

### Polygon/Triangle meshes
Collection of *vertices*, (*edges*), and *faces*. Represent a *piecewise linear* approximation of a surface. Can approximate very closely if fine enough, though.

Advantages: arbitrary topologies, easy editing/manipulation, and easy rendering.

![[circle-approximations.png]]

Definition: a polygon mesh is a finite set of closed (i.e. end = start point) and simple (not self-intersecting) polygons. Each polygon defines a face of the polygonal mesh.

*Boundary*: set of edges that belong to only one polygon. The boundary is either empty or consists of closed loops; if it is empty, the polygon mesh is closed.

Triangular meshes: polygons are triangulated. This simplies data structures/algorithms/rendering, since only triangles need to be considered.

Meshes can have additional attributes - for example, textured meshes.

##### Mesh data structures
###### STL format (*Triangle List* format)
Binary format, simply a list of triangles described by their corner coordinates: three bytes per coordinate, 9 coordinates per triangle => 36 bytes per triangle.

No connectivity information!

###### OBJ format (*Indexed Face Set* format)
First, list of point coordinates (three numbers per line preceded by a `v`); then, list of faces (three or more indices per line, which refer to the points specified above, preceded by a `f`). More primitives, e.g. lines, also possible.

Other indexed face set formats: OFF, WRL

### Parametric surfaces and curves
Functions $p: \mathbb{R} \to \mathbb{R}^3$ (curves) or $p: \mathbb{R}^2 \to \mathbb{R}^3$ (surfaces). More advanced: 
- Bezier curves
- Splines
- Bezier surfaces
- Bicubic patches

![[bezier-surface.png]]

Advantages of Bezier patch meshes:
- requires fewer points than triangle mesh
- easy to manipulate: just transform the control points under a linear transformation to transform the whole mesh
- easy to sample points
- easy to ensure continuity

Disadvantages:
- hard to determine if point is inside/outside/on surface
- more complex rendering

### Implicit surfaces
Implicit surfaces are represented by functions that assign values to points which relate to the surface.

##### Signed distance function
*Signed distance function*: Function $f: \mathbb{R}^m \to \mathbb{R}$ s.t. $f(x)< 0$ on the inside, $f(x) > 0$ on the outside, and $f(x) = 0$ on the surface. Instead of using a function, one can also use a voxel grid with the SDF values filled in.

If we mostly care about values close to the surface: use a *truncated signed distance field* with N/A values far from the surface.

##### Operations on SDFs
Very efficient:
- union = $\min$ operation
- intersection = $\max$ operation
- subtraction = $\max(f, -g)$ operation
 
![[sdf-operations.png]]

Advantages:
- easy operations
- easy to determine if a point is inside/outside/on the surface
Disadvantages:
- hard to sample points on the surface!


### Conversion between representations

##### Point cloud -> Implicit: Poisson Surface Reconstruction
Fit a function $f$ s.t. $f < 0$ on the inside, $f > 0$ on the outside.

![[points-to-implicit.png|300]]

Poisson surface reconstruction [Kazhdan et al. ’06]: uses *oriented points* (i.e. points + surface normals) as inputs, computes an indicator function $\chi_M$ (0 or 1).
From the point normals, the *gradient* of $\chi_M$, a vector field $V$, is computed. Then find a function $f$ whose gradient approximates $V$: Solve

$$\min_f ||\nabla f - V||$$

This can be transformed to a *Poisson problem* with the solutoin $\Delta f = \nabla * V$, then solved as least-squares fitting problem. (not more details given)

##### Implicit -> Mesh: Marching Cubes
Extract the surface belonging to $f = 0$ in form of a mesh.

Marching cubes algorithm [Lorensen and Cline 1987]: Discretize space into voxel grid. For each cube, compute the implicit function at the 8 corners. This allows to approximate the *zero crossings* (i.e. where the surface crosses the cube surface).

Lookup configuration in lookup table ($2^8$ possibilities depending on the 8 values at the edges).

![[marching-cubes-lookup.png]]

Improve by linearly interpolating the exact position on the cube edges (i.e. if the sdf is -1 on one corner and +10 on the other, the zero crossing should be closer to the first corner).

Advantages:
- widely applicable
- easy to implement, trivial to parallelize

Disadvantages:
- can create skinny triangles
- lookup table with many special cases; some ambiguities are resolved arbitrarily
- No sharp features

##### Mesh -> Point cloud
...to solve problems with bad triangles in meshes, etc.

Generate point cloud by sampling the mesh: Sample each triangle uniformly with barycentric coordinates, sample triangles with probability proportional to their area.

If $r_1, r_2 \sim U([0, 1])$ are uniformly sampled, a random piont on the triangle is given by

$$p = (1 - \sqrt(r_1)) A + \sqrt(r_1)(1-r_2) B + \sqrt(r_1) r_2 C$$


Alternatively: farthest point sampling (sample next point to be farthest from all previously sampled points). However, this depends on the notion of distance (on mesh: discrete geodesic distance = path along edges).

### Geometric operators
how to describe geometry of local observation (i.e. point + its neighborhood)?

- tangent along a surface
- curvature (limiting circle as three points come together)

##### On discrete curves
On discrete curves: problem that points have no well-defined tangent/normal. One reasonable definition: weighted average of incident edges' normals, weighted by the edge lengths.

$$n_v = \frac{|e_1| n_{e_1} + |e_2| n_{e_2}}{|||e_1|n_{e_1} + |e_2| n_{e_2}||}$$

![[discrete-curves-normals.png]]


##### On point clouds
Estimate normal by approximating the plane tangent to the surface, as least-squares fitting problem.

Find neighborhood around point, then estimate a plane by PCA of this neighborhood. (Watch out: Orientation of normal is ambiguous.)

##### Mesh Laplacian
Local descriptor that describes connectivity of nodes/edges and surface geometry.

![[mesh-laplacian.png]]
![[mesh-laplacian-2.png]]  

### Useful Software
- Meshlab (for viewing/processing meshes)
- OpenMesh (for processing meshes)
- CGAL (for computational geometry)



