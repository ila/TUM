from collections import defaultdict


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


class Relation:
    def __init__(self, name, cardinality):
        self.name = name
        self.cardinality = cardinality

    def __str__(self):
        return self.name


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
    def __init__(self, operator='HJ', left=None, right=None, relations=None, cost=-1):
        self.left = left
        self.right = right
        self.cost = cost
        self.relations = relations
        if self.relations is None:
            self.relations = []