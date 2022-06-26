package org.ets.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.ets.core.bean.CategorySchema;
import org.ets.core.bean.ContentSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;

public class FetchNewsHelper {

    private static final String CATEGORY = "category";
    protected static final Logger log = LoggerFactory.getLogger(FetchNewsHelper.class);
    private List<ContentSchema> listOfContents = new ArrayList<>();
    private List<CategorySchema> listOfCategory = new ArrayList<>();
    private Map<String, String> categoryMap = new HashMap<>();
    private AssetMetadataHelper assetMetadataHelper=new AssetMetadataHelper();

    public List<ContentSchema> getListofcontents() {
        return new ArrayList<>(listOfContents);
    }

    public List<CategorySchema> getListofcategory() {
        Set<String> set = categoryMap.keySet();
        for (String key : set) {
            CategorySchema cats = new CategorySchema();
            cats.setTitle(categoryMap.get(key));
            cats.setValue(key);
            listOfCategory.add(cats);
        }
        return new ArrayList<>(listOfCategory);
    }

    public void getList(String parentPagePath, String resultCategory, String contentType,
            ResourceResolver resourceResolver, Session session ,QueryBuilder queryBuilder) {
        try {
            log.info("Page Servlet started");
            listOfContents.clear();
            listOfCategory.clear();
            categoryMap.clear();
            if (resourceResolver.getResource(parentPagePath) != null) {
                List<String> childPagePath = getArticlePages(parentPagePath, resultCategory, contentType, session, queryBuilder);
                log.debug("Child Pages List {}", childPagePath);
                for (String childPagePathString : childPagePath) {
                    ContentSchema cs = new ContentSchema();
                    Node pageNode = resourceResolver.getResource(childPagePathString).adaptTo(Node.class);
                    getContentSchemaData(cs, resourceResolver, pageNode);
                    listOfContents.add(cs);
                }
            }
        } catch (RepositoryException e) {
            log.error("RepositoryException in FetchNewsPagesImpl class {}", e.getMessage());
        }
    }

    /**
     * @param cs
     * @param pageNode
     * @throws RepositoryException
     */
    private void getContentSchemaData(ContentSchema cs, ResourceResolver resourceResolver, Node pageNode)
            throws RepositoryException {
        if (pageNode.hasNode(JcrConstants.JCR_CONTENT)) {
            Node jcrNode = pageNode.getNode(JcrConstants.JCR_CONTENT);
            getPageCategory(resourceResolver, jcrNode);
            cs.setTitle(jcrNode.hasProperty("shortTitle") 
                    ? jcrNode.getProperty("shortTitle").getValue().getString()
                    : StringUtils.EMPTY);
            getPageImage(cs, resourceResolver, jcrNode);
            cs.setDescription(jcrNode.hasProperty("shortDescription")
                    ? descriptionEllipsis(jcrNode.getProperty("shortDescription").getValue().getString())
                    : StringUtils.EMPTY);
            cs.setDate(jcrNode.hasProperty("articlePublishDate")
                    ? jcrNode.getProperty("articlePublishDate").getValue().getString()
                    : jcrNode.getProperty(JcrConstants.JCR_CREATED).getValue().getString());
            String pagepath = pageNode.getPath();
            cs.setPath(pagepath + ".html");
        }
    }

    /**
     * @param cs
     * @param resourceResolver
     * @param jcrNode
     * @throws RepositoryException
     * @throws PathNotFoundException
     * @throws ValueFormatException
     */
    private void getPageCategory(ResourceResolver resourceResolver, Node jcrNode)
            throws RepositoryException {
        if (jcrNode.hasProperty(CATEGORY)) {
            if (jcrNode.getProperty(CATEGORY).isMultiple()) {
                Value[] categoryArray = jcrNode.getProperty(CATEGORY).getValues();
                List<Value> categoryList = Arrays.asList(categoryArray);
                for (Value value : categoryList) {
                    String tagID = value.getString();
                    getTagList(resourceResolver, tagID);
                }
            } else {
                String cat = jcrNode.getProperty(CATEGORY).getValue().getString();
                getTagList(resourceResolver, cat);
            }
        }
    }

    /**
     * @param resourceResolver
     * @param tagID
     */
    private void getTagList(ResourceResolver resourceResolver, String tagID) {
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag tag = tagManager.resolve(tagID);
        if (tag != null && categoryMap.get(tag.getTagID()) == null) {
            categoryMap.put(tagID, tag.getTitle());
        }
    }

    /**
     * @param cs
     * @param jcrNode
     * @throws RepositoryException
     * @throws PathNotFoundException
     * @throws ValueFormatException
     */
    private void getPageImage(ContentSchema cs,ResourceResolver resourceResolver, Node jcrNode)
            throws RepositoryException {
        if (jcrNode.hasNode("image")) {
            Node imageNode = jcrNode.getNode("image");
            String image = imageNode.hasProperty("fileReference")
                    ? imageNode.getProperty("fileReference").getValue().getString()
                    : StringUtils.EMPTY;
            String alttxt=assetMetadataHelper.getMetadata(resourceResolver, image);
            cs.setImage(image);
            cs.setAlt(alttxt);
        } else {
            cs.setImage(StringUtils.EMPTY);
            cs.setAlt(StringUtils.EMPTY);
        }
    }

    private String descriptionEllipsis(String text) {
        if (text.length() <= 114) {
            return text;
        }
        int end = text.lastIndexOf(' ', 114 - 1);
        return text.substring(0, end) + "...";
    }

    private List<String> getArticlePages(String parentPagePath, String resultCategory, String contentType,
            Session session,QueryBuilder queryBuilder) {
        List<String> childPagesArrList = new ArrayList<>();
        if(queryBuilder!=null) {
            Query query = queryBuilder.createQuery(PredicateGroup.create(fetchPages(parentPagePath, resultCategory, contentType)), session);
            SearchResult result = query.getResult();
            Iterator<Resource> hits = result.getResources();
            while(hits.hasNext()) {
                childPagesArrList.add(hits.next().getPath());
            }
        }
        return childPagesArrList;
    }

    /**
     * @param path
     * @param resultCategory
     * @param contentType
     * @return
     */
    private Map<String, String> fetchPages(String path, String resultCategory, String contentType) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", path);
        queryMap.put("1_property", JcrConstants.JCR_PRIMARYTYPE);
        queryMap.put("1_property.value", NameConstants.NT_PAGE);
        if (null != resultCategory) {
            queryMap.put("2_property", "jcr:content/category");
            queryMap.put("2_property.value", resultCategory);
        }
        if (null != contentType) {
            queryMap.put("3_property", "jcr:content/contentType");
            queryMap.put("3_property.value", contentType);
        }
        queryMap.put("orderby", "@jcr:content/articlePublishDate");
        queryMap.put("orderby.sort", "desc");
        queryMap.put("p.limit", Long.toString(-1));
        return queryMap;
    }
}