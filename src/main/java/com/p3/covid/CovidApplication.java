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
				Thread.sleep(1100);
				if (requestsNumber > 0)
					requestsNumber--;
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		return requestsNumber < 60;
	}

}

@SpringBootApplication
public class CovidApplication {
	
	public static void main(String[] args) {
		Thread myThread = new ThreadWithGlobals();
		myThread.start();
		SpringApplication.run(CovidApplication.class, args);
	}

}
