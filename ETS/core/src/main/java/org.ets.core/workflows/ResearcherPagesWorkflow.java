package org.ets.core.workflows;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.ets.core.services.ResearcherService;
import org.ets.core.utils.EtsResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = WorkflowProcess.class, property = {"process.label = XML to Researcher Page Workflow"})
public class ResearcherPagesWorkflow implements WorkflowProcess {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ResearcherPagesWorkflow.class);

    @Reference
    private ResearcherService researcherService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    /**
     * Overridden method which executes when the workflow is invoked
     */
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        log.info("Start of researcher workflow");
        try {
            WorkflowData workflowData = workItem.getWorkflowData();
            final String type = workflowData.getPayloadType();
            if (StringUtils.equals(type, "JCR_PATH")) {
                String assetPath= workflowData.getPayload().toString();
                ResourceResolver resourceResolver = EtsResourceUtil.getResourceResolver(resolverFactory);
                InputStream xmlAsset = researcherService.getXmlAsset(resourceResolver, assetPath);
                if(xmlAsset!=null) {
                    researcherService.convertXmlToPages(resourceResolver,xmlAsset);
                }else {
                    log.error("XML asset is null");
                }
            }
            log.info("End of researcher workflow");  
        } catch (Exception e) {
            log.error("Exception in Researcher Pages Workflow : {}",e.getMessage());
        }
    }

}