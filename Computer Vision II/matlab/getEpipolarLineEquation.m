function [m, b] = getEpipolarLineEquation(T, R, p, K1, K2)
    % get the slope and bias given
    % T, R and a point p in homogeneous coordinates
    assert(numberofelements(T) == 3);
    assert(numberofelements(R) == 9);
    assert(numberofelements(p) == 3);
    T_hat = hat(T);
    E = inv(K2).' * T_hat * R * inv(K1);

    % if p is a true pixel coordinate you have to change
    % the following equation to
    % l = E * p; % because K1 was already applied then
    l = E * K1 * p;

    [m, b] = getMnBfromL(l)
end
