package com.na.api.test.EnvSetup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.na.api.util.Configuration;
import com.na.api.util.DatabaseService;

public class SetupTestDatabase {

	Properties p ;
	String testdbFile;
	
	@BeforeClass
	  public void beforeClass() {
		testdbFile = Configuration.getDatabaseDirPath()+Configuration.getProperty("MonitoredDatabase");
		p = Configuration.openPropertyFile(testdbFile);
	}
	
	@Test 
	public void testAddConnectionAdvanced() throws Exception{
		boolean connExists =DatabaseService.checkConnectionExists(p.getProperty("name"));
		if(connExists){
			DatabaseService.deleteDatabaseConnection(p.getProperty("name"));
		}
		boolean result = DatabaseService.addDatabaseConnection(testdbFile);
		Thread.sleep(60000); // Give some time for monitoring to start
		Assert.assertTrue(result, "Add Connection in Advanced Mode Failed");
		
	}
	
	//@Test (dependsOnMethods = {"testAddConnectionAdvanced"})
	public void enableMonitoring() throws FileNotFoundException, IOException, InterruptedException{
		while(!DatabaseService.isMonitoringEnabled())
		{
			Thread.sleep(15000);
		}
		Assert.assertTrue(DatabaseService.isMonitoringEnabled());
	}
	
}
