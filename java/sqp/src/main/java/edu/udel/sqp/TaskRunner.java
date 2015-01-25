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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Functionality of the threads in the thread pool. Each instance contains a shared queue.
 * Also, it implements the naive task-stealing functionality.
 * 
 * Based on code presented in "Patterns for Parallel programming by Mattson et al"
 */
public class TaskRunner extends Thread {
    
    // Group that manages this TaskRunner
    private final TaskRunnerGroup g;
    // thread random number generator
    private final Random chooseToStealFrom;
    // poison task
    private final Task poison;
    // whether this TaskRunner is active or not. (Volatile since we don't want the value to be cached in the thread stack)
    protected volatile boolean active;
    // TaskRunner id, (index in the TaskRunnerGroup)
    private final int id;
    // non-blocking share queue
    private final SharedQueue q;
    
    // operations related to queue
    public void put (Task task) {
        q.put(task);
    }
    
    public Task take () {
        return q.take();
    }
    
    public Task takeLast() {
        return q.takeLast();
    }
    
    public TaskRunner (TaskRunnerGroup g, int id, Task poison) {
        this.g = g;
        this.id = id;
        this.poison = poison;
        chooseToStealFrom = ThreadLocalRandom.current();
        setDaemon(true);
        
        q = new SharedQueue();
    }
    
    protected final TaskRunnerGroup getTaskRunnerGroup() {
        return g;
    }
    
    protected final int getID () {
        return id;
    }
    
    /**
     * Tries to steal a task from the queue of another thread. First chooses a random victim,
     * then continues with the other threads until either a task has been found or all have
     * been checked. If a task is found then it is invoked. 
     * 
     * @param waitingFor a task on which this thread is waiting for a join. If stead is not called
     * as part of a join, the use waitingFor = null
     */
    public void steal (final Task waitingFor) {
        Task task = null;
        
        TaskRunner[] runners = g.getRunners();
        int victim = chooseToStealFrom.nextInt(runners.length);
        for (int i = 0; i != runners.length; ++i) {
            TaskRunner tr = runners[victim];
            if (waitingFor != null &&
                    waitingFor.isDone()) {
                break;
            } else {
                if (tr != null &&
                        tr != this)
                    task = (Task) tr.q.take();
                
                if (task != null)
                    break;
                
                yield();
                victim = (victim + 1) % runners.length;
                
            }
        } // have either found a task or have checked all other queues
        
        // if have a task, invoke it
        if (task != null && ! task.isDone()) {
            task.invoke();
        }
    }
    
    /**
     * Main loop of the thread. First attempts to find a task on local queue and execute it.
     * If not found, then tries to steal a task from another thread. 
     * The thread terminates when it retrieves the poison task from the queue.
     */
    public void run () {
        Task task = null;
        try {
            while (!poison.equals(task)) {
                task = q.takeLast();
                if (task != null) {
                    if (!task.isDone()) {
                        task.invoke();
                    }
                } else {
                    steal(null);
                }
            }
        } finally {
            active = false;
        }
    }
    
    /**
     * Looks for another task to run and continues when Task w is done.
     */
    protected final void taskJoin (final Task w) {
        while (!w.isDone()) {
            Task task = q.takeLast();
            if (task != null) {
                if (!task.isDone()) {
                    task.invoke();
                }
            } else {
                steal(w);
            }
        }
    }
}
