#include "JoinQuery.hpp"
#include <gtest/gtest.h>
#include "Util.hpp"

//---------------------------------------------------------------------------
TEST(JoinQuery, LineCount)
/// test if line counting works
{
   auto dataDir = getDir(__FILE__) + "/data/tpch/sf0_001/";
   JoinQuery query(dataDir + "lineitem.tbl", dataDir + "orders.tbl",
                   dataDir + "customer.tbl");
   auto result =
       query.lineCount(getDir(__FILE__) + "/data/tpch/sf0_001/lineitem.tbl");
   ASSERT_EQ(result, size_t(6005));
}
//---------------------------------------------------------------------------
TEST(JoinQuery, SmallInput)
/// test if the correct average if returned for test data
{
   auto dataDir = getDir(__FILE__) + "/data/tpch/sf0_001/";
   JoinQuery query(dataDir + "lineitem.tbl", dataDir + "orders.tbl",
                   dataDir + "customer.tbl");
   auto result = query.avg("MACHINERY");
   ASSERT_EQ(result, size_t(2539));
}
//---------------------------------------------------------------------------
