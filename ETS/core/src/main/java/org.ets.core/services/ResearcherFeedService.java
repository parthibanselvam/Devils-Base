package org.ets.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.search.QueryBuilder;

public interface ResearcherFeedService {
    public void createJsonFeed(ResourceResolver resourceResolver,QueryBuilder queryBuilder);
}