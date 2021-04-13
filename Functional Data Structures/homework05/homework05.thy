theory homework05
  imports Complex_Main "HOL-Library.Tree"
begin

(* exercise 1 *)
lemma exp_fact_estimate: "n > 3 \<Longrightarrow> (2::nat)^n < fact n"
proof (induction n)
  case 0 
  then show ?case 
    by auto
next
  case (Suc n)
  have "n = 3 \<or> n > 3"
    using Suc.prems
    by auto
  then show ?case
  proof
    assume GT: "n > 3"
      hence "(2::nat) ^ n < fact n"
        using Suc.IH (* induction hyphothesis *)
        by simp
      hence "(2::nat) * 2 ^ n < fact n + fact n" (* hence = then have = using *)
        by simp
      also have "... < fact n + n * fact n"
        using GT
        by simp
      also have "... = fact (Suc n)"
        by simp
      finally show "(2::nat) ^ Suc n < fact (Suc n)"
          by simp
    next
      assume "n = 3"
      thus "(2::nat) ^ Suc n < fact (Suc n)"
        by (simp add: eval_nat_numeral)
  qed
qed

(* exercise 2 *)
fun sumto::"(nat \<Rightarrow> nat) \<Rightarrow> nat \<Rightarrow> nat" where
  "sumto f 0 = f 0" |
  "sumto f (Suc n) = f (Suc n) + sumto f n"

lemma sum_of_naturals: "2 * sumto (\<lambda>x. x) n = n * (n + 1)"
  by (induction n)
  auto

lemma "sumto (\<lambda>x. x) n ^ 2 = sumto (\<lambda>x. x ^ 3) n"
proof (induction n)
  case 0 show ?case by simp
next
  case (Suc n)
  note [simp] = algebra_simps
  have "(sumto (\<lambda>x. x) (Suc n)) ^ 2 = ((sumto (\<lambda>x. x) n) + (n + 1)) ^ 2"
    by simp
  also have "... = (sumto (\<lambda>x. x) n) ^ 2 + (2::nat) * (sumto (\<lambda>x. x) n) * (n + 1) + (n + 1) ^ 2"
    by (simp only: power2_sum)
  also have "... = (sumto (\<lambda>x. x ^ 3) n)  + (2::nat) * (sumto (\<lambda>x. x) n) * (n + 1) + (n + 1) ^ 2"
    using Suc.IH
    by simp
  also have "... = (sumto (\<lambda>x. x ^ 3) n) + n * (n + 1) * (n + 1) + (n + 1) ^ 2"
    using sum_of_naturals
    by simp
  also have "... = (sumto (\<lambda>x. x ^ 3) n) + (n + 1) ^ 3"
    by (simp add: eval_nat_numeral)
  also have "... = (sumto (\<lambda>x. x ^ 3) (Suc n))"
    by simp
  finally show ?case
    by simp
qed

(* exercise 3 *)
fun enum:: "nat \<Rightarrow> unit tree set" where
"enum 0 = {Leaf}" |
"enum (Suc n) = enum n \<union> {Node l () r | l r. l \<in> enum n \<and> r \<in> enum n}"

lemma enum_sound: "t \<in> enum n \<Longrightarrow> height t \<le> n"
  apply (induction n arbitrary: t)
   apply (auto simp: le_SucI)
  done

lemma enum_complete: "height t \<le> n \<Longrightarrow> t \<in> enum n"
  apply (induction n arbitrary: t)
  subgoal for t
    apply auto
    done
  subgoal for n t
    apply (cases t)
    apply auto
    done
  done

lemma enum_complete_isar: "height t \<le> n \<Longrightarrow> t \<in> enum n"
proof (induction n arbitrary: t)
  case 0
  then show ?case
    by auto
  next
    case (Suc n)
    then show ?case
      by (cases t) auto
  qed

(* exercise 4 *)
datatype 'a tchar = L | N 'a

fun pretty:: "'a tree \<Rightarrow> 'a tchar list" where
"pretty Leaf = [L]" |
"pretty (Node l a r) = N a # (pretty l) @ (pretty r)"

lemma pretty_unique: "pretty t @ xs = pretty t' @ ys \<Longrightarrow> t = t'"
  apply (induction t arbitrary: t' xs ys)
  subgoal for t' 
    apply (cases t')
   apply auto
    done
  subgoal for t1 x t2 t' xs ys
    apply (cases t')
     apply auto
    apply force
    done
done

(*

(* assignment 1 *)
lemma split_lists:
  shows "\<exists> ys zs. length ys = length xs div n \<and> xs = ys @ zs"
  apply (induction n arbitrary: xs)
  subgoal for xs
    apply auto
    done
  subgoal for n xs
    apply (cases xs)
     apply (induction xs)
      apply auto
    apply auto
    done
  done

*)

(* assignment 2 *)
lemma sum_indent: "sumto (\<lambda>i. i * 2 ^ i) n = n * 2 ^ (n + 1) - (2 ^ (n + 1) - 2)"
proof (induction n)
  case 0 show ?case by simp
next
  case (Suc n)
  note [simp] = algebra_simps
  have "sumto (\<lambda>i. i * 2 ^ (Suc i)) n = sumto "



end