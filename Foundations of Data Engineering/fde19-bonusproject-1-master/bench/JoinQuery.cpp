#include "JoinQuery.hpp"
#include "Util.hpp"
#include <benchmark/benchmark.h>
#include <string>
//---------------------------------------------------------------------------
static void BM_LineCount(benchmark::State &state) {
  auto dataDir = getDir(__FILE__) + "/../test/data/tpch/sf0_001/";
  JoinQuery query(dataDir + "lineitem.tbl", dataDir + "orders.tbl",
                  dataDir + "customer.tbl");
  auto file = getDir(__FILE__) + "/../test/data/tpch/sf0_001/lineitem.tbl";
  size_t result;
  while (state.KeepRunning())
    result = query.lineCount(file);

  state.SetItemsProcessed(result);
}
BENCHMARK(BM_LineCount);
//---------------------------------------------------------------------------
static void BM_SmallQuery(benchmark::State &state) {
  auto dataDir = getDir(__FILE__) + "/../test/data/tpch/sf0_001/";
  JoinQuery query(dataDir + "lineitem.tbl", dataDir + "orders.tbl",
                  dataDir + "customer.tbl");
  while (state.KeepRunning()) {
    auto result = query.avg("MACHINERY");
    // guards against the compiler optimizing away this result
    benchmark::DoNotOptimize(result);
  }
}
BENCHMARK(BM_SmallQuery);
//---------------------------------------------------------------------------
BENCHMARK_MAIN();
//---------------------------------------------------------------------------
