package org.ets.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

public class AssetMetadataHelper {

    public String getMetadata(ResourceResolver resolver,String imgPath) {
        String alttxt=StringUtils.EMPTY;
        if (imgPath != null) {
            Resource imageResource = resolver.getResource(imgPath);
            if (imageResource != null) {
                Asset imageAsset = imageResource.adaptTo(Asset.class);
                alttxt = imageAsset.getMetadataValue(DamConstants.DC_DESCRIPTION);
                if(alttxt == null) {
                    alttxt = imageAsset.getMetadataValue(DamConstants.DC_TITLE);
                    if(alttxt == null) {
                        alttxt = StringUtils.EMPTY;
                    }
                }
            }
        }
        return alttxt;
    }
}