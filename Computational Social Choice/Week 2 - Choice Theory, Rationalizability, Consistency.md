### Individual Choice
$U$ is a universe of alternatives, $F(U) = 2^U \setminus \{\emptyset\}$ are the *feasible sets*.

Choice function: $S : F(U) \to F(U)$ s.t. $\forall A: S(A) \subseteq A$.

Define the set of maximal elements ("maximal set") wrt. a relation as $Max(R, A) = \{x \in Y \mid \not\exists y \in A : y P x\}$.

### Transitivity Notions
We use the following transitivity notions: A binary relation $R$ is

1. *transitive* if $\forall x, y, z$: $xRy \wedge yRz \implies xRz$
2. *quasi-transitive* if $\forall x,y,z: x P y \wedge y P z \implies x P z$
3. *acyclic* if for all $x_1, \dots, x_n: x_1 P x_2 P \dots P x_n \implies x_1 R x_n$

Transitivity is stronger than quasi-transitivity, which in turn is stronger than acyclicity.

*Lemma*. If $R$ is acyclic, $Max(R, A) \neq \emptyset$.


## Rationalizability
A choice function $S$ is *rationalizable* if there is a preference relation $R$ s.t. $\forall A \in F(U): S(A) = \max(R, A)$.

The *base relation* $R_S$ is defined by $x R_S y \Leftrightarrow x \in S(\{x, y\})$.

*Lemma*. $S$ is rationalizable if and only if it is rationalized by its base relation.


## Inconsistencies
### Contraction consistency $\alpha$
S satisfies  contraction $\alpha$ if $\forall A,B∈F(U): B⊆A \Rightarrow S(A)∩B ⊆ S(B)$

Lemma 3: $S$ satisfies $\alpha$ => base relation $R_S$ is acyclic.

However, $\alpha$ is not sufficient for rationalizability. Example:

| X   | S(X) |
|-----|------|
| ab  | ab   |
| bc  | b    |
| ac  | a    |
| abc | a    |


### Expansion consistency $\gamma$
$S$ satisfies expansion $\gamma$ if $\forall A, B\in F(U): S(A) \cap S(B) \subseteq S(A \cup B)$

Plurality fails to satisfy $\gamma$:  e.g. it is possible that $S(\{a, b\}) = \{b\}$, $S(\{b, c\}) = \{b\}$, but $S(\{a, b, c\}) = \{a\}$.

$\alpha$ and $\gamma$ are independent of each other.


## Rationalizability and Consistency
> **Theorem** (Sen's Theorem, 1973).
> $S$ is rationalizable if and only if it satisfies $\alpha$ and $\gamma$.


Alternative characterization of $\alpha, \gamma$ and rat. by Schwartz: (for all $A, B$ **and** $x \in A \cap B$ it holds that ....)
- $\alpha$: $S(A \cup B) \subseteq S(A) \cap S(B)$
- $\gamma$: $S(A \cup B) \supseteq S(A) \cap S(B)$
- rationalizability: $S(A \cup B) = S(A) \cap S(B)$

## Transitive Rationalizability
### Strong Expansion
Example: choice function $S$ which is rationalizable, but not by a quasi-transitive relation.

| X   | S(X) |
|-----|------|
| ab  | a   |
| bc  | b    |
| ac  | ac    |
| abc | a    |

Define *strong expansion* $\beta+$: For all $B \subseteq A$:  $S(A) \cap B \neq \emptyset \Rightarrow S(B) \subseteq S(A)$.

$\beta+$ implies $\gamma$.

### Transitive Rationalizability
> **Theorem** (Arrow, 1959).
> A choice function is rationalizable by a transitive relation iff it satisfies $\alpha$ and $\beta+$.

Conjunction of $\alpha$ and $\beta+$ is known as *weak axiom of revealed preference* (WARP).

Characterization: if $B\subseteq A$ and $S(A) \cap B \neq emptyset$, then
- WARP: $S(B) = S(A) \cap B$
- $\alpha$: $S(B) \supseteq S(A) \cap B$
- $\beta+$: $S(B) \subseteq S(A) \cap B$