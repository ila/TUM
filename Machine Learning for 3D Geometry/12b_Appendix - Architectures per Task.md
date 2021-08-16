##### Classification/Segmentation
- Shape Classification: **PointNet(++)**
- Part Segmentation: **PointNet**
- Unsupervised co-segmentation: **AdaCoSeg**
	- both on non-consistently segmented (PointNet++) and unsegmented shapes (co-segmentation)

##### Reconstruction
- Shape reconstruction: **3D-EPN**
	- distance field voxel-grid UNet
- Point cloud generation: **PSGN**
	- image -> 2D CNN -> MLP generates pointcloud
	- Chamfer loss
- Parametric reconstruction: **Parametric 3D Model Generation**
	- sketch -> ResNet latent space -> FC layer -> control points of templates (per category!)
- Mesh generation: **Scan2Mesh**
	- voxel grid -> predict vertices -> edges GraphNN -> refine
- Retrieval-based reconstruction: **Joint Embedding of 3D Scan and CAD Objects**
	- embedding space end-to-end, metric learning - triplet loss

##### Different representations
- Sparse reconstruction: **SG-NN**, **MinkowskiEngine**
	- sparse input scan encoded, UNet-style decoder
- multi-view: **MVCNN**
- irregular structures: **Geodesic CNN**
	- convolutions on local patches; rotation ambiguity -> several rotations of filters
- meshes: **MeshCNN**
	- convs to adjacency n'hood
- combine representations: **3DMV**
	- 2D CNN, backproject, 3D CNN


##### Scene semantic segmentation
- sliding window scene segmentation: **3DMV**
- multiview-/3D fusion: **BPNet**

##### Scene instance segmentation/object detection
- top-down anchor based (Mask-R-CNN): **3D-SIS**
	- convolutions, anchor prediction, refinement
- vs. bottom-up: just cluster after semantic seg.
- voting-based: **VoteNet**
	- PointNet++ backbone on point cloud -> predict object centers -> voting: seeds vote -> cluster votes

##### Scene Reconstruction/Generation
- Scene scan completion: **SG-NN*
- Implicit scene reconstruction: **Conv. occupancy networks**
- retrieval-based: **SceneCad**
	- aligns CAD models, takes dependencies into account via GraphNN

##### Functional Analysis
- predicting interactions: **PiGraph**
- Human motion extraction: **iMapper**
- ....

##### Data Efficiency
- Few-shot: **Learning to Reconstruct Shapes from
Unseen Classes**
- fewer supervision: **Latent 3D Keypoints**
- domain adaption: **CyCADA** (2D)
	- GAN architecture. Source image (GTA) stylized as realistic image