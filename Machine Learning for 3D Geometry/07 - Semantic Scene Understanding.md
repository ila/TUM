# 07 - Semantic Scene Understanding: Semantic Segmentation
3D Semantic segmentation: segment scene (e.g. room) in semantic parts (e.g. for each part: to which furniture type it belongs)

### Popular Benchmarks
- ScanNet benchmark (furniture scenes)
- KITTI / KITTI-360 (outdoor, captured on roads)


### Scene segmentation vs. Part segmentation
*Scene* segmentation and *part* semgentation are similar tasks. Differences: 
- in part segmentation, different types of objects have fixed sets of parts (i.e. a chair has legs, the seating surface, armrests, the back of the chair). Different types and are not considered/trained together, but separately.
- scale: small objects in part segmentation, larger rooms/building complexes in scene segmentation

### 3D inputs vs 2D inputs
- obvious: cubic vs quadratic space growth
- less obvious: in 3D, you get information about scale! 
	- in 2D you don't: you see a projection. One pixel can correspond to different real-world lengths at different positions in a picture.
	- but on the other hand, resizing 3D shapes changes this scale information

### Solving size problem
You often cannot process a huge scene at full resolution at once without resizing! Compare with [[04 - Shape Segmentation and Labeling|PointNet, PointNet++]]: combine local and global information, do things chunk by chunk.

##### Sliding window approach [Dai et al. '17]
For semantic segmentation.
- predict column-by-column in gravity direction (height-reliable feature)
- use data of local neighborhood around currently considered column

##### Joint 3d + multi-view [Dai et al. '18]
Multi-view images have higher resolution and also color information!

Process multiview: first process in 2d, then backproject to 3d using known camera params. Feed results + original 3d geometry into 3d convolutions.

##### ScanComplete [Dai et al. '18']
Convolutional architecture (no details given), key point: predicting complete geometry helps with semantic segmentation.
![[scan-complete.png]]


##### Sliding window approaches in general
Adaptive to varying scene sizes. Drawback: for w x h x d scene, needs to be run O(w x h) times.


### Convolutions to allow varying input sizes
Convolutions can have parameters s.t. the output size = input size.

##### ScanComplete [Dai et al '18] for geometric completion.
Relevant to semantic segmentation: first *completing* the scene helped later to correctly *segment* it.

### Allowing higher resolution

##### TextureNet [Huang et al. ‘19]
Augment input with textures (I think? Not clear what happens exactly...)

Textures give higher-resolution input signal.

##### Sparse convolutions
[[06 - Learning on Different 3D Representations#Submanifold sparse convolutions|Seen before:]] convolve on active sites only. Improved semantic segmentation drastically (e.g. SparseConvNet, MinkowskiNet; 2019). Mostly because whole scenes can be processed at once now without running out of memory.


### Non-Regular Grid Geometry
What about inputs that don't natively lie in a 3d grid?

##### KPConv
Based on point inputs; Goal: create *adaptive* convolution kernel ("deformable"?) by learning where points should be shifted (no details)

![[kpconv.png]]

### Online Semantic Segmentation
##### OccuSeg [Han et al. 20]
Can perform semantic segmentation in real time + online when walking around with a camera.

- pretty complex approach
- 3D UNet followed by more stuff; also, clustering to super-voxels; then graph NN
- Most important part apparently is the clustering into super voxels

![[OccuSeg.png]]

### Multi-View/3D Geometry Fusion Revisited
Goal: Fuse multi-view images with 3D geometry; get more information into the 3D geometry. 

Problems with simply backprojecting real-world images:
- limited set of views
- camera estimation, motion blur etc lead to inaccuracies
- colors may be view-dependent

##### Virtual MVFusion [Kundu et al. '20]
Generate Synthetic images from 3d scene. Lose real-world information, but also do away with inaccuracies in real-world image capture: more images, wider field of view, ensured to be consistent.

Fusing process: first, apply pretrained 2d semantic segmentation. Then back-project to 3d, aggregate projected 2d features for one 3d point by averaging.

(unclear to me: How to generaate synthetic images that are better than the original (limited) images?)

##### BPNet
*Bidirectional* interactions between 3d and 2d representations ("2d can inform 3d, but 3d can also inform 2d"). One 2D UNet which works with images and one 3D UNet which works on the reconstructed 3D scene, they interchange information and in the end predict 2D/3D labels.

![[BPNet.png]]

Both the 2D results and the 3D results (slightly) improve because of this bidirectional information sharing.