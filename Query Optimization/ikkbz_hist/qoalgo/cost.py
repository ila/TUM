from qoalgo.base import JoinTree, QueryGraph


def c_out(qg: QueryGraph, t: JoinTree):
    cost = 1
    for r in t.relations:
        cost *= r.cardinality

    for join in qg.get_joins(t.relations):
        cost *= join.selectivity

    if isinstance(t.left, JoinTree):
        cost += t.left.cost

    if isinstance(t.right, JoinTree):
        cost += t.right.cost

    return cost

