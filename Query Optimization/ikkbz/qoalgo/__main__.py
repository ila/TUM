#/usr/bin/env python3
import argparse

from qoalgo.base import QueryGraph
from qoalgo.encoding import unrank_permutation
from qoalgo.graphviz import precedence_graph_to_graphviz
from qoalgo.ikkbz import generate_precedence_graph_rooted, ikkbz_sub, is_chain, ikkbz_sub_step, ikkbz_denormalize, \
    generate_precedence_graphs, precedence_to_join_tree


def ikkbz_verbose(pg):
    print(pg.get_rank_table())
    print(precedence_graph_to_graphviz(pg))
    while not is_chain(pg):
        ikkbz_sub_step(pg)
        print(pg.get_rank_table())
        print(precedence_graph_to_graphviz(pg))
    ikkbz_denormalize(pg)
    print(pg.get_rank_table())
    print(precedence_graph_to_graphviz(pg))


def ex05():
    qg = QueryGraph()
    A = qg.add_relation('A', 10)
    B = qg.add_relation('B', 10)
    C = qg.add_relation('C', 100)
    D = qg.add_relation('D', 100)

    jAB = qg.add_join(A, B, 0.9)
    jAC = qg.add_join(A, C, 0.1)
    jBD = qg.add_join(B, D, 0.1)

    for r in [A,B,C,D]:
        print("ROOT IN " + str(r))
        precedence_graph = generate_precedence_graph_rooted(qg, r)
        ikkbz_verbose(precedence_graph)

        jt = precedence_to_join_tree(qg, precedence_graph)



def main():
    # Use a breakpoint in the code line below to debug your script.
    qg = QueryGraph()
    r1 = qg.add_relation('R1', 30)
    r2 = qg.add_relation('R2', 20)
    r3 = qg.add_relation('R3', 30)
    r4 = qg.add_relation('R4', 50)
    r5 = qg.add_relation('R5', 2)

    j12 = qg.add_join(r1, r2, 1 / 5)
    j13 = qg.add_join(r1, r3, 1 / 3)
    j34 = qg.add_join(r3, r4, 1 / 10)
    j35 = qg.add_join(r3, r5, 1)
    precedence_graph = generate_precedence_graph_rooted(qg, r3)
    ikkbz_verbose(precedence_graph)

    jt = precedence_to_join_tree(qg, precedence_graph)
    print("XX")


def ex06():
    qg = QueryGraph()
    r0 = qg.add_relation('R0', 20)
    r1 = qg.add_relation('R1', 10)
    r2 = qg.add_relation('R2', 50)
    r3 = qg.add_relation('R3', 40)
    r4 = qg.add_relation('R4', 50)
    r5 = qg.add_relation('R5', 55)
    r6 = qg.add_relation('R6', 50)
    r7 = qg.add_relation('R7', 60)

    j01 = qg.add_join(r0, r1, 0.1)
    j02 = qg.add_join(r0, r2, 0.2)
    j06 = qg.add_join(r0, r6, 0.2)
    j67 = qg.add_join(r6, r7, 0.2)
    j25 = qg.add_join(r2, r5, 0.2)
    j23 = qg.add_join(r2, r3, 0.2)
    j34 = qg.add_join(r3, r4, 0.3)

    precedence_graph = generate_precedence_graph_rooted(qg, r0)
    print(precedence_graph_to_graphviz(precedence_graph))
    ikkbz_verbose(precedence_graph)
    print(precedence_graph_to_graphviz(precedence_graph))


def unrank():
    unrank_permutation([1, 2, 3, 4, 5], 8, True)
    #unrank_permutation([1, 2, 3, 4, 5,6,7,8], 64, True)


if __name__ == '__main__':
    import argparse

    # ...
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "relations",
        help="""A list of relations separated by a comma including their cardina\
                lity e.g. R1:10,R2:20,R3:50"""
    )
    parser.add_argument(
        "selectivities",
        help="""The selectivities for the given relations with the syntac RELATI\
                ON1:RELATION2:SELECTIVITY e.g. R1:R2:0.4,R2:R3:0.6"""
    )
    parser.add_argument(
        "root",
        help="the root relation for the precedence graph e.g. R1"
    )
    args = parser.parse_args()

    qg = QueryGraph()
    relations = {}
    for r in args.relations.split(','):
        name, cardinality = r.split(':')
        relations[name] = qg.add_relation(name, float(cardinality))

    for e in args.selectivities.split(','):
        r1, r2, selectivity = e.split(':')
        qg.add_join(relations[r1], relations[r2], float(selectivity))

    precedence_graph = generate_precedence_graph_rooted(qg, relations[args.root])
    ikkbz_verbose(precedence_graph)

