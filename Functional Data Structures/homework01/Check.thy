theory Check
imports Submission
begin

lemma max_greater: "x \<in> set xs \<Longrightarrow> x\<le>lmax xs"
  by (rule max_greater)

lemma max_reverse: "lmax (reverse xs) = lmax xs"
  by (rule max_reverse)


end


