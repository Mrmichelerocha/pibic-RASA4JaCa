/*
	Original file: https://github.com/jacamo-lang/jacamo-rest/blob/master/src/main/java/jacamo/rest/RestImpl.java
	Changed by: Débora Engelmann
	May 3, 2020
*/

package br.pucrs.smart.Dial4JaCa;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import com.google.gson.Gson;

import br.pucrs.smart.Dial4JaCa.interfaces.IAgent;
import br.pucrs.smart.Dial4JaCa.models.RequestDialogflow;
import br.pucrs.smart.Dial4JaCa.models.RequestRasa;
import br.pucrs.smart.Dial4JaCa.models.ResponseDialogflow;
import br.pucrs.smart.Dial4JaCa.models.ResponseRasa;
import br.pucrs.smart.Dial4JaCa.models.Tracker;

@Singleton
@Path("/")
public class RestImpl extends AbstractBinder {
	 static IAgent mas = null;
	 private Gson gson = new Gson();
	 
	 public static void setListener(IAgent agent) {
		 mas = agent;
	 }
	 
	@Override
	protected void configure() {
		bind(new RestImpl()).to(RestImpl.class);
	}
	
	@Path("/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveRequest(String request) {
        try {
        	RequestDialogflow requestDialogflow = gson.fromJson(request, RequestDialogflow.class);
        	System.out.println("[Dial4JaCa] Agent communicated: " +  gson.toJson(requestDialogflow)); 
        	if (mas != null) {
        		ResponseDialogflow responseDialogflow = mas.intentionProcessing(requestDialogflow.getResponseId(),
        																	  requestDialogflow.getQueryResult().getIntent().getDisplayName(),
        																	  requestDialogflow.getQueryResult().getParameters(),
        																	  requestDialogflow.getQueryResult().getOutputContexts(),
        																	  requestDialogflow.getSession());
        		return Response.ok(gson.toJson(responseDialogflow)).build();
        	} else {
        		ResponseDialogflow responseDialogflow = new ResponseDialogflow();   
            	responseDialogflow.setFulfillmentText("Sorry, We couldn't find the Jason agent.");
            	return Response.ok(gson.toJson(responseDialogflow)).build();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(500).build();
    }

	@Path("/webhook")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response receiveRequestRasa(String request) {
		try {
			RequestRasa requestRasa = gson.fromJson(request, RequestRasa.class);
			System.out.println("[Rasa4JaCa] Agent communicated: " +  gson.toJson(requestRasa, RequestRasa.class));

			if (mas != null) {
				System.out.println(gson.toJson(requestRasa.getTracker(), Tracker.class));
				System.out.println(gson.toJson(requestRasa.getTracker().getSlots()));
				System.out.println(gson.toJson(requestRasa.getTracker().getSlots().get("intentName").toString()));


				System.out.println(requestRasa.getTracker().getSlots().get("intentName").toString());
				System.out.println(requestRasa.getTracker().getSlots().toString());
				System.out.println(requestRasa.getSenderID());

        		ResponseRasa responseRasa = mas.RasaIntentionProcessing(requestRasa.getTracker().getSlots().get("intentName").toString(),
																				requestRasa.getTracker().getSlots(),
																				requestRasa.getSenderID());
        		return Response.ok(gson.toJson(responseRasa)).build();
        	} else {
				ResponseRasa responseRasa = new ResponseRasa();
				responseRasa.setResponse("Sorry, We couldn't find the Jason agent.");
        		return Response.ok(gson.toJson(responseRasa)).build();
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(500).build();
	}

}
