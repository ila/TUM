#include "../include/DistCalculator.hpp"
#include <chrono>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include <list>
#include <climits>
#include <fstream>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <thread>
#include <queue>
#include <unordered_map>
#include <unordered_set>
#include <array>

using namespace std;
using Node = uint64_t;

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

const int num_actors = 1971697;
const int num_movies = 1151759;
vector<vector<Node>> actors = vector<vector<Node>>(num_actors);
vector<vector<Node>> movies = vector<vector<Node>>(num_movies);

// distance calculator between two actors
// csv entries with actorID, movieID
// computing the length between two actors

DistCalculator::DistCalculator(string edgeListFile)
{
	// implement graph parsing here
	// actor, movie
	// 1151758 max movie value

	// this code for some miracle manages to read the csv and store values
	// takes around 4 seconds on my computer

	MemoryMappedFile in;
	in.open(edgeListFile.data());

	uint64_t actor;
	uint64_t movie;
	// TODO HANDLE HEADER

	for (auto iter = in.begin(), limit = in.end(); iter != limit; ) {

		actor = 0;
		movie = 0;

		for ( ; iter != limit; ++iter) {

		    char c =* iter;
		    if (c == ',') break;
		    actor = 10 * actor + c - '0';
		
		}

		++iter;

		for ( ; iter != limit; ++iter) {

		    char c =* iter;
		    if (c == '\n') break;
		    movie = 10 * movie + c - '0';

		}

		actors[actor].push_back(movie);
		movies[movie].push_back(actor);

		iter = find<'\n'>(iter, limit);

		if (iter != limit) 
		    ++iter;

	}

}


// Node object is uint64_t
int64_t DistCalculator::dist(Node src, Node dest)
{

	if (src == dest)
		return 0;

	// implementing bidirectional search
	// shadowing GeeksForGeeks implementation

	// boolean arrays to keep track of visited actors
	vector<bool> src_visited = vector<bool>(num_actors, false);
    vector<bool> dest_visited = vector<bool>(num_actors, false);

    // boolean arrays to keep track of visited movies
    vector<bool> movies_visited = vector<bool>(num_movies, false);

    vector<Node> actor_dist_src = vector<Node>(num_actors, 0);
    vector<Node> actor_dist_dest = vector<Node>(num_actors, 0);

    // queue for front and backward search
    queue<Node> src_queue;
    queue<Node> dest_queue;

    // let's start
    src_queue.push(src);
    dest_queue.push(dest);

    src_visited[src] = true;
    dest_visited[dest] = true;

    while (!src_queue.empty() || !dest_queue.empty()) {

    	// BFS number 1
        for (unsigned long int i = 0; i < src_queue.size(); ++i)
        {
            Node front = src_queue.front();
            src_queue.pop();

            vector<Node>& movies_1 = actors[front];
            Node front_dist = actor_dist_src[front];

            if (!movies_1.empty())
            {
                for (auto movie = movies_1.begin(); movie < movies_1.end(); ++movie)
                {
                    if(!movies_visited[*movie])
                    {
                        movies_visited[*movie] = true;
                        vector<Node>& actors = movies[*movie];
                        for (auto actor = actors.begin(); actor < actors.end(); ++actor)
                        {
                            if(!src_visited[*actor])
                            {
                                src_visited[*actor] = true;
                                actor_dist_src[*actor] = front_dist + 1;
                                src_queue.push(*actor);
                            }
                        }
                    }
                }
            }
        }

        // BFS number 2
        // literally copy pasted but with destination this time
        for (unsigned long int i = 0; i < dest_queue.size(); ++i)
        {
            Node front = dest_queue.front();
            dest_queue.pop();

            vector<Node>& movies_2 = actors[front];
            Node front_dist = actor_dist_dest[front];

            if (!movies_2.empty())
            {
                for (auto movie = movies_2.begin(); movie < movies_2.end(); ++movie)
                {
                    if(!movies_visited[*movie])
                    {
                        movies_visited[*movie] = true;
                        vector<Node>& actors = movies[*movie];
                        for (auto actor = actors.begin(); actor < actors.end(); ++actor)
                        {
                            if(!dest_visited[*actor])
                            {
                                dest_visited[*actor] = true;
                                actor_dist_dest[*actor] = front_dist + 1;
                                dest_queue.push(*actor);
                            }
                        }
                    }
                }
            }
        }

        // now check for intersecting vertex
        uint64_t minimum = num_movies;

        for (uint64_t i = 0; i < src_visited.size(); ++i)
        {
            if (src_visited[i] && dest_visited[i])
            {
                auto dist = actor_dist_src[i] + actor_dist_dest[i];
                if (dist < minimum) {
                    minimum = dist;
                }
            }
        }

        if (minimum != num_movies) 
        	return minimum;
    }

    // not found
    return -1;
}

