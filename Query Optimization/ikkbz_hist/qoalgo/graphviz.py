from qoalgo.base import JoinTree, Relation, QueryGraph


def _precedence_graph_to_graphviz_inner(pg):
    dot = f'"{str(pg)}" [label="{str(pg)}"];\n'
    for child in pg.children:
        dot += f'"{str(pg)}" -> "{str(child)}"; \n'
        dot += _precedence_graph_to_graphviz_inner(child)

    return dot


def precedence_graph_to_graphviz(pg):
    dot = 'digraph G {\n'
    dot += _precedence_graph_to_graphviz_inner(pg)
    dot += '}'
    return dot


def join_tree_to_graphviz_inner(t):
    if isinstance(t, Relation):
        return f'"{hash(t)}" [label="{str(t)}"];\n'

    dot = f'"{hash(t)}" [label="{str(t.operator)} c:{t.cost:.2f}"];\n'
    for c in [t.left, t.right]:
        if c is None:
            continue
        dot += f'"{hash(t)}" -> "{hash(c)}"; \n'
        dot += join_tree_to_graphviz_inner(c)

    return dot


def join_tree_to_graphviz(t):
    dot = 'digraph G {\n'
    dot += join_tree_to_graphviz_inner(t)
    dot += '}'
    return dot


def querygraph_to_graphviz(qg: QueryGraph, cardinalities=True, selectivities=True):
    dot = 'digraph G {\n'

    for r in qg.relations:
        dot += f'"{hash(r)}" [label="{str(r)}'
        if cardinalities:
            dot += f' ({r.cardinality})'
        dot += '"];\n'

    for j in qg.joins:
        dot += f'"{hash(j.r1)}" -> "{hash(j.r2)}" [dir=none'
        if selectivities:
            dot += f', label="{j.selectivity:.2f}"'
        dot += '];\n'

    dot += '}'
    return dot
