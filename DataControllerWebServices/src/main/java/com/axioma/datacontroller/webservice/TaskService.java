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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.axioma.datacontroller.IDataController;
import com.axioma.datacontroller.webservice.util.DatabaseConnector;
import com.axioma.datacontroller.webservice.util.TaskConverter;
import com.axioma.model.task.Task;
import com.axioma.model.task.TaskType;
import com.axioma.taskcommander.TaskCommander;
import com.google.gson.Gson;

/**
 * @author rkannappan
 */
@Path("/TaskService")
public class TaskService {
	
   @Context
   ServletContext context;

   @GET
   @Path("{taskType}")
   @Produces(MediaType.APPLICATION_JSON)
   public String getTasksByType(@PathParam("taskType") final String taskType) {
      Gson gson = new Gson();
      IDataController dc = DatabaseConnector.getDataController(this.context);
      SortedSet<Task> tasks = dc.getTasks(null, TaskType.valueOf(taskType), false);
	  
      return gson.toJson(TaskConverter.getProxyTasks(tasks));
   }
  
   @GET
   @Path("/run/{taskType}/{taskName}")
   @Produces(MediaType.APPLICATION_JSON)
   public String runTaskByName(@PathParam("taskType") final String taskType, @PathParam("taskName") final String taskName,
		  @QueryParam("allEventsQueueName") final String allEventsQueueName, @QueryParam("progressEventsQueueName") final String progressEventsQueueName) {
      String serversProps = this.context.getRealPath(DatabaseConnector.DB_PROPS_PATH);
      
      TaskCommander.runMain(new String[] { "-s", serversProps, "-t", taskType, "-r", taskName, "-g", allEventsQueueName, "-p", progressEventsQueueName});
	  
	 return null;
  }
  
   @GET
   @Path("/run/events")
   @Produces(MediaType.APPLICATION_JSON)
   public String getEventInfo(@QueryParam("eventQueueName") final String eventQueueName) {
      Connection connection = null;
      Session session = null;
      MessageConsumer consumer = null;
      try {
         final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
         connection = connectionFactory.createConnection();
         connection.start();
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         final Destination destination = session.createQueue(eventQueueName);
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
}