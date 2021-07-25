function r = checkLFconstraint(l1, PI1, l2, PI2, l3, PI3)
    % checks if the line feature rank constraint
    % holds, given three lines and three projection matrices
    % see Ch6, slide 14
    % returns the rank
    Wl = [
        l1.' * PI1;
        l2.' * PI2;
        l3.' * PI3;
        ];
    r = rank(Wl);
    fprint('r = %d\n', r);

    if r <= 2
        fprintf('rank constraint holds\n');
    else
        fprintf('rank constraint does not hold\n');
    end

end
