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

Plots are good indicators of relationship between data:

* Covariance, $\frac{1}{n-1}\sum{i=1}{n}(x_{i1} - \bar{x}_1)(x_{i2} - \bar{x}_2)$;
* Pearson correlation, ideal to capture linear relationships but very sensitive to outliers;
* Spearman correlation, good for both linear and monotonous non linear relationships and less sensitive to outliers'

##### Multidimensional data

Analysis on multidimensional data assumes quantitative variables plotted on continuous axes.

* Small dimensionality: plot matrix;
* Medium dimensionality: correlation plot;
* High dimensionality: heat map.

Heat maps can also created using hierarchical clustering based on similarity, and it can be useful to scale the data.

##### Conditioning

A confounder is a casual variable that influences both the dependent variable and the independent one, causing a spurious association.

Correlations are often driven by confounding factors: depending on the context, they can lead to spurious associations and false interpretation. 

A confounder variable, in fact, induces correlation, but it needs to be proven that variables are actually dependent. Facets allow plotting conditioning on a third variable, the potential confounder.

If all three plots show correlation, it is likely to be in presence of a confounder. Conditioned by it, the other variables are uncorrelated. 

Correlation does not imply causation!

Categorical variables can be mapped  together with multi-way histograms, stacking observations. To compare observations, all bars having the same weight makes it easier.

To compare individual values, instead, positioning with `dodge` places overlapping objects directly beside one another. 

Typically the y-axis represents the response variable, while x-axis is the explanatory variable. Mathematically, they correspond to the description of the conditional distributions. 

##### Colors and representation

Plotting can follow default aesthetic mapping or predefined color numbers and names. Colors can also be specified by RGB or HTML codes: three number pairs for red, green and blue in hexadecimal. 

Color perception is influenced by the context, and not an absolute value. The background must be consistent and contrast sufficiently with the object. 

Color palettes can be defined in a categorical way, sequential (quantitative differences) or diverging with a breakpoint. 

In general, plotting data can have a substantial impact even without exaggerating additional graphic features.

Unnecessary text should not be present, pictures and 3D perspectives are superfluous: it can be hard to visualize values and their intersection. 

Barplots (y-axis) should always start at 0, since the length is proportional to the quantities being displayed, and relatively small differences could be made to look much bigger.



### Dimensionality reduction

An alternative to visualize high dimension datasets is by fitting a 2D map to the data. Popular methods include PCA, U-MAP, T-SNE.

Principal components analysis (`prcomp`) is a statistical procedure that uses an orthogonal transformation to convert a set of possibly correlated variables into a set of linearly uncorrelated ones.

The first principal component has the largest possible variance, and each successive one has the highest variance possible under the constraint that is orthogonal to the preceding components.

PCA can be thought of as fitting an n-dimensional ellipsoid to the data. The first two principal components can be plotted to accurately visualize high-dimensional data.

Studying the dynamics of data, it is not known a priori how the variables can be expressed and which axes are important. 

The goal of PCA is identifying the most meaningful basis to re-express the data to reveal hidden dynamics, determining which direction is the most important in terms of change of a unit. PCA aims to find another basis being a linear combination of the original one to best represent data.

Information is represented with $n$ samples of $p$ variables, centered and possibly also scaled (mean 0, standard deviation 1). It is linearly transformed by a rotation matrix $W$, whose columns are the new basis vectors (principal components).

The column vectors of $T = XW$ represent the projection of the data on the principal components, each of them explaining a fraction of the total variance of the data. The aim is finding the direction maximizing variance.

1. The normalized matrix is expressed in terms of covariance matrix $S(X) = X^TX$;
2. The covariance matrix is transformed in $(XW)^TXW$, where $W$ is the matrix of eigenvectors of the covariance matrix;
3. The highest eigenvalues, representing the direction with the largest variance, are selected and a sub-matrix is extracted (after eventually transforming the matrix);
4. The covariance of features is 0, therefore this corresponds to an orthonormal basis. The eigenvalues of $X^TX$ equal to the variance of the projection of $X$.

A scree plot shows the variance in each projected direction. If the scree plot has an elbow shape it can be used to decide how many PC to use for further analysis.

The biplot shows the projection of the data on the first two principle components, along with the correlation between variables.

##### Multi-dimensional scaling

Given a distance matrix, the aim of multi-dimensional scaling (`cmdscale`) is to construct a map that preserves the distances. The output map has coordinates with Euclidean distance. 

This is typically used for low dimensional solution, and works by optimizing a stress function: $\big[ \sum_{i \neq j}(d_{ij} - ||x_i - x_j||) \big]^2$.



### Clustering

Clustering is the task of dividing the population or data points into a number of groups such that data points in the same groups are more similar to other data points in the same group than those in other groups.

Different algorithms perform in different ways, and are most suitable depending on the use case.

##### K-means

K-means aims to partition observations into a pre-determined number of clusters in which every observation belongs to the cluster with the nearest mean.

It is the most popular partitioning method, but requires knowing the value $k$ in advance. It uses Euclidean distance as metric, and variance as measure of cluster scatter.

It might not always be correct, since convergence to a local minimum produces counterintuitive results. 

1. Choosing an initial centroid at random;
2. Calculating the distance between centroid and every other point;
3. Choose $k-1$ other centroids by taking the mean value of all the samples assigned to each previous centroid;
4. Compute difference between the old and the new centroids, and repeat steps 2 and 3 to minimize the difference.

K-means does not always work in cases when a finite mixture module would be more appropriate.

##### Gaussian mixture models

Gaussian mixture models (`mclust`) are probabilistic frameworks allowing to determine number of clusters based on information criteria. Having $k$ estimated parameters of $L$ maximum likelihood with $n$ observations, useful estimators of quality of statistical models are:

* AIC, $2k -2\log(L)$ (to minimize);
* BIC, $\ln(n)k - 2\ln(L)$ (to minimize in general, to maximize in R).

Clusters are allowed individual covariance and prior probabilities. 

1. The EM algorithm's parameters are initialized randomly;

2. Iterate until convergence:
   1. Expectation step: estimate the membership (binary) using the mean and covariance (of the multivariate Gaussian of each cluster) of last iteration;
   2. Maximization step: update the parameters of the distribution using the membership.

The algorithm is based on probabilities instead of next centroids, and uses all data points to re-estimate centroids.

Using the likelihood of the data alone would favor large models, therefore AIC and BIC combine the likelihood with a penalty for model complexity. 

The number of parameters can be reduced imposing some constraints to the covariance matrices: equality, being spherical and of the same orientation.

##### Hierarchical clustering

Hierarchical clustering (`hclust`) is an agglomerative method with a bottom-up approach that iteratively merges clusters, starting from individual points.

This technique works with the following assumptions: 

* Multivariate data;
* Distance metric (Euclidean, Manhattan) between points $d(a, b)$;
  * Mean or average linkage clustering, or UPGMA: $\frac{1}{|A| |B|} \sum_{a \in A} \sum_{b \in B} d(a, b)$;
* The larger the distance, the less similar the data points.

It aims to calculate an inter-cluster distance between either sets or data points, such as the average for elements of sets.

The output is a binary tree whose leaves are the data points and each node is a set that is the union of its two children nodes.

Sets are merged according to the two closest elements, computing a pairwise distance matrix which gets updated after each iteration.

To obtain a partition from a hierarchical clustering, a threshold can be decided based either on visual inspection or number of groups.

##### Rand index

$$ R = \frac{a+b}{{n}\choose{2}}$$

* $S$ is a set of $n$ elements;
* $X$ is a partition of $S$ into $k$ sets;
* $Y$ is a partition of $S$ into $l$ sets;
* $a$ is the number of pairs of elements in $S$ that are in the same set both in $X$ and $Y$;
* $b$ is the number of pairs of elements in $S$ that are in different sets both in $X$ and $Y$;
* ${n}\choose{2}$ is the total number of pairs of elements in $S$.

The purpose of this value is to compare different clustering techniques of the same dataset with different number of clusters, trying to maximize similarity in the same set and minimize it between others. 

It can be seen as a measure of the percentage of the correct decisions made by the algorithm.

Its values range between 0 and 1, where 1 implies that the two partitions are identical. It can be applied in hierarchical clustering fixing the number of clusters.



### Multiple testing

Multiple testing is a method that can be used to assess a distribution, fairness of probability or in general anything concerning multiple hypotheses.

The more inferences are made, the more likely erroneous ones are to occur, therefore it is needed a strict significant threshold for individual comparisons.



### Classification

Classification is important when trying to predict the value of $y$ applying linear regression, when $y$ is a category (diagnostic, spam detection, speech recognition).

The variable used to predict are called features, while the prediction is the outcome. An efficient approach takes features as input and returns a valid outcome when it is unknown, eventually training algorithms using a dataset with outcome observations already present.

##### Binary classification

Binary classification has outcome $k$ which can only take values of 0 or 1.  Linear regression is not appropriate for classification, since values are not necessarily correlated: it is though useful to model the conditional expectation of the outcome as linear combination of the features.

The system is rewritten so that $\epsilon_i$ (error), following a normal distribution, becomes the probability of having a value $y_i$ according to a normal with mean $y_i | \mu_i$. The expectation is then $E(y_i | x_i) = \mu_i$.

Logistic regression models the conditional expectation of the outcome conditioned on the features. The expectation $\mu$ in a binary classification is probability of class 1 ($\mu > 0.5$):  real numbers used in linear regression are mapped to the $[0, 1]$ interval using the logistic function $ \lambda(t) = \frac{1}{1+e^{-t}}$ or the inverse sigmoid (logit). 

Logistic regression can also be applied to generalized linear models (Poisson, Gamma) exploiting a probability distribution from the exponential family, a linear predictor and a link function (inverse of activation function).

In R, it can be fit using the `glm` function (generalized linear model). The fitted model can be applied to data (seen or unseen) using `predict`, returning the linear predictor or probabilities.

The odds for a binary variable $y$ are defined as $\frac{p(y)}{1-p(y)}$. The odds ratio is the ratio of the odds of $y$ with and without a specific condition. It can be obtained with `exp`.

The $\beta$ coefficients from logistic regression are log odds ratios, given that all other variables are fixed.

##### KNN

K-nearest neighbor is a classifier which works assigning the most common class among the $k$ nearest training observation, using Euclidean distance measurement.

In very high dimensions, the classifier explodes since the volume of space grows exponentially (curse of dimensionality). More modeling assumptions are required.

##### Neural networks

Neural network apply logistic regression modeling more complex functions, composing successions of linear transformations and non-linear activation functions. Most popular applications concern images and sequences.

$$ \mu = \theta(\eta) = \frac{e^\eta}{1+e^\eta}$$

##### SVM

todo

##### Decision trees

todo

##### Random forests

Random forests are an efficient strategy against overfitting which can be applied in presence of multiple decisions. The output is a majority vote of many decision trees, fitting each of them to a random sampling of data with random subsets of features. 

They are widely used for complex unknown models.



### Assessing classifiers

Assessing classifiers can be done through a confusion matrix, a table containing predicted class and true class distinguishing true positives, false negatives (type I error), false positives (type II error) and true negatives. A classifier should minimize type I-II errors.

Classifiers can also be assessed through other measurements:

* Sensitivity, $\frac{TP}{P} = \frac{TP}{TP+FN}$;
* Specificity, $\frac{TN}{N} = \frac{TN}{TN+FP}$;
* Positive predictive value, $\frac{TP}{TP+FP}$;
* False discovery rate (hypothesis testing), $E[\frac{FP}{TP+FP}] = E[1 - \frac{FP}{TP+FP}] = 1 - E[PPV]$.

Accuracy (fraction of correct predictions) can be problematic in presence of unequal class distributions. These measurements are also calculated applying Bayes theorem, solving class imbalance with other tests or further screening.

Many classification methods do not assign a class, but rather output a score. It is necessary to set a cutoff above which a prediction is considered positive, depending on the problem. 



### Training classifiers

Models don't have hyperparameters to control their complexity (degree, depth, layers): complex models overfit data.

Model fitting is performed without using the test dataset: if the model does not generalize, the quality on test set will be low.

##### Cross-validation

Cross-validation identifies optimal mode complexity without needing test data, mimicking it using training data. A part of the attributes are used to train and another to validate, and then subsets are switched through the $k$ iterations. Quality measures need to be aggregated over the $k$ folds.

This method works on the assumption that training and test samples are independently and identically distributed, which is not always the case. Data sometimes comes in clusters, and measures are correlated.

Performing cross-validation at the level of individual data will favor models that learns the cluster, therefore it needs to be performed at a cluster level, requiring application knowledge and eventual data visualization techniques.