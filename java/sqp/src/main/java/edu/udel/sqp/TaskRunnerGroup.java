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

package edu.udel.sqp;

/**
 * Handles the TaskRunners (as a thread pool).
 * 
 * Based on code presented in "Patterns for Parallel programming by Mattson et al"
 */
public class TaskRunnerGroup {

    protected final TaskRunner[] threads;
    protected final int groupSize;
    protected final Task poison;
    
    public TaskRunnerGroup(int groupSize) {
        this.groupSize = groupSize;
        threads = new TaskRunner[groupSize];
        poison = new Task() {
            
            @Override
            public void run() {
                assert false;
            }
        };
        poison.setDone();
        
        for (int i = 0; i < groupSize; i++) {
            threads[i] = new TaskRunner(this, i, poison);
        }
        
        for (int i = 0; i != groupSize; i++) {
            threads[i].start();
        }
    }
    
    /**
     * Start executing task t and wait for its completion. The wrapper task is used
     * in order to start t from within a task (thus allowing fork and join to be used).
     */
    public void executeAndWait(final Task t) {
        final TaskRunnerGroup thisGroup = this;
        Task wrapper = new Task() {
            
            @Override
            public void run() {
                t.fork();
                t.join();
                setDone();
                synchronized (thisGroup) {
                    thisGroup.notifyAll(); // notify waiting thread
                }
            }
        };
        
        // add wrapped task to queue of thread[0]
        threads[0].put(wrapper);
        // wait for notification that t has finished.
        synchronized (thisGroup) {
            try {
                thisGroup.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
    
    /**
     * cause all threads to terminate. The programmer is responsible for
     * ensuring that the computation is complete.
     */
    public void cancel () {
        for (int i = 0; i != groupSize; i++) {
            threads[i].put(poison);
        }
    }
    
    public TaskRunner[] getRunners() {
        return threads;
    }

}
