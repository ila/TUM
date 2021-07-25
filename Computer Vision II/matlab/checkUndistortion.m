function checkUndistortion(x1, X1, x2, X2, x3, X3)
    % check if a undistortion worked perfectly
    % given the three image points and three 3D points
    X2X1 = X2 - X1;
    X3X2 = X3 - X2;
    x2x1 = x2 - x1;
    x3x2 = x3 - x2;

    if ~isequal(X2X1, X3X2)
        fprintf('Undistortion did not work perfectly (X2X1 X3X2)\n');
    end

    if ~isequal(x2x1, x3x2)
        fprintf('Undistortion did not work perfectly (x2x1 x3x2)\n');
    end

end
