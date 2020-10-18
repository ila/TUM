#include <chrono>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include "../include/DistCalculator.hpp"

//---------------------------------------------------------------------------
int main(int argc, char *argv[])
{
   using namespace std;

   if (argc != 2) {
      cout << "Usage: " << argv[0] << " <playedin.csv>";
      exit(1);
   }

   string playedinFile(argv[1]);

   // Create dist calculator
   DistCalculator dc(playedinFile);

   // read queries from standard in and return distances
   DistCalculator::Node a, b;
   while (cin >> a && cin >> b) cout << dc.dist(a, b) << "\n";

   // flush output buffer
   cout.flush();
}
//---------------------------------------------------------------------------
