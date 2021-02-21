import math

def unrank_permutation(relations, rank, verbose=False):
    for i in range(len(relations), 0, -1):
        relations[i-1], relations[rank % i] = relations[rank % i], relations[i-1]
        if verbose:
            print(f'{len(relations) - i + 1}: r: {rank}, i: {i} || i-1: {i-1}, r%i: {rank % i}, {relations}')
        rank = math.floor(rank/i)