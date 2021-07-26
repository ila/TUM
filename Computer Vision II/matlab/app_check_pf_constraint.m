clear;
syms x

% boilerplate for PG rank check
x1 = [3; 2; 1];
x2 = [6; 5; 1];
x3 = [2; 1; 1];

T1 = [0; 0; 0];
T2 = [0; 2; -2];
T3 = [0; -2; 2];

R = eye(3);

PI1 = zeros(3, 4);
PI1(1:3, 1:3) = R;
PI1(1:3, 4) = T1;

PI2 = zeros(3, 4);
PI2(1:3, 1:3) = R;
PI2(1:3, 4) = T2;

PI3 = zeros(3, 4);
PI3(1:3, 1:3) = R;
PI3(1:3, 4) = T3;

r = checkPFconstraint(x1, PI1, x2, PI2, x3, PI3);
