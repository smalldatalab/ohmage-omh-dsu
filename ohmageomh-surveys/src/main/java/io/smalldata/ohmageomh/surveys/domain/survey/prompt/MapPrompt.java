package io.smalldata.ohmageomh.surveys.domain.survey.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import name.jenkins.paul.john.concordia.schema.Schema;
import io.smalldata.ohmageomh.surveys.domain.Coordinates;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.Media;
import io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition;

import java.util.Map;

/**
 * Created by changun on 12/12/15.
 */
public class MapPrompt extends Prompt<Coordinates> {

    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "map_prompt";



    /**
     * Creates a new prompt.
     *
     * @param surveyItemId    The survey-unique identifier for this prompt.
     * @param condition       The condition on whether or not to show this prompt.
     * @param displayType     The display type to use to visualize the prompt.
     * @param text            The text to display to the user.
     * @param displayLabel    The text to use as a short name in visualizations.
     * @param skippable       Whether or not this prompt may be skipped.
     * @param defaultResponse The default response for this prompt or null if a default is not
     *                        allowed.
     * @throws InvalidArgumentException A parameter was invalid.
     */
    @JsonCreator
    public MapPrompt(
            @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
            @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
            @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
            @JsonProperty(JSON_KEY_TEXT) final String text,
            @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
            @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
            @JsonProperty(JSON_KEY_DEFAULT_RESPONSE)
            final Coordinates defaultResponse)
            throws InvalidArgumentException {

        super(
                surveyItemId,
                condition,
                displayType,
                text,
                displayLabel,
                skippable,
                defaultResponse);
        if(!(DisplayType.GOOGLE_MAP.equals(displayType))) {

            throw
                    new InvalidArgumentException(
                            "The display type '" +
                                    displayType.toString() +
                                    "' is not valid for the prompt, which must be '" +
                                    DisplayType.GOOGLE_MAP.toString() +
                                    getSurveyItemId());
        }
    }

    @Override
    public Coordinates validateResponse(Coordinates response, Map<String, Media> media) throws InvalidArgumentException {
        return null;
    }

    @Override
    public Schema getResponseSchema() throws IllegalStateException {
        return null;
    }
}
