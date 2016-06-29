package io.smalldata.ohmageomh.surveys.domain.survey.prompt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ArraySchema;
import name.jenkins.paul.john.concordia.schema.Schema;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import io.smalldata.ohmageomh.surveys.domain.survey.Media;
import io.smalldata.ohmageomh.surveys.domain.survey.condition.Condition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A prompt for the user to make any number of choices among a list of choices.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class MultiChoicePrompt<ChoiceType>
    extends ChoicePrompt<ChoiceType, Collection<? extends ChoiceType>> {

    /**
     * The JSON key for the minimum number of choices.
     */
    public static final String JSON_KEY_MIN_CHOICES = "min_choices";
    /**
     * The JSON key for the maximum number of choices.
     */
    public static final String JSON_KEY_MAX_CHOICES = "max_choices";

    /**
     * The minimum number of choices the user must select.
     */
    @JsonProperty(JSON_KEY_MIN_CHOICES)
    private final Integer minChoices;
    /**
     * The maximum number of choices the user may select.
     */
    @JsonProperty(JSON_KEY_MAX_CHOICES)
    private final Integer maxChoices;

    /**
     * Creates a new multi-choice prompt.
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
     * @param minChoices
     *        The minimum number of choices the user must select.
     *
     * @param maxChoices
     *        The maximum number of choices the user may select.
     *
     * @throws InvalidArgumentException
     *         A parameter was invalid.
     */
    @JsonCreator
    public MultiChoicePrompt(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_DISPLAY_TYPE) final DisplayType displayType,
        @JsonProperty(JSON_KEY_TEXT) final String text,
        @JsonProperty(JSON_KEY_DISPLAY_LABEL) final String displayLabel,
        @JsonProperty(JSON_KEY_SKIPPABLE) final boolean skippable,
        @JsonProperty(JSON_KEY_DEFAULT_RESPONSE)
            final Set<? extends ChoiceType> defaultResponse,
        @JsonProperty(JSON_KEY_CHOICES)
            final List<? extends Choice<? extends ChoiceType>> choiceList,
        @JsonProperty(JSON_KEY_MIN_CHOICES) final Integer minChoices,
        @JsonProperty(JSON_KEY_MAX_CHOICES) final Integer maxChoices)
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

        if(defaultResponse != null) {
            for(ChoiceType defaultResponseValue : defaultResponse) {
                if(
                    (defaultResponse != null) &&
                    (getChoice(defaultResponseValue) == null)) {

                    throw
                        new InvalidArgumentException(
                            "The default response '" +
                                defaultResponseValue +
                                "' is unknown: " +
                                getSurveyItemId());
                }
            }
        }

        this.minChoices = minChoices;
        this.maxChoices = maxChoices;
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.Respondable#getResponseSchema()
     */
    @Override
    public Schema getResponseSchema() throws IllegalStateException {
        try {
            return
                new ArraySchema(
                    getText(),
                    (skippable() || (getCondition() != null)),
                    getSurveyItemId(),
                    new StringSchema(null, false, null));
        }
        catch(ConcordiaException e) {
            throw
                new IllegalStateException(
                    "There was a problem building the multi-choice prompt's " +
                        "schema.",
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.smalldata.ohmageomh.surveys.domain.survey.prompt.Prompt#validateResponse(java.lang.Object, java.util.Map)
     */
    @Override
    public Collection<? extends ChoiceType> validateResponse(
        final Collection<? extends ChoiceType> response,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        for(ChoiceType responseValue : response) {
            if(getChoice(responseValue) == null) {
                throw
                    new InvalidArgumentException(
                        "The response value '" +
                            response +
                            "' is unknown: " +
                            getSurveyItemId());
            }
        }

        if((minChoices != null) && (response.size() < minChoices)) {
            throw
                new InvalidArgumentException(
                    "The user must select at least " +
                        minChoices +
                        " choices: " +
                        getSurveyItemId());
        }

        if((maxChoices != null) && (response.size() > maxChoices)) {
            throw
                new InvalidArgumentException(
                    "The user may select at most " +
                        maxChoices +
                        " choices: " +
                        getSurveyItemId());
        }

        return response;
    }
}