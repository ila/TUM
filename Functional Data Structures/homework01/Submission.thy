theory Submission
imports Defs
begin

fun lmax:: "nat list \<Rightarrow> nat" where
  "lmax [] = 0" |
  "lmax (x#xs) = (if x > lmax xs then x else lmax xs)"

lemma max_greater:
  "(x::nat) \<in> set xs \<Longrightarrow> x \<le> lmax xs"
  by (induct xs) auto

lemma lmax_snoc:
 "lmax (snoc xs y) = lmax (y#xs)"
  apply (induct xs)
  apply (auto)
  done

lemma max_reverse:
  "lmax (reverse xs) = lmax xs"
  apply (induct xs)
  apply (auto simp add: lmax_snoc)
  done 

end