package org.ets.core.models;

import com.adobe.ca.dam.cfm.ContentElement; 
import com.adobe.cq.dam.cfm.ContentFragment; 
import com.day.co.wcm.api.Page; 
import com.day.cq.wcm.api.PageManager; 
import com.day.crx.JcrConstants; 
import org.apache.commons.lang3.StringUtils; 
import org.apache.sling.api.SlingHttpServletRequest; 
import org.apache.sling.api.resource.Resource; 
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy; 
import org.apache.sling.models.annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable; 
import org.apache.sling.models. annotations.injectorspecific.ValueMapValue; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct; 
import java.util.*;

@Model (adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class HeaderModel {
	
	@ScriptVariable 
	private ResourceResolver resolver;

	@ScriptVariable 
	private Page currentPage;

	@ValueMapValue 
	private String homePagePath;

	@ValueMapValue 
	private String productMenucf;

	@ValueMapValue 
	private String commonLinkCf;

	protected static final Logger log = LoggerFactory.getLogger(HeaderModel.class);

	private Map<String, String> cfMap=new HashMap<>();

	private List<Map<String, String>> cflists =new ArrayList<>();

	private Map<String, Resource> langMap = new HashMap<>();

	public Map<String, Resource> getLangMap() { 
		return langMap; 
	}

	public Map<String, String> getCfMap() { 
		return cfMap; 
	}

	public List<Map<String, String>> getCfLists() {
		return new ArrayList<>(cfLists);
	}

	@PostConstruct 
	public void headerMenulist() {
		log.info("Start of the header model"); 
		if(homePagePath!=null){
			cfMap.clear(); 
			PageManager pageManager=resolver.adaptTo(PageManager.class); 
			Page parentPage=pageManager.getPage(homePagePath); 
			if(!(currentPage.getPath().equals(homePagePath))) {
				Map<String, String> cfMenuMap=new HashMap<>(); 
				cfMenuMap.put("productLandingPage", homePagePath); 
				cfMenuMap.put("productTitle", parentPage.getTitle(); 
				cfMenuMap.put("themeCssClass", "theme-generic-bar"); 
				cfLists.add(cfMenuMap);
			}
			cfMap.put("langname", parentPage.getAbsoluteParent(3)!=null? parentPage.getAbsoluteParent().getTitle():StringUtils.EMPTY);
		}
		if (commonLinkCf!=null) {
			Resource commonLinkCfResource = resolver.getResource (commonLinkCf); 
			if(commonLinkCfResource!=null) {
				ContentFragment dcf = commonLinkCfResource adaptTo(ContentFragment.class); 
				if(dcf!=null) {
					Iterator<ContentElement> contentElement Edcf.getElements(); 
					while (contentElement. hasNext() {
					ContentElement contentElementObject = contentElement.next(); 
					String tagElement = contentElementObject.getName(); 
					String elementContent = contentElementObject.getContent(); 
					cfMap.put(tag Element, elementContent);
					}
				}
			}
		}
		if(productMenu(f!=null) {
		Resource menuList = resolver.getResource (productMenucf); getProductMenu(menulist);
		}
		getSiteProperties(); 
		log.info("End of the header model");
	}
	
	/**
	* Used to construct the product menu map 
	* @param menulist 
	*/ 
	private void getProductMenu(Resource menulist) {
		String templateName=(currentPage.getTemplate() !=null)? currentPage.getTemplate().getName(): Stringutils. EMPTY; 
		if (menulist!=null) {
			Iterator Resource> cfResourceList=menuList.getChildren() iterator();
			while(cfResourceList.hasNext()) {
				Map<String, String> cfMenuMap=new HashMap<>(); 
				Resource resource=cfResourceList.next(); 
				if(resource!=null) {
					Content Fragment cf = resource.adaptTo(ContentFragment.class);
					if(cf!=null) { 
						Iterator<ContentElement> contentElement = cf.getElements(); 
						while (contentElement.hasNext() {
							ContentElement contentElementObject = contentElement.next(); 
							String tagElement = contentElementObject.getName(); 
							String elementcontent = contentElementObject.getContent(); 
							if(tagElement.equals("themeCssClass")) {
								elementContent=elementContent.concat(getActive Product(elementContent, templateName));
							}
							cfMenuMap.put(tagElement, elementContent);
						}
						cfLists.add(cfMenuMap);
					}
				}
			}
		}
	}

	private String getActiveProduct(String themeCss, String templateName) {
		String product=Stringutils.substringBetween(themeCss, "theme-", "-bar"); 
		return templateName. contains (product)?" active": Stringutils. EMPTY;
	}

	private void getSiteProperties() { 
		if(!(currentPage.getPath().startsWith("/content/experience-fragments") || currentPage.getPath().startsWith("/conf"))) {
			Resource siteResource = resolver.getResource (currentPage.getAbsoluteParent(1).getPath(); 
			if(siteResource!=null && siteResource.hasChildren() {
				Resource jcrResource = siteResource.getChild(JcrConstants. JCR_CONTENT); 
				if(jcrResource!=null && jcrResource.hasChildren() {
					Resource languageList = jcrResource.getChild("langList"); 
					langMap.put("langList", languageList);
				}
			}
		}
	}
}