/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Similarity;

import com.Logs.LogFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("Similarity")
public class SimilarityResource {

    @Context
    private UriInfo context;

    public SimilarityResource() {
    }

    @POST
    @Produces("application/xml")
//     @Produces("text/plain")
    public String getXml(@FormParam("xmlinput") String xmlinput) throws Exception {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Task(xmlinput));
        try {
            future.get(30, TimeUnit.SECONDS);
            executor.shutdownNow();
            return future.get();
        } catch (Exception e) {
            LogFile logs = new LogFile();
            logs.writelogs(e, xmlinput, "Topics");
            executor.shutdownNow();
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<modelingOutput>null</modelingOutput>";
        }

    }
}
