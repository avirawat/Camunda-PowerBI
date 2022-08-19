package com.camunda.powerbi.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.camunda.powerbi.ConstantVariables;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.json.*;  
/**
 * @author AvinashRavat
 *
 */
@Service
public class GetTokenAccess {
	String CLIENT_ID = ConstantVariables.CLIENT_ID;
	String TENANT_ID=ConstantVariables.TENANT_ID;
	String DATASET_ID=ConstantVariables.DATASET_ID;
	String CLIENT_SECRET=ConstantVariables.CLIENT_SECRET;
	
	 private static Logger logger = LoggerFactory.getLogger(GetTokenAccess.class);
	
	@Autowired
	Environment env;
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	   return builder.build();
	}
	
	/**
	 * @author AvinashRavat
	 * @param Job Instances
	 * This function will take all inputs from job
	 */
	@ZeebeWorker(type = "getDataSetsTask", autoComplete = true)
	public void generateToken(final ActivatedJob job)
			throws IOException, InterruptedException {
		logger.error("Inside Service task");
		
		String jsonString = job.getVariables();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = mapper.readValue(jsonString, Map.class);
		String client_id=map.get(CLIENT_ID);
		String tenant_id=map.get(TENANT_ID);
		String dataset_id=map.get(DATASET_ID);
		String client_secret=map.get(CLIENT_SECRET);

		String url=env.getProperty("url")+tenant_id+"/oauth2/token";
		getAccessToken(url,client_id,dataset_id,client_secret);
		
	}
	
	/**
	 * @author AvinashRavat
	 * @param stringUrl
	 * @param clientId
	 * @param dataset_id
	 * @param client_secret
	 * This function will generate access token by using parameters
	 */
	public void getAccessToken(String stringUrl,String client_id,String dataset_id,String client_secret) throws IOException, InterruptedException {

		    logger.error("Generating access token ",stringUrl);

		    URL url = new URL(stringUrl);
	        String postData = "grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret+"&scope=Dataset.ReadWrite.All";
	 
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));
	 
	        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
	            dos.writeBytes(postData);
	        }
	 
	        try (BufferedReader bf = new BufferedReader(new InputStreamReader(
	                                                        conn.getInputStream())))
	        {
			
	        	JSONObject json = new JSONObject(bf.readLine());  
	        	logger.error(json.getString("access_token")); 
	        }
		
	}

}
