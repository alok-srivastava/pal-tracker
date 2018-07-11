package test.pivotal.pal.trackerapi;

import com.jayway.jsonpath.DocumentContext;
import io.pivotal.pal.tracker.PalTrackerApplication;
import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryHealthIndicator;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PalTrackerApplication.class, webEnvironment = RANDOM_PORT)
public class HealthApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void healthTest() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/health", String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext healthJson = parse(response.getBody());

        assertThat(healthJson.read("$.status", String.class)).isEqualTo("UP");
        assertThat(healthJson.read("$.db.status", String.class)).isEqualTo("UP");
        assertThat(healthJson.read("$.diskSpace.status", String.class)).isEqualTo("UP");
    }

    @Test
    public void testHealthUp(){
        Health.Builder builder = new Health.Builder();
        TimeEntryRepository timeEntryRepository = mock(TimeEntryRepository.class);
        List<TimeEntry>  list = new ArrayList<TimeEntry>(5);
        TimeEntry timeEntry = new TimeEntry();
        for (int i=0; i<5;i++){list.add(timeEntry);}
        assertThat(list.size()).isEqualTo(5);
        when(timeEntryRepository.list()).thenReturn(list);

        TimeEntryHealthIndicator timeEntryHealthIndicator = new TimeEntryHealthIndicator(timeEntryRepository);
        assertThat(timeEntryHealthIndicator.health()).isEqualTo(builder.status(Status.DOWN).build());
        verify(timeEntryRepository).list();
    }

    @Test
    public void testHealthDown(){
        Health.Builder builder = new Health.Builder();
        TimeEntryRepository timeEntryRepository = mock(TimeEntryRepository.class);
        List<TimeEntry>  list = new ArrayList<TimeEntry>(1);
        assertThat(list.size()).isEqualTo(0);
        when(timeEntryRepository.list()).thenReturn(list);

        TimeEntryHealthIndicator timeEntryHealthIndicator = new TimeEntryHealthIndicator(timeEntryRepository);
        assertThat(timeEntryHealthIndicator.health()).isEqualTo(builder.status(Status.UP).build());
        verify(timeEntryRepository).list();
    }
}
