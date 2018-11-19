package com.hintservice; 
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Application;


@Path("/hint")
public class HintService extends Application{
  
  @GET
  @Path("/{testId}/{taskId}")
  @Produces("text/plain")
  public String getHints(@PathParam("testId") String testId, @PathParam("taskId") String taskId) {
   
    int testId2 = Integer.parseInt(testId);
    int taskId2 = Integer.parseInt(taskId);
    String hints = "";
    
    try {
      Context context = new InitialContext();
      DataSource ds = (DataSource) context.lookup("java:/MySqlDS");
      Connection conn = ds.getConnection();
      
      PreparedStatement pStmt = conn.prepareStatement("select milestoneId, url, element, "
          + "elementIndex, event from milestones where testId=? and seqOrder=?");
      pStmt.setInt(1,testId2);
      pStmt.setInt(2, taskId2);
      ResultSet res = pStmt.executeQuery();
      if(!res.last()) {
        hints = "Either no task " + taskId + " or no test " + testId + " found!";
      } else {
        res.beforeFirst();
        while(res.next()) {
          if(res.isLast()) {
            hints += res.getString("event") + " " + res.getString("element") 
            + " " + res.getInt("elementIndex") + " " + res.getString("url");
          } else {
            hints += res.getString("event") + " " + res.getString("element") 
            + " " + res.getInt("elementIndex") + " " + res.getString("url") + ";";
          }
        }
      }
        
        
      
      conn.close();

    }  catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NamingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return hints;
  }

}
