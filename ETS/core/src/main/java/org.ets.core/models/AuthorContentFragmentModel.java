AuthorContentFragmentModel - Notepad
File Edit Format View Help
package org.ets.core.models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation. PostConstruct;
import javax.inject. Inject;
import javax. jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.sling.api.resource. Resource;
import org.apache.sling.api.resource. ResourceResolver;
import org.apache.sling.models. annotations. Model;
import org.apache.sling.models. annotations. Source;
import org.apache.sling.models. annotations.injectorspecific. Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api. PageManager;

@Model (adaptables = Resource.class)
public class AuthorContentFragmentModel {

	protected static final Logger log = LoggerFactory.getLogger (AuthorContentFragmentModel.class);

	@Self 
	Resource resource;

	@Inject
	@Source("sling-object")

	private ResourceResolver resourceResolver;
	private List<Map<String, String>> cfLists =new ArrayList<>();

	@PostConstruct
	public void init() {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		log.debug("Start of Author Content FragmentModel init class");
		cfLists.clear();
		if (pageManager != null) {
			Page currentPage = pageManager.getContainingPage (resource);
			if (currentPage != null) {
				String[] authorList=currentPage.getProperties ().get("author", String[].class);
				if(authorList!=null) {
					for (String cfPath:authorList) {
					cfMapCreation (cfPath);
					}
				}
			}
		}
		log.debug("End of Author Content FragmentModel init class");
	}

	private void cfMapCreation (String cfPath) {
		Map<String, String> cfMap=new HashMap<>();
		Resource cfResource=resourceResolver.getResource (cfPath);
		if(cfResource!=null) {
			ContentFragment dcf = cfResource.adaptTo(Content Fragment.class);
			Iterator<ContentElement> contentElement =dcf.getElements();
			while (contentElement.hasNext()) {
				ContentElement contentElementObject = contentElement.next(); 
				String tagElement = contentElementObject.getName(); 
				String elementContent = contentElementObject.getContent(); 
				cfMap.put(tagElement, elementContent);
			}
				cflists.add(cfMap);
		}
	}
	
	public List<Map<String, String>> getCflists() {
	return new ArrayList<>(cfLists);
	}
}