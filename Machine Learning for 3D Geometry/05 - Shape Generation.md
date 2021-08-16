# 05 - Shape Generation
Goal: be able to generate shapes automatically. Usecases: for example, allow amateurs to create quality 3D models, and professionals to reduce repetitive work. Also, complete 3D structures that were partially observed.

### Example usecases
- Modeling by Example [Funkhouser et al. ’04]: Technique where the user selects a part from a model and part of another model to replace it with, which are then automatically fused. For example, fuse a chair back, chair legs, seating surface from different chair models.
- Part suggestions to support creativity [Chaudhuri and Koltun ‘10]
- Semantic part suggestion [Chaudhuri et al. ‘13] (make plane *more aerodynamic* or animal *more scary*)


### Shape Reconstruction

##### 3D-EPN for Shape Completion [Dai et al. ’17]
Classification of partial shape; class label + input scan -> encoder - predictor network (32^2 voxel grid) -> database prior, multiresolution 3D shape synthesis -> output distance field

![[3d-epn.png]]

##### 3D-R2N2: 3D Reconstruction from Multi-View Images [Choy et al. ‘16]
Recurrent approach: Encode each image with 2D CNN, then run feature vectors through convolutional LSTM and decode into a 3D occupancy grid with a CNN.

![[3d-R2N2.png]]


##### DeepSDF: Implicit 3D Reconstruction [Park et al. ‘19]
Auto-decoder architecture: like auto-encoder without the encoder part. Instead, the decoder works on codes sampled from an artificial latent space, which is optimized jointly with the decoder.

Allows
- shape completion from single depth image (how?)
- shape interpolation via interpolating the corresponding latent codes


##### Occupancy Networks [Mescheder et al. ‘19]
Also implicit 3D reconstruction: instead of an SDF, just reconstruct the function "p $\mapsto$ p inside the object?" implicitly.

### Point Cloud Generation
"Just predict 512 points" or similar; less resolution <-> space tradeoff problems than with voxels.

##### PSGN (Point Set Generation) [Fan et al. ‘17]
Input: segmented image. Output: point cloud that represents the segmented object.

Input image -> 2D CNN -> MLP generates points.
- Chamfer loss: for each point in target point set, distance to closest point in prediction point set; and vice versa; summed together
	- both directions, since else the loss could be "cheated" by predicting a sub- or superset

![[psgn.png]]


### Parametric  3D model generation [Smirnov et al. ‘21]
Sketch-based task: from a 2D sketch, generate 3D shape. 

Reconstruction via *Coon's patch*:
- parametric representation of a surface in computer graphs (smoothly joins surfaces together)
- specified: four curves that bound the patch
	- $P(\mu, 0), P(\mu, 1), P(0, \nu), P(1, \nu)$
- Linearly interpolate between these curves: 
$$P(\mu, \nu) = P(\mu, 0)(1 - \nu) + P(\mu, 1)\nu + P(0, \nu)(1-\mu) + P(1, \nu)\mu - P(0, 0)(1 - \mu)(1-\nu) - P(0, 1)(1-\mu)\nu - P(1,0)\mu(1-\nu) - P(1,1)\mu\nu$$

Per category, templates of part decomposition are generated (details?). For each category, a generator is trained:
- Resnet encodes sketch
- FC layer predicts control points

Trained with Chamfer distance loss + additional losses (*normal alignment*, *collission penalization*, *patch flatness regularizer*)

![[parametric-3d-models.png]]

### Reconstructing Explicit 3D Meshes
(indirectly possible via previous methods, e.g. predict sdf -> apply marching cubes)

Directly: can have loss actually on the mesh. Also, possibly more efficient mesh output than from marching cubes.

##### Pixel2Mesh: Deforming template mesh [Wang et al. ‘18]
Start with ellipsoid mesh; deform to e.g. airplane

Architecture: Two pipelines, one convolving the input image, another deforming the mesh.
- Image undergoes several convolutions
- Graph NN predicts vertex displacements based on features from CNN pipeline

![[pixel2mesh.png]]

Disadvantage: no different topology possible

##### Mesh R-CNN [Gkioxari et al. ‘19]
- Start with coarse occupancy grid obtained from image -> create template mesh from it -> refine with deformation approach.

(not many details)


##### Freeform Mesh Generation: Scan2Mesh [Dai and Niessner ‘19]
"Cut out the template intermediary"
Details: see [[06 - Learning on Different 3D Representations#Scan2Mesh Dai et al '19|here in Lecture 6]].

##### Retrieval-based Object Representation [Li et al. ‘15]
Retrieve a similar looking object's mesh from a database (say, ShapeNet). Enables real-time 3D reconstruction!

- Retrieving similar objects: matching constellations of keypoints and descriptors.

Joint Embedding Space: space of both real images and shapes, s.t. semantically similar things are close
	- constructed from multi-view features
	- images mapped into space via CNN
	
### Joint Embedding for Retrieval
...means that shapes and images are embedded in a joint embedding space (*CNN image purification*). Used in the [[#Retrieval-based Object Representation Li et al ‘15|previous method]].

##### Joint embedding of shapes/images  [Li et al. ‘15]

- shapes: construct embedding space based on multi-view features
- images: train CNN to learn to map images into the embedding space

![[joint-embedding.png]]

##### Joint embedding of 3D scans and CAD objects [Dahnert et al. ‘19]
Construct embedding space end-to-end; use triplet loss for metric learning. This means that an instance is compared with a known positive correspondence (should be close) and a known negative correspondence (should be far away).
	
	
##### Mask2CAD [Kuo et al. ‘20]
Start with image input. Segment into instances -> calculate embedding -> retrieve shape with close embeding. Also, classify the object pose. Combine retrieved shapes with refined poses into reconstruction.

![[mask2cad.png]]
