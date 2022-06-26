package org.ets.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Research Configuration", 
        description = "This configuration reads the values for researcher page batch job")

public @interface ResearcherConfiguration {

    @AttributeDefinition(
            name = "Researcher initial page path")
    public String researcher_parent_page_path();

}