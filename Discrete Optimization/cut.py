# usage: python3 cut.py
# change the input!

import numpy as np

# directly insert the subset of A here
Ab = np.array([[2, 0], [1, 3]])

# insert the vector with respect to which calculate the cut
v = np.array([1, 2])

# optimum point
x = np.array([3, 8/3])

# do not edit below this line

# matrix inversion
Ab_inv = np.linalg.inv(Ab)
print('Inverse of Ab: \n', Ab_inv)

# multiply the two
v_Ab_inv = np.matmul(np.transpose(v), Ab_inv)
print('\nv^t * Ab^t^-1 (column vector): \n', v_Ab_inv)

# now calculate y
y = v_Ab_inv - np.floor(v_Ab_inv)
print('\ny^t: \n', y)

# now calculate q
q = np.matmul(np.transpose(y), Ab)
print('\nq: \n', q)

# finally, find the cut
qx = np.matmul(q, x)
print('\nq^t x*: \n', qx)

string = ''

it = np.nditer(q, flags=['f_index'])
for i in it:
	if (i >= 0):
		string += ' + '
	#else:
	#	string += '-'
	string += str(i)[0:1] + 'x' + str(it.index + 1)

print('\n', string[1:], ' <= abs(', str(qx)[0:4], ') = ', np.floor(qx))
