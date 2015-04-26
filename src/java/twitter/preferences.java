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
import java.sql.Statement;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

/**
 *
 * @author Liza Girsova
 */
@ManagedBean
@SessionScoped
public class preferences implements Serializable {
    
    @Resource(lookup = "jdbc/twitter")
    private DataSource dataSource;
    // Properties
    @ManagedProperty(value = "#{user}")
    protected User user;
    protected String followHandle;
    protected String unfollowHandle;
    protected String followHashtag;
    protected String unfollowHashtag;
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

    public String getFollowHandle() {
        return followHandle;
    }

    public void setFollowHandle(String followHandle) {
        this.followHandle = followHandle;
    }
    
    public String getUnfollowHandle() {
        return unfollowHandle;
    }

    public void setUnfollowHandle(String unfollowHandle) {
        this.unfollowHandle = unfollowHandle;
    }

    public String getFollowHashtag() {
        return followHashtag;
    }

    public void setFollowHashtag(String followHashtag) {
        this.followHashtag = followHashtag;
    }
    
    public String getUnfollowHashtag() {
        return unfollowHashtag;
    }

    public void setUnfollowHashtag(String unfollowHashtag) {
        this.unfollowHashtag = unfollowHashtag;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
    
    public preferences() {
    }
    
    public String addFollower(){
        String navString = null;
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            
            connection = dataSource.getConnection();
            Statement statementone = connection.createStatement();  
            Statement statementtwo = connection.createStatement();
            String getSearchSQL = "SELECT handle_follower.handle,following FROM twitter.handle_follower WHERE handle_follower.handle='"+user.getHandle()+"' AND handle_follower.following='"+followHandle+"'";
            String checkUserSQL = "SELECT users.handle FROM twitter.users WHERE users.handle='"+followHandle+"'";
            ResultSet result = statementone.executeQuery(getSearchSQL);
            ResultSet result2 = statementtwo.executeQuery(checkUserSQL);
            if(!result2.next()){
            FacesContext.getCurrentInstance().addMessage("form:followUser", new FacesMessage("User Does Not Exist"));    
            }
            if(!result.next()){
            insertStmt = connection.prepareStatement("insert into handle_follower(handle,following) values(?,?)");
            insertStmt.setString(1, user.getHandle());
            insertStmt.setString(2, followHandle);
            insertStmt.executeUpdate();
            navString="homepage";
            } else
                FacesContext.getCurrentInstance().addMessage("form:followUser", new FacesMessage("User Already Followed"));
        } catch (Exception ex) {
            System.out.println("Error: Cannot add follower: "+ex.getMessage());
        }
        return navString;
    }
    
    public ArrayList<String> getFollowers(){
        String navString = null;
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ArrayList<String> allFollowers = new ArrayList<>();
        try {          
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();        
            String getSearchSQL = "SELECT handle_follower.handle,following FROM twitter.handle_follower WHERE handle_follower.handle='"+user.getHandle()+"'";
            ResultSet result = statement.executeQuery(getSearchSQL);          
            String actualFollower = null;
            while(result.next()){
                actualFollower = result.getString("following");
                allFollowers.add(actualFollower);
            }
        } catch (Exception ex){
            System.out.println("Error: Cannot get followers: "+ex.getMessage());
        }
        return allFollowers;      
    }
    
    public String removeFollowers(){
    String navString = null;
        Connection connection = null;
        PreparedStatement removeStmt = null;
        try {          
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();        
            String getRemoveSQL = "delete from handle_follower WHERE handle_follower.following='"+unfollowHandle+"' AND handle_follower.handle='"+user.getHandle()+"'";
            statement.executeUpdate(getRemoveSQL);  
            navString = "homepage";
        } catch (Exception ex){
            System.out.println("Error: Cannot remove followers: "+ex.getMessage());
        }
        return navString;          
    }
    
    public String addFollowHashtag(){
        String navString = null;
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();        
            String getSearchSQL = "SELECT hashtag_follower.handle,hashtag FROM twitter.hashtag_follower WHERE hashtag_follower.handle='"+user.getHandle()+"' AND hashtag_follower.hashtag='"+followHashtag+"'";
            ResultSet result = statement.executeQuery(getSearchSQL);
            if(!result.next()){
            insertStmt = connection.prepareStatement("insert into hashtag_follower(handle,hashtag) values(?,?)");
            insertStmt.setString(1, user.getHandle());
            insertStmt.setString(2, followHashtag);
            insertStmt.executeUpdate();
            navString="homepage";}
            else
                FacesContext.getCurrentInstance().addMessage("form:followHashtag", new FacesMessage("Hashtag Already Followed"));
        } catch (Exception ex) {
            System.out.println("Error: Cannot follow hashtag: "+ex.getMessage());
        }
        return navString;
    }
    
     public ArrayList<String> getFollowedHashtags(){
        String navString = null;
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ArrayList<String> allHashtags = new ArrayList<>();
        try {          
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();        
            String getSearchSQL = "SELECT hashtag_follower.handle,hashtag FROM twitter.hashtag_follower WHERE hashtag_follower.handle='"+user.getHandle()+"'";
            ResultSet result = statement.executeQuery(getSearchSQL);          
            String actualFollower = null;
            while(result.next()){
                actualFollower = result.getString("hashtag");
                allHashtags.add(actualFollower);
            }
        } catch (Exception ex){
            System.out.println("Error: Cannot get followed hashtags: "+ex.getMessage());
        }
        return allHashtags;      
    }
     
      public String removeHashtags(){
        String navString = null;
        Connection connection = null;
        PreparedStatement removeStmt = null;
        try {          
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();        
            String getRemoveSQL = "delete from hashtag_follower WHERE hashtag_follower.hashtag='"+unfollowHashtag+"' AND hashtag_follower.handle='"+user.getHandle()+"'";
            statement.executeUpdate(getRemoveSQL);  
            navString = "homepage";
        } catch (Exception ex){
            System.out.println("Error: Cannot remove followers: "+ex.getMessage());
        }
        return navString;          
    }
    
    
    
}
