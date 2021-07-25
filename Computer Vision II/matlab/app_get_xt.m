clear;
% Exam P10 e)
syms x;

% TODO: write down the equations
Max = @(aa, bb) piecewise(aa > bb, aa, bb);
r1 = sign(x) * abs(x)^(1/3);
r2 = x^2;
r3 = Max(x, 2 * x - 1); % wtf

% Tip: try the easy diffs first
r = r2; % TODO adjust r here in the exam
x0 = 2; % TODO adjust
mode = 'lm'; % TODO choose either gn or lm
x1 = calcXt(r, x0, mode);
x2 = calcXt(r, x1, mode);

fprintf('x1 = %s \n', rats(x1));
fprintf('x2 = %s \n', rats(x2));
