#!/bin/bash

NUM=$1
NUM_THREADS=$2

export OMP_NUM_THREADS=$NUM_THREADS

time $PWD/fib_openmp $NUM
