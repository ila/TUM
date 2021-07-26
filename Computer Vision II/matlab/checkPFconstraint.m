function r = checkPFconstraint(x1, PI1, x2, PI2, x3, PI3)
    % checks if the point feature rank constraint
    % holds, given three points and three projection matrices
    % see Ch6, slide 13
    % returns the rank
    Wp = [
        hat(x1) * PI1;
        hat(x2) * PI2;
        hat(x3) * PI3;
        ];
    r = rank(Wp);
    fprintf('r = %d\n', r);

    if r <= 3
        fprintf('rank constraint holds\n');
    else
        fprintf('rank constraint does not hold\n');
    end

end
