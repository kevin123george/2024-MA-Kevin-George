package com.cattleDB.FrontEndService.controller;

import com.cattleDB.FrontEndService.service.LogStreamerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Controller
public class LogStreamingController {

    @Autowired
    private LogStreamerService logStreamerService;

    // Serve the Thymeleaf template for the log streaming page
    @GetMapping("/log-stream")
    public String logStreamPage() throws FileNotFoundException {
//        logStreamerService.readFile();
        return "log"; // The Thymeleaf template we created above
    }

    // SSE endpoint to stream logs
    @GetMapping(value = "/log-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        SseEmitter emitter = new SseEmitter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        final boolean[] isCompleted = {false}; // Track the completion state of the emitter

        // Thread-safe queue to maintain the last 50 log lines
        LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>(50);

        executorService.execute(() -> {
            try {
                while (!isCompleted[0]) {
                    String logLine = logStreamerService.getNextLogLine();

                    // Add the log line to the queue
                    if (!logQueue.offer(logLine)) {
                        logQueue.poll(); // Remove the oldest line if the queue is full
                        logQueue.offer(logLine); // Add the new line
                    }

                    // Send the contents of the queue to the client
                    StringBuilder logsToSend = new StringBuilder();
                    for (String log : logQueue) {
                        logsToSend.append(log).append("\n");
                    }
                    emitter.send(logsToSend.toString().trim());

                    Thread.sleep(1000); // Optional: Adjust the delay for log streaming frequency
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
                isCompleted[0] = true; // Mark the emitter as completed in case of error
            } finally {
                if (!isCompleted[0]) {
                    emitter.complete(); // Complete the emitter if no error occurred
                }
                executorService.shutdown(); // Shutdown the executor service
            }
        });

        return emitter;
    }

}
