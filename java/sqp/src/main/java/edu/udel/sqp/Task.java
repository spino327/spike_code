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
 * Abstract: A subclass override the run method to implement the functionality of the task in the computation.
 * Includes the fork and join methods.
 * 
 * Based on code presented in "Patterns for Parallel programming by Mattson et al"
 */
public abstract class Task implements Runnable {
    
    // indicates whether the task is finished
    // we don't want this instance variable to be cached by the threads
    private volatile boolean done;

    public final void setDone () {
        done = true;
    }
    
    public boolean isDone () {
        return done;
    }
    
    /**
     * Returns the TaskRunner (thread in the thread pool) that is executing this task.
     * @return current executing TaskRunner thread
     */
    public static TaskRunner getTaskRunner () {
        return (TaskRunner) Thread.currentThread();
    }
    
    /**
     * Offer this task to the local queue of the current thread
     */
    public void fork () {
        getTaskRunner().put(this);
    }
    
    /**
     * Wait until this task is done
     */
    public void join () {
        getTaskRunner().taskJoin(this);
    }
    
    /**
     * Executes the run method of this task
     */
    public void invoke() {
        if (!isDone()) {
            run();
            setDone();
        }
    }
    
    /**
     * Actual workload of this task
     */
    public abstract void run();
}
