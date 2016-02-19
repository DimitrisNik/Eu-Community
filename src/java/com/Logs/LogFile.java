package com.Logs;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {

    private final String path = "C:\\Test\\Logs\\";

    /**
     * this method return the time of system
     */
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SS");
        String time = sdf.format(new Date());
        return time;
    }

    /**
     * this method write logs
     */
    public void writelogs(Exception e, String input, String process) throws IOException {
        String time = getCurrentTime();

        PrintWriter log = new PrintWriter(path + "log_" + process + "_" + time + ".txt");
        PrintWriter in = new PrintWriter(path + "input_" + process + "_" + time + ".txt");

        if (input != null) {
            in.println(input);
        }

        log.println(e.getMessage());
        e.printStackTrace(log);

        log.close();
        in.close();
    }

    /**
     * this method write logs
     */
    public void writelogs(String message, String input, String process) throws IOException {
        String time = getCurrentTime();
        PrintWriter log = new PrintWriter("logs//" + "log_" + process + time + ".txt");
        PrintWriter in = new PrintWriter("logs//" + "input_" + process + time + ".txt");

        if (input != null) {
            in.println(input);
        }

        if (message != null) {
            log.println(message);
        }

        log.close();
        in.close();
    }
}
