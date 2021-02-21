from operator import attrgetter

from qoalgo.base import JoinTree
from qoalgo.cost import c_out


class PrecedenceGraph:
    def __init__(self, relations=None, rank=-1, cardinality=-1, selectivity=-1, cost=-1, t=-1, children=None):
        self.relations = relations
        self.rank = rank
        self.cardinality = cardinality
        self.selectivity = selectivity
        self.cost = cost
        self.t = t
        self.children = children

        if self.relations is None:
            self.relations = []
        if self.children is None:
            self.children = []

    def __str__(self):
        return '(' + ','.join([str(r) for r in self.relations]) + ')'

    def get_rank_table(self):
        return f'{str(self)} \t n: {self.cardinality} \t s: {self.selectivity:.2f} \t'\
               f'C: {self.cost:.2f} \t T: {self.t} \t rank: {self.rank:.3f}\n' + \
                ''.join([c.get_rank_table() for c in self.children])

    def get_rank_table_dict(self):
        root = [{
            'label': str(self),
            'n': self.cardinality,
            's': self.selectivity,
            'C': self.cost,
            'T': self.t,
            'rank': self.rank
        }]
        for child in self.children:
            root += child.get_rank_table_dict()
        return root

def generate_precedence_graph_rooted(qg, root, visited=None):
    if visited is None:
        visited = [root]
    pg = PrecedenceGraph(relations=[root], cardinality=root.cardinality)
    for _, join in qg.join_dict[root].items():
        r2 = join.get_other(root)
        if r2 in visited:
            continue

        visited.append(r2)
        child_pg = generate_precedence_graph_rooted(qg, r2, visited)
        child_pg.selectivity = join.selectivity
        child_pg.cost = child_pg.cardinality * child_pg.selectivity
        child_pg.t = child_pg.cardinality * child_pg.selectivity
        child_pg.rank = (child_pg.t - 1) / child_pg.cost
        pg.children.append(child_pg)

    return pg


def generate_precedence_graphs(qg):
    for r in qg.relations:
        yield generate_precedence_graph_rooted(qg, r)


def is_chain(pg):
    child = pg
    while len(child.children) > 0:
        if len(child.children) > 1:
            return False
        child = child.children[0]
    return True


def find_children_chain_only(pg):
    root = pg

    if len(root.children) == 0:
        return None

    for child in root.children:
        if not is_chain(child):
            return find_children_chain_only(child)

    return root

def ikkbz_normalize(pg):
    if not is_chain(pg):
        raise Exception("precedence graph has to be a chain in order to run ikkbz_normalize")
    root = pg
    while len(root.children) > 0:
        child = root.children[0]
        if root.rank > child.rank:
            root.relations += child.relations
            root.cost = root.cost + root.t * child.cost
            root.t = root.t * child.t
            root.rank = (root.t - 1) / root.cost
            root.cardinality = -1
            root.selectivity = -1
            root.children = child.children
        else:
            root = child

def iterate_precedence_graph_children(pg):
    for child in pg.children:
        yield child
        yield from iterate_precedence_graph_children(child)


def ikkbz_merge_children(pg):
    children = list(iterate_precedence_graph_children(pg))
    children = sorted(children, key=attrgetter('rank'))
    root = pg
    for child in children:
        root.children = [child]
        root = child

def ikkbz_sub_step(pg):
    child_chain_only = find_children_chain_only(pg)
    for child in child_chain_only.children:
        ikkbz_normalize(child)
    ikkbz_merge_children(child_chain_only)

def ikkbz_denormalize(pg):
    root = pg
    if len(root.relations) > 1:
        child = PrecedenceGraph(relations = root.relations[1:], children=root.children)
        root.relations = [root.relations[0]]
        root.children = [child]
    if len(root.children) > 0:
        ikkbz_denormalize(root.children[0])


def precedence_to_join_tree(qg, pg, cost_fn=None):
    """
    todo!
    :param qg:
    :param pg:
    :param cost_fn:
    :return:
    """
    if cost_fn is None:
        cost_fn = c_out
    parent = pg
    child = pg.children[0]
    relations = parent.relations + child.relations
    jt = JoinTree('HJ', left=parent.relations[0], right=child.relations[0], relations=relations, cost=0)
    jt.cost = cost_fn(qg, jt)

    while len(child.children) > 0:
        relations = jt.relations + child.children[0].relations
        jt = JoinTree('HJ', left=jt, right=child.children[0].relations[0], relations=relations)
        jt.cost = cost_fn(qg, jt)
        child = child.children[0]

    return jt


def ikkbz_sub(pg):
    while not is_chain(pg):
        ikkbz_sub_step(pg)
    ikkbz_denormalize(pg)