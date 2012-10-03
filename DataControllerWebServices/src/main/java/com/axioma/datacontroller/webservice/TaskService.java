package com.axioma.datacontroller.webservice;

import java.util.SortedSet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.cli.ParseException;
import org.joda.time.DateTime;

import com.axioma.datacontroller.IDataController;
import com.axioma.datacontroller.webservice.util.DatabaseConnector;
import com.axioma.datacontroller.webservice.util.TaskConverter;
import com.axioma.model.task.Task;
import com.axioma.model.task.TaskInstance;
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
  @Path("/run/{taskType}/{taskName}")
  @Produces(MediaType.APPLICATION_JSON)
  public String runTaskByName(@PathParam("taskType") String taskType, @PathParam("taskName") String taskName) {
      String serversProps = context.getRealPath(DatabaseConnector.DB_PROPS_PATH);
      
      TaskCommander.runMain(new String[] { "-s", serversProps, "-t", taskType, "-r", taskName });
	  
	 return null;
  }
  
  @GET
  @Path("/progress/{taskType}/{taskName}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getProgressByTaskName(@PathParam("taskType") String taskType, @PathParam("taskName") String taskName) {
	  Connection connection = null;
	  Session session = null;
	  MessageConsumer consumer = null;
	  try {
		final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final DateTime startTime = this.getLatestTaskInstanceStartTime(taskType, taskName);
		System.out.println("Queue is " + taskName + startTime.toString());
		final Destination destination = session.createQueue(taskName + startTime.toString());
		consumer = session.createConsumer(destination);

			final Message m = consumer.receive(1000);
			if (m != null) {
				final TextMessage textMessage = (TextMessage) m;
				String message = textMessage.getText();

				Gson gson = new Gson();  
				return gson.toJson(message);
			}
	  } catch (JMSException ex) {
		  ex.printStackTrace();
	  } finally {
		  try {
			  if (consumer != null) {
				  consumer.close();
			  }
			  if (session != null) {
				  session.close();
			  }
			  if (connection != null) {
				  connection.close();
			  }
		  } catch (JMSException ex) {
			  ex.printStackTrace();
		  }
	  }
	  
	  return null;
  }
  
  private DateTime getLatestTaskInstanceStartTime(final String taskType, final String taskName) {
	  IDataController dc = DatabaseConnector.getDataController(context);
	  TaskInstance ti = dc.getLatest(new Task(taskName, TaskType.valueOf(taskType)));
	  return ti.getStartDateTime();
  }
}