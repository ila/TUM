from collections import defaultdict
from typing import List, Iterator


class Relation:
    def __init__(self, name, cardinality):
        self.name = name
        self.cardinality = cardinality

    def __str__(self):
        return self.name

class QueryGraph:

    def __init__(self, relations=None, joins=None):
        self.relations = relations
        self.joins = joins
        if self.relations is None:
            self.relations = []

        if self.joins is None:
            self.joins = []

        self.join_dict = defaultdict(lambda: defaultdict(None))

    def add_join(self, r1, r2, selectivity=0):
        j = Join(r1, r2, selectivity)
        self.join_dict[r1][r2] = j
        self.join_dict[r2][r1] = j
        self.joins.append(j)
        return j

    def add_relation(self, name, cardinality):
        r = Relation(name, cardinality)
        self.relations.append(r)
        return r

    def get_neighbours(self, relations: List[Relation]) -> Iterator[Relation]:
        yielded = []
        for relation in relations:
            for name, join in self.join_dict[relation].items():
                other = join.get_other(relation)
                if other not in relations and other not in yielded:
                    yielded.append(other)
                    yield other

    def get_joins(self, relations: List[Relation]):
        for join in self.joins:
            if join.r1 in relations and join.r2 in relations:
                yield join


class Join:
    def __init__(self, r1, r2, selectivity=-1):
        self.r1 = r1
        self.r2 = r2
        self.selectivity = selectivity

    def get_other(self, r):
        if r == self.r1:
            return self.r2
        return self.r1


class JoinTree:
    def __init__(self, operator='⨝', left=None, right=None, relations=None, cost=-1):
        self.left = left
        self.right = right
        self.cost = cost
        self.relations = relations
        self.operator = operator
        if self.relations is None:
            self.relations = []

    def __str__(self):
        r = ''
        if isinstance(self.left, Relation):
            r += str(self.left)
        else:
            r += '(' + str(self.left) + ')'

        r += self.operator

        if isinstance(self.right, Relation):
            r += str(self.right)
        else:
            r += '(' + str(self.right) + ')'

        return r