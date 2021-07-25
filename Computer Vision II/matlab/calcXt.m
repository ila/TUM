function [xt_new] = calcXt(r, xt)
    % r is a function
    % xt is the value of x at iteration t
    % returns the value of x after iteration t+1
    r_prime = diff(r);
    r_triangle = -subs(r, xt) * inv(subs(r_prime, xt));
    xt_new = xt + r_triangle;
end
