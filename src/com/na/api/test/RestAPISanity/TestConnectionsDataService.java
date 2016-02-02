package com.na.api.test.RestAPISanity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.na.api.util.ConnectionService;
import com.na.api.util.FileTools;
/*
 * Scenario - Sanity test all the calls to ConnectionsData service
 */
public class TestConnectionsDataService {
	
	private final Logger logger = Logger.getLogger(getClass());
	ConnectionService cs;

	@DataProvider(name="testdata")
	public Object[][] loadJSON() throws FileNotFoundException, IOException{
	
		cs = new ConnectionService();
		Properties p = cs.getJSONProperties();
		return FileTools.readProperties(p);
	
	}
	
	@Test(dataProvider = "testdata")
	public void testConnectionService(Object key ,Object jsonObj){
				
		JSONObject result =cs.callConnectionsDataService(jsonObj.toString());
		int responseCode = (Integer)result.get("ResponseCode");
		if(responseCode!=200)
			Assert.fail("FAILURE=> Response code ="+responseCode+" for URL: "+result.get("URL")+", POSTDATA:"+result.get("PostData"));
		else{ // Check for exception in the response
			JSONObject responseData = (JSONObject) result.get("ResponseData");
			
			boolean exceptionCaught = responseData.toString().contains("\"exceptionCaught\":true");//validateResponseData(responseData);
			if(exceptionCaught)
				Assert.fail("FAILURE:=>ExceptionCaught=true in responseJson for URL: "+result.get("URL")+", POSTDATA:"+result.get("PostData")+" with RESPONSECODE:"+responseCode+" and RESPONSEDATA:"+responseData);
			
			logger.info("SUCCESS=> URL: "+result.get("URL")+", POSTDATA:"+result.get("PostData"));
			
		}
	
	}
	
	// This method is not being used anymore - Will leave it as such for now.  
	
	public boolean validateResponseData(JSONObject responseData){
		
		boolean result = false;
		if(responseData.toString().contains("exceptionCaught")){
			result = (Boolean) responseData.getJSONArray("data").getJSONArray(0).getJSONObject(1).get("exceptionCaught");
			
		}
		return result;
	
	}
	

}
