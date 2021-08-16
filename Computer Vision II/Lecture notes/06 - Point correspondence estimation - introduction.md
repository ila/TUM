# Chapter 4 - Estimating Point Correspondence
Goal: find point descriptors/characteristic image features to be able to identify keypoints accross several images from different views.

Things to consider:
- small vs. wide baseline case (small vs. large displacements)
	- higher fps makes things easier!
	- small baseline: plausible corresponding points are likely to be close (only search there)
- textured reconstructions can look misleadingly good (egg with face projected looks a lot like a human head)
- non-Lambertian materials (shiny: view point dependence of reflection)
- non-rigid transformations (person bending their head)
- partial occlusions (sunglasses) - a point may not have a correspondence



##### Small Deformation vs. Wide Baseline
- *small deformation case*: classical *optical flow estimation*.
	- Lucas/Kanade method (1981) (find correspondences sparsely)
	- Horn/Schunk method (1981) (find correspondences densely)

- *wide baseline stereo*: Select feature points and find an appropriate pairing of points

Comment: with improving methods, increasingly large deformations can be handled by optical flow estimation (e.g. coarse-to-fine approaches)


### Small Deformations: Optical Flow
The rigid transformation of a point $x_1$ in one image to $x_2$ in the other image is given by

$$x_2 = h(x_1) = \frac{1}{\lambda_2(X)}(R \lambda_1(X) x_1 + T)$$

##### Local approximation models for motion
This can be approximated locally e.g. by a *translational model*

$$h(x) = x + b$$

or an affine model

$$h(x) = Ax + b$$

The 2D affine model can also be written as

$$h(x) = x + u(x) = x + S(x) p = \begin{pmatrix} x  & y & 1 & & & \\ & & & x & y & 1\end{pmatrix}(p_1, p_2, p_3, p_4, p_5, p_6)^\top$$

for some parameters $p_i$ depending on the rotation/translation.

Affine models include much more types of motion (divergent motions, rotations etc.)

##### Optical Flow Estimation
The *optical flow* refers to the part of the motion that can be seen in the image plane (i.e. the projection of the real motion onto the image plane).

- **Lucas-Kanade**: sparse method (estimate motion field at certain points, under the assumption that the motion in a small neighborhood is *constant*)
- **Horn-Schunck**: dense method (estimate motion field at every pixel, under the assumption that the motion in a small neighborhood is *smooth*)

Lucas-Kanade was prefered at the time the methods were published because it is simpler and already was realtime-capable in the 80's. In more recent years, Horn-Schunck is becoming more popular ("now we have GPUs").

