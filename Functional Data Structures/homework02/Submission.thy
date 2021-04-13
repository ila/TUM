theory Submission
  imports Defs
begin

(* distinct *)
fun contains :: "'a \<Rightarrow> 'a list \<Rightarrow> bool"  
  where
  "contains x [] = False" |
  "contains y (x#xs) = (if x = y then True else contains y xs)"

fun ldistinct:: "'a list \<Rightarrow> bool" 
  where
  "ldistinct [] = True" |
  "ldistinct (x#xs) = ((~ contains x xs) \<and> (ldistinct xs))"

thm contains.induct
thm ldistinct.induct

lemma lemma1: "contains x (xs @ [a]) = contains x ([a] @ xs)"
  apply (induction xs)
   apply (auto)
  done

lemma lemma2: "contains x (rev xs) = contains x xs"
  apply (induct xs)
  apply (auto simp: lemma1)
  done 

lemma lemma3: "ldistinct (xs @ [x]) = ldistinct ([x] @ xs)"
  apply (induct xs)
   apply (auto simp: lemma1)
  done

lemma ldistinct_rev: "ldistinct (rev xs) \<longleftrightarrow> ldistinct xs"
  apply (induct xs)
   apply (auto simp: lemma3 lemma2)
  done 


(* folding *)
thm fold.simps
thm foldr.simps

fun a::  "'a ⇒ nat ⇒ nat" where
  "a n m = m + 1"

definition length_fold:: "'a list ⇒ nat" where
  "length_fold x ≡ fold (a) x 0"

definition length_foldr:: "'a list ⇒ nat" where
  "length_foldr x ≡ foldr (a) x 0" 

lemma length_fold_sum: "fold a xs x = length xs + x"
  apply (induct xs arbitrary: x)
   apply auto
  done

lemma length_fold: "length_fold xs = length xs"
  apply (induct xs)
   apply (auto simp: length_fold_def length_fold_sum)
  done

lemma length_foldr: "length_foldr xs = length xs"
  apply (induct xs)
   apply (auto simp: length_foldr_def length_fold_sum)
  done


(* slicing *)
fun slice:: "'a list ⇒ nat ⇒ nat ⇒ 'a list" where
  "slice [] n l = []" |
  "slice xs 0 0 = []" |
  "slice (x#xs) 0 l = Cons x (slice xs 0 (l-1))" |
  "slice (x#xs) n l = slice xs (n-1) l"

thm slice.induct

lemma slice_append: "slice xs s l1 @ slice xs (s+l1) l2 = slice xs s (l1+l2)"
  apply (induction xs s l1 rule: slice.induct)
  apply (auto)
  done

lemma contain_slice: "contains x (slice xs s l) \<Longrightarrow> contains x xs"
  apply (induct xs s l rule: slice.induct)
  apply auto
  done

lemma ldistinct_slice: "ldistinct xs \<Longrightarrow>  ldistinct (slice xs s l)"
  apply (induct xs s l rule: slice.induct)
  apply (auto simp: contain_slice)
  done

end

