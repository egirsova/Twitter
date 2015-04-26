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
import java.sql.SQLException;
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
public class Login implements Serializable {

    private Connection connection;

    @Resource(lookup = "jdbc/twitter")
    private DataSource dataSource;
    // Properties
    @ManagedProperty(value = "#{user}")
    protected User user;
    protected String name;
    protected String password;
    
    

    public void setUser(User user) {
    this.user = user;
  }
    
    public User getUser(){
        return user;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName(){       
    return name;    
    }
    

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }    
    
    public Login() {
  }
    
    public String findHandle(String username){
    String navString = null;

    Connection connection = null;
    PreparedStatement getHandleStmt = null;
    String getHandleSQL = "select handle from users where username=?";
    String actualHandle = null;
    try {
      connection = dataSource.getConnection();
      getHandleStmt = connection.prepareStatement(getHandleSQL);
      getHandleStmt.setString(1, name);
      ResultSet result = getHandleStmt.executeQuery();
      if (result.next()) 
        actualHandle = result.getString("handle");
    } catch (Exception ex){
      System.out.println("Error: Cannot get handle: "+ex.getMessage()); 
    }
    return actualHandle;       
    }
    
    public String findFullName(String username){
    String navString = null;

    Connection connection = null;
    PreparedStatement getNameStmt = null;
    String getNameSQL = "select name from users where username=?";
    String actualName = null;
    try {
      connection = dataSource.getConnection();
      getNameStmt = connection.prepareStatement(getNameSQL);
      getNameStmt.setString(1, name);
      ResultSet result = getNameStmt.executeQuery();
      if (result.next()) 
        actualName = result.getString("name");
    } catch (Exception ex){
      System.out.println("Error: Cannot get name: "+ex.getMessage()); 
    }
    return actualName;       
    }
    
    public String doLogin() {
    String navString = null;

    Connection connection = null;
    PreparedStatement loginStmt = null;
    String loginSQL = "select password from users where username=?";
    try {
      connection = dataSource.getConnection();
      loginStmt = connection.prepareStatement(loginSQL);

      loginStmt.setString(1, name);
      ResultSet result = loginStmt.executeQuery();

      if (result.next()) {
        String actualPassword = result.getString("password");
        if (actualPassword != null && actualPassword.equals(password)) {
          user.setUserName(name);
          navString = "homepage";
        } else {
          FacesContext.getCurrentInstance().addMessage("form:password", new FacesMessage("Incorrect password."));
        }

      } else {
          FacesContext.getCurrentInstance().addMessage("form:name", new FacesMessage("No such user."));
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      try {
        if (loginStmt != null) {
          loginStmt.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
    user.setHandle(findHandle(name));
    user.setName(findFullName(name));
    return navString;
  }

}
