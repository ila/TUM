theory homework04
  imports BST_Demo
begin


(* exercise 01 *)

fun in_range:: "'a::linorder tree \<Rightarrow> 'a \<Rightarrow> 'a \<Rightarrow> 'a list" where
"in_range Leaf _ _ = []" |
"in_range (Node l a r) u v = (if u < a then in_range l u v else []) @
  (if u \<le> a \<and> a \<le> v then [a] else []) @ 
  (if a < v then in_range r u v else [])"

value "Node (Node (Node Leaf (1::nat) Leaf) 2 (Node Leaf (3::nat) Leaf))
 4 (Node Leaf (5::nat) Leaf)"

lemma "bst t \<Longrightarrow> set (in_range t u v) = {x \<in> set_tree t. u \<le> x \<and> x \<le> v}"
  apply (induct t)
  apply auto
  done

thm filter_empty_conv

lemma [simp]: "[x] = xs @ x # ys \<longleftrightarrow> xs = [] \<and> ys = []"
  apply (induct xs)
  apply auto
  done

lemma[simp]: "[] = filter y xs \<longleftrightarrow> filter y xs = []"
  apply (induct xs)
  apply auto
  done

lemma "bst t \<Longrightarrow> in_range t u v = filter (\<lambda>x. u \<le> x \<and> x \<le> v) (inorder t)"
  apply (induct t)
  apply (auto simp: filter_empty_conv)+
  done


(* exercise 3 *)

fun enum:: "nat \<Rightarrow> unit tree set" where
  "enum 0 = {}" |
  "enum (Suc n) = enum n \<union> {Node l () r | l r. l \<in> enum n \<and> r \<in> enum n}"

find_theorems "_ \<le> _ \<Longrightarrow> _ \<le> Suc _"

lemma enum_sound: "t \<in> enum n \<Longrightarrow> height t \<le> n"
  apply (induct n arbitrary: t)
  apply (auto simp: le_SucI)
  done


(* assignment *)

datatype 'a mtree = 
  Leaf |
  Node "'a mtree" (minimum: 'a) (element: 'a) "'a mtree"

fun set_mtree2:: "'a mtree \<Rightarrow> 'a set" where
"set_mtree2 Leaf = {}" |
"set_mtree2 (Node l m a r) = set_mtree2 l \<union> {a} \<union> set_mtree2 r"

fun mbst:: "'a:: {linorder, zero} mtree \<Rightarrow> bool" where
"mbst Leaf \<longleftrightarrow> True" |
"mbst (Node l m a r) \<longleftrightarrow>
 mbst l \<and> mbst r \<and> 
    (\<forall> x \<in> set_mtree2 l. (<) x a ) \<and> 
    (\<forall> x \<in> set_mtree2 r. (<) a x) \<and>
    (\<forall> x \<in> set_mtree2 (Node l m a Leaf). m \<le> x) \<and>
    (m \<in> set_mtree2 (Node l m a Leaf))"

fun min_val:: "'a::{linorder, zero} mtree \<Rightarrow> 'a" where
"min_val (Node Leaf m a _) = a" |
"min_val (Node l m a r) = min_val l" |
"min_val Leaf = 0"

lemma "mbst (Node l m a r) \<Longrightarrow> min_val (Node l m a r) = m"
  apply (induction l rule: min_val.induct)
  apply auto
  done

fun misin:: "'a::linorder \<Rightarrow> 'a mtree \<Rightarrow> bool" where
"misin x Leaf = False" |
"misin x (Node l m a r) = (if x < m then False
                          else if x < a then misin x l
                          else if x > a then misin x r
                          else True)"

lemma "mbst t \<Longrightarrow> misin x t \<longleftrightarrow> x \<in> set_mtree2 t"
  apply (induction t rule: misin.induct)
  apply auto
  done





end