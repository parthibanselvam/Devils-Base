package org.ets.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.ets.core.helper.SecondaryNavMultifieldHelper;
import org.ets.core.helper.TertiaryNavMultifieldHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NestedMultifieldListModel {

    @Inject
    Resource resource;

    @ScriptVariable
    private Page currentPage;

    private static final Logger log = LoggerFactory.getLogger(NestedMultifieldListModel.class);
    private List<SecondaryNavMultifieldHelper> secondaryList = new ArrayList<>();
    private List<TertiaryNavMultifieldHelper> tertiaryList = new ArrayList<>();

    @PostConstruct
    public void getNavDetailsWithNestedMultifield() {
            try {
            Resource secondaryListResource = resource.getChild("secondaryList");
            String currentPagePath=currentPage.getPath();
            int flag = 0;
            if(secondaryListResource!=null){
                for (Resource secondaryListItem : secondaryListResource.getChildren()) {
                    SecondaryNavMultifieldHelper secondaryNavMultifieldHelper = new SecondaryNavMultifieldHelper(secondaryListItem,currentPagePath);
                    if(secondaryListItem.hasChildren()){
                        List<TertiaryNavMultifieldHelper> tempTertiaryList = new ArrayList<>();
                        Resource tertiaryListResource = secondaryListItem.getChild("tertiaryList");
                        for(Resource tertiaryListItem : tertiaryListResource.getChildren()){
                            TertiaryNavMultifieldHelper tertiaryNavMultifieldHelper = new TertiaryNavMultifieldHelper(tertiaryListItem);
                            tempTertiaryList.add(tertiaryNavMultifieldHelper);
                            if(tertiaryNavMultifieldHelper.getTabLink2().equals(currentPagePath) || secondaryNavMultifieldHelper.getTabLink1().equals(currentPagePath)){
                                flag = 1;
                            }
                        }
                        if(flag == 1){
                            tertiaryList.addAll(tempTertiaryList);
                            flag = 0;
                            secondaryNavMultifieldHelper.setActive(true);
                        }
                        secondaryNavMultifieldHelper.setTertiaryList(tempTertiaryList);
                    }
                    secondaryList.add(secondaryNavMultifieldHelper);
                }
            }
            }
            catch(SlingException e) {
                log.error("Sling exception in Secondary navigation {}",e.getMessage());
            }
    }

    public List<SecondaryNavMultifieldHelper> getSecondaryList() {
        return new ArrayList<>(secondaryList);
    }

    public List<TertiaryNavMultifieldHelper> getTertiaryList() {
        return new ArrayList<>(tertiaryList);
    }
}