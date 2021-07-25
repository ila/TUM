clear;
% Exam P10 e)
syms x;

% internal max and min functionc can't be differentiated
Max = @(aa, bb) piecewise(aa > bb, aa, bb);
Min = @(aa, bb) piecewise(aa < bb, aa, bb);

% TODO: write down the residuals
r1 = sign(x) * norm(x)^(1/3);
r2 = x^2;
r3 = Max(x, 2 * x - 1);

% TODO if using gn or nt define f
f = (x + 1)^2; % replace with the given function

r = r3; % TODO adjust r
x0 = 1; % TODO adjust for given value
mode = 'lm'; % choose either gn or lm

% you might have to modify calcXt for given lambda or step values
% if you use lm or gd
x1 = calcXt(f, r, x0, mode);
x2 = calcXt(f, r, x1, mode);

fprintf('x1 = %s \n', rats(x1));
fprintf('x2 = %s \n', rats(x2));
