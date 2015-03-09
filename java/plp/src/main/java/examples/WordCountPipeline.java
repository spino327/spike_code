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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.udel.plp.LinearPipeline;
import edu.udel.plp.PipelineStage;


/**
 * Wordcount mapreduce like pipeline
 * 
 */
public class WordCountPipeline extends LinearPipeline {

    static final KV DONE = new KV(null, null);
    
    public WordCountPipeline (File inputFile, File outFile) {
        super(inputFile, outFile);
    }

    private boolean checkArgs (Object... args) {
        if (args.length != 2 
                || (args[0] != null && !(args[0] instanceof File))
                || (args[0] != null && !(args[1] instanceof File))) {
            return false;
        }
        return true;
    }
    
    @Override
    public PipelineStage[] getPipelineStages(Object... args) {
        if (!checkArgs(args)) {
            System.err.println("Bad input arguments");
            return new PipelineStage[0];
        }
        
        PipelineStage[] array = new PipelineStage[3];
        
        array[0] = new ReadStage((File) args[0]);
        array[1] = new SplitStage();
        array[2] = new CountStage((File) args[1]);
        
        return array;
    }

    @Override
    public BlockingQueue[] getQueues(Object... args) {
        if (!checkArgs(args)) {
            System.err.println("Bad input arguments");
            return new BlockingQueue[0];
        }
        
        BlockingQueue[] array = new BlockingQueue[this.numStages - 1];
        
        for (int i = 0; i < array.length; i++)
            array[i] = new LinkedBlockingQueue<KV>();
        
        return array;
    }
    
    public static void main (String[] args) throws InterruptedException {
        
        if (args.length != 2) {
            System.err.println("USAGE: ./app <input file> <output file>");
            System.exit(-1);
        }
        
        File input = new File(args[0]);
        File output = new File(args[1]);
        
        LinearPipeline pipeline = new WordCountPipeline(input, output);
        pipeline.start();
        pipeline.cdl.await();
        
        System.out.println("All threads terminated!");
    }
    
    static class KV {
        public Object key;
        public Object val;
        
        public KV(Object k, Object v) {
            this.key = k;
            this.val = v;
        }
    }
    
    static class ReadStage extends PipelineStage<Object, KV> {

        private File inputFile;
        private BufferedReader bReader;
        
        public ReadStage(File file) {
            inputFile = file;
        }
        
        @Override
        public void firstStep() throws Exception {
            System.out.println("Initializing ReadInput");
            bReader = new BufferedReader(new FileReader(inputFile)); 
        }

        @Override
        public void step() throws Exception {
            String line = bReader.readLine();
            if (line != null)
                out.put(new KV(null, line));
            else
                this.done = true;
        }

        @Override
        public void lastStep() throws Exception {
            out.put(DONE);
            bReader.close();
            System.out.println("Finish ReadInput");
        }
        
    }
    
    static class SplitStage extends PipelineStage <KV, KV> {

        @Override
        public void firstStep() throws Exception {
            System.out.println("Initializing split");
        }

        @Override
        public void step() throws Exception {
            
            KV input = in.take();
            if (input == DONE) {
                this.done = true;
                return;
            }
            
            String[] split = ((String) input.val).split("\\s");
            for (String word : split) {
                out.put(new KV(word, 1));
            }
        }

        @Override
        public void lastStep() throws Exception {
            out.put(DONE);
            System.out.println("Finalizing split");
        }
        
    }
    
    static class CountStage extends PipelineStage <KV, KV> {

        private File outFile;
        private PrintWriter pWriter;
        private Map<String, Integer> words;
        
        public CountStage(File file) {
            outFile = file;
            words = new HashMap<>();
        }
        
        @Override
        public void firstStep() throws Exception {
            System.out.println("Initializing Count");
            pWriter = new PrintWriter(new FileWriter(outFile));
        }

        @Override
        public void step() throws Exception {
            
            KV input = in.take();
            if (input == DONE) {
                this.done = true;
                return;
            }

            String word = (String) input.key;
            int in_count = (Integer) input.val;
            
            Integer count = words.get(word);
            count = count != null ? count + in_count : in_count;
            words.put(word, count);
        }

        @Override
        public void lastStep() throws Exception {
            for (Map.Entry<String, Integer> entry : words.entrySet()) {
                pWriter.println(entry.getKey() + " : " + entry.getValue());
            }
            pWriter.close();
            System.out.println("Finalizing Count");
        }
    }
}
