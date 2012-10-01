package com.axioma.datacontroller.webservice;

import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.cli.ParseException;

import com.axioma.datacontroller.IDataController;
import com.axioma.datacontroller.webservice.util.DatabaseConnector;
import com.axioma.datacontroller.webservice.util.TaskConverter;
import com.axioma.model.task.Task;
import com.axioma.model.task.TaskType;
import com.axioma.taskcommander.TaskCommander;
import com.google.gson.Gson;

@Path("/TaskService")
public class TaskService {
	
   @Context
   ServletContext context;

  @GET
  @Path("{taskType}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getTasksByType(@PathParam("taskType") String taskType) {
	  Gson gson = new Gson();  
	  IDataController dc = DatabaseConnector.getDataController(context);
	  SortedSet<Task> tasks = dc.getTasks(null, TaskType.valueOf(taskType), false);
	  
	  return gson.toJson(TaskConverter.getProxyTasks(tasks));
  }
  
  @GET
  @Path("{run}/{taskType}/{taskName}")
  @Produces(MediaType.APPLICATION_JSON)
  public String runTaskByName(@PathParam("taskType") String taskType, @PathParam("taskName") String taskName) {
      String serversProps = context.getRealPath(DatabaseConnector.DB_PROPS_PATH);
      
      TaskCommander.runMain(new String[] { "-s", serversProps, "-t", taskType, "-r", taskName });
	  
	 return null;
  }
}