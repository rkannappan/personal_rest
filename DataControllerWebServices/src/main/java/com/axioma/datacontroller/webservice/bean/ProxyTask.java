package com.axioma.datacontroller.webservice.bean;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Maps;

public class ProxyTask {
	
	private String name;
	private String desc;
	private String type;
	
	private Map<String, Collection<String>> params = Maps.newHashMap();
	
	public ProxyTask() {	
	}
	
	public ProxyTask(String name, String desc, String type, Map<String, Collection<String>> params) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.params = params;
	}

}
