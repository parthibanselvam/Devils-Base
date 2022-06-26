package org.ets.core.models;

import org.apache.commons.lang3.Stringutils; 
import org.apache.sling.api.SlingHttpServletRequest; 
import org.apache.sling.api.resource.Resource; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models. annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;

import javax.annotation.PostConstruct;

@Model (adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class LinkValidatorModel {
	
	@RequestAttribute 
	private String link;

	@PostConstruct 
	public void init() { 
		if (Stringutils.isBlank (link) || Stringutils.isEmpty(link) || (StringUtils.startsWith(link, "https://")) || (Stringutils.startsWith(link, "http://"))) {
			return;
		}
		if (StringUtils.isNotEmpty (link) && link.toLowerCase().startsWith("/content")) {
			link = link.contains (".html") ? link : (link+".html"); 
		}
		else if (Stringutils.isNotEmpty(link) && (Stringutils.startsWith(link, "www")) {
			link = "https://".concat(link);
		}
	}
	
	public String getLink() {
		return link;
	}
}