package org.ets.core.models;
 
import java.util.HashMap; 
import java.util.Map;

import javax.annotation.PostConstruct; 
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models.annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class MultifieldListModel {
	
	private Map<String, Resource> childNodeMap = new HashMap<>();
	
	@Inject 
	@Self 
	private Resource resource;
	
	@PostConstruct 
	private void childResourceMap() { 
		for (Resource childResource : resource.getChildren()) {
			childNodeMap.put(childResource.getName(),childResource);
		}
	}
	
	public Map<String, Resource> getMultifield() {
		return childNodeMap;
	}
}