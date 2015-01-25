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
 * Nonblocking shared queue of tasks.
 * 
 * Based on code presented in "Patterns for Parallel programming by Mattson et al"
 */
public class SharedQueue {

    class Node {
        // task that is wrapped by the node
        Task task;
        // next task in the queue
        Node next;
        // previous task in the queue
        Node prev;
        
        /**
         * Creates a new Node with task and previous node.
         * @param task
         * @param prev
         */
        public Node (Task task, Node prev) {
            this.task = task;
            this.prev = prev;
            next = null;
        }
    }
    
    // the head is a null task
    private Node head = new Node (null, null);
    private Node last = head;
    
    /**
     * Offers a new task to the queue
     * @param task
     */
    public synchronized void put (Task task) {
        if (task == null)
            throw new RuntimeException("Cannot insert null task");
        
        Node p = new Node (task, last);
        last.next = p;
        last = p;
    }
    
    /**
     * Returns the first task in the queue or null if the queue is empty
     * @return a Task object
     */
    public synchronized Task take () {
        Task task = null;
        if (!isEmpty()) {
            Node first = head.next;
            task = first.task;
            first.task = null;
            head = first;
        }
        
        return task;
    }
    
    /**
     * Returns the last task in the queue or null if the queue is empty
     * @return Task
     */
    public synchronized Task takeLast () {
        Task task = null;
        if (!isEmpty()) {
            task = last.task;
            last = last.prev;
            last.next = null;
        }
        
        return task;
    }
    
    /**
     * Check is the queue is empty of Tasks
     * @return boolean
     */
    private boolean isEmpty() {
        return head.next == null;
    }
}
