package org.ets.core.models

import java.text.SimpleDateFormat; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Stringutils; 
import org.apache.sling-api.SlingHttpServlet;
import org.apache.sling-api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.api.resource.ValueMap; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models.annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import com.adobe.ca.dam.cfm.ContentFragment; 
import com.day.co.tagging.Tag; 
import com.day.cq.tagging.TagManager; 
import com.day.cq.wcm.api.Page;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class), defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class PageMetadataModel {

	@ScriptVariable 
	private ResourceResolver resolver;

	@ScriptVariable 
	private Page currentPage;

	protected static final Logger log = LoggerFactory.getLogger(PageMetadataModel.class);
	private String publishDate=Stringutils.EMPTY;
	private List<String> cfLists =new ArrayList<>();
	private List<String> categoryLists =new ArrayList<>()
	private String contentType=StringUtils.EMPTY;

	public String getContentType() {
		return contentType;
	}
	
	public List<String> getCategory Lists() {
		return new ArrayList<>(categoryLists)
	}
	
	public List<String> getCfLists() {
		return new ArrayList<>(cfLists);
	}
	
	public String getPublishDate() {
		return publishDate;
	}

	@PostConstruct 
	private void init() {
		TagManager tagmanager = resolver. adaptTo(Tagmanager.class); 
		log.info("Start of PageMetadataModel init()"); 
		ValueMap currentPageValueMap=currentPage.getProperties();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy"); 
		Date articlePublishDate=currentPageValueMap.get("articlePublishDate", Date.class); 
		if(articlePublishDate! =null) {
			publishDate = formatter.format(articlePublishDate);
		}
		String[] authorList=currentPageValueMap.get("author", String[].class); 
		if(authorList!=null) { 
			for(String authorCfPath:authorList) {
				Resource cfResource=resolver.getResource(authorCfPath); 
				if(cfResource !=null) {
					ContentFragment dcf = cfResource. adaptTo(Content Fragment.class); 
					log.debug("Content fragment using resource {}", dcf.getTitle(); 
					cfLists.add(def.getTitle());
				}
			}
		}
		String[] categoryTagIdlist=currentPageValueMap.get("category", String[].class); if(categoryTagIdList!=null) { 
			for(String categoryTagId:categoryTagIdList) {
				Tag tag = tagmanager.resolve(categoryTagId); 
				if (tag != null) {
					category Lists.add(tag.getTitle();
				}
			}
		}
		String contentTypeTagID=currentPageValueMap.get("contentType", String.class); if(contentTypeTagID!=null) {
			Tag tag = tagmanager.resolve(contentTypeTagID); 
			if (tag != null) {
				contentType=tag.getTitle();
			}
		}
		log.info("End of PageMetadataModel init()");
	}
}