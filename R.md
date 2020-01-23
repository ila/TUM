# R cheatsheet

### Functions

Functions do not need all argument names: after two arguments, a variable number of parameters can be passed, specified through the `...` syntax.

`%in%` is a special function that allows to check whether the elements of a structure also belong to another structure.

`%>%` is the equivalent of the Unix pipe, concatenating an operation to the output of another.

##### Apply

`apply(df, margin, function, ...)` helps avoiding to update the environment at every step of an iteration (for/while loop). It can apply a function to the elements of the steps, in case they are independent of each other.

* `margin` specifies rows or columns (1, 2);
* `sapply` is a variation which returns a vector;
* `lapply` works on elements of an array (row-wise), returns a list, and can be generally used to create lists.

Additional arguments are permitted, yet they must be explicitly stated.



### Data wrangling

A data table modifies columns by reference, offering subsetting, ordering and merging in a more efficient way respect to data frames. It is faster than other external packages, and easier to read or maintain.

`DT[i, j, by]` where `i` represents the subset of rows to select, `j` is the expression to calculate, and `by` the eventual grouping. More expressions can also be applied concatenating sets of square brackets. Sorting is performed through the `order` or `setorder` keywords.

Columns are seen as variables, and usually accessed by name. It is possible to insert lists into expressions through the `.(...)` notation (alternative to list). 

`.N` holds the number of observations of a particular group (count).

`:=` updates the table (adds, deletes or changes columns).

`.SD` is an operator to subset data (by row indexing) according to the current group, containing all the columns except the grouping ones, preserving the original order. It is used for instance to get the minimum of each category, or along with `sapply`.

`which` displays the whole row where the true condition is located.

`.SDcols` specifies a subset of columns to apply the desired functions, to be inserted after the `by` clause. 

##### Joining two datasets

Joining is an operation which can be done in two ways:

- `merge(x, y, by, by.x, by.y, all, ...)`
  This function merges by a common column, by default the one(s) with the same name. Merging by several columns helps removing eventual duplicates. It is usually not symmetric. There are four types of merge:
  1. Inner `(all = F)`, discarding the mismatching rows;
  2. Outer or full `(all = T)`;
  3. Left `(all.x = T)`;
  4. Right `(all.y = T)`.
- `rbind(df1, df2)`
  This function joins two datasets vertically, which must have the same variables. Additional ones are either removed or set to NA.



### Data tidying

Data must be rearranged to determine the most important information and represent it in the most suitable way, depending on its purpose. Clean data allows easier subsetting, manipulation and grouping.

Tidy data definition:

* Each variable has its own column;
* Each observation has its own row;
* Each value has its own cell.

Untidy datasets are characterized by messy column headers, variables stored either in rows or only one column, or multiple types of observations in the same table. Column names should be descriptive and informative enough by themselves. 

Normalizing a table consists in dividing several types of observational units in more tables,  to eliminate inconsistencies. It should only be used when tools do not require relational data.

##### Best practices

Lists should always be converted to data tables, preferably unnested. The function `as.data.table` can be used for this purpose, eventually filling missing values with NA.

`rbindlist` has a parameter called `idcol` to assign the names of the elements to a new column, in case of observations split into multiple tables, so that the table names do not get lost. 

Melting and casting are essential to switch from wide to long data, or vice versa. They address variables spread across multiple columns and observations scattered across multiple rows.

`melt` transforms from wide to long. It is used to remove confusing column names and turning them to attributes of observations. 

`dcast` transforms from long to wide. It is used to remove multiple appearances of observations, turning variable types into columns. 

`separate` and `unite` change multiple variables to one, or the opposite. For instance, aggregating two columns containing year and century, or the same column having two values. 



### Plotting

