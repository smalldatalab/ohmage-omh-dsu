package io.smalldata.ohmageomh.surveys.domain.survey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import io.smalldata.ohmageomh.surveys.domain.MetaData;
import io.smalldata.ohmageomh.surveys.domain.Schema;
import io.smalldata.ohmageomh.surveys.domain.exception.InvalidArgumentException;
import org.openmhealth.schema.domain.SchemaId;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;

/**
 * <p>
 * A survey definition.
 * </p>
 *
 * @author John Jenkins
 */
public class Survey extends Schema {

    /**
     * The JSON key for the list of survey items.
     */
    public static final String JSON_KEY_SURVEY_ITEMS = "survey_items";

    /**
     * The list of survey items.
     */
    @JsonProperty(JSON_KEY_SURVEY_ITEMS)
    private final List<SurveyItem> surveyItems;



    /**
     * Builds the Survey object.
     *
     * @param schemaId
     *        The unique identifier for this object.
     *
     *
     * @param name
     *        The name of this survey.
     *
     * @param description
     *        The description of this survey.
     *
     * @throws IllegalArgumentException
     *         The ID is invalid.
     *
     * @throws InvalidArgumentException
     *         A parameter is invalid.
     */
    @JsonCreator
    @PersistenceConstructor
    protected Survey(
            @JsonProperty(JSON_KEY_ID) SchemaId schemaId,
            @JsonProperty(JSON_KEY_NAME) final String name,
            @JsonProperty(JSON_KEY_DESCRIPTION) final String description,
            @JsonProperty(JSON_KEY_SURVEY_ITEMS)
            final List<SurveyItem> surveyItems)
            throws IllegalArgumentException, InvalidArgumentException {

        super(
            schemaId,
            name,
            description);

        if(surveyItems == null) {
            throw
                new InvalidArgumentException(
                    "The list of survey items is null.");
        }

        if(surveyItems.isEmpty()) {
            throw
                new InvalidArgumentException(
                    "The survey needs at least one survey item.");
        }

        this.surveyItems = surveyItems;
    }

    /**
     * Validates that some meta-data and prompt responses conform to this
     * survey.
     *
     * @param metaData
     *        The meta-data to validate.
     *
     * @param promptResponses
     *        The prompt responses to validate.
     *
     * @return The validated responses.
     *
     * @throws InvalidArgumentException
     *         The meta-data or prompt responses were invalid.
     */
    public Map<String, Object> validate(
        final MetaData metaData,
        final Map<String, Object> promptResponses,
        final Map<String, Media> media)
        throws InvalidArgumentException {

        // Create an iterator for the survey items.
        Iterator<SurveyItem> surveyItemIter = surveyItems.iterator();

        // Create a map of previously validated responses to use for validating
        // future response's conditions.
        Map<String, Object> checkedResponses = new HashMap<String, Object>();

        // Loop through all of the possible survey items.
        while(surveyItemIter.hasNext()) {
            // Get the survey item.
            SurveyItem surveyItem = surveyItemIter.next();

            // Get the survey item's ID.
            String surveyItemId = surveyItem.getSurveyItemId();

            // If it's respondable, then check for a response.
            if(surveyItem instanceof Respondable) {
                // Cast the survey item to a prompt.
                Respondable respondable = (Respondable) surveyItem;

                // Get the response.
                Object response = promptResponses.get(surveyItemId);

                // Validate the response.
                respondable
                    .validateResponse(response, checkedResponses, media);
            }
            // Otherwise, be sure a response was not given.
            else if(promptResponses.containsKey(surveyItemId)) {
                throw
                    new InvalidArgumentException(
                        "A survey item that should not have had a response " +
                            "did: " +
                            surveyItemId);
            }
        }

        // Ensure that we used all of the prompt responses.
        Set<String> extraResponses =
            new HashSet<String>(promptResponses.keySet());
        extraResponses.removeAll(checkedResponses.keySet());
        if(! extraResponses.isEmpty()) {
            throw
                new InvalidArgumentException(
                    "More responses exist than prompts in the survey.");
        }

        // Remove the NoResponse values from the checked responses.
        Iterator<String> responseKeys = checkedResponses.keySet().iterator();
        while(responseKeys.hasNext()) {
            if(checkedResponses.get(responseKeys.next()) instanceof NoResponse) {
                responseKeys.remove();
            }
        }

        // Return the validated responses.
        return checkedResponses;
    }

    /**
     * Returns the list of survey items.
     *
     * @return The list of survey items.
     */
    public List<SurveyItem> getSurveyItems() {
        return Collections.unmodifiableList(surveyItems);
    }

    /*
     * (non-Javadoc)
     * @see org.ohmage.domain.Schema#getDefinition()
     */
    @Override
    public Concordia getDefinition() {
        // Create the list of prompt definitions.
        List<name.jenkins.paul.john.concordia.schema.Schema> fields =
            new LinkedList<name.jenkins.paul.john.concordia.schema.Schema>();
        for(SurveyItem surveyItem : surveyItems) {
            if(surveyItem instanceof Respondable) {
                // Get the survey item's response schema.
                name.jenkins.paul.john.concordia.schema.Schema schema =
                    ((Respondable) surveyItem).getResponseSchema();

                // If the schema is null, ignore it.
                if(schema == null) {
                    continue;
                }

                // Add the schema to the list of fields.
                fields.add(schema);
            }
        }

        // Build the root schema.
        ObjectSchema rootSchema;
        try {
            rootSchema = new ObjectSchema(
                getDescription(),
                false,
                getSchemaId().toString(),
                fields);
        }
        catch(ConcordiaException e) {
            throw new IllegalStateException("The root object was invalid.", e);
        }

        // Build and return the Concordia object.
        try {
            return new Concordia(rootSchema);
        }
        catch(IllegalArgumentException | ConcordiaException e) {
            throw
                new IllegalArgumentException(
                    "The Concordia object could not be built.",
                    e);
        }
    }

}