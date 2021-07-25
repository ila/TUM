clear;
P = [8; -1; 1]; % TODO
C = [4; 2; 3]; % TODO
R = eye(3); % might be different in exam
K = [% TODO
    640 0 320;
    0 480 240;
    0 0 1;
    ];

[u, v] = getUandV(P, K, C, R);

fprintf('u =\t%d\n', u);
fprintf('v =\t%d\n', v);
