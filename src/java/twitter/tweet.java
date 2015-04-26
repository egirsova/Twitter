 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.sql.DataSource;

/**
 *
 * @author Liza Girsova
 */
@ManagedBean
@SessionScoped
public class tweet implements Serializable {

    @Resource(lookup = "jdbc/twitter")
    private DataSource dataSource;
    // Properties
    @ManagedProperty(value = "#{user}")
    protected User user;
    protected String tweet;
    protected String handle;
    protected String postTime;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public tweet() {
    }

    public ArrayList<String> getTweets() {
        String navString = null;
        Connection connection = null;       
        String actualTweet = null;
        String actualDate = null;
        String actualHandle = null;
        ArrayList<String> outputTweets = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            String getTweetSQL = "(SELECT tweet,post_time,tweets.handle FROM twitter.tweets JOIN twitter.handle_follower where tweets.handle=handle_follower.following AND handle_follower.handle='"+user.getHandle()+"')"
                    + " UNION (SELECT tweet,post_time,tweets.handle FROM twitter.tweets WHERE tweets.handle='"+user.getHandle()+"')"
                    + " UNION (SELECT tweets.tweet,post_time,tweets.handle FROM twitter.tweets INNER JOIN twitter.hashtags ON tweets.tweet=hashtags.tweet INNER JOIN twitter.hashtag_follower ON hashtags.hashtag=hashtag_follower.hashtag AND hashtag_follower.handle='"+user.getHandle()+"') "
                    + " UNION (SELECT tweets.tweet,post_time,tweets.handle FROM twitter.tweets INNER JOIN twitter.atsigns ON tweets.tweet=atsigns.tweet AND atsigns.atsign='"+user.getHandle()+"')order by post_time desc";
            ResultSet result1 = statement.executeQuery(getTweetSQL);
            int count = 0;
            while (result1.next() && count<20) {
                actualTweet = result1.getString("tweet");
                actualDate = result1.getString("post_time");
                actualHandle = result1.getString("handle");
                outputTweets.add("@"+actualHandle+": "+actualTweet+" \n"+actualDate+" \n\n");
                count++;
            }
        } catch (Exception ex) {
            System.out.println("Error: Cannot get tweets: " + ex.getMessage());
        }
        return outputTweets;
    }

    public void setHashtags() {
        String delims = "[ .,?!]+";
        String[] stringArray = tweet.split(delims);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].contains("#")) {
                Connection connection = null;
                PreparedStatement insertStmt = null;
                try {
                connection = dataSource.getConnection();
                insertStmt = connection.prepareStatement("insert into hashtags(tweet,hashtag) values(?,?)");
                insertStmt.setString(1, tweet);
                insertStmt.setString(2, stringArray[i]);
                insertStmt.executeUpdate();
                } catch (Exception ex){
                  System.out.println("Error: Cannot store hashtags: "+ex.getMessage());
                }
            }
        }          
    }
    
    public void setAtSign(){
    String delims = "[ .,?!]+";
        String[] stringArray = tweet.split(delims);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].contains("@")) {
                Connection connection = null;
                PreparedStatement insertStmt = null;
                try {
                connection = dataSource.getConnection();
                insertStmt = connection.prepareStatement("insert into atsigns(tweet,atsign) values(?,?)");
                insertStmt.setString(1, tweet);
                insertStmt.setString(2, stringArray[i].substring(1));
                insertStmt.executeUpdate();
                } catch (Exception ex){
                  System.out.println("Error: Cannot store atSigns: "+ex.getMessage());
                }
            }
        }    
    }

    public String publishTweet() {
        String navString = null;
        Connection connection = null;
        PreparedStatement insertStmt = null;
        getTweets();
        setHashtags();
        setAtSign();
        try {
            connection = dataSource.getConnection();
            insertStmt = connection.prepareStatement("insert into tweets(tweet,handle,post_time) values(?,?,NOW())");
            insertStmt.setString(1, tweet);
            insertStmt.setString(2, user.getHandle());
            insertStmt.executeUpdate();
        } catch (Exception ex) {

        }
        return navString;
    }
}
