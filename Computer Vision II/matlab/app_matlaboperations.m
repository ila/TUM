% workhorse for the first exercises "matlab operations"

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% define some of your helper matrixes: 
% TODO adjust once per entire exercise
m = 5;
p = 3;
q = 3;

A = -10 + 10 * rand(p,q);
B = A'*A
u = -5 + 5 *rand(q,1)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% TODO adjust for each subproblem in exercise
% define what you want as result: 
C = kron(B,A);
desired_result = norm(A*u)^2

% define what you want to test

% optional: prefix and suffix of anything in string_test for speedup
prefix = ""; % optional e.g use "size("; as prefix, ")" as suffix of strings
suffix = "";

% all options you want to test, copy & paste from exam
string_test = {
     "sum(kron(A(:),B)*u)"
     "sum(kron(A,u)'*B(:))"
     "kron(u,u)'*B(:)"
     "kron(u,u)'*A(:)"
     "A.*A"
     "sum(sum(kron(u,u')'*B))"
     "sum(sum(kron(A,u)*B))"
     "A*F'"
     "A*A(:)"
     "A```A(:)"
    };


%%%%%%%%%%%%%%%%%%%%%%%%
% test sequentially all cells in string_test
a = testmatlabop(string_test, desired_result, prefix, suffix);
celldisp(a)