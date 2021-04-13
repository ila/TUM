theory Defs
imports Main
begin

fun snoc :: "'a list \<Rightarrow> 'a \<Rightarrow> 'a list" where
"snoc [] x = [x]" |
"snoc (y # ys) x = y # (snoc ys x)"

fun reverse :: "'a list \<Rightarrow> 'a list" (*<*) where
"reverse [] = []" |
"reverse (x # xs) = snoc (reverse xs) x" (*>*)

end

