package com.cattleDB.FrontEndService.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LogStreamerService {
    private final LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

    public LogStreamerService() {
        Executors.newSingleThreadExecutor().submit(this::readLastLines);
    }


//    public String readFile() throws FileNotFoundException {
//        File logFile = new File("C:\\Users\\hikev\\Desktop\\cattleDB\\services\\smartspectest\\smartspec-service\\smartspec\\data\\output\\data_log.txt");
//        BufferedReader reader = new BufferedReader(new FileReader(logFile));
//        return  ;
//    }

    private void readLastLines() {
        try {
            File logFile = new File("C:\\Users\\hikev\\Desktop\\cattleDB\\services\\smartspectest\\smartspec-service\\smartspec\\data\\output\\data_log.txt");

            // Read the last 50 lines
            Deque<String> lastLines = new ArrayDeque<>(50);
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (lastLines.size() == 50) {
                        lastLines.pollFirst(); // Remove the oldest line
                    }
                    lastLines.addLast(line); // Add the new line
                }
            }

            // Add the last 50 lines to the log queue
            for (String line : lastLines) {
                logQueue.offer(line);
            }

            // Keep listening for new lines (simulate streaming)
            watchForNewLines(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchForNewLines(File logFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            reader.skip(logFile.length()); // Skip to the end of the file

            String line;
            while (true) {
                line = reader.readLine();
                if (line != null) {
                    logQueue.offer(line);
                } else {
                    Thread.sleep(100); // Polling delay to wait for new lines
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNextLogLine() {
        try {
            return logQueue.take(); // Waits for a new line if the queue is empty
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
