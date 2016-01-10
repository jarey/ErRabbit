package org.mintcode.errabbit.core.eventstream.stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mintcode.errabbit.core.eventstream.event.EventChecker;
import org.mintcode.errabbit.core.eventstream.event.EventMapping;
import org.mintcode.errabbit.core.eventstream.event.action.EventAction;
import org.mintcode.errabbit.model.Log;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by soleaf on 10/18/15.
 */
public abstract class AbstractEventStream implements EventStream {

    protected Set<EventChecker> eventCheckers = new HashSet<>();
    protected Boolean active = false;
    protected ThreadPoolTaskExecutor jobExecutor;

    private Logger logger = LogManager.getLogger(getClass());

    @Override
    public void registerEventChecker(EventChecker eventChecker) {
        logger.trace("Register eventChecker : " + eventChecker);
        eventCheckers.add(eventChecker);
    }

    @Override
    public void removeEventChecker(EventChecker eventChecker) {
        eventCheckers.remove(eventChecker);
    }

    public void input(Log log) {
        if (!active){
            return;
        }
        for (EventChecker checker : eventCheckers) {
            checker.check(log);
        }
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Boolean isActive() {
        return active;
    }

    @Override
    public void setJobExecutor(ThreadPoolTaskExecutor executor) {
        this.jobExecutor = executor;
    }

    @Override
    public void runAction(final EventMapping eventMapping, final EventAction action, final Log log) {

        // TODO: Add event action logging advice.

        jobExecutor.execute(new Runnable() {
            @Override
            public void run() {
                action.run(eventMapping.getCondition(), log);
            }
        });
    }
}
