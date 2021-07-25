clear;

X1 = [-1; 0; 10];
X2 = [1; 1; 10];
X3 = [3; 2; 10];
x1 = [270; 240];
x2 = [370; 280];
x3 = [470; 310];

X2X1 = X2 - X1;
X3X2 = X3 - X2;
x2x1 = x2 - x1;
x3x2 = x3 - x2;

checkUndistortion(x1, X1, x2, X2, x3, X3);
