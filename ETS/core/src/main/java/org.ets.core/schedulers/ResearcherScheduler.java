package org.ets.core.schedulers;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.ets.core.config.CronSchedulerConfiguration;
import org.ets.core.services.ResearcherFeedService;
import org.ets.core.utils.EtsResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = CronSchedulerConfiguration.class)

public class ResearcherScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ResearcherScheduler.class);
    private int schedulerId;

    @Reference
    QueryBuilder queryBuilder;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    private ResearcherFeedService researcherFeedService;

    @Reference
    private Scheduler scheduler;

    @Activate
    protected void activate(CronSchedulerConfiguration config) {
        schedulerId = config.schedulerName().hashCode();
        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(CronSchedulerConfiguration config) {
        removeScheduler();
    }

    protected void removeScheduler() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    protected void addScheduler(CronSchedulerConfiguration config) {
        ScheduleOptions scheduleOptions = scheduler.EXPR(config.cron_expression());
        scheduleOptions.name(String.valueOf(schedulerId));
        scheduler.schedule(this, scheduleOptions);
        ScheduleOptions scheduleOptionsNow = scheduler.NOW();
        scheduler.schedule(this, scheduleOptionsNow);
        log.info("\n ====> SOLR FEED SCHEDULER ADDED");
    }
    @Override
    public void run() {
        try {
            ResourceResolver resourceResolver = EtsResourceUtil.getResourceResolver(resolverFactory);
            researcherFeedService.createJsonFeed(resourceResolver,queryBuilder);
        } catch(LoginException e) {
            log.error("Exception in Researcher Scheduler {}",e.getMessage());
        }
    }

}