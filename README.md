# Dimensionality-Reduction-FastMap

For fast searching in traditional and multimedia databases, it is necessary to map objects into
points in k-d space, using k feature-extraction functions provided by a domain expert.
Fast map is a fast algorithm to map objects into points in k-dimensional space such that the dis-
similarities are preserved.

Thus we can use highly fine-tuned spatial access methods to answer several types of queries
including
Query By Example,
All pairs query,
Nearest-neighbor query,
and Best-match query.
Also we can use it for visualization and data-mining. The objects can now be plotted in 2-d, 3-d
space, revealing potential clusters, correlations among attributes.
This method is compared with Multidimensional Scaling although it is unsuitable for indexing
whereas Fast Map is suitable for indexing.
It is significantly faster than MDS. Fast map is a linear time algorithm where as MDS is a
quadratic time algorithm.

￼Given a set of simulation files, coordinates of each file is computed in the reduced space. Given a simulation file as a query object, top k similar simulations are returned. Distance between files is ￼considered to be the inverse of the similarity measure of the files.

Computation of distance matrix is O(N^2) where N is the number of objects. Given the distance matrix, finding the coordinates in the reduced space is O(rN) as number of iterations is r and in each iteration
finding the distant objects is O(N).
For mapping the query object, as the pivot objects and projected distances between the pivot objects are stored in the reduction phase, time complexity is O(r) as for every iteration only the
coordinate(projection) of the query object on the pivot line is to be calculated.
