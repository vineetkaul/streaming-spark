package myapp.streamingspark.com;


import java.util.ArrayList;
import java.math.BigDecimal;
import org.apache.spark.SparkConf;

/**
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.*;
**/

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;

import twitter4j.*;

public class tstream {

	public static void main(String[] args) {
		
		
		final ArrayList<ArrayList<String>> InpstreamParent = new ArrayList<ArrayList<String>>();
		
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("SparkTwitter");
		Duration d = new Duration (20000);
		JavaSparkContext context = new JavaSparkContext(conf);
		JavaStreamingContext jssc = new JavaStreamingContext(context, d);
		
		System.setProperty("twitter4j.oauth.consumerKey", "yBAwY9ruYrUKhNNi2DE6c8Jo9");
		System.setProperty("twitter4j.oauth.consumerSecret", "Ad8xnruXIUMmBtQBVKOYQtfEqCOxCcAkyfPJOu2LZnmm0l4FF5");
		System.setProperty("twitter4j.oauth.accessToken", "2800092260-4AiHTFwtMFxVno1QQRs9AkPGvtJ3I0i7iCgrykV");
		System.setProperty("twitter4j.oauth.accessTokenSecret", "KOvV6XxfQiQHy7hCP7YS0PWjT4IDLR31cEBCLMFCSevGa");
		
		/**
		System.setProperty("twitter4j.http.proxyHost", "blrproxy.igate.com");
		System.setProperty("twitter4j.http.proxyPort", "8080");
		System.setProperty("twitter4j.http.http.proxyUser", "vikaul");
		System.setProperty("twitter4j.http.http.proxyPassword", "Samsung@01");
		System.setProperty("twitter4j.http.useSSL", "false");
		**/
		 
		FilterQuery fq = new FilterQuery();
	
		String[] filters = new String[] {"#WhyPath-E-Tech"};
		//String[] filters = new String[] {"Agusta"};
		JavaDStream<Status> twitterStream = TwitterUtils.createStream(jssc,filters);
		
		JavaDStream<String> statuses = twitterStream.map(new Function<Status, String>() {
		    public String call(Status status) throws Exception {
		      System.out.println("****************** New Dstream STARTS *************");
		      ArrayList<String> InpstreamChild = new ArrayList<String>();
		      Double FRatio;
		      if (status.isRetweet())
		      {    
			      InpstreamChild.add(0, status.getRetweetedStatus().getUser().getScreenName());
			      InpstreamChild.add(1, status.getText());
			      InpstreamChild.add(2, "true");
			      System.out.println("{This is retweet} " + status.getText() + "}---{retweeted by " + status.getUser().getScreenName() + "} originally by " + status.getRetweetedStatus().getUser().getName());
			      if (status.getRetweetedStatus().getUser().getFriendsCount() == 0){
			    	  FRatio = (double) 0;
			      }
			      else
			      {
			    	  System.out.println("Influence calculations....Follower= " + status.getRetweetedStatus().getUser().getFollowersCount() + "Following= " + status.getRetweetedStatus().getUser().getFriendsCount()); 
			    	  FRatio = (double) (status.getRetweetedStatus().getUser().getFollowersCount()/status.getRetweetedStatus().getUser().getFriendsCount());
			      }
		      }
			  else
		      {
			      InpstreamChild.add(0, status.getUser().getScreenName());
			      InpstreamChild.add(1, status.getText());
			      InpstreamChild.add(2, "false");
			      System.out.println("{This is not retweet} " + status.getText() + "}---{" +status.getUser().getScreenName() + "}");
			      if (status.getUser().getFriendsCount() == 0){
			    	  FRatio = (double) 0;
			      }
			      else
			      {
			    	  System.out.println("Influence calculations....Follower= " + status.getUser().getFollowersCount() + "Following= " + status.getUser().getFriendsCount()); 
			    	  FRatio = (double) (status.getUser().getFollowersCount()/status.getUser().getFriendsCount());
			      }
		      }
		      
		      //FRatio = rounder(FRatio, 2);
		      System.out.println("FRatio is -- " +status.getUser().getScreenName() + "} " + FRatio);
		      InpstreamChild.add(3, FRatio.toString());
		      InpstreamParent.add(InpstreamChild);
		      SteamingUtils UtilConnection = new SteamingUtils();
		      UtilConnection.streamloader(InpstreamParent);
		      System.out.println("****************** New Dstream ENDS *************");
		      InpstreamParent.clear();
		      return status.getText(); 
		    }
		  });
		
		/***************************************************************
		######   #######   #
		##          #      # 
		######      #      # 
		##          #      # 
		######      #      ######
		****************************************************************/
		/**
		twitterStream.foreachRDD(new Function<JavaRDD<Status>, Void>()
			{
				public Void call(JavaRDD<Status> rdd) 
					{
					System.out.println("Enetring first process RDD ");
						
						return null;
					}
			});
		***/
		/***************************************************************/
		/***************************************************************/
		/***************************************************************/

		//twitterStream.dstream().saveAsTextFiles("file:///d://","abc.txt");
	    statuses.print();
	    jssc.start();
	    //jssc.awaitTermination();
	}
	
	    public static double rounder(double value, int numberOfDigitsAfterDecimalPoint) {
	        BigDecimal bigDecimal = new BigDecimal(value);
	        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP);
	        return bigDecimal.doubleValue();
	    }
}
