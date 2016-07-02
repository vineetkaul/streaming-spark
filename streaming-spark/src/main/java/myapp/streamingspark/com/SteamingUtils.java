package myapp.streamingspark.com;

import com.exacttarget.fuelsdk.*;

import java.util.*;

import myapp.streamingsparknlp.com.WhatToThink;

import myapp.streaminsparksfapi.com.RESTConnection;
import myapp.streaminsparksfapi.com.RESTWhatToDo;

public class SteamingUtils {

	public void streamloader(ArrayList<ArrayList<String>> streamtext)
	{
		try 
		   {
				// loop through the RDD array
				for (int i = 0; i < streamtext.size(); i++) 
				{
				
					//*******************************************************************************
					// Step -1 - Create a lead.	
					//******************************************************************************* 
					WhatToThink.init();
		            System.out.println(" Sentiment Score : " + WhatToThink.findSentiment(streamtext.get(i).get(1)));
		            switch(WhatToThink.findSentiment(streamtext.get(i).get(1).toString())){
			            case 1 :
			            	streamtext.get(i).add(4, "1");
			               break; //optional
			            case 2 :
			            	streamtext.get(i).add(4, "2");
			               break; //optional
			            //You can have any number of case statements.
			            case 3 :
			            	streamtext.get(i).add(4, "3");
			                break; //optional
		            }
					RESTConnection.CreateRestConnection();
					RESTWhatToDo.createLeads(streamtext.get(i));
					
					
					//*******************************************************************************
					// Step 1 - Develop a triggered email + add subscriber to list.	
					//******************************************************************************* 
					// instantiate ETClient object
			   		ETClient client = new ETClient("src/main/resources/fuelsdk.properties"); 
					ETTriggeredEmail trigEmail = new ETTriggeredEmail();
					trigEmail.setClient(client);
					ETSubscriber trigSubs = new ETSubscriber();
					ETResponse<ETTriggeredEmail> eresponse = new ETResponse<ETTriggeredEmail>();
					
					trigSubs.setClient(client);
					trigSubs.setEmailAddress("kaul.vineet@gmail.com");
					trigSubs.setKey(streamtext.get(i).get(0));
					
					System.out.println("Keys for subscriber --- " + trigSubs.getKey());
					if (streamtext.get(i).get(4) == "3" || streamtext.get(i).get(4) == "2"){
						trigEmail.setKey("3165");
					}
					else
					{
						trigEmail.setKey("3164");
					}
					eresponse =  trigEmail.send(trigSubs);
					System.out.println("***" + eresponse.getResponseMessage() + "- -" + eresponse.getRequestId());
					//*******************************************************************************
					
					/**
					//*******************************************************************************
					// Step 2 - Add a row to the data extension for the subscriber added.
					//*******************************************************************************
					ETDataExtension dataExtension = new ETDataExtension();
				    dataExtension.setClient(client);
					ETResponse<ETDataExtensionRow> response = new ETResponse<ETDataExtensionRow>();
					ETDataExtensionRow row = new ETDataExtensionRow();
					row.setDataExtensionKey("0C7EDE82-F275-4F97-ABE8-BB38DC196DCD");
					row.setColumn("Social Name", streamtext.get(i));
					row.setColumn("Social Email Id", "vineet.kaul@inforte.com");
					System.out.println("*** Inserting Rows");
					response = dataExtension.insert(row);
					//*******************************************************************************
					**/
					
				}
		   } 
		   /**
		   catch (ETSdkException e) 
		   {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
			   System.out.println("*** ETSdkException");
		   }
		   **/
		   catch (Exception exp)
		   {
			   exp.printStackTrace();
			   System.out.println("*** Exception");
		   }
			
		}
		
	}
