package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * Created by davchen on 5/22/15.
 */

public class Client {
	private static CacheServiceInterface server1 = null;
	private static CacheServiceInterface server2 = null;
	private static CacheServiceInterface server3 = null;
	
    public static void main(String[] args) {
    	try {
    		System.out.println("Begin initialize all server nodes....");
    		
            server1 = new DistributedCacheService("http://localhost:3000");
            server2 = new DistributedCacheService("http://localhost:3001");
            server3 = new DistributedCacheService("http://localhost:3002");
            
	    	if (args.length > 0) {
	    		if (args[0].equals("write")) {
	    			write();
	    		} else if (args[0].equals("read")) {
	    			CRDTClient.readOnRepair(server1, server2, server3);
	    		}
	    	}
	    	
	    	System.out.println("Exiting...\n");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}        
    }
    
    public static void write() throws Exception {       
        long key = 1;
        String value = "a";
        
        Future<HttpResponse<JsonNode>> server3000 = server1.put(key, value);
        Future<HttpResponse<JsonNode>> server3001 = server2.put(key, value);
        Future<HttpResponse<JsonNode>> server3002 = server3.put(key, value);
        
        final CountDownLatch countDown = new CountDownLatch(3);
        
        try {
        	server3000.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server3001.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	server3002.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }

        countDown.await();
        
        if (DistributedCacheService.successCount.intValue() < 2) {	        	

            server1.delete(key);
        	server2.delete(key);
        	server3.delete(key);

        } else {

            server1.get(key);
        	server2.get(key);
        	server3.get(key);

            Thread.sleep(1000);

            System.out.println("Server A value is: " + server1.getValue());
    	    System.out.println("Server B value is: " + server2.getValue());
    	    System.out.println("Server C value is: " + server3.getValue());
        }
        DistributedCacheService.successCount = new AtomicInteger();
    }
}
