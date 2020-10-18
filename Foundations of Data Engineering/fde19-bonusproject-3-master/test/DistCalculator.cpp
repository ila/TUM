#include "DistCalculator.hpp"
#include <gtest/gtest.h>
#include <iostream>

//---------------------------------------------------------------------------
TEST(DistCalculator, SomeCases)
/// test some distances
{
  std::string loc(__FILE__);
  auto dir = loc.substr(0,loc.find_last_of("/")+1);

  DistCalculator dc(dir + "SimpleGraph.txt");

  // Check for distance to self
  ASSERT_EQ(dc.dist(2,2), 0);
  // Check for distance to self of unknown node
  ASSERT_EQ(dc.dist(8,8), 0);
  // Check distance of collaborators on one movie
  ASSERT_EQ(dc.dist(1,2), 1);
  // Check distance of one removed
  ASSERT_EQ(dc.dist(1,4), 2);
  // Check non connected actors
  ASSERT_EQ(dc.dist(3,1), -1);
}

// TODO: Create more test cases if you need any
