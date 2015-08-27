#include <stdio.h>
#include <cstdlib>
#include <chrono>
#include <iostream>

#ifndef N
#define N 1024
#endif
#ifndef TILE
#define TILE 4
#endif

void naive (int** a, int* b, int* c) {
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < N; ++j) {
            c[i] = c[i] + a[i][j] * b[j];
        }
    }
}

int min (int a, int b) {
    if (a < b)
        return a;
    return b;
}

void tiling (int** a, int* b, int* c) {
    
    for (int i = 0; i < N; i += TILE) {
        for (int j = 0; j < N; j += TILE)
            for (int x = i; x < std::min(i + TILE, N); ++x)
                for (int y = j; y < std::min(j + TILE, N); ++y)
                    c[x] = c[x] + a[x][y] * b[y];
        
    }
}

int main (int argc, char** argv) {

    std::cout << "Working with tile = " << TILE << ", N = "
        << N << "\n";

    int** a;
    int* b;
    int* c;
    a = (int**) malloc(N*sizeof(int*)); 
    b = (int*) malloc(N*sizeof(int));
    c = (int*) malloc(N*sizeof(int));
    for (int i = 0; i < N; ++i) {
        a[i] = (int*) malloc(N*sizeof(N*sizeof(int)));
        c[i] = 0;
    }

    auto start = std::chrono::system_clock::now();
    naive (a, b, c);
    auto end = std::chrono::system_clock::now();
    std::cout << "Naive elapse time : " << std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count() << " ms\n";


    a = (int**) malloc(N*sizeof(int*)); 
    b = (int*) malloc(N*sizeof(int));
    c = (int*) malloc(N*sizeof(int));
    for (int i = 0; i < N; ++i) {
        a[i] = (int*) malloc(N*sizeof(N*sizeof(int)));
        c[i] = 0;
    }

    start = std::chrono::system_clock::now();
    tiling (a, b, c);
    end = std::chrono::system_clock::now();
    std::cout << "Tiling elapse time : " << std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count() << " ms\n";

    return 0;
}
