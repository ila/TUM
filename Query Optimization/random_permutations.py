import math

def unrank(n, r):
	"""
	Unrank a permutation with n elements (index starting from 0).
	:param n: number of elements to be permuted
	:param r: rank
	:return: permutation pi
	"""
	pi = []

	for i in range(0, n):
		pi.append(i)

	return unrank_perm(pi, r)


def unrank_perm(pi, r):
	"""
	Unrank a permutation pi
	:param pi: initial permutation to unrank
	:param r:  rank
	:return: unranked permutation pi
	"""
	print("\tswapping")
	print("\tv\tv")
	print("i\ti-1\tr%i\t⌊r/i⌋\tπ")
	print("\t\t\t\t\t{}".format(" ".join(map(str, pi))))
	for i in range(len(pi), 0, -1):
		temp = pi[i - 1]
		pi[i - 1] = pi[r % i]
		pi[r % i] = temp
		new_r = math.floor(r / i)

		print("{}\t{}\t{}\t{}\t\t{}".format(i, i-1, r % i, new_r, " ".join(map(str, pi))))

		r = new_r

	return pi


# INSERT INPUT DATA HERE
# output is displayed assuming relations in natural order
# index starts from 0
# piFinal = unrank(8, 64)
piFinal = unrank_perm(["r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8"], 64)
print("\nFinal permutation: ", " ".join(map(str, piFinal)))
