theory Submission
  imports Defs
begin

abbreviation bst_eq :: "('a::linorder) tree \<Rightarrow> bool" where
"bst_eq \<equiv> bst_wrt (\<le>)"

lemma isin_bst_eq: "bst_eq t \<Longrightarrow> isin t x = (x \<in> set_tree t)"
  apply (induction t)
  apply auto
  done

lemma set_tree_isin: "bst t \<Longrightarrow> isin t x = (x \<in> set_tree t)"
  apply (induction t)
  apply auto
  done

lemma set_tree_ins: "set_tree (ins x t) = {x} \<union> set_tree t"
  apply (induction t)
  apply auto
  done

lemma bst_eq_ins: "bst_eq t \<Longrightarrow> bst_eq (ins x t)"
  apply (induction t arbitrary: x)
  apply (auto simp: set_tree_ins)
  done

fun ins_eq:: "'a::linorder \<Rightarrow> 'a tree \<Rightarrow> 'a tree" where
"ins_eq x Leaf = Node Leaf x Leaf" |
"ins_eq x (Node l a r) =
  (if x \<le> a then Node (ins_eq x l) a r else
   if x > a then Node l a (ins_eq x r)
   else Node l a r)"

lemma set_tree_ins_eq: "set_tree (ins_eq x t) = {x} \<union> set_tree t"
  apply(induction t)
  apply auto
  done

lemma bst_eq_ins_eq: "bst_eq t \<Longrightarrow> bst_eq (ins_eq x t)"
  apply (induction t arbitrary: x)
  apply (auto simp: set_tree_ins_eq)
  done

fun count_tree:: "'a \<Rightarrow> 'a tree \<Rightarrow> nat" where
"count_tree x Leaf = 0" |
"count_tree x (Node l a r) = 
  (if x = a then 1 + (count_tree x l) + (count_tree x r)
   else (count_tree x l) + (count_tree x r))"
  
lemma count_tree_ins_eq: "count_tree x (ins_eq x t) = Suc (count_tree x t)"  
  apply (induction t arbitrary: x)
  apply (auto)
  done

lemma count_tree_ins_eq_diff: "x\<noteq>y \<Longrightarrow> count_tree y (ins_eq x t) = count_tree y t"
  apply (induction t arbitrary: x y)
  apply auto
  done

end