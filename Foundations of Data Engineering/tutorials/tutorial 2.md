## Foundations in Data Engineering - Tutorial 2

#### Exercise 1

**For  the  following  SQL  keywords,  which  GNU  tools  are  useful  to  implement  a similar functionality?**

* FROM: `cat`
* LIMIT: `head`, `tail`
* SELECT: `cat` with `col`
* WHERE: `grep`, `egrep`
* ORDER BY: `sort`
* GROUP BY: `awk` + `sort`



#### Exercise 2

**Answer  the  following  queries  for  the  Linux  Kernel  Mailing  List  data  set  using GNU utils and bash functionality:**

1. **Of how many lines does the longest mail consist?  Challenge:  Compute the number of lines in each mail without using the ’Lines:’  attribute in the dataset.**
   `cat gmane.linux.kernel | grep "Lines" | grep -o '[0-9]*' | sort -n | tail -1`
2. **Which mail received most (direct) replies?** 
   `grep -i '^In-Reply-To' gmane.linux.kernel | sort | uniq -c | sort -n |  tail -1`
   Note: always use `sort` before `uniq`!



#### Exercise 3

Answer the following queries for the TPC-H data set using GNU utils and bash functionality:

1.  According to the data set, which nations are in Europe?
    `join -t '|' <(grep EUROPE region.tbl) <(sort -t '|' -k3 -n nation.tbl) -1 1 -2 3 | cut -d '|' -f6`
2.  How many lines are in lineitem.tbl?
    `wc -l lineitem.tbl`
3.  Determine for each part how often it has been bought.  (Hint:  Sum up the quantities in lineitem.tbl, grouped by partkey).

Now use the command `time` to determine how long it takes to compute query 3.

