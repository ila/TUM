# 11 Software for UQ
### From Scratch vs. using packages
in short: avoid re-inventing the wheel for simple things (Monte Carlo); avoid re-writing huge packages for more difficult algorithms.

Assess the quality of packages, the flexibility, maintainability, license issues.

### UQ Software vs. UQ Packages
Assess: 
- how many people develop it
- is it for specific tasks or applicable in a broader way
- how hard to get up to speed with the software?
- is there quality assurance/user support etc.?

![[UQ map.png]]

### Concrete UQ Software Examples

##### Chaospy
Ph.D. thesis development (2014 - ), development continues.
- Python
- Fast support via mail, good doc and tutorials
- meant for fast prototyping
- some exactness issues

##### Dakota
From Sandia National Lab. Most mature: since 1994. Originally optimization, increasingly for UQ.
- C++
- supports high-performance computing
- not prototyping, more for production; requires experience
- large user base

##### MUQ
Group of Marzouk (MIT, prominent researcher). Since 2011.
- C++
- making developments of this research group available (cutting-edge research)

##### Openturns
From 4 companies Airbus, EDF, IMACS, Phimeca Engineering. Since 2005.
- C++ / interface support for Python
- large user base

##### SG++
From University of Stuttgart/TUM, since 2007.
- C++ / interface for C++, Python, Java, MATLAB
- focuses on sparse grids (interpolation, quadrature, PDEs, ML, QU, optimization)
- TUM chair has experience with it (can ask for tips)

##### TASMANIAN
From Oak Ridge National Lab, since 2013
- C++ / interface for Python
- mainly for sparse grids (*combination technique*): high-dim. integration. interpolation, parameter calibration

##### UQTk ("UQ Toolkit")
From Sandia National Lab, but different focus than [[#Dakota]]. Since 2005.
- C++ / interface for Python
- focus on forward/inverse UQ
- for fast prototyping, less focus on performance
- broad: implements many methods

Planned: joint work with Dakota 

##### Uranie
CEA France (nuclear energy organization; also defense aspects)
- Python, C++, C / interface for Python
- more for their internal use, but public with open license
- mostly for nuclear/renewable energies
- HPC support