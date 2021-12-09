package com.p3.covid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class ThreadWithGlobals extends Thread{
	public static int requestsNumber;

	ThreadWithGlobals(){
		requestsNumber=0;
	}

	@Override
	public void run(){
		while(true){
			try {
				Thread.sleep(1000);
				if (requestsNumber > 0)
					requestsNumber--;
			}catch (InterruptedException ignored) {
			}
		}
	}

	public static void incrementUsedRequests(){
		requestsNumber++;
	}

	public static void couldNotRequest(){
		requestsNumber=60;
	}

	public static boolean canRequest(){
		return requestsNumber <= 45;
	}

}

@SpringBootApplication
public class CovidApplication {
	
	public static void main(String[] args) {
		Thread myThread = new ThreadWithGlobals();
		myThread.start();
		SpringApplication application = new SpringApplication(CovidApplication.class);
		application.run(args);
	}

}
