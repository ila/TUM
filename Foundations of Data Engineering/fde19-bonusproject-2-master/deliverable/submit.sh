#! /usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
spark-submit --class "ReturnTripSubmit" --driver-memory 25g $DIR/target/scala-2.11/return-trip-test_2.11-1.0.jar $1
