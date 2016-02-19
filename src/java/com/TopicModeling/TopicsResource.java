package com.TopicModeling;

import com.Logs.LogFile;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Path("topics")
@Provider
//public class TopicsResource  implements ExceptionMapper<Throwable>{
public class TopicsResource {

    @Context
    private UriInfo context;

    public TopicsResource() {

    }
    


    @POST
    @Produces("application/xml")
    public String getXml(@FormParam("xmlinput") String xmlinput) throws Exception {
//        LogManager.getLogManager().reset();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Task(xmlinput));


        try {
            future.get(3000, TimeUnit.SECONDS);
            executor.shutdownNow();
            return future.get();
        } catch (Exception e) {
            LogFile logs = new LogFile();
            logs.writelogs(e, xmlinput, "Similarity");
            executor.shutdownNow();
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<similarityOutput>null</similarityOutput>";
        }

    }

}
