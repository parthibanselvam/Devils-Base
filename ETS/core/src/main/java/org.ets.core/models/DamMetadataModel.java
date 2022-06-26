package org.ets.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest; 
import org.apache.sling.api.resource.Resource; 
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model; 
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute; 
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable; 
import org.ets.core.helper.AssetMetadataHelper;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

@Model(adaptables = { Resource.class,SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class DamMetadataModel {
	protected static final Logger log = LoggerFactory.getLogger(DamMetadataModel.class);
	@RequestAttribute 
	private String imgPath;
	
	@ScriptVariable 
	private ResourceResolver resolver;
	
	private String alttxt;
	
	private AssetMetadataHelper assetMetadataHelper=new AssetMetadataHelper();
	
	@PostConstruct 
	private void imageMetadata() {
		alttxt=assetMetadataHelper.getMetadata(resolver, imgPath);
	}
	public String getalttxt() {
		return alttxt;
	}
}