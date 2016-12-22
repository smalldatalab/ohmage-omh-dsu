package io.smalldata.ohmageomh.dpu.reader;

import com.fasterxml.jackson.databind.JsonNode;
import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.data.service.EndUserService;
import io.smalldata.ohmageomh.dpu.service.OmhShimService;
import io.smalldata.ohmageomh.dpu.util.ItemDTO;
import io.smalldata.ohmageomh.dpu.util.JobParamsDTO;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

/**
 * Generates a list of users who have authorized the Fitbit integration. Each
 * read fetches raw data from the Fitbit HTTP API and appends it to the ItemDTO.
 *
 * @author Jared Sieling.
 */
@Component
@StepScope
public class FitbitUserReader implements ItemReader<ItemDTO> {

    @Autowired
    private EndUserService endUserService;

    @Autowired
    private OmhShimService omhShimService;

    @Value("#{jobParameters}")
    private Map<String, JobParameter> jobParameters;

    private List<ItemDTO> itemDTOs = new ArrayList<>();
    private Iterator<ItemDTO> iterator;
    private JobParamsDTO jobParamsDTO;


    @PostConstruct
    void getUsers() {
        for(EndUser user : endUserService.findAuthorizedUsers("fitbit")) {
           itemDTOs.add(new ItemDTO(user));
        }
        iterator = itemDTOs.iterator();

        // Put in DTO for helper methods.
        jobParamsDTO = new JobParamsDTO(jobParameters);
    }

    @Override
    public ItemDTO read() throws Exception {
        if (iterator.hasNext()) {
            ItemDTO item = iterator.next();

            if(jobParamsDTO.hasDates()){
                item.setStartDate(jobParamsDTO.getStartDate());
                item.setEndDate(jobParamsDTO.getEndDate());
            } else {
                // Set date range for past calendar week, ending Sunday
                LocalDate now = LocalDate.now();
                item.setEndDate(now.minusDays(now.getDayOfWeek().getValue()));
                item.setStartDate(item.getEndDate().minusDays(6));
            }


            // Fetch data
            JsonNode responseRoot = omhShimService.getDataAsJsonNode(item.getUser(), "fitbit", "steps", true, item.getStartDate(), item.getEndDate());
            JsonNode responseBody = responseRoot.get("body");

            item.setExtra("stepsNode", responseBody);

            return item;
        } else {
            return null;
        }
    }
}
