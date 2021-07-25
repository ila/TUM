clear;

% Exam P9 d)
% check the epipolar constraint for a pair of points
R = eye(3);
T = [5; 4; 1];

% write in non-homog coords
% as the function takes care of it
% saves time during exam
x1s = {
    [30; 20]
    [30; 20]
    [50; 20]
    [50; 20]
    };
x2s = {
    [5; 5]
    [20; 68/5]
    [2; 139/45]
    [31/16; 3]
    };

checkEpipolarConstraints(x1s, x2s, T, R);

% Alternative for single point:
%
%x1=[30;20;1];
%x2=[2;139/45;1];
%result = checkEpipolarConstraint(x1,x2,T,R);
%fprintf('r = %d\n', result);
