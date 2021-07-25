clear;

C = [1; 0; 0];
R = eye(3);
X1 = [0; 1; 2; 1];
X2 = [1; 0; 1; 1];
x1 = [230; 300; 1];
x2 = [280; 200; 1];

[fx, fy, ox, oy] = getIntrinsicCamera(x1, X1, x2, X2, C, R)
