package twitter;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
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
public class search implements Serializable {

    @Resource(lookup = "jdbc/twitter")
    private DataSource dataSource;
    // Properties
    @ManagedProperty(value = "#{user}")
    protected User user;
    protected String search;
    protected String hashtag;
    protected String tweet;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public search() {
    }

    public String searchButton() {
        String navString = null;
        outputSearchResults();
        return navString;
    }

    public ArrayList<String> outputSearchResults() {
        String getSearchSQL = "SELECT handle,post_time,tweets.tweet FROM twitter.tweets JOIN twitter.hashtags WHERE tweets.tweet=hashtags.tweet AND hashtags.hashtag='"+search+"'";
        String actualTweet = null;
        Timestamp actualDate = null;
        String actualHandle = null;
        ArrayList<String> outputTweets = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(getSearchSQL);
            int count = 0;
            while (result.next() && count < 20) {
                actualTweet = result.getString("tweet");
                System.out.println(actualTweet);
                actualDate = result.getTimestamp("post_time");
                System.out.println(actualDate);
                actualHandle = result.getString("handle");
                System.out.println(actualHandle);
                outputTweets.add("@"+actualHandle+": "+actualTweet+"\n"+actualDate+"\n\n");
                count++;
            }
        } catch (Exception ex) {
            System.out.println("Error: Cannot get tweets: " + ex.getMessage());
        }
        return outputTweets;
    }

}
