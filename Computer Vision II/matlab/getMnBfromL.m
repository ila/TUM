function [m, b] = getMnBfromL(l)
    % given a line l, extract the slope and bias
    m =- l(1) / l(2);
    b =- l(3) / l(2);
end
