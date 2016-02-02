package com.na.api.test.Storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import com.na.api.Workloads.TablePerformanceWorkload;
import com.na.api.util.ConnectionService;
import com.na.api.util.ResponseParser;

/*
 * Scenario : Monitor-Storage-Table Performance
 * 1. Run a simple workload with create, insert, update, select statements.
 * 2. Verify these metrics in UI - ROWS_INSERTED,ROWS_DELETED,ROWS_UPDATED,TABLE_SCANS,ROWS_READ
 * 
 */
public class TablePerformanceTest {

	private static final Logger logger = Logger.getLogger(TablePerformanceTest.class);
	
	JSONObject workloadResult;
	String tableName ="TBLPER1";
	
	@BeforeClass
	public void setUp(){
		System.out.println("Starting test workload");
		try {
			workloadResult =TablePerformanceWorkload.runWorkload(tableName);
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Test Workload failed to run");
			
		}
		System.out.println("Test workload completed");
	}
	
	@Test
	public void testTableperformanceMetrics() throws FileNotFoundException, IOException, InterruptedException{
		
		logger.info("Running Table Performance Card test...");
		Thread.sleep(45000);//Sleep 45 seconds
		ConnectionService cs = new ConnectionService();
		JSONObject resObj =cs.callConnectionsDataService(cs.getJSONData("get_table_perf_metrics"));
		JSONObject responseData =(JSONObject) resObj.get("ResponseData");
		JSONObject tableDetails = ResponseParser.getTableDetails(responseData, tableName);
		
		logger.info(tableDetails);
		
		Assert.assertEquals(tableDetails.get("ROWS_INSERTED"),workloadResult.get("ROWS_INSERTED"), "Failed: ROWS_INSERTED data mismatch");
		Assert.assertEquals(tableDetails.get("ROWS_DELETED"),workloadResult.get("ROWS_DELETED"), "Failed: ROWS_DELETED data mismatch");
		Assert.assertEquals(tableDetails.get("ROWS_UPDATED"),workloadResult.get("ROWS_UPDATED"), "Failed: ROWS_UPDATED data mismatch");
		Assert.assertEquals(tableDetails.get("TABLE_SCANS"),workloadResult.get("TABLE_SCANS"), "Failed: TABLE_SCANS data mismatch");
		Assert.assertEquals(tableDetails.get("ROWS_READ"),workloadResult.get("ROWS_READ"), "Failed: ROWS_READ data mismatch");
		
		logger.info("PASSED: Table performance metrics test was successful");
		
	}
	
	

}
