theory Homework02
imports Main
begin

(* primitive recursive functions *)
thm fold.simps
thm foldr.simps

(* folding *)
definition list_sum':: "nat list \<Rightarrow> nat"  where
  "list_sum' xs \<equiv> fold (+) xs 0"

thm list_sum'_def

value "list_sum' [1, 2, 3]"
value "fold (*) [1::nat, 2, 3] 2"

fun list_sum:: "nat list \<Rightarrow> nat" where
  "list_sum [] = 0" |
  "list_sum (x#xs) = x + list_sum xs"

lemma lemma_aux: "fold (+) xs a = list_sum xs + a"
  apply (induction xs arbitrary: a)
  apply auto
  done

lemma "list_sum xs = list_sum' xs"
  apply (auto simp: list_sum'_def lemma_aux)
  done


(* binary trees *)
datatype 'a ltree = Leaf 'a | Node "'a ltree" "'a ltree"

fun inorder:: "'a ltree \<Rightarrow> 'a list" where
  "inorder (Leaf x) = [x]" |
  "inorder (Node l r) = inorder l @ inorder r"

value "inorder (Node (Node (Leaf (1::nat)) (Leaf 2)) (Leaf 3))"
term "fold f (inorder t) s"

fun fold_ltree:: "('a \<Rightarrow> 's \<Rightarrow> 's) \<Rightarrow> 'a ltree \<Rightarrow> 's \<Rightarrow> 's" where
  "fold_ltree f (Leaf x) s = f x s" |
  "fold_ltree f (Node l r) s = fold_ltree f r (fold_ltree f l s)"

lemma "fold f (inorder t) s = fold_ltree f t s"
  apply (induct t arbitrary: s)
  apply auto
  done

fun mirror:: "'a ltree \<Rightarrow> 'a ltree" where
  "mirror (Leaf x) = Leaf x" |
  "mirror (Node l r) = (Node (mirror r) (mirror l))"

lemma "inorder (mirror t) = rev (inorder t)"
  apply (induct t)
  apply auto
  done


(* list of lists *)
fun shuffles:: "'a list \<Rightarrow> 'a list \<Rightarrow> 'a list list" where
  "shuffles xs [] = [xs]" |
  "shuffles [] ys = [ys]" |
  "shuffles (x#xs) (y#ys) = map (\<lambda>xs. x # xs) (shuffles xs (y#ys)) @ 
                            map (\<lambda>ys. y # ys) (shuffles (x#xs) ys)"

thm shuffles.induct

lemma "zs \<in> set (shuffles xs ys) \<Longrightarrow> length zs = length xs + length ys"
  apply (induction xs ys arbitrary: zs rule: shuffles.induct)
    apply auto
  done


(* contains *)
fun contains:: "'a \<Rightarrow> 'a list \<Rightarrow> bool" where
  "contains x [] = False" |
 "contains y (x#xs) = (if x = y then True else contains y xs)"

(* ldistinct *)
fun ldistinct:: "'a list \<Rightarrow> bool" where
  "ldistinct [] = True" |
  "ldistinct (x#xs) = (if contains x xs = True then False else ldistinct xs)"

thm contains.induct
thm ldistinct.induct

(* show that a reversed list is distinct iff original is distinct *)

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

lemma "ldistinct (rev xs) \<longleftrightarrow> ldistinct xs"
  apply (induct xs)
   apply (auto simp: lemma3 lemma2)
  done 


(* folding *)

fun a::  "'a \<Rightarrow> nat \<Rightarrow> nat" where
  "a n m = m + 1"

definition length_fold:: "'a list \<Rightarrow> nat" where
  "length_fold x \<equiv> fold (a) x 0"

definition length_foldr:: "'a list \<Rightarrow> nat" where
  "length_foldr x \<equiv> foldr (a) x 0" 

lemma length_fold_sum: "fold a xs x = length xs + x"
  apply (induct xs arbitrary: x)
   apply auto
  done

lemma "length_fold xs = length xs"
  apply (induct xs)
   apply (auto simp: length_fold_def length_fold_sum)
  done

lemma "length_foldr xs = length xs"
  apply (induct xs)
   apply (auto simp: length_foldr_def length_fold_sum)
  done


(* list slices *)
fun slice:: "'a list \<Rightarrow> nat \<Rightarrow> nat \<Rightarrow> 'a list" where
  "slice [] n l = []" |
  "slice xs 0 0 = []" |
  "slice (x#xs) 0 l = Cons x (slice xs 0 (l-1))" |
  "slice (x#xs) n l = slice xs (n-1) l"

thm slice.induct

value "slice [0, 1 ,2 ,3 ,4 ,5 ,6::int] 0 1"
value "slice [0 ,1 ,2 ,3 ,4 ,5 ,6 ::int] 2 10"
value "slice [0 ,1 ,2 ,3 ,4 ,5 ,6 ::int] 10 10" 

(* show that concatenation of two adjacent slices can be expressed as a single slice *)
lemma l1: "(slice xs s l1) @ slice xs (s + l1) l2 = slice xs s (l1 + l2)"
  apply (induction xs s l1 rule: slice.induct)
  apply (auto)
  done

(* slice of a distinct is distinct *)

lemma "ldistinct xs \<Longrightarrow> ldistinct (slice xs s l)"
  apply (induct xs arbitrary: s l rule: ldistinct.induct)
  apply (auto simp: l2)
  done
