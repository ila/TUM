#! /usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$DIR/build.sh || exit 1
spark-submit --class "ReturnTripTest" --driver-memory 25g $DIR/target/scala-2.11/return-trip-test_2.11-1.0.jar $DIR/../data/yellow_tripdata_2016-01.short.csv
