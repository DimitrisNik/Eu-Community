package com.Rapidminer;

import com.Logs.OtherException;
import com.rapidminer.RapidMiner;
import com.rapidminer.operator.IOContainer;
import java.io.File;


public final class RapidminerProccess {

    private String filename = "";
    private File file = null;
    private com.rapidminer.Process process = null;
    private static boolean  isinit = false;



    
    

    public RapidminerProccess(String filename) throws OtherException {
        if (filename != null) {
            this.filename = filename;
            file = new File(this.filename);
            initRM();
            
        } else {
            throw new com.Logs.OtherException("rapidminer filename is null");
        }

    }

    /**
     * this method init Rapidminer
     */
    public void initRM() {
        if(!isinit){
            RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
            RapidMiner.init();
            isinit = true;
        }
    }

    /**
     * this method executes a rapiminer process
     */
    public IOContainer runproccess() throws Exception {
        process = new com.rapidminer.Process(file);
        return process.run();

    }

}
