package com.na.api.test.DatabaseConnection;


import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.na.api.util.Configuration;
import com.na.api.util.DatabaseService;

public class DBConnectionTest {
	
	private final Logger logger = Logger.getLogger(getClass());
	String simpleDBConnectionName; //to save the connection name assigned by OTS- so it can be used for delete connection test

	Properties p ;
	String testdbFile;
	
	@BeforeClass
	  public void beforeClass() {
		
		testdbFile = Configuration.getDatabaseDirPath()+Configuration.getProperty("TestDatabase");
		p = Configuration.openPropertyFile(testdbFile);
	}
	
	
	@Test
	public void testAddConnectionSimple() throws Exception{
		
		JSONObject result = DatabaseService.addDatabaseConnection(p.getProperty("databaseName"), p.getProperty("host"), Integer.parseInt(p.getProperty("port")), p.getProperty("user"), p.getProperty("password"));
		int responseCode = (Integer) result.get("ResponseCode");
		JSONObject responseData = (JSONObject) result.get("ResponseData");
		JSONArray data = (JSONArray) responseData.get("data");
		JSONObject addRes = (JSONObject) data.get(0);
		
		simpleDBConnectionName= addRes.getString("name");
		
		if (responseCode==200)
			logger.info("INFO: Database Connection added successfully.Assigned connection name is :"+simpleDBConnectionName);
		else 
			Assert.fail("ERROR: Add Database Connection failed with response code:"+responseCode+" and error :"+addRes.get("exceptionMsg"));
		
			
	}
	
	
	@Test (dependsOnMethods = {"testAddConnectionSimple"})
	public void testDeleteConnection() throws Exception{
		
		logger.info("Deleting connection name :"+simpleDBConnectionName);
		Thread.sleep(30000);//Sleep 30 seconds to avoid deadlock issues
		boolean result = DatabaseService.deleteDatabaseConnection(simpleDBConnectionName);
		Assert.assertTrue(result, "Delete Connection Failed");
		
	}
	
	@Test (dependsOnMethods = {"testDeleteConnection"})
	public void testAddConnectionAdvanced() throws Exception{
		
		String testdbFile = Configuration.getDatabaseDirPath()+Configuration.getProperty("TestDatabase");
		
		//Checking if connName already exists- If ues delete it
		boolean connExists =DatabaseService.checkConnectionExists(p.getProperty("name"));
		if(connExists){
			DatabaseService.deleteDatabaseConnection(p.getProperty("name"));
		}
			
		
		boolean result = DatabaseService.addDatabaseConnection(testdbFile);
		Assert.assertTrue(result, "Add Connection in Advanced Mode Failed");
		
	}
	
	@Test (dependsOnMethods = {"testAddConnectionAdvanced"})
	public void testDeleteConnectionAdvanced() throws Exception{
		
		boolean result = DatabaseService.deleteDatabaseConnection(p.getProperty("name"));
		Assert.assertTrue(result, "Delete Connection Failed");
			
	}
	
	
	@DataProvider(name="invalidConnData")
	public Object[][] loadData() {
	
		Object[] testdata1 = {"junkxxxx", p.getProperty("host"), Integer.parseInt(p.getProperty("port")), p.getProperty("user"), p.getProperty("password")};
		Object[] testdata2 = {p.getProperty("databaseName"), "somedummyhostname", Integer.parseInt(p.getProperty("port")), p.getProperty("user"), p.getProperty("password")};
		Object[] testdata3 = {p.getProperty("databaseName"), p.getProperty("host"), 12340, p.getProperty("user"), p.getProperty("password")};
		Object[] testdata4 = {p.getProperty("databaseName"), p.getProperty("host"), Integer.parseInt(p.getProperty("port")), "dummyuser", p.getProperty("password")};
		Object[] testdata5 = {p.getProperty("databaseName"), p.getProperty("host"), Integer.parseInt(p.getProperty("port")), p.getProperty("user"), "dummypassword"};
		
		Object[][] testdata = {testdata1,testdata2,testdata3,testdata4,testdata5};
		return testdata;
	
	}
	
	@Test(dataProvider = "invalidConnData",dependsOnMethods = {"testDeleteConnectionAdvanced"})
	public void testInvalidConnection(String dbName,String host,int port,String user,String pwd){
		JSONObject result = DatabaseService.addDatabaseConnection(dbName, host, port,user, pwd);
		JSONObject responseData = (JSONObject) result.get("ResponseData");
		System.out.println("RESPONSE DATA:"+responseData);
		if(responseData.toString().contains("com.jcc.am.DisconnectNonTransientConnectionException") ||
				responseData.toString().contains("Credentials Invalid:Could not make connection") || 
				responseData.toString().contains("unknown host"))
			logger.info("");
		else
			Assert.fail("ERROR: Expected error message not found in response file");
	}
	
	
	
}
