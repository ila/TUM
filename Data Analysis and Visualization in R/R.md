# R cheatsheet

### Functions

Functions do not need all argument names: after two arguments, a variable number of parameters can be passed, specified through the `...` syntax.

`%in%` is a special function that allows to check whether the elements of a structure also belong to another structure.

`%>%` is the equivalent of the Unix pipe, concatenating an operation to the output of another.

##### Apply

`apply(df, margin, function, ...)` helps avoiding to update the environment at every step of an iteration (for/while loop). It can apply a function to the elements of the steps, in case they are independent of each other.

* `margin` specifies rows or columns (1, 2);
* `sapply` is a variation which returns a vector, performed on the columns by default;
* `lapply` works on elements of an array, returns a list, and can be generally used to create lists.

Example:

```R
> df <- data.frame(x = 1:3, y = 9:11)
  x  y
1 1  9
2 2 10
3 3 11
> sapply(df, sum) = apply(df, 2, sum)
 x  y 
 6 30 
> lapply(df, sum)
$x
[1] 6
$y
[1] 30
> apply(df, 1, sum)
[1] 10 12 14
```

Additional arguments are permitted, yet they must be explicitly stated.



### Data wrangling

A data table modifies columns by reference, offering subsetting, ordering and merging in a more efficient way respect to data frames. It is faster than other external packages, and easier to read or maintain.

`DT[i, j, by]` where `i` represents the subset of rows to select, `j` is the expression to calculate, and `by` the eventual grouping. More expressions can also be applied concatenating sets of square brackets. Sorting is performed through the `order` or `setorder` keywords.

When `i` is not specified, a new data table is created with aggregated values.

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

`melt(dt, id.vars, variable.name)` transforms from wide to long. It is used to remove confusing column names and turning them to attributes of observations. 

`dcast(dt, columns ~ element, value)` transforms from long to wide. It is used to remove multiple appearances of observations, turning variable types into columns. 

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

K-means does not always work in cases when a finite mixture module would be more appropriate: clusters should have the same covariance and the same individual prior probability.

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

$$ R = \frac{a+b}{{{n}\choose{2}}}$$

* $S$ is a set of $n$ elements;
* $X$ is a partition of $S$ into $k$ sets;
* $Y$ is a partition of $S$ into $l$ sets;
* $a$ is the number of pairs of elements in $S$ that are in the same set both in $X$ and $Y$;
* $b$ is the number of pairs of elements in $S$ that are in different sets both in $X$ and $Y$;
* ${n}\choose{2}$ is the total number of pairs of elements in $S$.

The purpose of this value is to compare different clustering techniques of the same dataset with different number of clusters, trying to maximize similarity in the same set and minimize it between others. 

It can be seen as a measure of the percentage of the correct decisions made by the algorithm.

Its values range between 0 and 1, where 1 implies that the two partitions are identical. It can be applied in hierarchical clustering fixing the number of clusters.



### Statistical testing

Trends can sometimes be unlinked to hypotheses, therefore statistical testings are needed to assess correctness.

Examples of wrong assessments:

* Statistical significance, trends observed purely random, with not enough data to infer whether they are outliers or actual phenomenon;
* Confounding, analysis not necessarily inducing correlations or correlation caused by another variable

Summary statistics consists in a single number that summarizes the data and captures the trend, for instance the difference of medians or means. The larger the statistics, the stronger the trend.

The hypothesis to assess is: having an infinite number of observation, would the trend still hold?

This can be proved setting a null hypothesis $H_0$ assuming that there is no relationship between the two measured phenomena. Under the null hypotheses, the statistics follows a certain distribution.

##### P-value and Monte Carlo schemes

The p-value is the probability that the statistics would be the same or more extreme than the actual observed results. 

Being extreme depends on the tail of events: for right-tail it should be greater, and so on.
Double tail events: $P = 2 * min\{p(T \leq T_{obs} | H_0), p(T \geq T_{obs} | H_0)\}$

The null hypothesis is said to be rejected for sufficiently small p-values, generally $0.05$.

The p-value does not infer the probability that the null hypothesis is true given the data, since absence of evidence is not evidence of absence, and neither the probability of the observed statistics being true.

Let $m$ be the number of Monte Carlo permutations, $r = \#{T^* \geq T_{obs}}$ be the number of random permutations that produce a test statistic greater or equal to that calculated for the actual data.

Then, the estimated p-value is $\hat{P} = \frac{r+1}{m+1}$.

When there are many statistical tests performed, it is good to use statistical tests with analytical solutions or approximations (t-test, F-test).

##### Confidence intervals

When conducting an hypothesis test, it is necessary to assume that the data follows a normal distribution. 

In many situations there is no need to assess the null hypotheses, yet it can be useful to report on the uncertainty of the estimate. A confidence interval is an interval which, would the data generation process to be repeated, would contain a parameter $\theta$ with probability $1 - \alpha$. 

The empirical distribution function is a cumulative distribution function that jumps at $\frac{1}{n}$ at each of the data points, assuming observations to be independent.

A single draw amounts to pick one data point with probability $\frac{1}{n}$, therefore equal to sampling with replacement. 

Case resampling bootstrap approach uses sampling with replacement of  the  same size from the observed data to simulate further observations.

Assuming $R$ random simulations by case resampling, each of them gives a random value for the statistics. Ranking those values, the bootstrap percentile interval is an approximation of the confidence interval.

Other estimations include the t-test, z-test and F-test.

If the distributional assumption does not hold, some non-parametric approaches can be used since they do not need normality.

To summarize:

* Hypothesis testing is performed with empirical approach and permutation testing;
* Uncertainty assessment is made using confidence intervals and case resampling bootstraps;
* Confounding requires conditioning on further variables.

This method has some issues with large datasets: testing the association of every combination of variables leads to a huge number of tests to perform, with a relevant amount of them casually having sufficiently small p-value.



### Multiple testing

Multiple testing is a method that can be used to assess a distribution, fairness of probability or in general anything concerning multiple hypotheses.

The more inferences are made, the more likely erroneous ones are to occur, therefore it is needed a strict significant threshold for individual comparisons: p-value is not estimated using $P = \frac{r+1}{m+1}$ with $m$ permutations, and a new scalable way is necessary.

##### 1 binary variable

The binomial test (`binom.test`) involves understanding whether the distribution of a marker is balanced, encoding it with binary values and computing means.

Its abstraction consists in $N$ independent tosses of a coin: the null hypothesis is the fairness of the coin, so $\mu = E(X_i) = p(X_i = 1)$ and $H_0 : \mu = 0.5$.

Since there only are 2 possible outcomes (with supposedly equal probability), the p-value is two-sided, but this does not provide sufficient evidence for rejecting the null evidence having performed only one toss.

Tossing $N$ times, instead, the outcome is a series of heads and tails having $0.5^N$ probability. The number of series with the same number of heads $T$ is ${{N}\choose{T}} = \frac{N!}{T!(N-T)!}$, therefore the total number of heads for a fair coin is ${{N}\choose{T}} 0.5^N$, following a binomial distribution.

##### 2 binary variables

Addressing tests concerning association between two binary variables can be performed by permuting the values for one while keeping the other fixed.

The total number of observations per category is preserved: the counts summarize the contingency table entirely.

Fisher's exact test (`fisher.test`) calculates the probability of obtaining a certain set of values, following the hypergeometric distribution. Having 4 observations (2 for column):

$$ p(k=a|H_0) = \frac{(a+b)!(c+d)!(a+c)!(b+d)!}{a!b!c!d!n!} $$

For right-tail one-sided tests, this quantity is computed and summed for all values of the statistics larger or equal than observed: $\sum_{i \geq a} p(k = i | H_0)$.

An approximation of Fisher's test is the Chi-squared test.

##### 1 binary, 1 continuous variable

Let $x_1, \dots, x_n$ real values of the first group, and $y_1, \dots, y_n$ real values of the second group. The hypothesis is $X$ and $Y$ coming from the same distribution, therefore independent from the groups.

Student's t-statistic (`t.test`) considers the ratio of the differences of sample means divided by the unbiased estimate of common standard deviation.

$$ t = \frac{\bar{x} - \bar{y}}{sp \sqrt{\frac{1}{n_x} + \frac{1}{n_y}}} \qquad sp = \frac{\sum_i(x_i - \bar{x})^2 + \sum_i(y_i - \bar{y})^2}{n_x + n_y - 2}$$

This test requires assumption of normal distribution. The null hypothesis states that the expected values are equal, and the t-distribution has $n_x + n_y - 2$ degrees of freedom. It does not depend on the variance, but assumes it to be equal for both samples.

In the case of unequal variance, Welch's test (default option in R) assumes a slightly different degree of freedom.

The Wilcoxon test (`wilcox.test`) is a non-parametric variant to check whether the two distributions are equal, lacking the normality assumption.

Observed values are ranked in ascending order among both distributions, and the statistic is defined as $U_x = R_x - \frac{n_x(n_x + 1)}{2}$ where $R$ is the sum of the ranks of $x_i$. The minimum $U$ is chosen, and the distribution approximates a Gaussian. 

$$ E(U) = \frac{n_xn_y}{2} \qquad Var(U) = \frac{(n_xn_y)(n_x + n_y + 1)}{12}$$

##### 2 continuous variables

To test association between two quantitative variables, linear regression can be useful to check whether they are correlated.

Pearson coefficient (covariance divided by product of standard deviations) has a correspondent t-statistic using $r^2$ and $n-2$ degrees of freedom.

Spearman correlation makes no assumptions of the distribution, and also works for non linear distributions since it has less sensitivity to outliers.



### Statistical assessments

In statistics, an effect size is a quantitative measure of the magnitude of a phenomenon. If it is small, the significance of the event is not relevant.

Increasing the sample size can help detecting differences, allowing to estimate how large a sample must be to detect effects. Big data detect factors concerning small percentages of the population.

In some cases, the p-value is significant, yet the estimated effect size is small, therefore it is important to report both.

Confidence intervals can be set to obtain different probabilities, giving an idea of the size of the estimated, and are calculated normalizing the data according to Central Limit Theorem. 

If a 95% confidence interval does not include 0, the p-value for the null hypothesis that the parameter is equal to 0 must be smaller than 0.05.

Multiple testing has some indicators that can help identifying false positives:

* Family-wise error rate, $P(V > 0)$, probability of having one or more false positives (usually large for large number of true null hypotheses);
  * Supposing $m$ tests are taken, producing each a p-value $p_g$, Bonferroni adjustment chooses $\tilde{p}_g = min\{mp_g, 1\}$. Selecting all tests with $\tilde{p}_g \leq \alpha$ controls the FWER at level $\alpha$, i. e. $Pr(V > 0) \leq \alpha$ without making assumptions about dependence or quantity;
* False discovery rate, $E[V/max(R, 1)]$, the expected fraction of false positives among all discoveries.



### Linear regression

A linear model (`lm`) allows to study the relationship between two continuous variables: the predictor and the outcome. 

A linear model is defined as $y = \alpha + \beta x_i + \epsilon_i$ with $\epsilon \sim N(0, \sigma^2)$ independently and identically distributed.

The likelihood of data can be computed using a normal distribution model: $L_(\alpha, \beta, \sigma^2) = \prod{i=1}{N} N(\epsilon_i, \sigma^2)$. Parameter estimation is performed maximizing the likelihood of data, computing the gradient and setting it to zero.

Maximizing the likelihood is equivalent to minimizing the squared distance between observation and prediction.

Measuring the accuracy of the model consists in:

* Computing predictions;
* Computing residuals (comparison of predictions with actual values);
* Computing the residual sum of squares $RSS = \sum_{i=1}^{N} \hat{\epsilon_i}^2$;
* Compare this to the total sum of squares $R^2 = 1 - \frac{RSS}{SS}$ to understand the percentage of variance explained by the model.

Standard error and estimated parameters are distributed according to a t-student, assuming there is no correlation ($\beta = 0$).

##### Multiple linear regression

$$ y_i = \beta_0 + \sum_{j=1}^{p} \beta_j x_{ij} + \epsilon_i \qquad y = X\beta + \epsilon \qquad \epsilon \sim N(0, \Sigma) \qquad \Sigma = \sigma^2I$$

Parameters can be estimated calculating the hat matrix: $\beta = (X^TX)^{-1}X^Ty$. A nested model is a special case of a general model useful to test individual predictors, setting some $\beta = 0$. Setting them all implies that only the mean $\beta_0$ can take any value.

Maximizing the log-likelihood is equal to minimizing the squared distance between observation and prediction. ANOVA provides a test of whether two or more population means are equal, splitting the groups according to characteristics.

An useful statistic is the ratio of the two maximized likelihoods for full and reduced models, to reject if it's too large.

The distribution of the likelihood ratio test follows a F distribution, and the hypothesis is rejected if $F$ is too large.

While performing ANOVA, confounders should be included in the reduced models to understand which variable has the most influence.

Model selection takes care of removing unnecessary predictors:

* Backward elimination starts with all predictors and removes the ones with greatest p-value;
* Forward selection starts with no variables and chooses the ones with lowest p-value.

Plotting can help checking the assumptions of linearity, independence of residual and normal distribution. 

If variance is not constant, some transformations such as log or square root can be applied. If residuals are not normal, the QQ-plot will not show a linear relationship and it will lead to unreliable error rates.



### Classification

Classification is important when trying to predict the value of $y$ applying linear regression, when $y$ is a category (diagnostic, spam detection, speech recognition).

The variable used to predict are called features, while the prediction is the outcome. An efficient approach takes features as input and returns a valid outcome when it is unknown, eventually training algorithms using a dataset with outcome observations already present.

##### Binary classification

Binary classification has outcome $k$ which can only take values of 0 or 1.  Linear regression is not appropriate for classification, since values are not necessarily correlated: it is though useful to model the conditional expectation of the outcome as linear combination of the features.

The system is rewritten so that $\epsilon_i$ (error), following a normal distribution, becomes the probability of having a value $y_i$ according to a normal with mean $y_i | \mu_i$. The expectation is then $E(y_i | x_i) = \mu_i$.

Logistic regression models the conditional expectation of the outcome conditioned on the features. The expectation $\mu$ in a binary classification is probability of class 1 ($\mu > 0.5$):  real numbers used in linear regression are mapped to the $[0, 1]$ interval using the logistic function $\lambda(t) = \frac{1}{1+e^{-t}}$ or the inverse sigmoid (logit). 

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

SVM works finding a hyperplane of $p - 1$ dimensions ($p$ features), separating the two classes and trying to maximize the margins in terms of distance to closest data points.

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
