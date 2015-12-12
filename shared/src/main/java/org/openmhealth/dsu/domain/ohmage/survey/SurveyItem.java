package org.openmhealth.dsu.domain.ohmage.survey;

import org.openmhealth.dsu.domain.ohmage.exception.InvalidArgumentException;
import org.openmhealth.dsu.domain.ohmage.survey.condition.Condition;
import org.openmhealth.dsu.domain.ohmage.survey.prompt.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * <p>
 * The base class for all survey items including prompts and messages.
 * </p>
 *
 * @author John Jenkins
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.DEFAULT,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.DEFAULT)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = SurveyItem.JSON_KEY_SURVEY_ITEM_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = Message.class,
        name = Message.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = AudioPrompt.class,
        name = AudioPrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = ImagePrompt.class,
        name = ImagePrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = VideoPrompt.class,
        name = VideoPrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = NumberPrompt.class,
        name = NumberPrompt.SURVEY_ITEM_TYPE),

    @JsonSubTypes.Type(
        value = NumberSingleChoicePrompt.class,
        name = NumberSingleChoicePrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = StringSingleChoicePrompt.class,
        name = StringSingleChoicePrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = NumberMultiChoicePrompt.class,
        name = NumberMultiChoicePrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = StringMultiChoicePrompt.class,
        name = StringMultiChoicePrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = TextPrompt.class,
        name = TextPrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
        value = TimestampPrompt.class,
        name = TimestampPrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
            value = VisualAnalogPrompt.class,
            name = VisualAnalogPrompt.SURVEY_ITEM_TYPE),
    @JsonSubTypes.Type(
            value = MapPrompt.class,
            name = MapPrompt.SURVEY_ITEM_TYPE)

})
public abstract class SurveyItem {
    /**
     * The JSON key used to define which kind of survey item this is.
     */
    public static final String JSON_KEY_SURVEY_ITEM_TYPE = "survey_item_type";

    /**
     * The JSON key for the ID.
     */
    public static final String JSON_KEY_SURVEY_ITEM_ID = "survey_item_id";
    /**
     * The JSON key for the condition.
     */
    public static final String JSON_KEY_CONDITION = "condition";

    /**
     * The survey-unique identifier for this survey item.
     */
    @JsonProperty(JSON_KEY_SURVEY_ITEM_ID)
    private final String surveyItemId;
    /**
     * The condition on whether or not this survey item should not be
     * displayed.
     */
    @JsonProperty(JSON_KEY_CONDITION)
    private final Condition condition;

    /**
     * Creates a new survey item.
     *
     * @param id
     *        The survey item's unique identifier within this survey.
     *
     * @param condition
     *        The condition on whether or not to show the survey item.
     *
     * @throws InvalidArgumentException
     *         The iID is null.
     */
    public SurveyItem(
        final String surveyItemId,
        final Condition condition)
        throws InvalidArgumentException {

        if(surveyItemId == null) {
            throw new InvalidArgumentException("The survey item ID is null.");
        } else if(surveyItemId.contains(" ")) {
            throw new InvalidArgumentException("The survey item ID cannot contain spaces.");
        }

        this.surveyItemId = surveyItemId;
        this.condition = condition;
    }

    /**
     * Returns the unique identifier for this survey item.
     *
     * @return The unique identifier for this survey item.
     */
    public String getSurveyItemId() {
        return surveyItemId;
    }

    /**
     * Returns the condition determining if this prompt should be displayed or
     * not. This may be null if the prompt has no condition.
     *
     * @return The condition determining if this prompt should be displayed or
     *         not. This may be null if the prompt has no condition.
     */
    public Condition getCondition() {
        return condition;
    }
}