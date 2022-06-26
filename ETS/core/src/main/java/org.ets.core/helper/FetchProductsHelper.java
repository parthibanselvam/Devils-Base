package org.ets.core.helper;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;

public class FetchProductsHelper {

    protected static final Logger log = LoggerFactory.getLogger(FetchProductsHelper.class);
    private List<Map<String,String>> cfLists =new ArrayList<>();
    private Set<String> categorySet = new HashSet<>();

    public Set<String> getCategorySet() {
        return new HashSet<>(categorySet);
    }

    public List<Map<String,String>> getProductsList(String productsCfFolderPath, ResourceResolver resourceResolver) {
        try {
            cfLists.clear();
            categorySet.clear();
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (resourceResolver.getResource(productsCfFolderPath) != null) {
                Node cfProductFolder = resourceResolver.getResource(productsCfFolderPath).adaptTo(Node.class);
                if (cfProductFolder!=null && cfProductFolder.hasNodes()) {
                    NodeIterator cfIterator = cfProductFolder.getNodes();
                    storeFragmentElementsInList(resourceResolver, tagManager, cfIterator);
                }
            }
        }
        catch(Exception e){
            log.error("Exception in FetchProductsHelper class {}", e.getMessage());
        }
        return new ArrayList<>(cfLists);
    }

    private void storeFragmentElementsInList(ResourceResolver resourceResolver, TagManager tagManager, NodeIterator cfIterator) throws RepositoryException {
        while (cfIterator.hasNext()) {
            Node productNode = cfIterator.nextNode();
            if (productNode!=null && !productNode.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
                ContentFragment productCf = resourceResolver.getResource(productNode.getPath()).adaptTo(ContentFragment.class);
                Resource productCfResource = productCf.adaptTo(Resource.class);
                Resource data = productCfResource.getChild(JcrConstants.JCR_CONTENT + "/data");
                String fragmentModel = data.getValueMap().get("cq:model").toString();
                if(productCf!=null && fragmentModel.equalsIgnoreCase("/conf/ets/settings/dam/cfm/models/all-products-list")) {
                    Iterator<ContentElement> productElement = productCf.getElements();
                    Map<String,String> productMap=new HashMap<>();
                    while(productElement.hasNext()) {
                        ContentElement productElementObject = productElement.next();
                        String tagElement = productElementObject.getName();
                        if(tagElement.equals("categories")) {
                            getTags(tagManager, productMap, productElementObject, tagElement);
                        } else if(tagElement.equals("productLink")){
                            String productLink = productElementObject.getContent().startsWith("/content/")
                                    ? productElementObject.getContent() + ".html"
                                    : productElementObject.getContent();
                            productMap.put(tagElement, productLink);
                        }
                        else {
                            String elementContent = productElementObject.getContent();
                            productMap.put(tagElement, elementContent);
                        }
                    }
                    cfLists.add(productMap);
                }
            }
        }
    }

    //Fetching the tags from Content Fragment Elements
    private void getTags(TagManager tagManager, Map<String, String> productMap, ContentElement productElementObject, String tagElement) {
        List<String> categories = new ArrayList<>();
        String[] categoryList = productElementObject.getValue().getValue(String[].class);
        if(categoryList!=null) {
            for(String category : categoryList) {
                Tag tag = tagManager.resolve(category);
                if(tag != null){
                    String tagName = tag.getTitle();
                    categories.add(tagName);
                    categorySet.add(tagName);
                }
            }
            productMap.put(tagElement, String.join(", ",categories));
        }
    }

}