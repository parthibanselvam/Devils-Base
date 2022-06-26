package org.ets.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.ets.core.bean.ResearcherSchema;
import org.ets.core.config.ResearcherFeedConfiguration;
import org.ets.core.services.ResearcherFeedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component(service = ResearcherFeedService.class, immediate = true)
@Designate(ocd = ResearcherFeedConfiguration.class)
public class ResearcherFeedServiceImpl implements ResearcherFeedService {

    private static final Logger log = LoggerFactory.getLogger(ResearcherFeedServiceImpl.class);

    private static final String MIME_TYPE = "application/json";

    /**
     * Instance of the OSGi configuration class
     */
    private ResearcherFeedConfiguration configuration;

    @Activate
    protected void activate(ResearcherFeedConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void createJsonFeed(ResourceResolver resourceResolver, QueryBuilder queryBuilder) {
        try {
            log.info("\n Researcher Scheduler has triggered the Service Class");
            AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);
            List<ResearcherSchema> schemaCollection = getResearcherPages(session, resourceResolver, pageManager, queryBuilder);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonFeed = mapper.writeValueAsString(schemaCollection);
            log.info("\nJSON Feed with Jackson: {}",jsonFeed);
            /*Gson gson = new GsonBuilder().create();
            String jsonFeed = gson.toJson(schemaCollection);
            log.info("\nJSON Feed: {}",jsonFeed);*/
            InputStream jsonInputStream = new ByteArrayInputStream(jsonFeed.getBytes(StandardCharsets.UTF_8));
            String jsonStoragePath = configuration.researcher_json_storage_path();
            String jsonFilename = configuration.researcher_json_filename();
            assetManager.createAsset(jsonStoragePath.concat("/".concat(jsonFilename)), jsonInputStream, MIME_TYPE,true);
        }
        catch(Exception e) {
            log.error("EXCEPTION CAUGHT IN RESEARCHER FEED SERVICE CLASS: *{}* ",e.getMessage());
        }
    }

    private List<ResearcherSchema> getResearcherPages(Session session, ResourceResolver resourceResolver,PageManager pageManager, QueryBuilder queryBuilder) throws NullPointerException, RepositoryException {
        String parentPagePath = configuration.researcher_parent_path();
        List<ResearcherSchema> researcherPageSchema = new ArrayList<>();
        if(queryBuilder!=null) {
            Query query = queryBuilder.createQuery(PredicateGroup.create(fetchPages(parentPagePath)), session);
            SearchResult result = query.getResult();
            Iterator<Resource> hits = result.getResources();
            while(hits.hasNext()) {
                String childPagePathString = hits.next().getPath();
                if(!childPagePathString.contains(parentPagePath + "/patent")) {
                    ResearcherSchema rs = new ResearcherSchema();
                    Node componentNode = resourceResolver.getResource(childPagePathString).adaptTo(Node.class);
                    if (componentNode != null) {
                        getResearcherSchemaData(rs,componentNode,pageManager);
                        researcherPageSchema.add(rs);
                    }
                }
            }
        }
        return new ArrayList<>(researcherPageSchema);
    }

    private void getResearcherSchemaData(ResearcherSchema rs,Node componentNode,PageManager pageManager) throws RepositoryException {
        rs.setId(pageManager.getContainingPage(componentNode.getPath()).getName());
        if(componentNode.hasProperty("title")){
            rs.setTitle(componentNode.getProperty("title").getValue().getString());
        }
        if(componentNode.hasProperty("url")){
            rs.setUrl(componentNode.getProperty("url").getValue().getString());
        }
        if(componentNode.hasProperty("publicationType")){
            rs.setPublicationType(componentNode.getProperty("publicationType").getValue().getString());
        }
        if(componentNode.hasProperty("authors")) {
            List<String> authorsList = getStringArrayValues(componentNode.getProperty("authors"));
            rs.setAuthors(authorsList);
        }
        if(componentNode.hasProperty("year")){
            rs.setYear(componentNode.getProperty("year").getValue().getString());
        }
        if(componentNode.hasProperty("source")) {
            List<String> sourceList = getStringArrayValues(componentNode.getProperty("source"));
            rs.setSource(sourceList);
        }
        if(componentNode.hasProperty("keywords")) {
            List<String> keywordsList = getStringArrayValues(componentNode.getProperty("keywords"));
            rs.setKeywords(keywordsList);
        }
        rs.setPublicationAbstract(".");
        rs.setRefURL("null");
        rs.setPdfURL("null");
        if(componentNode.hasProperty("reportNumber")) {
            rs.setReportNumber(getStringArrayValues(componentNode.getProperty("reportNumber")));
        } else {
            rs.setReportNumber(Collections.singletonList("NA"));
        }
        rs.setPatentNumber("null");
        if(componentNode.hasProperty("numPages")){
            rs.setNumPages(componentNode.getProperty("numPages").getValue().getString());
        }
        rs.setPatentFamily("null");
        if(componentNode.hasProperty("bodyDescription")){
            rs.set_text_(componentNode.getProperty("bodyDescription").getValue().getString());
        }
    }

    private List<String> getStringArrayValues(Property stringArrayProperty) throws RepositoryException {
        List<String> propertyAsList = new ArrayList<>();
        if(stringArrayProperty.isMultiple()){
            Value[] arrayValues = stringArrayProperty.getValues();
            for (Value value : arrayValues) {
                propertyAsList.add(value.getString());
            }
        }
        return new ArrayList<>(propertyAsList);
    }

    private Map<String, String> fetchPages(String parentPagePath) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", parentPagePath);
        queryMap.put("1_property", JcrConstants.JCR_PRIMARYTYPE);
        queryMap.put("1_property.value", JcrConstants.NT_UNSTRUCTURED);
        queryMap.put("group.p.or", "true");
        queryMap.put("group.property", "sling:resourceType");
        queryMap.put("group.property.1_value", "ets/components/researcher-patent");
        queryMap.put("group.property.2_value", "ets/components/researcher-info-details");
        queryMap.put("p.limit", Long.toString(-1));
        return queryMap;
    }

}