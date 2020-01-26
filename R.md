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

Data visualization is important to explore information revealing facts, and showing interesting relationships. 

Plotting in R follows the theory of Grammar of Graphics: it separates data from aesthetics, defining common elements and combining them as layers.

Components of graphics are:

* Data, the object with variables;
* Aesthetics, describe visual characteristics;
* Layers, made of geometric objects that represent data;
* Scales, convert visual characteristics to display values (eventually removing values outside the range);
* Facets, split data into multiple graphs;
* Stats, summarize data;
* Coordinate system, describes 2D space data is projected onto.

##### Types of plots

Histograms are used with one continuous variable, eventually setting the number of bins (columns). They are sometimes not optimal to investigate the distribution of a variable due to the discretization effect during division into bins.

Density plots are useful for this purpose, plotting by kernel density estimation. Bandwidth can be set manually, but can have a huge impact on visualization.

Boxplots are graphical representations of numerical data (either continuous or discrete-continuous variables) through their quartiles, with whiskers indicating variability (1.5% of the interquartile range). 

Outliers may be plotted as individual points, without making assumptions regarding distribution of the data. 

Boxplots can be used for symmetric or exponential distributions, but do not work well with bimodal data since they only show one mode (the median). 

A violin plot is similar to a boxplot, with the addition of a rotated kernel density plot on each side, showing the probability density.

A bean plot is another alternative where the individual observations are shown as small lines in an one-dimensional scatter plot.

Barplots combine two attributes to encode quantitative values, emphasizing the individual amounts by category and supporting their comparison. 

Uncertainty can be visualized with error bars, for instance standard deviation and standard error of the mean (tending to 0 because of Central Limit theorem). 

Scatterplots are used with two continuous variables, showing the relationship between them. They can also be converted to 2D density plots.

Line plots can be added to highlight trends connecting individual dots, since the shape of data and its movement are subject to change. 



### Relationship between data

