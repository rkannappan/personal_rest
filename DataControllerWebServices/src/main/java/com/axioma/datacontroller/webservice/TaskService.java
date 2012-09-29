package com.axioma.datacontroller.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.axioma.datacontroller.DataControllerFactory;
import com.axioma.datacontroller.IDataController;
import com.axioma.datacontroller.exception.IncompatibleSchemaException;
import com.axioma.datacontroller.webservice.util.DatabaseConnector;
import com.axioma.datacontroller.webservice.util.TaskConverter;
import com.axioma.db.commons.model.DataSourceProperties;
import com.axioma.model.task.Task;
import com.axioma.model.task.TaskType;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

@Path("/TaskService")
public class TaskService {
	
	@Context
	ServletContext context;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String sayJsonHello() {
    Gson gson = new Gson();
//    Map<String, String> params = Maps.newHashMap();
//    params.put("riskmodel", "US2AxiomaMH");
//    params.put("classification", "GICS");
//    Task task = new Task("MyTask", "MyTaskDesc", "MyTaskType", params);
//    return gson.toJson(task);
    
//    return task;
	  
	  IDataController dc = DatabaseConnector.getDataController(context);
	  SortedSet<Task> tasks = dc.getTasks(null, TaskType.REPORT, false);
	  
	  return gson.toJson(TaskConverter.getProxyTasks(tasks));
  }   
} 