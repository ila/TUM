theory Check
  imports Submission
begin

lemma isin_bst_eq: "bst_eq t \<Longrightarrow> isin t x = (x \<in> set_tree t)"
  by (rule isin_bst_eq)

lemma bst_eq_ins: "bst_eq t \<Longrightarrow> bst_eq (ins x t)"
  by(rule bst_eq_ins)

lemma bst_eq_ins_eq: "bst_eq t \<Longrightarrow> bst_eq (ins_eq x t)"
  by(rule bst_eq_ins_eq)

lemma count_tree_ins_eq: "count_tree x (ins_eq x t) = Suc (count_tree x t)"  
  by (rule count_tree_ins_eq)

lemma "x\<noteq>y \<Longrightarrow> count_tree y (ins_eq x t) = count_tree y t"
  by (rule count_tree_ins_eq_diff)

end