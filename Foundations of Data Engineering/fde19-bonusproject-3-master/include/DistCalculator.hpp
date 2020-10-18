#include <string>

class DistCalculator{
public:
  using Node = uint64_t;
  DistCalculator(std::string edgeListFile);
  int64_t dist(Node a, Node b);
};
