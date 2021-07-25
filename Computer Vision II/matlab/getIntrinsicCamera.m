function [ffx, ffy, oox, ooy] = getIntrinsicCamera(x1, X1, x2, X2, C, R)
    syms fx fy ox oy lambda1 lambda2
    % x X must be homogeneous coordinates

    % init
    PI_0 = [1 0 0 0; 0 1 0 0; 0 0 1 0]; % standard projection
    K = [fx 0 ox; 0 fy oy; 0 0 1]; % camera parameters
    T = -C; % translation = -C
    g = [R T; 0 0 0 1];

    % equations
    eq1 = lambda1 * x1 == K * PI_0 * g * X1;
    eq2 = lambda2 * x2 == K * PI_0 * g * X2;

    % solve
    [ffx, ffy, oox, ooy, ~, ~] = solve([eq1, eq2], [fx fy ox oy, lambda1, lambda2]);
end
