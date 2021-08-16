# 04 - Shape Segmentation and Labeling

### 3D Classification tasks
##### VoxNet [Maturana and Scherer ’15]
NN architecture working on occupancy grids. One of the first 3D CNNs, used for object classification on occupancy grids.

##### 3D CNN [Qi et al. ’16]
Also, object classification on occupancy grids. Uses *anisotropic kernels* and *network in network* (details?). Interesting: Performs much better on synthetic objects (89.9% acc.) than on real objects (74.5% acc.).

##### PointNet [Qi et al. ’17]
Works on a point cloud (unordered set of points). Can then be used for
- classification
- part segmentation
- semantic segmentation

Architecture:
1. Transform all points by a learned transformation (3x3 matrix; goal: bring into *aligned coordinate system*)
2. 1x1 1d convolutions from 3 -> 64 channels (basically *shared MLP*) with ReLU and Batch norm
3. Another learned transform, 64x64 (close to orthogonal)
4. 1x1 1d convolutions from 64 channels -> 128 channels -> 1024 channels  with ReLUs and Batch norms
5. Max pooling (they argue: important that it's a symmetric operation, since the input set is unordered)
6. More MLPs -> final output

![[point-net.png]]

Question: How does this relate to the architecture in Ex. 2? Is it really the same?

- advantage: more efficient on sparse input data
- disadvantage: doesn't exploit local information

##### PointNet++ [Qi et al. ’17]
Unlike PointNet, also capture local structures (introduce hierarchical processing layer: grouping)

### 3D Shape segmentation tasks
Motivation: human understand shapes by their parts; can guide other tasks like shape matching, shape recognition, part-based modeling.

Labeling: assign part label to each point (or face) of the shape.

#### Classical approaches
Some (older) approaches work on single shapes: K-Means [Shalfman et al. ’02], Normalized Cuts [Golovinskiy and Funkhouser ’08], Primitive Fitting [Attene et al. ’06], Random Walks [Lai et al. ’08].

Better: learn from whole collection of shapes (consistently segment on a collection of objects)

##### Conditional Random Fields for Shape Labeling [Kalogerakis et al. ’10]
Conditional Random Fields (CRFs), encoding relationships between neighboring faces (details: see slides). 

Some limitations: e.g. all legs are labeled together, the individual legs cannot be distinguished. Also, sensitive to topology.

#### Deep Learning-based Part Segmentation
Helpful: ShapeNet dataset (~51k models in *ShapeNetCore*)

Simple option: 3D CNN for segmentation (convolve, have # classes channels in the end)

More advanced options:
##### PointNet for part segmentation [Qi et al. ’17]
Different part segmentation head (concat each of the point features from before the pooling with the global feature obtained from the pooling. Put resulting nx1088 data through shared MLPs to get output scores nxm - n points, m classes)

![[pointnet-part-segmentation.png]]

##### StructureNet [Mo et al. ’19]
Take into account part hierarchy.

Encode relationships between neighboring segments via a graph that encodes object parts. Use a Graph NN and encoder/decoder structure (*graph variational autoencoder*). 

(details -> slides)

![[structure-net.png]]

##### Excursion: Graph NNs

### Unsupervised Co-Segmentation
Goal: find *most consistent* segmentation accross whole collection of shapes, without any supervision signal.

##### Feature-based approach [Sidi et al. '11]
Segment per object; find similar parts accross objects (clustering) -> end result is a part segmentation of whole collection.

![[descriptor-space-co-segmentation.png]]

Drawback: uses handcrafted features.

##### BAE-NET: Branched Autoencoder for Shape Co-Segmentation [Chen et al. ’19]
*Branched autoencoder*. Reconstruct each part of the shape. Intuition: apparently the autoencoder does not only learn good features for reconstructing the whole thing, but also for reconstructing/segmenting the parts.

##### AdaCoSeg: Weakly Supervised Co-Segmentation [Zhu et al. '20]
Train 
a) on shapes that are segmented (but not necessarily consistently; e.g. accross several chairs, you don't always have one-to-one corresponding parts/labels) (*Part prior network*)
b) in an unsupervised way on a *co-segmentation network*

Overall architecture complex -> see slides.



### Active Learning: Human-in-the-loop
System queries human "oracle", minimally. For example: 
- verification
- producing labels

Goal: human should do as little work as possible.

![[human-in-the-loop.png]]

##### Active Part Segmentation Learning
- first: human annotates some shapes
- then: model propagates the annotations to other shapes
- next: human verifies the labelings of the model

### Datasets for Shape Segmentation
- PartNet (most useful)
- COSEG Dataset
- LabelMeshes
- Princeton Segmentation Benchmark