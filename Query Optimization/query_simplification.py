import pandas as pd

# this is only the first table!
# does not work with following steps

relations = [
	{'name': 'r0', 'cardinality': 10},
	{'name': 'r1', 'cardinality': 20},
	{'name': 'r2', 'cardinality': 50},
	{'name': 'r3', 'cardinality': 500},
	{'name': 'r4', 'cardinality': 5000},
]

edges = [
	{'nodes': ['r0', 'r1'], 'selectivity': 0.1},
	{'nodes': ['r1', 'r2'], 'selectivity': 0.1},
	{'nodes': ['r2', 'r3'], 'selectivity': 0.01},
	{'nodes': ['r0', 'r3'], 'selectivity': 0.2},
	{'nodes': ['r1', 'r4'], 'selectivity': 0.05},
]

table = pd.DataFrame(columns = ['Relation', 'OrderingBenefit', 'CardinalitySecondJoin'])

for relation in relations:

	neighbours = []

	# traverse edges
	for edge in edges:

		# find edges with a relation in common
		# compute a list of neighbours
		if edge['nodes'][0] == relation['name']:
			neighbours.append({'name': edge['nodes'][1], 'selectivity': edge['selectivity']})


		if edge['nodes'][1] == relation['name']:
			neighbours.append({'name': edge['nodes'][0], 'selectivity': edge['selectivity']})

	# all neighbours have been found
	for neighbour1 in neighbours:
		for neighbour2 in neighbours:
			for relation1 in relations:
				for relation2 in relations:
			
					if neighbour1['name'] != neighbour2['name'] and relation1['name'] != relation2['name'] and relation1['name'] == neighbour1['name'] and relation2['name'] == neighbour2['name']:

									# compute ordering benefit
									join1 = relation['cardinality'] * relation1['cardinality'] * neighbour1['selectivity'] + (relation['cardinality'] * relation1['cardinality'] * neighbour1['selectivity']) * relation2['cardinality'] * neighbour2['selectivity']

									join2 = relation['cardinality'] * relation2['cardinality'] * neighbour2['selectivity'] + (relation['cardinality'] * relation2['cardinality'] * neighbour2['selectivity']) * relation1['cardinality'] * neighbour1['selectivity']

									ordering_benefit = join1 / join2

									# adding cardinality for next steps
									cardinality = relation['cardinality'] * relation2['cardinality'] * neighbour2['selectivity']

									table = table.append({'Relation': relation['name'] + relation1['name'] + ',' + relation['name'] + relation2['name'], 'OrderingBenefit': ordering_benefit, 'CardinalitySecondJoin': cardinality}, ignore_index = True)

print(table.sort_values(by = 'OrderingBenefit', ascending = False))
