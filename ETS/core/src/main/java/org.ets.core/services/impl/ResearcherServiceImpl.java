package org.ets.core.services.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.ets.core.bean.ETSAuthorSchema;
import org.ets.core.config.ResearcherConfiguration;
import org.ets.core.helper.PagePropertyHelper;
import org.ets.core.services.ResearcherService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;


/**
 *   Implementation class of HttpService interface and this class reads
 *   values from the OSGi configuration as well
 */
@Component(service = ResearcherService.class, immediate = true)
@Designate(ocd = ResearcherConfiguration.class)
public class ResearcherServiceImpl implements ResearcherService {

    private static final String RECORD = "record";

    private static final String RESEARCHER_TEMPLATE_PATH = "/conf/ets/settings/wcm/templates/researcher-template";

    private static final String ETS_AUTHORS = "ETSAuthors";

    /* Logger*/
    private static final Logger log = LoggerFactory.getLogger(ResearcherServiceImpl.class);

    /**
     * Instance of the OSGi configuration class
     */
    private ResearcherConfiguration configuration;

    PagePropertyHelper pagePropertyHelper = new PagePropertyHelper();

    @Activate
    protected void activate(ResearcherConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public InputStream getXmlAsset(ResourceResolver resourceResolver, String assetPath) {
        AssetManager assetManager=resourceResolver.adaptTo(AssetManager.class);
        InputStream stream=null;
        if(assetManager!=null && assetPath!=null) {
                Asset asset=assetManager.getAsset(assetPath);
                if(asset!=null) {
                    Rendition originalRendition=asset.getRendition("original");
                    stream = originalRendition.getStream();
                }
        }
        return stream;
    }

    @Override
    public String convertXmlToPages(ResourceResolver resourceResolver, InputStream inputStream) {
        PageManager pageManager=resourceResolver.adaptTo(PageManager.class);
        Session session=resourceResolver.adaptTo(Session.class);
        String status="";
        try {
            log.info("Start of researcher service");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            Node templateItem =doc.getElementsByTagName("template").item(0);
            if(templateItem.getNodeType() == Node.ELEMENT_NODE) {
                Element templateElement = (Element) templateItem;
                log.debug("Template id {}",templateElement.getAttribute("id"));
                String attribute = templateElement.getAttribute("id");
                if(attribute.equals(ETS_AUTHORS)) {
                    parseAuthor(doc,attribute, session, pageManager);
                }else {
                    parsePatent(doc,attribute,session, pageManager);
                }
            }
            log.info("End of researcher service");
        }catch(Exception e) {
            log.error("Exception in convertXmlToPages {}",e.getMessage());
        }
        finally {
            resourceResolver.close();
        }
        return status;
    }

    private Page getResearcherPage(String pageAttrPath,String pageName, String title, PageManager pageManager, String researcherAK) throws WCMException {
        Page parentPage=pageManager.getPage(pageAttrPath);
        if(parentPage==null) {
            String[] pathArray = pageAttrPath.split("/");
            String currentPageName=pathArray[pathArray.length-1];
            String parentPath=String.join("/", ArrayUtils.remove(pathArray,pathArray.length-1));
            getResearcherPage(parentPath,currentPageName,currentPageName,pageManager,researcherAK);
            return pageManager.create(pageAttrPath,pageName,RESEARCHER_TEMPLATE_PATH,title,true);
        }
        return pageManager.create(pageAttrPath,pageName,RESEARCHER_TEMPLATE_PATH,title,true);
    }

    private void parsePatent(Document doc, String attribute,Session session, PageManager pageManager) {
        try {
            NodeList nodeList = doc.getElementsByTagName(RECORD);
            for (int x = 0; x < (nodeList.getLength()); x++) {
                ETSAuthorSchema etsAuthorSchema = new ETSAuthorSchema();
                List<String> authorList = new ArrayList<>();
                List<String> subjectList = new ArrayList<>();
                Node item = nodeList.item(x);
                NodeList childNodes = item.getChildNodes();
                Map<String, String> fieldMap = new HashMap<>();
                for (int j = 0; j < (childNodes.getLength()); j++) {
                    Node fieldItem = childNodes.item(j);
                    if (fieldItem.getNodeType() == Node.ELEMENT_NODE) {
                        Element fieldElement = (Element) fieldItem;
                        if (fieldElement.getAttribute("id").equals("PatSubLK")) {
                            subjectList.add(fieldElement.getTextContent());
                        } else if(fieldElement.getAttribute("id").equals("patissdate")) {
                            String dateString=fieldElement.getTextContent();
                            Date dateObj=new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
                            SimpleDateFormat yearPattern=new SimpleDateFormat("yyyy");
                            SimpleDateFormat datePattern=new SimpleDateFormat("MMM dd, yyyy");
                            fieldMap.put("patYear", yearPattern.format(dateObj));
                            fieldMap.put(fieldElement.getAttribute("id"), datePattern.format(dateObj));
                        }
                        else if (fieldElement.getAttribute("id").equals("inventorLK")) {
                            authorList.add(fieldElement.getTextContent());
                        } else {
                            fieldMap.put(fieldElement.getAttribute("id"), fieldElement.getTextContent());
                        }
                        etsAuthorSchema.setAuthorList(authorList);
                        etsAuthorSchema.setPrimaryFields(fieldMap);
                        etsAuthorSchema.setSubjectList(subjectList);
                    }
                }
                String predefinedRootPath = configuration.researcher_parent_page_path();
                String pagePath = String.join("/", predefinedRootPath,"patent",fieldMap.get("patYear"));
                String pageName = fieldMap.get("patAK");
                Page researcherPage=pageManager.getPage(pagePath+"/"+pageName);
                if(researcherPage==null) {
                    researcherPage = getResearcherPage(pagePath, pageName, fieldMap.get("pattitle"), pageManager,fieldMap.get("patAK"));
                }
                pagePropertyHelper.setComponentProperties(researcherPage, session, etsAuthorSchema, attribute);
            }
        } catch (Exception e) {
            log.error("Exception in parsePatent {}", e.getMessage());
        }
    }
	private void parseAuthor(Document doc, String attribute,Session session, PageManager pageManager) {
        try {
            String predefinedRootPath = configuration.researcher_parent_page_path();
            NodeList nodeList = doc.getElementsByTagName(RECORD);
            for (int i = 0; i < (nodeList.getLength()); i++) {
                ETSAuthorSchema etsAuthorSchema = new ETSAuthorSchema();
                Map<String,List<String>> multifieldValueList=new HashMap<>();
                multifieldValueList.clear();
                Node item = nodeList.item(i);
                NodeList childNodes = item.getChildNodes();
                Map<String, String> fieldMap = new HashMap<>();
                for (int j = 0; j < (childNodes.getLength()); j++) {
                    Node fieldItem = childNodes.item(j);
                    if (fieldItem.getNodeType() == Node.ELEMENT_NODE) {
                        Element fieldElement = (Element) fieldItem;
                        String attributeID = fieldElement.getAttribute("id");
                        if (attributeID.equals("Author") || attributeID.equals("Reportno2") || attributeID.equals("Subject") || attributeID.equals("Citation")) {
                            List<String> tempList= multifieldValueList.get(attributeID);
                            if(tempList==null) {
                                tempList=new ArrayList<>();
                                tempList.add(fieldElement.getTextContent());
                                multifieldValueList.put(attributeID, tempList);
                            }else {
                                String value=fieldElement.getTextContent();
                                tempList.add(value);
                                multifieldValueList.put(attributeID, tempList);
                            }
                        }else if (fieldElement.getAttribute("id").equals("DateGt")) {
                            Node dateRecord = fieldElement.getElementsByTagName(RECORD).item(0);
                            NodeList dateChildNodes = dateRecord.getChildNodes();
                            for (int temp = 0; temp < dateChildNodes.getLength(); temp++) {
                                Node dateNode = dateChildNodes.item(temp);
                                if (dateNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element dateElement = (Element) dateNode;
                                    fieldMap.put(dateElement.getAttribute("id"), dateElement.getTextContent());
                                }
                            }
                        } else {
                            fieldMap.put(attributeID, fieldElement.getTextContent());
                        }
                        etsAuthorSchema.setPrimaryFields(fieldMap);
                        etsAuthorSchema.setMultiValueFields(multifieldValueList);
                    }
                }
                if(fieldMap.get("DocumentTyp")!=null) {
                    String pagePath = String.join("/", predefinedRootPath,fieldMap.get("DocumentTyp").toLowerCase(),fieldMap.get("DateYear")).replace(" ", "-");
                    String pageName = fieldMap.get("EAccessKey").replace(" ", "-");
                    Page researcherPage=pageManager.getPage(pagePath+"/"+pageName);
                    if(researcherPage==null) {
                        researcherPage = getResearcherPage(pagePath, pageName, fieldMap.get("Title"), pageManager,fieldMap.get("EAccessKey"));
                    }
                    pagePropertyHelper.setComponentProperties(researcherPage, session, etsAuthorSchema, attribute);
                }
            }
        } catch (Exception e) {
            log.error("Exception in parseAuthor {}", e.getMessage());
        }
    }
}