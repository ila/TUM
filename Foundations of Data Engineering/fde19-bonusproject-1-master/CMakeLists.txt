cmake_minimum_required(VERSION 2.8 FATAL_ERROR)

set(CMAKE_C_COMPILER gcc)
set(CMAKE_CXX_COMPILER g++)
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(bonusTpchJoin C CXX)

include(lib/gtest.cmake)
include(lib/benchmark.cmake)

# Compiler flags for the different targets
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14 -fPIC -Wall -Wextra -fno-omit-frame-pointer -march=native -Wno-unknown-pragmas ")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC -Wall -Wextra -fno-omit-frame-pointer -march=native")

### Library for join query
add_library(join_query src/JoinQuery)
target_include_directories(join_query PUBLIC
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
    $<INSTALL_INTERFACE:include>
    PRIVATE src)

### Tests
add_executable(test_all test/JoinQuery.cpp)
target_link_libraries(test_all join_query gtest gtest_main pthread)

### Performance Measurements for development
add_executable(bench bench/JoinQuery.cpp)
target_link_libraries(bench benchmark join_query pthread)

### Performance Measurements for leaderboard
add_executable(main src/main.cpp)
target_link_libraries(main join_query)
