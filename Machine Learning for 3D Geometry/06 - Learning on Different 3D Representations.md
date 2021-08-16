# 06 - Learning on Different 3D Representations

### Types of 3D Represenations
1. Volumetric grids => 3D CNNs
2. Multi-view
3. Point clouds
4. Meshes

### Volumetric Grids

More efficient than dense grid?
=> octtree

##### OctNet: Operating on Octtree structure
Problem: "neighbor queries" needed for convolutions can be less efficient
Needed: known octtree structure => no generative tasks

Other architectures support generative tasks:

##### Octtree Generating Networks
Predict if octtree cell is empty / filled / mixed

#### Sparse Techniques
"Representing 'the fine level' in a sparse fashion"
e.g. storing only the surface voxels with hashtable

##### Submanifold sparse convolutions
Avoid spreading sparse information to dense via convolution
![[submanifold-sparse.png]]
Submanifold sparse convolutions operate basically only close to the surface (details?)
* memory-efficient; uses hashing
* significant performance improvement (e.g. room-scale @1-2cm now possible)
* See: `MinkowskiEngine`

(Slight) disadvantage: close points in euclidean space can be far removed in geodesic distance or even disconnected, in this case no information propagation by subman. sparse conv. is possible.

##### Sparse Generative NNs (SG-NN, [Dai et al. '20])
(Used for scan completion)

Sparse Encoder -> Coarse Resolution
Dense Predictor @ Coarse Prediction
Upsample + Filter out by Geometry (e.g. mask out space known to be empty)


### Multi-View Representations
Idea:
* exploit already existing powerful 2D CNN work
* leverage high resolution of images

Advantage: can use fine-tuning on huge available 2D image datasets (which are not there for 3D)

Put in multiple images from the same object, put them through CNN. Idea: maybe one view is surer than another, and can provide more information.

Actually outperforms approaches with 3D inputs because of more training data. (Note: possibly only back then, not now)

![[mvcnn.png]]

Another idea: use on point clouds by rendering point clouds as spheres


### Handling Irregular Structures
Meshes, Point Sets, Molecule Graphs, ...

Surface geometry: up to now, gridify 2d surface in 3d space and apply (possibly sparse) convolution. => Point Cloud represents this without grid

##### Point Clouds
e.g. [[04 - Shape Segmentation and Labeling|PointNet]]: pool over points, such that agnostic to ordering

### Meshes
Interpret as graphs!

Convolutions over graphs should have, like regular convolutions: local support, weight sharing; and they should enable analysis at varying scales.

##### Geometric operators
Some geometric operators that might be useful for convolutions (how exactly? After all we're not on a continuous surface?)

- tangent plane of a point $x$
- geodesic distance between two points $x, x'$
- gradient $\nabla f(x)$ of a scalar field $f$ on the surface
- divergence $\text{div} F(x)$ of a vector field $F$ on the surface (density of outward flux of $F$ from an infinitesimal volume around $f$)
- Laplacian: $\Delta f(x) = -\text{div}(\nabla f(x))$ (difference between $f(x)$ and the average of $f(x)$ on an infinitesimal sphere around $x$)
- discrete Laplacian (on a grid structure)
	- in 1D: $(\Delta f)_i \approx 2f_i - f_{i-1} - f_{i+1}$
	- in 2D: $(\Delta f)_{i,j} \approx 4f_{ij} - f_{i-1,j} - f_{i+1,j} - f_{i,j-1} - f_{i,j+1}$

Similar definitions of discrete Laplacian on undirected graphs/triangular meshes (formulas -> see slides). Related to this: Graph Fourier transform (no more details on this).


##### Geodesic CNN
Convolutions in local geodesic coordinate system
![[geodesic.png]]
(Polar coordinates $\rho, \theta$)

Apply filters to geodesic patches (to be rotation-invariant: apply several rotated variants of the filter. Otherwise the choice of $\theta=0$ is arbitrary)

![[geodesic-CNN.png|350]]

Then one can define Convolutional networks with these geodesic convolutions [Masci et al. '15]

![[geodesic-CNN-full-network.png]]

Computing this in practice: not trivial, but possible: uses a marching-like procedure that needs a triangular mesh. One needs to choose the radius of geodesic patches (compare with the kernel size).

Drawback: There is no natural pooling operation; can only use convolutions to increase receptive field. This is a huge drawback and hinders the performance.

##### Spectral Graph Convolutions
Relies on Graph fourier analysis. Convolution thereom: convolution in spatial domain = multiplication in frequency space.

Disadvantages: unless you have "perfect data", there are some drawbacks; e.g.g no guarantee that kernels have local support, no shift invariance. Also no natural pooling operations.

Open area for new developments.

##### MeshCNN [Hanocka et al. '19]
CNN for triangle meshes: conv. and pooling defined on edge
- each edge has a feature, and four edge neighbors (from two incident faces)
- convolution applied to edge feature + 4 neighbors
- pooling: edge collapse

![[MeshCNN.png]]

Can perform reasonably well for part segmentation.

#### Message Passing Graph NNs
Nodes in a graph have features (*hidden states*) - optionally, also the edges and the graph as a whole. Hidden states are updated by aggregating messages from neighboring vertices/edges.

##### Scan2Mesh [Dai et al. '19]
Constructs a mesh from a 3D scan.

3d Input scan -> downsample (convolutions), predict vertices -> predict edges (message passing network)
-> dual graph (where each vertex corresponds to a mesh face, and an edge means that two faces are adjacent) -> output mesh.
(see slides)

![[scan2mesh.png]]

Difficulty: how to define loss? combine: be close to a ground-truth mesh (not very flexible), be close to the correct surface (can have weird artifacts in mesh: e.g. self intersections)

### Combining Representations
It can be an advantage to use multiple representations at the same time.

##### Dynamic Graph CNN for Point Clouds
From point cloud, compute local neighborhood graph ($k$-nn graph)
- apply graph convolution
- re-compute local neighborhood graph for the pointcloud points

##### 3DMV (Joint 3D-Multi-View Learning) [Dai et al. ‘18]
First use a 2d CNN on images, then backproject to 3d, then a 3d CNN on reconstructed geometry (and also the actual geometry)

##### Virtual Multi-View Fusion [Kundu et al. ‘20]
Instead of real views, use rendered views. Also see [[07 - Semantic Scene Understanding#Virtual MVFusion Kundu et al '20|here]]

##### TextureNet [Huang et al. ‘19]
Semantic labeling of textured meshes. Can leverage the texture signal as well.