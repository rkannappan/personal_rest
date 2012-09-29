package com.axioma.datacontroller.webservice.util;

import java.util.Set;

import com.axioma.datacontroller.webservice.bean.ProxyTask;
import com.axioma.model.task.Task;
import com.google.common.collect.Sets;

public class TaskConverter {
	
	public static Set<ProxyTask> getProxyTasks(final Set<Task> tasks) {
		Set<ProxyTask> proxyTasks = Sets.newLinkedHashSet();
		
		for (Task task : tasks) {
			ProxyTask pTask = new ProxyTask(task.getName(), task.getDescription(), task.getType().toString(), task.getAll().asMap());
			proxyTasks.add(pTask);
		}
		
		return proxyTasks;
	}

}
