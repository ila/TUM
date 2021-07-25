function [xt_new] = calcXt(f, r, xt, mode)
    % f is the function needed for gradient descent and newton
    %  => you can enter whatever value if you don't use gd or newt
    % r is the residual function
    % xt is the value of x at iteration t
    % mode defines the algorithm to use
    % Gauss-Newton or Levenberg-Marquardt
    % returns the value of x after iteration t+1
    if strcmp(mode, 'gn') % Gauss-Newton
        r_prime = diff(r);
        r_triangle = -subs(r, xt) * inv(subs(r_prime, xt));
        xt_new = xt + r_triangle;
    elseif strcmp(mode, 'lm')
        % Levenberg-Marquardt
        lambda = 0; % TODO adjust if given
        r_prime = diff(r);
        r = subs(r, xt);
        r_prime = subs(r_prime, xt);
        r_triangle = -inv(r_prime * r_prime + lambda * r_prime * r_prime) * r_prime * r;
        xt_new = xt + r_triangle;
    elseif strcmp(mode, 'gd')
        % Gradient Descent
        step = 3; % TODO adjust if given
        f_prime = diff(f);
        f_prime = subs(f_prime, xt);
        f_triangle =- step * f_prime;
        xt_new = xt + f_triangle;
    elseif strcmp(mode, 'newt')
        % Newton
        f_prime = diff(f);
        f_prime = subs(f_prime, xt);
        h = diff(diff(f));
        h = subs(h, xt);
        f_triangle =- f_prime * inv(h);
        xt_new = xt + f_triangle;
    else
        fprintf('choose a valid algorithm');
    end

end
