// usage:
// g++ -o src --std=c++17 dyck-words.cpp JoinTree.cpp
// ./src

#include <iostream>

#include "JoinTree.h"


inline double factorial(int n) {
	double f = 1.0;
	while (n > 1) {
		f *= n--;
	}
	return f;
}

inline int ballotNumber(double i, double j) {
	return (j + 1) / (i + 1) * factorial(i + 1) / factorial(0.5 * (i + j) + 1) / factorial(0.5 * (i - j));
}

inline int possibilities(int i, int j, int n) {
	return ballotNumber(2 * n - i, j);
}

int main(int argc, const char** argv) {

	int relations;
	int rank;
	std::string parentheses = "";

	std::cout << "Enter the number of relations: ";
	std::cin >> relations;

	std::cout << "Enter the rank: ";
	std::cin >> rank;


	int p = possibilities(0, 0, relations - 1);

	if (rank >= p) {
		std::cout << "Rank cannot be greater than the total number of possibilities! ";
		return -1;
	}

    std::cout << std::endl;
    std::cout << "Dyck grid" << std::endl;
    for (int i = 0; i < 2 * relations - 2; i++) {
        std::vector<int> spu;
        for (int j = i % 2; j <= std::min(i, 2 * relations - 2 - i); j += 2) {
            spu.push_back(possibilities(i, j, relations - 1));
        }
        if (i % 2) {
            std::cout << "\t";
        }
        for (const auto& c: spu) std::cout << c << "\t\t";
        std::cout << std::endl;
    }
    std::cout << 0 << std::endl;

	int j = 1;
	for (int i = 1; i < 2 * relations - 2; i++) {

		p = possibilities(i, j, relations - 1);

		if (rank < p) {
			parentheses += "(";
			std::cout << "\nMoving upwards! Rank: " << rank;
			j++;
		}

		else {
			parentheses += ")";
			std::cout << "\nMoving downwards! Previous rank: " << rank;
			rank -= p;
			std::cout << ", new rank: " << rank;
			j--;
		}
	}
	parentheses += ")";
	std::cout << "\n\nObtained Dyck word: " << parentheses << "\n";

    std::vector<bool> traversal(parentheses.size());
    for (uint32_t i = 0; i < traversal.size(); i++) {
        if (parentheses[i] == '(') {
            traversal[i] = true;
        }
    }

    std::vector<std::string> nodeOrder;
    TNodes nodes;

    for (uint32_t i = 0; i < relations; i++) {
        std::string name{static_cast<char>(i + 'A')};
        nodeOrder.emplace_back(name);
        nodes.emplace(name, 1.0);
    }

    auto joinTree = JoinTree::Create(
        traversal,
        nodeOrder,
        nodes,
        {}
    );

    std::cout << joinTree->GetRoot()->Visualize() << std::endl;

	return 0;
}
