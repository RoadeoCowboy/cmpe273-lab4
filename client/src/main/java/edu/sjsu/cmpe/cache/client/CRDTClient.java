package edu.sjsu.cmpe.cache.client;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davchen on 5/21/15.
 */

public class CRDTClient {
    public static void readOnRepair(CacheServiceInterface value1, CacheServiceInterface value2,
    		CacheServiceInterface value3) throws Exception {
    	CacheServiceInterface server1  = value1;
    	CacheServiceInterface server2  = value2;
    	CacheServiceInterface server3  = value3;
    	
        long key = 1;
        String value = "a";
        
        server1.put(key, value);
        server2.put(key, value);
        server3.put(key, value);
        
        System.out.println("Initial value is: a");
        Thread.sleep(30000);
        
        server1.get(1);
	    server2.get(1);
	    server3.get(1);
	        
	    System.out.println("Read value is: a");
	    Thread.sleep(1000);
	    
	    System.out.println("Server 1 value: " + server1.getValue());
	    System.out.println("Server 2 value: " + server2.getValue());
	    System.out.println("Server 3 value: " + server3.getValue());
        
        value = "b";
        server1.put(key, value);
        server2.put(key, value);
        server3.put(key, value);
        
        System.out.println("Initializing value b...");
        Thread.sleep(30000);
	        
	    server1.get(1);
	    server2.get(1);
	    server3.get(1);
	        
	    System.out.println("Reading value b...");
	    Thread.sleep(1000);
	    
	    System.out.println("Server 1 value: " + server1.getValue());
	    System.out.println("Server 2 value: " + server2.getValue());
	    System.out.println("Server 3 value: " + server3.getValue());
	        
	    String[] values = {server1.getValue(), server2.getValue(), server3.getValue()};
	    
	    Map<String, Integer> map = new HashMap<String, Integer>();
	    String majority = null;
	    for (String thisValue : values) {
	        Integer countValue = map.get(thisValue);
	        map.put(thisValue, countValue != null ? countValue + 1 : 1);
	        if (map.get(thisValue) > values.length / 2) {
	        	majority = thisValue;
	        	break;
	        }	
	    }
	    
	    server1.put(key, majority);
        server2.put(key, majority);
        server3.put(key, majority);
        
        System.out.println("Read repair...\n");
	    Thread.sleep(1000);
	    
	    server1.get(key);
        server2.get(key);
        server3.get(key);
        
        System.out.println("b after repair...\n ");
	    Thread.sleep(1000);
	    
	    System.out.println("Server 1: " + server1.getValue());
	    System.out.println("Server 2: " + server2.getValue());
	    System.out.println("Server 3: " + server3.getValue());
    }
}
