# include <cstdlib>
# include <iostream>
# include <iomanip>
# include <ctime>
# include <omp.h>

using namespace std;

int main (int argc, char *argv[]);
long fib (int n);
int SEQ_THRESHOLD  = 20;
//****************************************************************************80

int main (int argc, char *argv[]) {

    if (argc < 2) {
        cout << "usage ./fib_openmp <number> [<sequentialThreshold>]\n";
        return -1;        
    }
    if (argc == 3) {
        SEQ_THRESHOLD = atoi(argv[2]);
    }

    long res;
    int num = atoi(argv[1]);

    cout << "\n";
    cout << " Computing fibonacci of " << num << " with seq threshold = " << SEQ_THRESHOLD << "\n";
    cout << "  Number of processors available = " << omp_get_num_procs ( ) << "\n";
    cout << "  Number of threads =              " << omp_get_max_threads ( ) << "\n";

#pragma omp parallel
    {
#pragma omp single nowait
        {
            res = fib(num);
        } // end of single region
    } // end of parallel region

    cout << "Fibonacci(" << num << ") = " << res << "\n";

    return 0;
}
//****************************************************************************80

long fib(int n) {
    // Basic algorithm: f(n) = f(n-1) + f(n-2)
    long fnm1, fnm2, fn;
    if ( n == 0 || n == 1 ) return(n);

    // In case the sequence gets too short, execute the serial version
    if ( n < SEQ_THRESHOLD ) {
        return(fib(n-1)+fib(n-2));
    }
    else {
#pragma omp task shared(fnm1)
        fnm1 = fib(n-1);
#pragma omp task shared(fnm2)
        fnm2 = fib(n-2);
#pragma omp taskwait
        fn = fnm1 + fnm2;
        return(fn);
    }
}
