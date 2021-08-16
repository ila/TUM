### Reconstruction with Uncalibrated Camera
We cannot assume in general that we know the calibration matrix $K$, i.e. for internet images!

Rewrite the epipolar constraint: Replace $x_1$ with $K^{-1} x_1'$, $x_2$ with $K^{-1} x_2'$. Then => 
$$x_2'^\top F x_1' = 0, ~~F := (K^{-1})^\top E K^{-1} \quad \text{(Epipolar constraint for uncalibrated cameras)}$$

$F$ is called the *fundamental matrix*. It has an SVD $U \text{diag}(\sigma_1, \sigma_2, 0) V^\top$ (in fact, it can be an arbitrary rank 2 matrix; space much larger than $\mathcal{E}$).

##### Uncalibrated Reconstruction and Limitations
8-point algorithm can be extended to estimate $F$ instead of $E$. But a given $F$ can not uniquely (even up to scale) be decomposed into $R, T, K$.

Possible: find *projective reconstructions*, i.e. geometry reconstructions defined up to projective transformations => find *canonical reconstruction* from these. But in reality, one rather calibrates the camera instead.


# Chapter 6 - Reconstruction from Multiple Views
Not just two, but multiple views. Each new view gives 6 new parameters, but many more point measurements -> ratio params / measurements improves.

Different approaches:
- trifocal tensors ("trilinear relations" between three images, generalize Fundamental Matrix)
	- Textbooks: Faugeras and Luong 2001; Hartley and Zisserman 2003
- matrices instead of tensors
	- Textbook: Invitation to 3D vision


### Preimages
- Preimage of a point/line on the image plane: points that get projected to that point/line 
- Preimage of points/lines from multiple views: Intersection of preimages
$$\text{preimage}(x_1,\dots,x_m) = \bigcap_i \text{preimage}(x_i)$$

The preimage of multiple lines should be a line for the reconstruction to be consistent.

![[Pasted-image-20210529195238.png]]

Next denote time-dependent image coordinates by $x(t)$. Parametrize 3D lines in homog. coord. as $L = {X_0 + \mu V}$. $L$'s preimage is a plane $P$ with normal $\ell(t)$, $P = \text{span}(\hat{\ell})$.

The $\ell$ is orthogonal to points $x$ on $L$: $\ell(t) x(t) = \ell(t) K(t) \Pi_0 g(t) X = 0$ (why?)

Then $\lambda_i x_i = \Pi_i X$ (relation i-th image of point p <-> world coordinates $X$) and $\ell_i^\top \Pi_i X_0 = \ell_i^\top \Pi_i V = 0$ (relation i-th coimage of $L$ <-> world coordinates $X_0, V$)