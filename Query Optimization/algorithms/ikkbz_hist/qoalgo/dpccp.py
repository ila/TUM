from collections import defaultdict
from itertools import chain, combinations
from typing import List, Iterator

from qoalgo.base import QueryGraph, Relation, JoinTree
from qoalgo.cost import c_out


def dp_key(relations):
    """
    generates a unique key for the dptable dictionary
    :param relations: set of relations
    :return: str
    """
    return '-'.join(sorted([r.name for r in relations]))


def powerset(iterable):
    """
    source: https://docs.python.org/3/library/itertools.html#itertools-recipes
    powerset([1,2,3]) --> () (1,) (2,) (3,) (1,2) (1,3) (2,3) (1,2,3)
    :param iterable:
    :return:
    """

    s = list(iterable)
    return chain.from_iterable(combinations(s, r) for r in range(len(s) + 1))


class Dpccp():

    def __init__(self, qg: QueryGraph, cost_fn=None):
        """

        :param qg: query graph, the relations of qg.relations should already be in BFS order!
        """
        self.dptable = defaultdict(lambda: None)  # only contains the best
        self.dpverbose = defaultdict(list)  # containing all possibilities
        self.cost_fn = cost_fn
        self.qg = qg

        if self.cost_fn is None:
            self.cost_fn = c_out

    def init_dp_table(self):
        for r in self.qg.relations:
            self.dptable[dp_key([r])] = r

    def run(self):
        self.init_dp_table()
        for csg, cmp in self.get_csg_cmp_pairs():
            p1 = self.dptable[dp_key(csg)]
            p2 = self.dptable[dp_key(cmp)]
            P = JoinTree(left=p1, right=p2, relations=csg + cmp)
            P.cost = self.cost_fn(self.qg, P)
            cost = self.cost_fn(self.qg, P)
            self.dpverbose[dp_key(csg + cmp)].append(P)
            if self.dptable[dp_key(csg + cmp)] is None or self.dptable[dp_key(csg + cmp)].cost > cost:
                self.dptable[dp_key(csg + cmp)] = P

        return self.dptable[dp_key(self.qg.relations)]

    def get_csg_cmp_pairs(self):
        for csg in self.enumerate_csg():
            for cmp in self.enumerate_cmp(csg):
                yield csg, cmp

    def enumerate_csg(self) -> Iterator[List[Relation]]:
        for relation in reversed(self.qg.relations):
            yield [relation]
            yield from self.enumerate_csg_rec([relation], [r for r in self.qg.relations
                                                           if self.qg.relations.index(r)
                                                           <= self.qg.relations.index(relation)])

    def enumerate_csg_rec(self, relations: List[Relation], blocked: List[Relation]):
        neighbours = [n for n in self.qg.get_neighbours(relations) if n not in blocked]
        neighbour_subsets = [list(n) for n in powerset(neighbours) if len(list(n)) > 0]
        for neighbour in neighbour_subsets:
            yield relations + neighbour

        for neighbour in neighbour_subsets:
            yield from self.enumerate_csg_rec(relations + neighbour, blocked + neighbours)

    def enumerate_cmp(self, csg):
        smallest_index = len(self.qg.relations)
        for r in csg:
            if self.qg.relations.index(r) < smallest_index:
                smallest_index = self.qg.relations.index(r)

        blocked = [r for r in self.qg.relations if self.qg.relations.index(r) <= smallest_index] + csg
        neighbours = [n for n in self.qg.get_neighbours(csg) if n not in blocked]

        for n in reversed(neighbours):
            yield [n]
            blocked_n = [r for r in self.qg.relations if self.qg.relations.index(r) <= self.qg.relations.index(n)]
            yield from self.enumerate_csg_rec([n], blocked + list(set(blocked_n) & set(neighbours)))
