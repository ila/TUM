# IKKBZ implementation with input data

import pandas as pd

end = False
root = True
relations = pd.DataFrame(columns = ['relation', 'n', 's', 'C', 'T', 'rank'])

print("IKKBZ table simulation")
print("Note: this only works knowing merges, with less than 10 relations")
print("\nFirst of all, insert the query graph starting from the root")
print("Fields must be separated by a space")
print("Type \"end\" after the last relation")

# first of all collecting input
while not end:

	print("\nEnter relation name - cardinality - selectivity")

	r = input()

	if r == "end":
		end = True

	else:

		data = r.split()

		C = int(data[1]) * float(data[2])
		T = int(data[1]) * float(data[2])

		if root: 
			rank = 0.0
			root = False

		else: 
			rank = (T - 1) / C

		json = {'relation': data[0], 'n': data[1], 's': data[2], 'C': C, 'T': T, 'rank': rank}
		relations = relations.append(json, ignore_index=True)

print("\n", relations)
end = False

while not end:

	print("\nInsert names of relations to normalize: ")
	print("Type \"end\" after the last merge")
	r = input()

	if r == "end":
		end = True

	else:

		data = r.split()

		C = float(relations[relations['relation'] == data[0]]['C']) + float(relations[relations['relation'] == data[0]]['T']) * float(relations[relations['relation'] == data[1]]['C'])

		T = float(relations[relations['relation'] == data[0]]['n']) * float(relations[relations['relation'] == data[0]]['s']) * float(relations[relations['relation'] == data[1]]['n']) * float(relations[relations['relation'] == data[1]]['s'])

		n = float(relations[relations['relation'] == data[0]]['n']) * float(relations[relations['relation'] == data[1]]['n']) 

		s = float(relations[relations['relation'] == data[0]]['s']) * float(relations[relations['relation'] == data[1]]['s']) 

		rank = (T - 1) / C

		names = [int(s) for s in data[1] if s.isdigit()]
		name = ''.join(map(str, names))

		json = {'relation': data[0] + name, 'n': n, 's': s, 'C': C, 'T': T, 'rank': rank}
		relations = relations.append(json, ignore_index=True)

		print("\n", relations)


