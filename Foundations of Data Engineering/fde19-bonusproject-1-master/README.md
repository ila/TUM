# FDE Bonus Projects

## Task 1

Implement the avg function and constructor in src/JoinQuery.cpp. See the corresponding header include/JoinQuery.hpp 
for documentation of this function. All solutions that compute correct results 
and achieve a time measurement better than 15 seconds on the leaderboard will be accepted for 
the bonus.

The performance evaluation will run the binary compiled from main.cpp so that the avg query is run
for multiple market segments on a TPC-H dataset with scale factor 1. The overall runtime from start to end is measured.
Details about the machine this will be executed on: Intel(R) Core(TM) i7-4770K CPU @ 3.50GHz with 4 cores, 8 hyperthreads and 32GB of memory.

You can use the test provided in test/JoinQuery.cpp to check if your implementation works
correctly.

When you do performance tuning, you may use and extend the benchmark suite in bench/JoinQuery.cpp.

## Build
A configuration file is provided to build this project with CMake. This allows you to build the project
in the terminal but also provides the option to use Jetbrains CLion or Microsoft Visual Studio and other
IDEs.

Building from Terminal:
Start in the project directory.
```
mkdir -p build/debug
cd build/debug
cmake -DCMAKE_BUILD_TYPE=Debug ../..
make
```

This creates the binaries test_all, bench and main.

## Submit
1. Change teamname and realname in team.txt. The teamname will be displayed publicly on the leaderboard.
You can choose an arbitrary name for this, but you are also welcome to use your real name. The realname will
be used later to verify your submission for the grade bonus.
2. Implement the task.
3. Commit your changes in git and push to the gitlab repository.
4. The build system will pick up your changes and evaluate the submission. If all tests succeed, your binary
is handed over to the performance measurement harness. The resulting times will be entered into the leaderboard.
5. Visit http://contest.db.in.tum.de/ to see your result.

Make sure your builds are not failing! <br/>
*Left Sidebar > CI /CD > Pipelines*
