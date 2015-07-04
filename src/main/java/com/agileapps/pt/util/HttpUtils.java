package com.agileapps.pt.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.ResponseHandler;

import com.agileapps.pt.pojos.StatusAndResponse;

public class HttpUtils {

	public static StatusAndResponse getHttpResponse(String url) throws ClientProtocolException, IOException{
		HttpClient client= new DefaultHttpClient();
		HttpUriRequest request=new HttpGet(url);
		final StringBuilder stringBuilder=new StringBuilder();
		int responseCode=client.execute(request,new ResponseHandler<Integer>(){
			public  Integer handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity=response.getEntity();
				Scanner scanner= new Scanner(entity.getContent());
				while ( scanner.hasNextLine()){
					stringBuilder.append(scanner.nextLine().trim()).append("\n");;
				}
				return response.getStatusLine().getStatusCode();
			}
		}
		);
		return (new StatusAndResponse(responseCode,stringBuilder.toString().trim()));
	}
}
