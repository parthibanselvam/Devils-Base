package org.ets.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class SecondaryNavMultifieldHelper {

    private String tabName1;
    private String tabLink1;
    private String newTab1;
    private String secondaryAriaLabel;
    private boolean isActive=false;
    private boolean dynamicLink=false;
    private List<TertiaryNavMultifieldHelper> tertiaryList;

    public SecondaryNavMultifieldHelper(Resource resource,String currentPagePath){
            if(StringUtils.isNotBlank(resource.getValueMap().get("tabName1", String.class))) {
                this.tabName1 = resource.getValueMap().get("tabName1", String.class);
            }
            if(StringUtils.isNotBlank(resource.getValueMap().get("tabLink1", String.class))) {
                this.tabLink1=resource.getValueMap().get("tabLink1",String.class);
                if(currentPagePath.equals(tabLink1)){
                    isActive = true;
                }
            }
            if(StringUtils.isNotBlank(resource.getValueMap().get("newTab1", String.class))) {
                this.newTab1=resource.getValueMap().get("newTab1",String.class);
            }

            if(StringUtils.isNotBlank(resource.getValueMap().get("dynamicLink", String.class))) {
                this.dynamicLink=resource.getValueMap().get("dynamicLink",boolean.class);
            }

            if(StringUtils.isNotBlank(resource.getValueMap().get("dynamicPath", String.class))) {
                String dynamicPath=resource.getValueMap().get("dynamicPath",String.class);
                if(currentPagePath.contains(dynamicPath)) {
                    this.isActive=true;
                }
            }

            if(StringUtils.isNotBlank(resource.getValueMap().get("secondaryAriaLabel", String.class))) {
                this.secondaryAriaLabel=resource.getValueMap().get("secondaryAriaLabel",String.class);
            }
    }

    public String getTabName1() {
        return tabName1;
    }

    public String getTabLink1() {
        return tabLink1;
    }

    public String getNewTab1() {
        return newTab1;
    }

    public String getSecondaryAriaLabel() {
        return secondaryAriaLabel;
    }

    public void setTertiaryList(List<TertiaryNavMultifieldHelper> tertiaryList) {
        this.tertiaryList =  new ArrayList<>(tertiaryList);
    }

    public List<TertiaryNavMultifieldHelper> getTertiaryList() { return new ArrayList<>(tertiaryList); }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isDynamicLink() {
        return dynamicLink;
    }
}