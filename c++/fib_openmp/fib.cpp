# include <cstdlib>
# include <iostream>
# include <iomanip>

using namespace std;

int main (int argc, char *argv[]);
long fibRecursive (int n);
long fibLoop (int n);

int main (int argc, char *argv[]) {

    if (argc < 3) {
        cout << "usage ./fib <number> <recursive(0) or loop(1)>\n";
        return -1;        
    }

    long res;
    int num = atoi(argv[1]);
    int version = atoi(argv[2]);

    cout << "\n";
    cout << " Computing fibonacci of " << num;

    // computing with loop
    if (version == 1) {
        cout << " using loop version" << "\n";
        res = fibLoop(num);
    } else {
        cout << " using recursive version" << "\n";
        res = fibRecursive(num);
    }

    cout << "Fibonacci(" << num << ") = " << res << "\n";

    return 0;
}

long fibLoop(int n) {
    long last = 1, next = -1, sum;

    //Fibonacci Series Calculation
    for (int i = 0; i <= n; i++) {
        sum = next + last;
        next = last;
        last = sum;
    }

    return last;
}

long fibRecursive(int n) {
    // Basic algorithm: f(n) = f(n-1) + f(n-2)
    if ( n == 0 || n == 1 ) return n;

    return fibRecursive(n-1) + fibRecursive(n-2);
}
