package org.ets.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Research Feed Configuration",
        description = "This configuration reads the values for creating JSON feed")

public @interface ResearcherFeedConfiguration {

    @AttributeDefinition(
            name = "Researcher Initial Page Path")
    public String researcher_parent_path();

    @AttributeDefinition(
            name = "Researcher Json Storage Path")
    public String researcher_json_storage_path();

    @AttributeDefinition(
            name = "Researcher Json Filename With Extension")
    public String researcher_json_filename();

}