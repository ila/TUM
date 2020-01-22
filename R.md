# R cheatsheet

### Functions

Functions do not need all argument names: after two arguments, a variable number of parameters can be passed, specified through the `...` syntax.

`%in%` is a special function that allows to check whether the elements of a structure also belong to another structure.

##### Apply

`apply(df, margin, function, ...)` helps avoiding to update the environment at every step of an iteration (for/while loop). It can apply a function to the elements of the steps, in case they are independent of each other.

* `margin` specifies rows or columns (1, 2);
* `sapply` is a variation which returns a vector;
* `lapply` works on elements of an array (row-wise), returns a list, and can be generally used to create lists.

Additional arguments are permitted, yet they must be explicitly stated.



### Data wrangling and tidying

A data table modifies columns by reference, offering subsetting, ordering and merging in a more efficient way respect to data frames.

`DT[i, j, by]` where `i` represents the subset of rows to select, `j` is the expression to calculate, and `by` the eventual grouping.

