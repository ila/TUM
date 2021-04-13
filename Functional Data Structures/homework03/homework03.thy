theory homework03
  imports "BST_Demo"
begin

thm isin.simps

(* exercise 1 *)
fun isin2:: "('a::linorder) tree \<Rightarrow> 'a option \<Rightarrow> 'a \<Rightarrow> bool" where
"isin2 Leaf z x = (case z of None \<Rightarrow> False | Some y \<Rightarrow> x = y)" |
"isin2 (Node l a r) z x =
  (if x < a then isin2 l z x 
   else isin2 r (Some a) x)" 

lemma isin2_Some:
"\<lbrakk>bst t; \<forall> x \<in> set_tree t. a < x\<rbrakk> \<Longrightarrow> 
    isin2 t (Some a) x = (isin t x \<or> a = x)"
  apply (induction t arbitrary: a)
   apply auto
  done

lemma isin2_None:
"bst t \<Longrightarrow> isin2 t None x = isin t x"
  apply (induction t)
  apply (auto simp add: isin2_Some)
  done

(* exercise 2 *)
fun join:: "'a tree \<Rightarrow> 'a tree \<Rightarrow> 'a tree" where
"join t \<langle>\<rangle> = t" |
"join \<langle>\<rangle> t = t" |
"join (Node l1 a1 r1) (Node l2 a2 r2) =
  (case join r1 l2 of \<langle>\<rangle> \<Rightarrow> Node l1 a1 (Node \<langle>\<rangle> a2 r2)
               | Node l a r \<Rightarrow> Node (Node l1 a1 l) a (Node r a2 r2))"

thm tree.splits

lemma join_inorder: "inorder (join t1 t2) = inorder t1 @ inorder t2"
  apply (induction t1 t2 rule: join.induct)
    apply (auto split: tree.splits)
  done

lemma "height (join t1 t2) \<le> max (height t1) (height t2) + 1"
  apply (induction t1 t2 rule: join.induct)
    apply (auto split: tree.splits)
  done

(* exercise 3 *)
fun delete:: "'a::linorder \<Rightarrow> 'a tree \<Rightarrow> 'a tree" where
"delete x \<langle>\<rangle> = \<langle>\<rangle>" |
"delete x (Node l a r) =
  (if x = a then join l r else
  if x < a then Node (delete x l) a r else
  Node l a (delete x r))"

thm set_inorder[symmetric]
thm bst_iff_sorted_wrt_less
thm sorted_wrt_append

lemma set_tree_join: "set_tree (join t1 t2) = set_tree t1 \<union> set_tree t2"
  by (auto simp del: set_inorder simp: set_inorder[symmetric] join_inorder)

lemma bst_join: "bst (Node l (x::'a::linorder) r) \<Longrightarrow> bst (join l r)"
  apply (auto simp del: bst_wrt.simps 
              simp: bst_iff_sorted_wrt_less join_inorder sorted_wrt_append)
  done

declare join.simps [simp del]

lemma [simp]: "bst t \<Longrightarrow> set_tree (delete x t) = (set_tree t) - {x}"
  apply (induction t arbitrary: x)
  apply (auto simp: set_tree_join)
  done

lemma "bst t \<Longrightarrow> bst (delete x t)"
  apply (induction t arbitrary: x)
  apply (auto simp: bst_join)
  done

(* assignment *)

(* define bst_eq that allows equal nodes *)
fun bst_wrt:: "('a \<Rightarrow> 'a \<Rightarrow> bool) \<Rightarrow> 'a tree \<Rightarrow> bool" where
"bst_wrt P \<langle>\<rangle> \<longleftrightarrow> True" |
"bst_wrt P \<langle>l, a, r\<rangle> \<longleftrightarrow>
 bst_wrt P l \<and> bst_wrt P r \<and> 
               (\<forall> x \<in> set_tree l. P x a) \<and> 
               (\<forall> x \<in> set_tree r. P a x)" 

abbreviation bst_eq:: "('a::linorder) tree \<Rightarrow> bool" where
"bst_eq \<equiv> bst_wrt (\<le>)"

(* show that isin and ins are correct *)
lemma "bst_eq t \<Longrightarrow> isin t x = (x \<in> set_tree t)"
  apply (induction t)
  apply auto
  done

lemma bst_eq_ins: "bst_eq t \<Longrightarrow> bst_eq (ins x t)"
  apply (induction t arbitrary: x)
  apply (auto simp: set_tree_ins)
  done

(* define new insert function *)
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

(* show that it works *)
lemma bst_eq_ins_eq: "bst_eq t \<Longrightarrow> bst_eq (ins_eq x t)"
  apply (induction t arbitrary: x)
  apply (auto simp: set_tree_ins_eq)
  done

(* define a function to count elements in tree *)
fun count_tree:: "'a \<Rightarrow> 'a tree \<Rightarrow> nat" where
"count_tree x Leaf = 0" |
"count_tree x (Node l a r) = 
  (if x = a then 1 + (count_tree x l) + (count_tree x r)
   else (count_tree x l) + (count_tree x r))"

value "count_tree 2 (Node (Node \<langle>\<rangle> (2::nat) \<langle>\<rangle>) 2 (Node \<langle>\<rangle> (3::nat) \<langle>\<rangle>))"

(* show that ins_eq inserts correctly *)
lemma "count_tree x (ins_eq x t) = Suc (count_tree x t)"
  apply (induction t arbitrary: x)
   apply (auto)
  done

(* show that ins_eq does not affect other elements *)
lemma "x \<noteq> y \<Longrightarrow> count_tree y (ins_eq x t) = count_tree y t"
  apply (induction t arbitrary: x y)
   apply auto
  done

end