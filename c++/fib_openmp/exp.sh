#!/bin/bash

if [ -z "$1" -o -z "$2" ]; then
    echo "USAGE: ./exp.sh NUM NUM_THREADS [SEQ_THRESHOLD]"
    exit -1;
fi

NUM=$1
NUM_THREADS=$2
SEQ_THRESHOLD=$3

if [ -z "$SEQ_THRESHOLD" ]; then
    SEQ_THRESHOLD=20
fi
echo Using sequential threshould = $SEQ_THRESHOLD;

export OMP_NUM_THREADS=$NUM_THREADS

(time $PWD/fib_openmp $NUM $SEQ_THRESHOLD) 2>&1 | tee fibopenmp.n${NUM}.t${NUM_THREADS}.s${SEQ_THRESHOLD}.txt
