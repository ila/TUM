# 09 - Reconstructing and Generating Scenes

Motivation: make shape generation more accessible, require less expertise

- capturing 3d photos (e.g. can change viewpoint a bit)

- 3d online interaction mimicking live interaction ("mixed reality")

- reconstructing with a Kinect
	- one problem: only partial geometry (*missing geometry*)
		- maybe because the camera didn't go there
		- or objects are occluded
		- sometimes even from metallic/reflecting surfaces
		- => need to complete the scans

### Generative Tasks
##### Scan completion
Fill in missing geometry in an incomplete scan.

##### Surface reconstruction
we have a room (in this particular case, pretty much completely observed); but only point measurements (point cloud). Go from point measurements to whole continuous surfaces (e.g. implicit representation)

This could be done by classical surface reconstruction algorithms, but our goal is to learn geometric priors to help the task (to still perform well e.g. if the measurements are more sparse).

##### Pure generative task: scene generation
Sample code from latent space, then construct a plausible scene from it.

![[scene generation.png]]


### Excursion: Generative tasks in 2D
Let's see how similar tasks are tackled in 2D.

- Encoder-Decoder approaches (encode -> decode -> L1/L2 reconstruction loss; problem with this loss: blurry generations)
- GANs (Generator generates; discriminator must tell real/fake apart)
	- both should train "at a similar pace"
- Autoregressive models
	- generate not the whole image at once, but pixel-by-pixel (i.e. when you generate the i-th pixel, you already know the i-1 previous ones)
	- examples: PixelRNN, PixelCNN, VQ-VAE, VQ-VAE2

### Scan completion/Surface reconstruction tasks (both appear in this section)
##### Synthetic vs. true data
Synthetic data: perfect ground-truth. Synthetic datasets:
- 3D-FRONT (furnished rooms with semantics)
- ICL-NUIM

Fully supervised approaches are fruitful with synthetic data because full ground-truth available.

##### SSCNet
RGB-D image -> geometry occupancy grid (of fixed size) + semantic labels

Architecture: bunch of convolutions and *dilated convolutions*.

#### ScanComplete
(not completely clear what the point is)

- Handle arbitrary-size scenes
- Trained on crops of scenes
- complete the scan at different resolutions

Also operates in auto-regressive fashion: (8 forward passes instead of one per voxel)
![[autoregressive-scancomplete.png]]

Note: on real-world scan data, the reconstruction is less clean

#### Learning just from incomplete real-world data
...in a self-supervised way, to get rid of the discrepancy between fake/real data.

##### SG-NN: Self-supervised Scan Complete [Dai et al. '20]
Goal: learn to reconstruct patterns that are missing in a less complete scan, but present in a more complete scan => self-supervised scan completion!

- reconstructed target scan from several depth frames (still some holes in it)
- other scan constructed from fewer frames -> more holes
- train reconstructing more complete frame from less complete frame (ignore space that is unobserved in the target scan in the loss)
	- note: if you don't ignore the missing space, the network *learns to generate holes*. Otherwise it manages to fill holes.

![[sg-nn-unsupervised-completion.png]]

Multi-Scale approach as well: dense predictions at coarse level; more sparse predictions at upsampled level.

##### SPSG: Self-supervised color generation [Dai et al '21']
[[#SG-NN Self-supervised Scan Complete Dai et al '20'|Like previous approach]], but also generate color.

Problems with simple loss like L1: many walls in the input => everything becomes wall-colored.

SPSG approach: project back to images (i.e. the actual input data); then use a 2d reconstruction loss, a 2d perceptual loss + a 2d adversarial loss.

![[spsg-losses.png]]

#### Leveraging implicit reconstruction networks
i.e. a technique that works well on 3D shapes -> now applied to scenes?
=> doesn't work as easily when working on scenes (not everything centered like for shapes, this makes it harder)

##### Local implicit grids [Jiang et al. '20']
- Decompose space into smaller patches (where the patches should be: convolutional enc/dec. approach: then fine detail on patches via implicit approach)

![[local-implicit-grids.png]]

Also an advantage: can reconstruct shapes it has never seen before (otherwise, if it has never seen a car, it will turn it into something that is not a car)

##### Convolutional Occupancy Network [Peng et al. '20']
First convolutional, then implicit occupancy network at the end.
- Advantage: convolutions have translational invariance and can recognize non-centered patterns; implicit representations can reconstruct finer details.

First: (coarse) occupancy voxel grid; convolutions => feature vector per voxel

Trilinear interpolation: interpolate voxel feature vectors into feature vector for particular point 

Second: feed this feature vector of a point in a implicit occupancy network (shared between decoding locations)

#### Retrieval as Reconstruction (Exploiting training data as "dictionary")
Usually: condense training data into network weights. But we could leverage the more detailed training data during test time by using it as a dictionary to look up examples of nice constructed objects.

Advantages/Disadvantages of Retrieval as Reconstruction:
- For example: can be more sure that reconstructed objects are physically plausible (e.g. no chairs without legs etc.)
- Disadvantage: usually no exact geometric matches.


##### RetrievalFuse [Siddiqui et al. ’21]
Idea: create initial reconstruction estimate by composing chunks of train data. Then make it consistent afterwards

QUESTION: how well does this work with unseen objects?

Database retrieval (k-NN) -> attention-based refinement -> reconstruction

![[retrievalfuse.png]]

kNN via embedding space. Constructed with *deep metric learning*:
- a point *f* should be close to similar points *f+*
- ...and far away from dissimilar points *f-*
- done one triple (*f, f+, f-*) at a time

Use dot product similarity in more complicated expression to compute loss

k NNs merged together with attention.


##### Scan2CAD [Avetisyan et al. ’19]
Reconstruct scene by aligning CAD models (e.g. ShapeNet models) to it

For a point, estimate a heatmap on the candidate CAD models on where this point is likely to be.

Scan input, point, + candidate CAD models -> Encoded by 3D convs -> output: match (0 or 1), heatmap over CAD model, scale

Problem: objects aligned independently of each other.


##### SceneCAD [Avetisyan et al. ’20]
Take into account dependency of objects on each other via a GraphNN (used at train time)


### Scene synthesis task
 
##### [Wang et al. '18]

 Trick: create scene by iteratively adding objects to a room (easier to generate everything from scratch). It must be learned e.g. which objects appear together usually.
 
 Loop: partial scene -> decide: continue adding objects? (CNN) -> if yes: predict object category + location (CNN) and place object -> repeat

(autoregressive approach because of the loop)

### Textured Scene Generation
For content creation and visualization: need more than geometry; also textures, materials, lightning...

#### Texture optimization task
Assume texture is already known (from images), but from motion blur etc., we get blur in the reconstruction.
(Challenges: camera pose estimation, motion blur, distortion artifacts, view-dependent materials)

##### 3DLite [Huang et al. '17']
Use simple geometric primitives (i.e. planes) and project high-res textures onto these.

![[3DLite.png]]

Apparently, uses more classical algorithms mostly. Also, kind of a "handcrafted" pipeline

##### Adversarial Texture Optimization [Huang et al. ’20]
Leraning an adversarial objective function that knows how real textures look.

![[adversarial-texture-optimization.png]]

In some way use differently aligned perspectives to feed into the discriminator (details a bit unclear)