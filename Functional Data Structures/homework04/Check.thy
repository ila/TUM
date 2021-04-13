theory Check
  imports Template
begin

lemma mbst_works: "mbst (Node l m a r) \<Longrightarrow> min_val (Node l m a r) = m"  
  by (rule mbst_works)

lemma mins_works: "mbst t \<Longrightarrow> mbst (mins x t)"
  by (rule mins_works)

lemma misin_works: "mbst t \<Longrightarrow> misin x t \<longleftrightarrow> x\<in>set_mtree2 t"
  by (rule misin_works)

lemma mtree_in_range_works: "mbst t \<Longrightarrow> set (mtree_in_range t u v) = {x\<in>set_mtree2 t. u\<le>x \<and> x\<le>v}"  
  by (rule mtree_in_range_works)

end