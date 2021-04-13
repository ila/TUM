theory Check
  imports Submission
begin

lemma ldistinct_rev: "ldistinct (rev xs) \<longleftrightarrow> ldistinct xs"
  by (rule ldistinct_rev) 

lemma length_fold: "length_fold xs = length xs"  
  by (rule length_fold)    

lemma length_foldr: "length_foldr xs = length xs"  
  by (rule length_foldr)

lemma slice_append: "slice xs s l1 @ slice xs (s+l1) l2 = slice xs s (l1+l2)"
  by (rule slice_append)

lemma ldistinct_slice: "ldistinct xs \<Longrightarrow> ldistinct (slice xs s l)"
  by (rule ldistinct_slice)

end

