# Appendix: stuff that was in the exercises
## Exercise 1
##### 1.1 Generating SDFs

##### 1.2 SDFs -> occupancy grids

##### 1.3 SDFs -> meshes (marching cubes)

##### 1.4 Manually creating .obj files

##### 1.5 Sampling point clouds from the mesh surface

##### 1.6 Procrustes rigid shape alignment


## Exercise 2
Dataset: shapenet.

##### 2.2 Simple 3D CNN in PyTorch
![[simplenn.png]]

##### 2.3 3DCNN for shape classification
Data: ShapeNetVox32 voxel grids (resolution: 32³)

![[3dcnn.png]]

##### 2.4 PointNet for shape classification
Data: ShapeNetPointClouds

![[pointnet.png]]

Some details:
- shared MLPs as 1d convs with kernel size 1
- mlp(64, 64) means two mlps with output size 64 each
- one head is for classification, another head is for part segmentation
- After all layers except the final classification layer: ReLUs and Batch norms
- Classification head: additional dropout between second linear layer and batch norm
- TNet: "basically small point net" (TODO: look at code again)

##### 2.5 PointNet for part segmentation
Data: ShapeNetPart

Like above, but now with segmentation head. The segmentation head takes a global feature vector concatenated with point-level feature vectors and applies shared mlps.


## Exercise 3

##### 3.1 3D-EPN for shape reconstruction
Data: SDF and DF grids (incomplete input data/complete target data) at resolution 32³, provided by the 3D-EPN authors.

Goal: scan completion.
![[3depn_teaser.png]]

![[3depn.png]]

Architecture: 3D-Unet encoder/decoder structure (we skipped the classification network part).

##### 3.2 DeepSDF for 3D reconstruction

Data: point clouds with sdf values, prepared by the paper authors; also, the mesh representation.

![[deepsdf_teaser.png]]

![[deepsdf_architecture.png]]


