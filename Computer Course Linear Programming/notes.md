### Computer course: Linear Programming

This course explains ways to solve complicated mathematical problems with the aid of computers, in specific Python and Gurobi.

Gurobi is a (proprietary) module to solve problems related to linear, quadratic and mixed integer programming in several coding languages.



#### Python basics

Python is open source (!!!) and easy to learn. It has many useful libraries to tackle high-level elements such as data visualization or graphs.

However, it is slower than other compiled languages. 



#### Linear Programming

Linear programs are in the form $min\ c^Tx\ s. t. Ax \leq b$, with an objective function $c$ over a feasible region (intersection of hyperplanes).

Solving linear programming problems can be done through the simplex method, iterating through edges until the optimal solution is found. 

An optimal solution minimizes the value of the objective function while respecting all the constraints. 



#### Gurobi

```python
from gurobipy import *

m = Model()

x = m.addVar(vtype=GRB.CONTINUOUS)
y = m.addVar(vtype=GRB.CONTINUOUS)
```

Variable types:

* Continuous;
* Binary;
* Integer;
* Semi-continuous $\{0\} \cup (a, b)$;
* Semi-integer $\{0\} \cup (a, b) \cap \mathbb{Z}$;

```python
addVar(lb=0, ub=GRB.INFINITY, obj=0.0, vtype=GRB.CONTINUOUS, name="")
addVars(indices, lb=0, ub=GRB.INFINITY, obj=0.0, vtype=GRB.CONTINUOUS, name="")

c1 = m.addConstr(2*x+y>=3)
c2 = m.addConstr(2*x+2*y>=5)

m.setObjective(3*x+5*y, GRB.MINIMIZE)

m.optimize()
```

- `lb`, `ub`, lower and upper bound;
- `obj`, coefficient of the linear objective function;
- `vtype`, variable type,
- `name`, name for further referencing:
- `indices`, array used to generate the set of variables.

Linear expression can be created by mathematical symbols, `x.prod()`, `x.sum()` or `quicksum([2*x, 3*y])`.

Gurobi runs multiple linear methods in parallel and outputs the solution of the fastest.