function [u, v] = getUandV2(P, K, g)
    % get the u and v vectors given a point P,
    % the camera Parameters K, motion g
    % g will be inverted
    syms uu vv lambda
    PI_0 = [1 0 0 0; 0 1 0 0; 0 0 1 0];
    P = [P; 1];
    g_inv = inv(g);
    eq = lambda * [uu; vv; 1] == K * PI_0 * g_inv * P;
    [u, v, ~] = solve([eq], [uu vv, lambda]);
end
