# 11 - Weak Supervision, n-shot Learning, Data Efficiency

Broad motivation for this chapter: We want to use as little (annotated) data as possible, because data collection and data annotation are expensive.


### Training methods and required amount of labeled data
- *Supervised*: manually labeled data by expert annotators (expensive)
- *Unsupervised*: no annotations at all (learn structural patterns)
- *Semi-Supervision*: both a labeled and an unlabeled set of data
- *Weak Supervision*: lower quality labels that you can get without expert annotators
- *Self-Supervision*: Automatically generate supervision signal
- *Transfer Learning*: transfer pretrained models to your task

Other hybrids exist: e.g. active learning -> use annotations more efficiently (human in the loop)


### Few-shot learning
See only few (or one) example per class.

##### Reconstruction from image on unseen classes [Zhang et al. ‘18]
Problem usually: if you train on tables and chairs then want to reconstruct a bed, it might end up looking like a table. Goal in order to avoid: "the model should memorize as little as possible"

Input image -> depth estimation -> 2d spherical map (geometry outprojected into a sphere) -> inpaint spherical map image to fill in missing geometry -> backproject to 3d shape -> refine to get a final 3d shape

![[reconstruction-on-unseen-classes.png]]

##### Learning Category-Specific Mesh Reconstruction [Kanazawa et al. ‘18]

Input: lots of annotated images of one specific class (e.g. birds), but no 3d information. Then for one specific image, reconstruct a textured mesh.

(didn't get the details of the architecture)

![[bird-mesh-reconstructed.png]]

##### Few-shot single image reconstruction [Wallace and Hariharan ‘19]
Transform an input image to a 3d representation, but this time a *category prior* can be used (e.g. image of a sofa, and the category prior is the mean of all known sofas).

Trained on image/3d shape pairs. But at test time, novel categories are presented where there are only a few (say 25) examples.

Architecture: encode image with 2d convolutions, prior shape with 3d convolutions -> concatenate (or sum?) the codes -> decode back with transposed convoutions.

The limitation is of course that some categories have much diversity, and then the approach doesn't work as well.

![[sofa-category-prior.png]]


### Generalizing accross datasets
"Train in Germany, Test in the USA". An issue e.g. in autonomous driving: if an object detector is trained in different circumstances than it used in. If applying such a model to the different test dataset blindly, there will be a performance gap that we want to avoid.

##### [Wang et al. ’20]
Frequent issues found in self-driving car scenarios: misdetection (detect something where there isn't anything), but even more mislocalization. The latter comes from different car sizes in e.g. Germany vs. the US, or accross different cities etc.

Solved by data normalization (*domain adaption*).


### Approaches with less/no supervision
##### Discovery of Latent 3D Keypoints [Suwajanakorn et al. ’18]
Given: multi-view image observations. Goal: find keypoints in 3D without having annotated training data. (Related: annotating 3D Keypoints is not well-defined anyway but very much subjective).

Use auxiliary task as weak supervisory signal: Aux. task is pose estimation from an image. Given are two views of which the ground-truth transformation between them is known . Then as supervision, they use that transforming the keypoints output for the first image should be close to the keypoints output for the second image

This matches what you want from a keypoint: Keypoints should be points that are identifiable from several views of the shape.

![[keypoint-detection.png]]


##### Learning the Depths of Moving People [Li et al. ‘19]
Input: Youtube videos of people standing still. Goal: estimate depth maps.

Using the "mannquin challenge" dataset (2000 videos): people try to stand still like mannequins, which satisfies the static scene assumption quite well. Via structure from motion and multiview stereo a depth image which serves as supervision signal ist estimated.

Goal at test time: also apply it to moving people, not just static people. To improve for this, also take into account a mask that shows where the humans are and a depth from flow estimation (obtained from having a video, not only a static image).

![[mannequin-challenge.png]]


##### SG-NN: Self-supervised scan completion [Dai et al. ‘20]
Mentioned before [[09 - Reconstructing and Generating Scenes#SG-NN Self-supervised Scan Complete Dai et al '20|in Lecture 9]]. This also uses self-supervision to complete scans, while having only a dataset of incomplete scans, by learning how to go from less to more complete.


##### PointContrast [Xie et al. ‘20]
Goal: semantic understanding of point clouds. Pretrained on large unannotated dataset, fine-tuned on small dataset.

These idea come from similar ideas in 2D representation learning:

###### Excursion: 2D Representation Learning
SimCLR [Chen et al. '20], MoCo [He et al. '20]. Use *contrastive loss function* to learn a representation where similar things are close, and dissimilar things are far away from each other. Compare with [[09 - Reconstructing and Generating Scenes#RetrievalFuse Siddiqui et al ’21|RetrievalFuse]], where a similar concept and loss were used.

The supervisory signal here is that we can generate more data samples we know to be similar by *data augmentation* (If you have an image of a cat and do some cropping and resizing, you know it's still an image of a cat; so make sure that the model learns this).

###### Similar ideas used in PointContrast
In 3D, we can even use more augmentation techniques related to multi-view constraints. So pretrain the model to be able to recognize originally close points in 2 images of the same 3d scene as similar, and far away points as dissimilar.


##### Data Efficient 3D Scene understanding [Hou et al. ‘21]
Extension of the previous PointContrast idea. Partition the scene into smaller regions, apply the contrastive loss in each region separately. This allows to get more performance out of using more sample points (while performance of the vanilla PointContrast approach saturates).

After the pretraining, train on limited number of fully-labeled scenes and on all scenes with limited point labeling budget (this would be suited to *active labeling*; not actually used here though).

### Domain Adaption
Scenario: Much labeled data in one domain, but much less in a second domain. One example: synthetic domain (object annotations for free) <-> real domain (annotations expensive).

Common tasks approached: semantic segmentation, object detection.

#### Excursion: Domain adaption in the 2D domain
##### CyCADA: Cycle-Consistent Adversarial Domain Adaption [Hoffman et al. ‘18]
Adapt between synthetic GTA images and real CityScapes images of cars on roads.

GAN: Generator should stylize the source image similar to the target image. Discrimnator tries to tell apart the images and also the extracted features. The source image and stylized source image should have semantically consistent features. Then goal: be able to use the same feature extractors from the source domain on the target domain.


#### Domain Adaption in 3D
No details here, but the same thing as for 2D applies: We have synthetic data that is labeled already, and want to use it for pretraining and then be able to apply the models to real data.