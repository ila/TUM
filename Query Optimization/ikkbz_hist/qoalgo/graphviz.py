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
