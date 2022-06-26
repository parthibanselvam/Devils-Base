package org.ets.core.models;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.Iterator; 
import java.util.List; 
import java.util.Map;
import javax. annotation.PostConstruct;
import org.apache.commons.lang3.Stringutils; 
import org.apache.sling.api.SlingHttpServletRequest; 
import org.apache.sling.api.resource. Resource; 
import org.apache.sling.api.resource. ValueMap; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models.annotations. Model; 
import org.apache.sling.models.annotations.injectorspecific.ChildResource; 
import org.apache.sling.models. annotations.injectorspecific.ScriptVariable; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page; 
import com.day.co.wcm. api.PageManager;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class FeatureNewsModel {
	
	@ScriptVariable 
	private PageManager pageManager;

	@ChildResource 
	Resource cards;

	protected static final Logger log = LoggerFactory.getLogger(FeatureNewsModel.class);

	private List<Map<String, String>> pageList = new ArrayList<>();

	@PostConstruct 
	public void init() { 
		if(cards!=null) {
			Iterator Resource> multifieldResource=cards.getChildren() iterator(); 
			while(multifieldResource.hasNext()) {
				Resource itemResource=multifieldResource.next(); 
				ValueMap itemValueMap=itemResource.getValueMap(); 
				Object pathobj=itemValueMap.get("pagepath"); 
				if(pathobj!=null) {
					Page newsPage = pageManager.getPage(pathobj.toString()); 
					newsPageMetadata(newsPage);
				}
			}
		}
	}
	/**
	* @param newsPage
	*/
	private void newsPageMetadata(Page newsPage) {
		if (newsPage != null) {
			Map<String, String> newsPageMap = new HashMap<>(); 
			newsPageMap.put("path", newsPage.getPath()); 
			ValueMap newsPageProperties = newsPage.getProperties();
			newsPageMap.put("title", (newsPageProperties.get("shortTitle") != null) ? news PageProperties.get("shortTitle").toString(): tringutils.EMPTY);
			newsPageMap.put("description", (newsPageProperties.get("shortDescription") != null) ? descriptionEllipsis newsPageProperties.get("shortDescription").toString(): Stringutils. EMPTY);
			pageList.add(newsPageMap);
		}
	}
	
	public List<Map<String, String>> getPageList() {
		return new ArrayList<>(pageList);
	}
	
	private String descriptionEllipsis(String text) { 
		if (text.length() <= 114) {
			return text;
		}
		int end = text.lastIndexOf('', 114 - 1); return text.substring(0, end) + "...";
	}
}