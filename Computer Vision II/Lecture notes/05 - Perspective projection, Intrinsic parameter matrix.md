# Chapter 3 - Perspective Projection

Goal of MVG: *invert the image formation process*. One part of the formation process is the *camera motion* (last lecture). The second one is the *projection of points to image coordinates*.

Historic remarks: perspective projection was known already to Euclid, but apparently lost after the fall of the Roman empire. It reemerged in the Renaissance period.

### Mathematics of Perspective Projection
Most idealized model: *Pinhole camera* (camera obscura).

To capture more light: use *lenses* which bundle light (by refraction at the material boundaries). Light which enters the lens in parallel to its optical axis leaves it in the direction of the *focal point* $F_r$. For a symmetric lens, the left/right focal points $F_r$, $F_l$ are at the same distance from the lens. We look at the idealized *thin lens* here: assume the two refraction points (light enters lens/leaves lens) are the same.

**Crucial point**: All the light rays that leave a point $P$ are bundled by the lens to meet at a point $p$ again.

![[thin-lens.png]]

Assumption: $z$ axis is the optical axis. Observe the two *similar triangles* $A, B$ with side lengths $Y$ and $Z$ corresponding to side lengths $y$ and $f$.
- $Y$ is the distance in y direction from the optical axis in the world
- $y$ is the distance in y direction from the optical axis in the projected image
- $Z$ is the distance of the object to the focal point (or its $Z$ coordinate? unsure...)
- $f$ is the *focal length* (distance of the focal point from the lens)
Also, assume that $Y > 0$, $y < 0$ ("positive/negative length") since they point in opposite directions. We get:

$$\frac{Y}{Z} = - \frac{y}{f} \quad\Rightarrow\quad y = -f \frac{Y}{Z}$$

We want to get rid of the minus sign. So we adapt the convention that *the image projection plane is not behind, but in front of the camera*. Then the perspective projection is given by:

$$\pi: \mathbb{R}^3 \to \mathbb{R}^2, \mathbf{X} \mapsto \begin{pmatrix} fX/Z \\ f Y/Z \end{pmatrix}$$

**This transformation is non-linear because you divide by the Z coordinate!**

##### Homogeneous coordinate representation

$$Z\mathbf{x}
= Z \begin{pmatrix} x \\ y \\ 1 \end{pmatrix}
= \begin{pmatrix} f & & & 0 \\ & f & & 0 \\ & & 1 &0 \end{pmatrix} \begin{pmatrix} X \\ Y \\ Z \\ 1 \end{pmatrix}
= \begin{pmatrix} f & & \\ & f & \\ & & 1\end{pmatrix}
 \begin{pmatrix} 1 & & & 0 \\ & 1 & & 0 \\ & & 1 &0 \end{pmatrix}
 \mathbf{X}
= K_f \Pi_0 \mathbf{X}
$$

$\Pi_0$ is the *standard projection matrix*. Assuming you are sufficiently far away s.t. $Z$ is approximately constant accross all points, $Z = \lambda > 0$, we finally get a linear transformation

$$\lambda \mathbf{x} = K_f \Pi_0 \mathbf{X}$$

##### Taking camera motion into account
Assume that $\mathbf{X} = g \mathbf{X_0}$ ($g$ transforms from world to camera coordinates). We get $\lambda \mathbf{x} = K_f \Pi_0 g \mathbf{X_0}$. Finally assume that the focal length is known: we can normalize our units and drop $K_f$ to get

$$\lambda \mathbf{x} = \Pi_0 \mathbf{X} = \Pi_0 g \mathbf{X}_0$$

### Intrinsic Camera Parameters
We have a number of different coordinate systems by now: World coordinates (3D) -> Camera coordinates (3D) -> Image coordinates (2D) -> Pixel coordinates (2D).

The camera coordinates assume that the optical center is in the middle; in pixel coordinates there are only positive coordinates, and the origin is at the bottom or top left. This shift is encoded by an translation by $(o_x, o_y)$.

If pixel coordinates are not equally scaled in x/y direction, we need scaling factors $s_x, s_y$. For non-rectangular pixels, we need a skew factor $s_\theta$ (typically neglected). Then the *pixel coordinates* are given by

$$\lambda \begin{pmatrix} x' \\ y' \\ 1 \end{pmatrix}
= \begin{pmatrix} s_x & s_\theta & o_x \\ 0 & s_y & o_y \\ 0 & 0 & 1 \end{pmatrix} K_f \Pi_0 \mathbf{X}
= K_s K_f \Pi_0 \mathbf{X}
$$

The combination of focal length information and pixel transformation information gives the *intrinsic camera parameter matrix* $K = K_s K_f$. We further simplify by defining $\Pi = K \Pi_0 g$: $\Pi = [KR, KT] \in \mathbb{R}^{3 \times 4}$ is the *general projection matrix*. This yields

$$\lambda \mathbf{x}' = \Pi X_0$$

 and dividing out $\lambda$, where $\pi_i^\top$ is the i-th row of $\Pi$:
 
 $$x' = \frac{\pi_1^\top \mathbf{X_0}}{\pi_3^\top \mathbf{X_0}}, \quad y' = \frac{\pi_2^\top \mathbf{X_0}}{\pi_3^\top \mathbf{X_0}}, z' = 1$$
 
 ##### Summary of intrinsic parameters
 We have

$$K = K_s K_f = \begin{pmatrix} fs_x & fs_\theta & o_x \\ 0 & fs_y & o_y \\ 0 & 0 & 1 \end{pmatrix}$$

and the parameters are 
- $o_x, o_y$: $x$ and $y$ coordinate of *principal point* (point through which optical axis goes) in pixels
- $f s_x = \alpha_x$: size of unit length *in horizontal pixels*
- $f s_y = \alpha_y$: size of unit length *in vertical pixels*
- $\alpha_x / \alpha_y = \sigma$: aspect ratio
- $f s_\theta \approx 0$: skew of the pixel




### Spherical Perspective Projection
Assumption [[#Mathematics of Perspective Projection|in previous section]]: Projection to an image *plane*. Now instead assume a projection onto a *sphere* of radius $r = 1$. The spherical projection is given by 

$$\pi_s: \mathbf{X} \mapsto X / ||X||$$

We have the same projection equation $\lambda \mathbf{x}' = K \Pi_0 g \mathbf{X}_0$, except that now $\lambda = ||X||$. So as above, $\mathbf{x}' \sim \Pi \mathbf{X}_0$. This property actually holds for any imaging surface where the ray between $\mathbf{X}$ and the origin intersects the surface.


### Radial Distortion
With realistic lenses (no thin lens), there is radial distortion (distortion which is bigger the longer the "radius"/distance from the optical center axis is). Extreme example: *Fisheye lenses*.

![[radial-distortion-bookshelves.png]]

Effective model for distortions ($x_d$, $y_d$ are the distorted coordinates):

$$x = x_d(1 + a_1 r^2 + a_2 r^4), \quad y = y_d (1 + a_1 r^2 + a_2 r^4)$$

This depends on the radius $r = ||(x_d, y_d)||$ and parameters $a_1, a_2$ which can be estimated by calibration.

More general model (for arbitrary center $c$ with four parameters):

$$\mathbf{x} = c + f(r) (\mathbf{x}_d - c), \quad f(r) = 1 + a_1 r + a_2 r^2 + a_3 r^3 + a_4 r^4$$


### Preimages of Points and Lines
The *preimage of a 2D point* is the equivalence class of 3D points that project to that point. Similarly the *preimage of a 2D line* is the set of 3D points that project to a point on the line. We can define preimages for arbitrary geometric regions in the image (but for points/lines they are vector spaces, which is easier).

Some intuition: we can in principle use preimages for multi-view reconstruction: the intersection of point preimages of multiple views gives the 3D target point.

We get the *coimage* of a point/line as the orthogonal complement of its preimage: The coimage is of a point is a plane, and the coimage of a line is a line.

The following relations hold:

$$\text{image} = \text{preimage} \cap \text{image plane}$$
$$\text{preimage} = \text{span}(\text{image})$$
$$\text{preimage} = \text{coimage}^\bot$$
$$\text{coimage} = \text{preimage}^\bot$$

We characterize the preimage of a line $L$ by its normal vector $\ell$ which spans the coimage. In particular, all points $x$ on $L$ are orthogonal to $\ell$: $\ell^\top x = 0$.

The row vectors of $\widehat{\ell}$ span the space of vector orthogonal to $\ell$, so for the preimage it holds that $P = \text{span}(\widehat{\ell})$.

If $x$ is the image of a point $p$, the coimage of $x$ is a plane that is orthogonal to $x$: So it is spanned by the rows of $\widehat{x}$.

![[preimage-coimage-summary.png]]


### Projective Geometry
(We don't dig deeper into this, this is just some background info)

We used homogeneous coordinates to map 3D vectors to 4D vectors $[X; Y; Z; 1]$. We can drop the normalization and identify a 3D point with the line $[XW; YW; ZW; W] \in \mathbb{R}^4, W \in \mathbb{R}$ through the origin of 4D-space (only the 3D direction of this line is what matters).

This leads to the definition of *projective coordinates*: The $n$-dimensional projective space $\mathbb{P}^n$ is the set of all one-dimensional subspaces (lines through the origin) of $\mathbb{R}^{n+1}$.