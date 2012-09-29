package com.axioma.datacontroller.webservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.axioma.datacontroller.DataControllerFactory;
import com.axioma.datacontroller.IDataController;
import com.axioma.datacontroller.exception.IncompatibleSchemaException;
import com.axioma.db.commons.model.DataSourceProperties;

public class DatabaseConnector {
	
   private static final String DB_PROPS_PATH = "WEB-INF/classes/server.properties";
	
   public static IDataController getDataController(final ServletContext context) {
      // only recreate entire DataController (and all bindings) if properties have changed, or it's invalid
      try {
         return DataControllerFactory.getInstance(DatabaseConnector.getDataSourceProperties(context),
                     DataControllerFactory.DEFAULT_USER_NAME, DataControllerFactory.DEFAULT_PASSWORD);
      }
      catch (IncompatibleSchemaException e) {
         e.printStackTrace();
      }
      catch (com.axioma.datacontroller.exception.ValidationException e) {
         e.printStackTrace();
      }
      catch (com.axioma.datacontroller.exception.DataException e) {
         e.printStackTrace();
      }
      
      return null;
   }

   private static DataSourceProperties getDataSourceProperties(final ServletContext context) {
      Properties dbConfigProperties = new Properties();
      try {
         InputStream fis = context.getResourceAsStream(DatabaseConnector.DB_PROPS_PATH);
         if (fis == null) {
            throw new IllegalArgumentException("Could not find database properties file at \"" + DatabaseConnector.DB_PROPS_PATH + "\" under deployment folder");
         }
         dbConfigProperties.load(fis);
      } catch(IOException e) {
    	  e.printStackTrace();
    	  return null;
      }
      
      return DataSourceProperties.buildFromProps(dbConfigProperties);
   }
}