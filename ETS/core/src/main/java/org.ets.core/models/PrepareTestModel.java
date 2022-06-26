package org.ets.core.models;

import org.apache.sling.api.SlingHttpServletRequest; 
import org.apache.sling.api.resource.Resource; 
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models.annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable; 
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue; 
import org.ets.core.bean.ContentSchema; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

import javax.annotation.PostConstruct; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Iterator; 
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PrepareTestModel {
	
	@ValueMapValue 
	private String path;
	
	@ScriptVariable 
	private ResourceResolver resolver;
	
	private List<ContentSchema> CsList = new ArrayList<>();
	protected static final Logger log = LoggerFactory.getLogger(PrepareTestModel.class);
	
	@PostConstruct 
	private void init() {
		log.info("Start of the prepare test model) 
		if (path != null) {
			Resource fragmentResource = resolver.getResource(path); 
			if (fragmentResource != null) {
				Iterator<Resource> childIterator = fragmentResource.getChildren().iterator(); 
				while (childIterator hasNext() {
					ContentSchema cs = new ContentSchema(); 
					Resource childResource = childIterator.next;
					Resource jcrResource = childResource.getChild(JcrConstants.JCR_CONTENT); 
					String title = jcrResource.getValueMap().get(Jcrconstants.JCR_TITLE, String.class); 
					cs.setTitle(title); 
					log.info("Fragment Paths: {}", childResource.getPath);
					String xfPath = childResource.getPath();
					cs.setPath(xfPath+"/master/jcr:content/root.html"); 
					log.info("Fragment Title: {}", childResource.getName()); 
					String name = childResource.getName();
					cs.setName(name); 
					csList.add(Cs); 
					Collections.sort(cslist);
				}
			}
			log.info("End of the prepare test model");
		}
	}

	public List<ContentSchema> getCsList() {
		return new ArrayList<>(cslist);
	}
}