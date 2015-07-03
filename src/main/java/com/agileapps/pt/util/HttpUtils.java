package com.agileapps.pt.util;

import java.io.IOException;
import java.io.InputStream;

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
		String response=client.execute(request,new ResponseHandler<String>(){
			public  String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				HttpEntity entity=response.getEntity();
				InputStream is=entity.getContent();
				byte buffer []=new byte[10000];
				while ( is.read(buffer) > 0){
					stringBuilder.append(new String(buffer, "UTF-8"));
				}				
				return response.getStatusLine().getStatusCode()+":"+response.getStatusLine().getReasonPhrase();
			}
		}
		);
		return (new StatusAndResponse(response,stringBuilder.toString()));
	}
}
