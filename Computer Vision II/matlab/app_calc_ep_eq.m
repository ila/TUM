clear;

T = [-1; 3; 3];
x1 = [2; 2; 1];
R = eye(3);

% for uncalibrated case:
K1 = [5 0 0;
    0 10 0;
    0 0 1];
K2 = [2 0 0
    0 5 0
    0 0 1];

% for calibrated case:
%K1=eye(3);
%K2=eye(3);

[m, b] = getEpipolarLineEquation(T, R, x1, K1, K2);

fprintf('m = %s\n', rats(m));
fprintf('b = %s\n', rats(b));
