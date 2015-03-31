package org.openmhealth.dsu.domain.ohmage.survey;

import org.openmhealth.dsu.domain.ohmage.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.ohmage.survey.condition.Condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A message to display to the user.
 * </p>
 *
 * @author John Jenkins
 */
public class Message extends SurveyItem {
    /**
     * The string type of this survey item.
     */
    public static final String SURVEY_ITEM_TYPE = "message";

    /**
     * The JSON key for the text.
     */
    public static final String JSON_KEY_TEXT = "text";

    /**
     * The text to show to the user.
     */
    @JsonProperty(JSON_KEY_TEXT)
    private final String text;

    /**
     * Creates a new message object.
     *
     * @param surveyItemId
     *        The survey-unique identifier for this message.
     *
     * @param condition
     *        The condition on whether or not to show the message.
     *
     * @param text
     *        The text to display to the user.
     *
     * @throws InvalidArgumentException
     *         The message text is null.
     */
    @JsonCreator
    public Message(
        @JsonProperty(JSON_KEY_SURVEY_ITEM_ID) final String surveyItemId,
        @JsonProperty(JSON_KEY_CONDITION) final Condition condition,
        @JsonProperty(JSON_KEY_TEXT) final String text)
        throws InvalidArgumentException {

        super(surveyItemId, condition);

        if(text == null) {
            throw new InvalidArgumentException("The text is null.");
        }

        this.text = text;
    }
}