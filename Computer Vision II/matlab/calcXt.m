function [xt_new] = calcXt(r, xt, mode)
    % r is a function
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
    else
        fprintf('choose a valid algorithm');
    end

end
