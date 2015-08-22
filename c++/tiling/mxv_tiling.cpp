#include <stdio.h>
#include <cstdlib>
#include <chrono>
#include <iostream>

#ifndef N
#define N 1024
#endif
#ifndef TAIL
#define TAIL 4
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

void tailing (int** a, int* b, int* c) {
    
    for (int i = 0; i < N; i += TAIL) {
        for (int j = 0; j < N; j += TAIL)
            for (int x = i; x < std::min(i + TAIL, N); ++x)
                for (int y = j; y < std::min(j + TAIL, N); ++y)
                    c[x] = c[x] + a[x][y] * b[y];
        
    }
}


int main (int argc, char** argv) {

    std::cout << "Working with tail = " << TAIL << ", N = "
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
    tailing (a, b, c);
    end = std::chrono::system_clock::now();
    std::cout << "Tailing elapse time : " << std::chrono::duration_cast<std::chrono::milliseconds>(end-start).count() << " ms\n";

    return 0;
}
