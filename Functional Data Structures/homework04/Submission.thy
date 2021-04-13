theory Submission
  imports Defs
begin

fun set_mtree2:: "'a mtree ⇒ 'a set" where
"set_mtree2 Leaf = {}" |
"set_mtree2 (Node l m a r) = set_mtree2 l ∪ {a} ∪ set_mtree2 r"

fun mbst:: "'a:: {linorder, zero} mtree ⇒ bool" where
"mbst Leaf ⟷ True" |
"mbst (Node l m a r) ⟷
 mbst l ∧ mbst r ∧ 
    (∀ x ∈ set_mtree2 l. (<) x a ) ∧ 
    (∀ x ∈ set_mtree2 r. (<) a x) ∧
    (∀ x ∈ set_mtree2 (Node l m a Leaf). m ≤ x) ∧
    (m ∈ set_mtree2 (Node l m a Leaf))"

fun min_val:: "'a::{linorder, zero} mtree ⇒ 'a" where
"min_val (Node Leaf m a _) = a" |
"min_val (Node l m a r) = min_val l" |
"min_val Leaf = 0"

lemma mbst_works: "mbst (Node l m a r) \<Longrightarrow> min_val (Node l m a r) = m"
  apply (induction l rule: min_val.induct)
  apply auto
  done

fun mins:: "'a::{linorder, zero} ⇒ 'a mtree ⇒ 'a mtree" where
"mins x Leaf = Node Leaf x x Leaf" |
"mins x (Node l m a r) =
  (if x < m then Node (mins x l) x a r else
   if x < a then Node (mins x l) m a r else
   if x > a then Node l m a (mins x r) 
   else Node l m a r)"

lemma mins_set: "mbst t \<Longrightarrow> set_mtree2 (mins x t) = insert x (set_mtree2 t)"
  apply (induction t)
  apply auto
  done

lemma mins_works: "mbst t \<Longrightarrow> mbst (mins x t)"
  apply (induction t)
  apply (auto simp: mins_set)
  done

fun misin:: "'a::linorder ⇒ 'a mtree ⇒ bool" where
"misin x Leaf = False" |
"misin x (Node l m a r) = (if x < m then False
                          else if x < a then misin x l
                          else if x > a then misin x r
                          else True)"

lemma misin_works: "mbst t \<Longrightarrow> misin x t \<longleftrightarrow> x\<in>set_mtree2 t"
  apply (induction t rule: misin.induct)
  apply auto
  done

fun mtree_in_range:: "'a::linorder mtree ⇒ 'a ⇒ 'a ⇒ 'a list" where
"mtree_in_range Leaf _ _ = []" |
"mtree_in_range (Node l m a r) u v = (if v < m then [] else
  if u < a then mtree_in_range l u v else []) @
  (if u ≤ a ∧ a ≤ v then [a] else []) @ 
  (if a < v then mtree_in_range r u v else [])"

lemma mtree_in_range_works: "mbst t \<Longrightarrow> set (mtree_in_range t u v) = {x\<in>set_mtree2 t. u\<le>x \<and> x\<le>v}"
  apply (induct t)
  apply auto
  done

end
  

