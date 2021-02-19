import math

def Unrank(n, r):

	phi = []

	for i in range(0, n):
		phi.append(i)

	it = 1

	for i in range(n, 0, -1):
		print("Iteration ", it)
		print("Swapping elements", phi[i - 1], "and", phi[r % i])
		temp = phi[i - 1]
		phi[i - 1] = phi[r % i]
		phi[r % i] = temp
		r = math.floor(r / i)
		print("The new rank is", r)
		it += 1

	return phi


# INSERT INPUT DATA HERE
# output is displayed assuming relations in natural order
# index starts from 0
phi = Unrank(5, 8)
print("Final permutation: ")
for x in phi:
	print(x, end=" ")
