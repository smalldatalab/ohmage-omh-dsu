package org.openmhealth.dsu.domain.ohmage.survey.prompt;

import java.util.List;
import java.util.Map;

import org.openmhealth.dsu.domain.ohmage.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.ohmage.survey.Media;
import org.openmhealth.dsu.domain.ohmage.survey.condition.Condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A prompt for the user to make one choice among a list of choices.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class SingleChoicePrompt<ChoiceType>
    extends ChoicePrompt<ChoiceType, ChoiceType> {

    /**
     * Creates a new single-choice prompt.
     *
     * @param surveyItemId
     *        The survey-unique identifier for this prompt.
     *
     * @param condition
     *        The condition on whether or not to show this prompt.
     *
     * @param displayType
     *        The display type to use to visualize the prompt.
     *
     * @param text
     *        The text to display to the user.
     *
     * @param displayLabel
     *        The text to use as a short name in visualizations.
     *
     * @param skippable
     *        Whether or not this prompt may be skipped.
     *
     * @param defaultResponse
     *        The default response for this prompt or null if a default is not
     *        allowed.
     *
     * @param choices
     *        The list of choices.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public SingleChoicePrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE)
            final ChoiceType defaultResponse,
        @JsonProperty(JSON_KEY_CHOICES)
            final List<? extends Choice<? extends ChoiceType>> choiceList)
        throws InvalidArgumentException {

        super(
            surveyItemId,
            condition,
            displayType,
            text,
            displayLabel,
            skippable,
            defaultResponse,
                choiceList);

        if((defaultResponse != null) && (getChoice(defaultResponse) == null)) {
            throw
                new InvalidArgumentException(
                    "The default response '" +
                        defaultResponse +
                        "' is unknown: " +
                        getSurveyItemId());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.openmhealth.dsu.domain.ohmage.survey.prompt.Prompt#validateResponse(java.lang.Object, java.util.Map)
     */
    @Override
    public ChoiceType validateResponse(
        final ChoiceType response,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        if(getChoice(response) == null) {
            throw
                new InvalidArgumentException(
                    "The response value '" +
                        response +
                        "' is unknown: " +
                        getSurveyItemId());
        }

        return response;
    }
}