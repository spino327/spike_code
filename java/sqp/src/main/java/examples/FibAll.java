/*  
 * Copyright (C) 2015 Computer Architecture and Parallel Systems Laboratory (CAPSL) 
 *
 * Original author: Sergio Pino 
 * E-Mail: sergiop@udel.edu
 *
 * License
 *  
 * Redistribution of this code is allowed only after an explicit permission is
 * given by the original author or CAPSL and this license should be included in
 * all files, either existing or new ones. Modifying the code is allowed, but
 * the original author and/or CAPSL must be notified about these modifications.
 * The original author and/or CAPSL is also allowed to use these modifications
 * and publicly report results that include them. Appropriate acknowledgments
 * to everyone who made the modifications will be added in this case.
 *
 * Warranty 
 *
 * THIS CODE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT
 * THE COVERED CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE
 * OF THE COVERED CODE IS WITH YOU. SHOULD ANY COVERED CODE PROVE DEFECTIVE IN
 * ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER
 * OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY
 * COVERED CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 */

package examples;

import edu.udel.sqp.Task;
import edu.udel.sqp.TaskRunnerGroup;

/**
 * 
 * Based on code presented in "Patterns for Parallel programming by Mattson et al"
 */
public class FibAll {
        
    private static class Fib extends Task {

        volatile long number; // number holds value to compute initially, after computation is replaced by answer
    
        public Fib(long n) {
            number = n;
        }
    
        @Override
        public void run() {
            long n = number;
        
            if (n <= 1) {
                // do nothing; fib(0) = 0; fib(1) = 1
            } else if (n <= sequentialThreshold) {
                number = seqFib(n);
            }
        
            // otherwise use recursive parallel decomposition
            else {
                // Construct subtasks:
                Fib f1 = new Fib(n - 1);
                Fib f2 = new Fib(n - 2);
            
                // run them in parallel
                f1.fork(); //System.out.println("forking f1 : " + (n-1));
                f2.fork(); //System.out.println("forking f2 : " + (n-2));
            
                // await completion
                f1.join(); //System.out.println("joining f1 : " + (n-1));
                f2.join(); //System.out.println("joining f2 : " + (n-2));
            
                // combine results
                number = f1.number + f2.number;
            }
        
            if (fibArray[(int) n] == 0) {
                fibArray[(int) n] = number;
            }
        }
    
        static long seqFib (long n) {
            if (n <= 1) return n;
            else
                return seqFib(n-1) + seqFib(n-2);
        }
    
        long getAnswer () {
            if (!isDone())
                throw new Error("Not yet computed");
            return number;
        }
    }

    static volatile long fibArray[];
    static int sequentialThreshold = 0;
    
    public static void main (String[] args) {
        
        int procs;
        int num;
        
        try {
            // read parameters from command line
            procs = Integer.parseInt(args[0]);
            num = Integer.parseInt(args[1]);
            
            if (args.length > 2)
                sequentialThreshold = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Usage: java Fib <thread> <number> [<sequentialThreshold>]");
            return;
        }
        
        // initialize thread pool
        TaskRunnerGroup g = new TaskRunnerGroup(procs);
        
        fibArray = new long[num+1];
        
        // create first task
        Fib f = new Fib(num);
        
        // execute it
        g.executeAndWait(f);
        
        // computation has finished. shutdown thread pool
        g.cancel();
        
        // show result
        long result;
        {result = f.getAnswer();}
        for (int i = 0; i < fibArray.length; ++i) {
            System.out.format("Fibonacci(%d) = %d\n", i, fibArray[i]);
        }
    }
}
