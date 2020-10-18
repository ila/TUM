#include "../include/JoinQuery.hpp"
#include <assert.h>
#include <fstream>
#include <thread>
#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <iterator>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <unordered_map>
#include <string.h>
#include <stdio.h>

using namespace std;

class MemoryMappedFile
{
   int handle = -1;
   uintptr_t size = 0;
   char* mapping = nullptr;

public:
   ~MemoryMappedFile() { close(); }

   bool open(const char* file);
   void close();

   const char* begin() const { return mapping; }
   const char* end() const { return mapping + size; }
};

bool MemoryMappedFile::open(const char* file)
{
   close();

   int h = ::open(file, O_RDONLY);
   if (h < 0) return false;

   lseek(h, 0, SEEK_END);
   size = lseek(h, 0, SEEK_CUR);

   auto m = mmap(nullptr, size, PROT_READ, MAP_SHARED, h, 0);
   if (m == MAP_FAILED) {
      ::close(h);
      return false;
   }

   handle = h;
   mapping = static_cast<char*>(m);
   return true;
}

void MemoryMappedFile::close()
{
   if (handle >= 0) {
      munmap(mapping, size);
      ::close(handle);
      handle =- 1;
   }
}


unordered_map<string, string> o;
unordered_map<string, string> c1;
unordered_map<int, pair<string, string>> l;


template <char c>
static inline const char* find(const char* iter, const char* limit)
{
   	auto limit8 = limit - 8;
   	while (iter < limit8) {
    	auto block = *reinterpret_cast<const uint64_t*>(iter);

      	constexpr uint64_t pattern = (static_cast<uint64_t>(c) << 56) | (static_cast<uint64_t>(c) << 48) | (static_cast<uint64_t>(c) << 40) | (static_cast<uint64_t>(c) << 32) | (static_cast<uint64_t>(c) << 24) | (static_cast<uint64_t>(c) << 16) | (static_cast<uint64_t>(c) << 8) | (static_cast<uint64_t>(c) << 0);

      	constexpr uint64_t lowerBits = 0x7F7F7F7F7F7F7F7Full;
      	constexpr uint64_t topBits = ~lowerBits;

      	uint64_t asciiChars = (~block) & topBits;
      	uint64_t foundPattern = ~((((block & lowerBits) ^ pattern) + lowerBits) & topBits);
      	uint64_t matches = foundPattern & asciiChars;

      	if (matches) {
        	return iter + __builtin_ctzll(matches) / 8;
        } 

        else {
        	iter += 8;
      	}
   	}
   	while ((iter != limit) && ((*iter) != c)) ++iter;
   	return iter;
}

template <char c>
static inline const char* findNth(const char* iter, const char* limit, unsigned n)
{
   	auto limit8 = limit - 8;
   	while (iter < limit8) {

    	auto block = *reinterpret_cast<const uint64_t*>(iter);

      	constexpr uint64_t pattern = (static_cast<uint64_t>(c) << 56) | (static_cast<uint64_t>(c) << 48)|(static_cast<uint64_t>(c) << 40)|(static_cast<uint64_t>(c) << 32) | (static_cast<uint64_t>(c) << 24) | (static_cast<uint64_t>(c) << 16) | (static_cast<uint64_t>(c) << 8) | (static_cast<uint64_t>(c) << 0);

      	constexpr uint64_t lowerBits = 0x7F7F7F7F7F7F7F7Full;
      	constexpr uint64_t topBits = ~lowerBits;
      	uint64_t asciiChars = (~block) & topBits;
      	uint64_t foundPattern = ~((((block & lowerBits) ^ pattern) + lowerBits) & topBits);
      	uint64_t matches = foundPattern & asciiChars;

      	if (matches) {
        	unsigned hits = __builtin_popcountll(matches);

         	if (hits < n) {
            	n -= hits;
            	iter += 8;
         	} 

         	else {

            	while (n > 1) {
               		matches &= matches - 1;
               		--n;
            	}

            return iter + __builtin_ctzll(matches) / 8;
        	}

      	} 

      	else {
        	iter += 8;
      	}
   	}

   	while ((iter != limit) && ((*iter) != c)) ++iter;
   	return iter;
}


//---------------------------------------------------------------------------
JoinQuery::JoinQuery(std::string arg1, std::string arg2,
					 std::string arg3)
{

	MemoryMappedFile in1;
	in1.open(arg1.data());

	MemoryMappedFile in2;
	in2.open(arg2.data());

	MemoryMappedFile in3;
	in3.open(arg3.data());


	string orderkey;
	string custkey;
	string mktsegment;
	string quantity;


	for (auto iter = in3.begin(), limit = in3.end(); iter != limit; ) {

		mktsegment = "";
		custkey = "";

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') break;

		    custkey += (*iter);
		
		}

		iter = findNth<'|'>(iter, limit, 6);

		if (iter != limit) 
			++iter;

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') 
		        break;

		    mktsegment += (*iter);

		}

		c1.insert(pair<string, string>(custkey, mktsegment)); 

		iter = find<'\n'>(iter, limit);

		if (iter != limit) 
		    ++iter;

	}


	for (auto iter = in2.begin(), limit = in2.end(); iter != limit; ) {

		orderkey = "";
		custkey = "";

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') break;

		    orderkey += (*iter);
		
		}

		if (iter != limit) 
			++iter;

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') 
		        break;

		    custkey += (*iter);

		}

		o.insert(pair<string, string>(orderkey, custkey)); 

		iter = find<'\n'>(iter, limit);

		if (iter != limit) 
		    ++iter;

	}

	int index = 0;

	for (auto iter = in1.begin(), limit = in1.end(); iter != limit; ) {

		orderkey = "";
		quantity = "";

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') break;

		    orderkey += (*iter);
		
		}
		
		iter = findNth<'|'>(iter, limit, 4);

		if (iter != limit) 
			++iter;

		for ( ; iter != limit; ++iter) {

		    char c =* iter;

		    if (c == '|') 
		        break;

		    quantity += (*iter);

		}

		l.insert({index, pair<string, string>(orderkey, quantity)});

		++index;
		
		iter = find<'\n'>(iter, limit);

		if (iter != limit) 
		    ++iter;

	}


	//JoinQuery::avg("MACHINERY");

	// joining vectors
	// l_quantity | l_orderkey | o_custkey | o_mktsegment

}

//---------------------------------------------------------------------------
size_t JoinQuery::avg(std::string segmentParam)
{

	uint64_t sum = 0;
	uint64_t items = 0;

	// o[orderkey, custkey]
	// c[custkey, segmentParam]
	// l[index, (orderkey, quantity)]


	for(auto& x: l) {

		auto y = o.find(x.second.first);
		auto z = c1.find(y -> second);

		if (z -> second == segmentParam) {

			sum += stoi(x.second.second);
    		++items;

		}

	}


    uint64_t avg = sum * 100 / items;

    return avg;
}

//---------------------------------------------------------------------------
size_t JoinQuery::lineCount(std::string rel)
{
	std::ifstream relation(rel);
	assert(relation);	// make sure the provided string references a file
	size_t n = 0;
	for (std::string line; std::getline(relation, line);) n++;
	return n;
}
//---------------------------------------------------------------------------
