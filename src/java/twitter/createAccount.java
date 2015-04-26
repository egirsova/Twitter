/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package twitter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

/**
 *
 * @author Liza Girsova
 */
@ManagedBean
@SessionScoped
public class createAccount {

private Connection connection;
  
@Resource(lookup = "jdbc/twitter")
  private DataSource dataSource;
  // Properties
  @ManagedProperty(value = "#{user}")
  protected User user;
  protected String name;
  protected String userName;
  protected String password;  
  protected String handle;
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getUserName(){
      return userName;
  }
  
  public void setUserName(String userName){
      this.userName = userName;
  }
  
  public String getHandle(){
      return handle;
  }
  
  public void setHandle(String handle){
      this.handle = handle;
  }
          
  
    public createAccount() {
        
    }
    
    public String newAccount() {
    String navString = null;

    Connection connection = null;
    PreparedStatement userQuery = null;
    String userSQL = "select username from users where username=?";
    PreparedStatement insertStmt = null;
    String insertSQL = "insert into users(name,username,password,handle) values(?,?,?,?)";

    try {
      connection = dataSource.getConnection();
      userQuery = connection.prepareStatement(userSQL);
      userQuery.setString(1, userName);
      ResultSet result = userQuery.executeQuery();

      if (result.next()) {
        FacesContext.getCurrentInstance().addMessage("form:userName", new FacesMessage("User Already Exists"));
      } else {
        insertStmt = connection.prepareStatement(insertSQL);
        insertStmt.setString(1, name);
        insertStmt.setString(2, userName);
        insertStmt.setString(3, password);
        insertStmt.setString(4, handle);
        insertStmt.executeUpdate();
        navString = "index";
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      try {
        if (userQuery != null) {
          userQuery.close();
        }
        if (insertStmt != null) {
          insertStmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }

    return navString;
  }
    
}
