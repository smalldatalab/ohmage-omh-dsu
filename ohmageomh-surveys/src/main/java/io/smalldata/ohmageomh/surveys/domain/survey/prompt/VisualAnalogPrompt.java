package io.smalldata.ohmageomh.surveys.domain.survey.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition;

/**
 * Created by changun on 10/21/15.
 */
public class VisualAnalogPrompt extends NumberPrompt {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "vas_prompt";

    /**
     * The JSON key for the minimum value.
     */
    public static final String JSON_KEY_MIN_LABEL = "min_label";
    /**
     * The JSON key for the maximum value.
     */
    public static final String JSON_KEY_MAX_LABEL = "max_label";


    /**
     * The minimum allowed value for a response.
     */
    @JsonProperty(JSON_KEY_MIN_LABEL)
    private final String minLabel;
    /**
     * The maximum allowed value for a response.
     */
    @JsonProperty(JSON_KEY_MAX_LABEL)
    private final String maxLabel;
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
     * @throws io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException A parameter was invalid.
     */
    @JsonCreator
    public VisualAnalogPrompt(
            @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
            @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
            @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
            @JsonProperty(JSON_KEY_TEXT) final String text,
            @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
            @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
            @JsonProperty(JSON_KEY_DEFAULT_RESPONSE) final Number defaultResponse,
            @JsonProperty(JSON_KEY_MIN_LABEL) final String minLabel,
            @JsonProperty(JSON_KEY_MAX_LABEL) final String maxLabel)
            throws InvalidArgumentException {

        super(
                surveyItemId,
                condition,
                displayType,
                text,
                displayLabel,
                skippable,
                defaultResponse,
                0.0, 1.0, false);

        if(!
                (
                                DisplayType.SLIDER.equals(displayType))) {

            throw
                    new InvalidArgumentException(
                            "The display type '" +
                                    displayType.toString() +
                                    "' is not valid for the prompt, which must be '" +
                                    DisplayType.SLIDER.toString() +
                                    getSurveyItemId());
        }

        this.minLabel = minLabel;
        this.maxLabel = maxLabel;
    }

}
