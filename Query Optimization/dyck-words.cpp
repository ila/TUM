// usage:
// g++ dyck-words.cpp -o dyck-words.out
// ./dyck-words.out

#include <iostream>

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


	int p = possibilities(0, 0, relations);

	if (rank >= p) {
		std::cout << "Rank cannot be greater than the total number of possibilities! ";
		return -1;
	}

	int j = 1;
	for (int i = 1; i < 2 * relations; i++) {

		p = possibilities(i, j, relations);

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

	return 0;
}
