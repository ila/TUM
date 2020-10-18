#include <chrono>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include "../include/JoinQuery.hpp"

// usage: because I am dumb with compiling
// g++ -O3 src/main.cpp src/JoinQuery.cpp -o JoinQuery
// ./JoinQuery test/data/tpch/sf0_001/lineitem.tbl test/data/tpch/sf0_001/orders.tbl test/data/tpch/sf0_001/customer.tbl 
// TODO link executable

//---------------------------------------------------------------------------
int main(int argc, char *argv[])
{
   using namespace std;
   if (argc != 4) {
      cout << "Usage: " << argv[0]
           << " <lineitem.tbl> <order.tbl> <customer.tbl>";
      exit(1);
   }

   vector<string> segments;
   
   for (string segment; getline(cin, segment); ) 
      segments.push_back(segment);

   JoinQuery q(argv[1], argv[2], argv[3]);

   // run a query for each retrieved segment
   for (auto &segment : segments) cout << q.avg(segment) << "\n";

   // flush output buffer
   cout.flush();
}
//---------------------------------------------------------------------------
