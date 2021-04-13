## Description

This Query Optimization algorithm calculator is 100% written by Oscar Lange. It is based upon the lecture QueryOptimization at TUM(see https://db.in.tum.de/teaching/ws2021/queryopt/).  
All code written is entirely made by me, with the help of lecture slides.  

## Configuration

First you need to change the values in `src/inputfiles` to fit the querygraph/precedencegraph you want.  
There are examples in the input files, how to use them, and what algorithms use the specified files.  
All other configurations like cost functions, cross products, etc. are automatically asked by the Console.  
There might be cases, where the program asks you to decide between 2 edges/relations if they have the same value,  
or if you choose the semi-join Cost Function, then the program asks you to evaluate the log, because I didn't get to implement log for BigDecimal.  

## Folder Structure

- `src`: the folder to maintain sources
  - `algorithms`: the folder contains all implemented algorithms
  - `datastructure`: the folder contains necessary classes like edges,querygraphs,relations...
  - `helper`: the folder contains help functionality like cost functions and comparators
  - `input`: the folder contains class that provide functionality to read files/console
  - `inputfiles`: the folder contains .txt files as input for the algorithms with examples
- `lib`: the folder to maintain dependencies

