package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private final int HEALTH_IS_GOOD = 5;
    TimeEntryRepository timeEntryRepository;

    public TimeEntryHealthIndicator( TimeEntryRepository timeEntryRepository){
        this.timeEntryRepository = timeEntryRepository;

    }
    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        if (timeEntryRepository.list().size() < HEALTH_IS_GOOD) {
            return builder.status(Status.UP).build();
        }
        else {
            return builder.status(Status.DOWN).build();

        }
    }
}
