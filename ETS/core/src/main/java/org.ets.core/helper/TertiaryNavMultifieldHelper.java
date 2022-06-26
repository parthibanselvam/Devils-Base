package org.ets.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class TertiaryNavMultifieldHelper {

    private String tertiaryAriaLabel;
    private String tabName2;
    private String tabLink2;
    private String newTab2;

    public TertiaryNavMultifieldHelper(Resource resource) {
        if (StringUtils.isNotBlank(resource.getValueMap().get("tabName2", String.class))) {
            this.tabName2 = resource.getValueMap().get("tabName2", String.class);
        }
        if (StringUtils.isNotBlank(resource.getValueMap().get("tabLink2", String.class))) {
            this.tabLink2 = resource.getValueMap().get("tabLink2", String.class);
        }
        if (StringUtils.isNotBlank(resource.getValueMap().get("newTab2", String.class))) {
            this.newTab2 = resource.getValueMap().get("newTab2", String.class);
        }
        if(StringUtils.isNotBlank(resource.getValueMap().get("tertiaryAriaLabel", String.class))) {
            this.tertiaryAriaLabel=resource.getValueMap().get("tertiaryAriaLabel",String.class);
        }
    }

    public String getTabName2() {
        return tabName2;
    }

    public String getTabLink2() {
        return tabLink2;
    }

    public String getNewTab2() {
        return newTab2;
    }

    public String getTertiaryAriaLabel() { return tertiaryAriaLabel; }

}