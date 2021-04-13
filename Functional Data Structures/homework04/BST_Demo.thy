theory BST_Demo
imports "HOL-Library.Tree"
begin

(* useful most of the time: *)
declare Let_def [simp]

section "BST Search and Insertion"

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

subsection "Functional Correctness"

lemma set_tree_isin: "bst t \<Longrightarrow> isin t x = (x \<in> set_tree t)"
apply(induction t)
apply auto
done

lemma set_tree_ins: "set_tree (ins x t) = {x} \<union> set_tree t"
apply(induction t)
apply auto
done

subsection "Preservation of Invariant"

lemma bst_ins: "bst t \<Longrightarrow> bst (ins x t)"
apply(induction t)
apply (auto simp: set_tree_ins)
done


section "BST Deletion"

fun split_min :: "'a tree \<Rightarrow> 'a * 'a tree" where
"split_min (Node l a r) =
  (if l = Leaf then (a,r)
   else let (x,l') = split_min l
        in (x, Node l' a r))"

fun delete :: "'a::linorder \<Rightarrow> 'a tree \<Rightarrow> 'a tree" where
"delete x Leaf = Leaf" |
"delete x (Node l a r) =
  (if x < a then Node (delete x l) a r else
   if x > a then Node l a (delete x r)
   else if r = Leaf then l else let (a',r') = split_min r in Node l a' r')"

(* A proof attempt *)

lemma "split_min t = (x,t') \<Longrightarrow> set_tree t' = set_tree t - {x}"
oops

(* The final proof (needs more than auto!): *)

lemma "\<lbrakk> split_min t = (x,t'); bst t; t \<noteq> Leaf \<rbrakk> \<Longrightarrow>
  set_tree t' = set_tree t - {x} \<and> x \<in> set_tree t"
apply(induction t arbitrary: x t')
 apply simp
apply (force split: if_split_asm prod.splits)
done

end
