theory Defs
  imports "HOL-Library.Tree"
begin

fun isin :: "('a::linorder) tree \<Rightarrow> 'a \<Rightarrow> bool" where
"isin Leaf x = False" |
"isin (Node l a r) x =
  (if x < a then isin l x else
   if x > a then isin r x
   else True)"

fun ins :: "'a::linorder \<Rightarrow> 'a tree \<Rightarrow> 'a tree" where
"ins x Leaf = Node Leaf x Leaf" |
"ins x (Node l a r) =
  (if x < a then Node (ins x l) a r else
   if x > a then Node l a (ins x r)
   else Node l a r)"

end