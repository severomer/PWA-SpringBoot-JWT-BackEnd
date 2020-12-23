package com.springist.demo.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


 //@RunWith(SpringRunner.class)
@RunWith(SpringRunner.class)

//  @RestClientTest(ToDoService.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class greetingTest{

	@Autowired TestRestTemplate testRest;
	
	@Test
	public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived()
	  throws ClientProtocolException, IOException {
	  
	    // Given
	    String name = RandomStringUtils.randomAlphabetic( 8 );
	    HttpUriRequest request = new HttpGet( "https://api.github.com/users/" + name );
	 
	    // When
	    HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
	 
	    // Then
	    Assert.assertThat(
	      httpResponse.getStatusLine().getStatusCode(),
	      is(HttpStatus.SC_NOT_FOUND));
	}
	
	@Test 
	public void apiTest() {
		String body = this.testRest.getForObject("/api/greetings", String.class);
		
		Assert.assertThat(body, containsString("Hello"));
		
	}
	
	
}
