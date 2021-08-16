# 08 - Object Detection + Instance Segmentation
Idea now: Not only detect object classes, but learn to actually distinguish different objects (the chair on the left, the chair on the right, etc.). So high-level goal: "Understand object-ness".

Applications: virtual furniture rearrangement; robots grabbing objects; both need to know as a first step *what the individual objects are*.

### Understanding Object-ness
1. Semantic segmentation
2. Object detection (Bounding boxes)
3. Instance segmentation (Geometry -> Instance mapping)

![[instance-segmentation.png]]


### 3D Object Detection (Bounding Boxes)
Bounding boxes usually work well (e.g. furniture objects). It stops to work well for more flexible things like computer cables.


##### Warm-up: Mask R-CNN for *2D* Object detection
Let's see what works well in 2D first.

Prespecified anchor boxes, find out for each: how likely is it that this covers an object => proposed bounding boxes

![[mask-r-cnn.jpg]]

Propose bounding boxes -> Refine bounding boxes -> generate object masks.

##### Adapting to 3D
- more anchor boxes?
- leverage scale information?
- (+) more spatial separation between objects
- (-) difficult to capture high resolution

### First Approaches: exploit spatial separation
Example [Liu et al. ’19] : U-Net style first, semantic segmentation, finally clustering. Clustering assumes good spatial separation.

![[chair-semantic-segmentation.png]]

Not working well if objects are close together (multiple chairs = one chair). Inverse problem as well: 2 parts of the same chair but with one part inbetween missing from the scan => 2 different clusters.

### 3D-SIS (Semantic Instance Segmentation) [Hou et al. ’19]
Also uses anchors like Mask R-CNN, predicts masks.

Architecture starts with 2D convs, backproj. to 3D and works with 3D Convs; predict anchors and predicts if they correspond to an object, then refines; finally predicts per voxel which mask it belongs to.
(complicated architecture; don't get all the details)

Trick: train on smaller chunks (while predicting whole scenes in the end).

### RevealNet [Hou et al. ’20]
Hallucinate missing geometry patterns ("to get better priors"), in order to improve the instance segmentation performance.

Architecture: Encoder-Decoder structure (for details see slides).

![[reveal-net.png]]

### Summary: Top-down, anchor-based (Mask-R-CNN-style) approaches
##### Challenges
- Anchor basis needs to be diverse enough to cover all possibilities
- think/"anisotropic" objects easily missed
- inefficient to predict objectness for many empty space locations (much more of a problem in 3d than 2d)

##### Top-Down vs. Bottom-up
The simple Clustering approach is Bottom-up.
Top down would be e.g. starting with finding an object, then refine it to really get the object

Spectrum between these two paradigms: E.g. the next model, VoteNet, combines some of both.

### Point Cloud Object detection
Anchors on point clouds: problematic; since points are often on the surface, don't coincide with object center. Instead if anchors not restricted to point cloud (what does that mean?) => not clear how to distribute them

##### VoteNet [Qi et al. ’19]
Goal: get *object detection* without scanning too much empty space.

Point cloud representation: get votes from each point to where the center of their object is.

Related to Hough transform (which is usually used to find lines). Instead of lines, we find centers.

1. Input: point cloud. Put through "backbone" (typically PointNet++)
2. Seeds (typically farthest-point sampling from PointNet++): locations + features learned
3. Vote (shared MLP): predict 3D location (of center) + feature offset (used together with original feature to compute object feature)
4. Cluster votes: farthest point sampling based on 3D locations
5. Shared PointNet processes vote cluster (predict objectness, bounding box params, class category)
6. "3D non-max suppression": filter out near-duplicate bounding boxes

![[VoteNet.png]]

This is a leading conceptual approach for detecting objects.

##### 3D-MPA [Engelmann et al. ’20]
Multi-proposal Aggregation for 3D Semantic Instance Segmentation.
Similar to VoteNet, but now also get *instance segmentation* out. Works on Point cloud (or here, rather sparse voxels?)

1. Proposal generation (propose features for seed locations)
2. Proposal consolidation (graph NN with seeds as nodes => refined proposal features)
3. Object generation
4. again, non-max suppression postprocesssing

![[3d-mpa.png]]

##### Probabilistic Embeddings for Point Cloud Instance Segmentation [Zhang and Wonka ’21]
(for point cloud instance segmentation)

Bottom-up instance segmentation on point clouds. Input unordered point set $x_i$; Output per-point embeddings $e_i$.

Measure similarities between point embeddings, and group the similar points together.

Not clear what happens exactly, but points are encoded as Gaussian distributions, and distances between the distributions are calculated (Bhattacharayya kernel, etc.)

Loss: BCE (nice to optimize; can suffer from class imbalance, e.g. way more background than interesting objects).

### Towards Part-Based Scene Understanding
Segment scene not only into instances, but even further segment instances into parts.

##### [Bokhovkin et al. ’21]

Start with synthetic priors of what e.g. chairs/tables/beds look like and how they decompose into parts. Combine linear combination of priors for a class to get a coarse estimate of how objects of this class look.

1. VoteNet-Like object detection
2. Decomposing objects into part trees => latent vector for each part
3. Prior decoder (recall: shape should be linear combination of part priors)
4. Refine priors => complete part decomposition

![[parts-of-tables.png]]