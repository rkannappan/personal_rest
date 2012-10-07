package com.axioma.datacontroller.webservice.bean;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author rkannappan
 */
public class ProxyTask {
	
   private String name;
	private String desc;
	private String type;
	
	private Map<String, Collection<String>> params = Maps.newHashMap();
	
   public ProxyTask() {
	}
	
	public ProxyTask(final String name, final String desc, final String type, final Map<String, Collection<String>> params) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.params = params;
	}

   public String getName() {
      return this.name;
   }

   public String getDesc() {
      return this.desc;
   }

   public String getType() {
      return this.type;
   }

   public Map<String, Collection<String>> getParams() {
      return this.params;
   }
}