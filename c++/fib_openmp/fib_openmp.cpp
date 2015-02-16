# include <cstdlib>
# include <iostream>
# include <iomanip>
# include <ctime>
# include <omp.h>

using namespace std;

int main (int argc, char *argv[]);
int fib ( int n );

//****************************************************************************80

int main (int argc, char *argv[]) {

    if (argc < 2)
        return -1;        

    int num = atoi(argv[1]);

//    cout << "\n";
//    cout << "MXV_OPENMP:\n";
//    cout << "  C++/OpenMP version\n";
//    cout << "\n";
//    cout << "  Compute matrix vector products y = A*x.\n";

    cout << "\n";
    cout << " Computing fibonacci of " << num <<"\n";
    cout << "  Number of processors available = " << omp_get_num_procs ( ) << "\n";
    cout << "  Number of threads =              " << omp_get_max_threads ( ) << "\n";


    int res = fib(num);

    cout << "Fibonacci(" << num << ") = " << res << "\n";

    return 0;
}
//****************************************************************************80

int fib(int n) {
    int i, j;
    if (n<2)
        return n;
    else {
#pragma omp task shared(i)
        i=fib(n-1);
#pragma omp task shared(j)
        j=fib(n-2);
#pragma omp taskwait
        return i+j;
    }
}
