clear;

% for a calibrated case where K1=K2=I
T = [-1; 3; 3];
x1 = [2; 2; 1];
R = eye(3);

[m, b] = getEpipolarLineEquation(T, R, x1);
fprintf('m = %s\n', rats(m));
fprintf('b = %s\n', rats(b));
