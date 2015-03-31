package org.openmhealth.dsu.domain.ohmage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import org.openmhealth.schema.domain.SchemaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.openmhealth.dsu.domain.ohmage.exception.InvalidArgumentException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.data.annotation.Id;

/**
 * <p>
 * A generic schema in the system.
 * </p>
 *
 * @author John Jenkins
 */

public abstract class Schema{

	/**
	 * The JSON key for the ID.
	 */
	public static final String JSON_KEY_ID = "schema_id";
	/**
	 * The JSON key for the version.
	 */
	public static final String JSON_KEY_VERSION = "schema_version";
	/**
	 * The JSON key for the name.
	 */
	public static final String JSON_KEY_NAME = "name";
	/**
	 * The JSON key for the description.
	 */
	public static final String JSON_KEY_DESCRIPTION = "description";

    /**
     * The JSON key for the definition.
     */
    public static final String JSON_KEY_DEFINITION = "definition";

    /**
     * The group ID for the Jackson filter. This must be unique to our class,
     * whatever the value is.
     */
    protected static final String JACKSON_FILTER_GROUP_ID =
        "org.ohmage.domain.Schema";


	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Schema.class.getName());

	/**
	 * The unique identifier for this schema.
	 */
    @Id
	@JsonProperty(JSON_KEY_ID)
	private SchemaId schemaId;

	/**
	 * The name of this schema.
	 */
	@JsonProperty(JSON_KEY_NAME)
	private String name;
	/**
	 * The description of this schema.
	 */
	@JsonProperty(JSON_KEY_DESCRIPTION)
	private  String description;




    /**
     * Rebuilds an existing Schema object.
     *
     * @param id
     *        The unique identifier for this object.
     * @param name
     *        The name of this schema.
     *
     * @param description
     *        The description of this schema.
     *
     * @throws IllegalArgumentException
     *         The ID is invalid.
     *
     * @throws InvalidArgumentException
     *         A parameter is invalid.
     */
    @JsonCreator
    protected Schema(
            @JsonProperty(JSON_KEY_ID) final SchemaId id,
            @JsonProperty(JSON_KEY_NAME) final String name,
            @JsonProperty(JSON_KEY_DESCRIPTION) final String description)
            throws IllegalArgumentException, InvalidArgumentException {


        // Validate the parameters.
        if(id == null) {
            throw new InvalidArgumentException("The schema_id is null.");
        }
        if(name == null) {
            throw new InvalidArgumentException("The name is null.");
        }
        if(description == null) {
            throw new InvalidArgumentException("The description is null.");
        }
        this.name = name;
        this.description = description;
        this.schemaId = id;
    }

    public SchemaId getSchemaId() {
        return schemaId;
    }
	/**
	 * Returns the name of this schema.
	 *
	 * @return The name of this schema.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description of this schema.
	 *
	 * @return The description of this schema.
	 */
	public String getDescription() {
		return description;
	}
    /**
	 * Returns the definition of this schema.
	 *
	 * @return The definition of this schema.
	 */
    @JsonProperty(JSON_KEY_DEFINITION)
	public abstract Concordia getDefinition();

	/**
	 * Parses a string into a Concordia object.
	 *
	 * @param definition
	 *        The definition to parse.
	 *
	 * @return The validated Concordia object.
	 *
	 * @throws InvalidArgumentException
	 *         The definition was not valid JSON or not a valid Concordia
	 *         definition.
	 *
	 * @throws IllegalStateException
	 *         There was a problem handling our own streams.
	 */
	protected static Concordia parseDefinition(
		final String definition)
		throws InvalidArgumentException, IllegalStateException {

		// Validate the input.
		if(definition == null) {
			throw new InvalidArgumentException("The definition is null.");
		}

		// Create an input stream for the input.
		ByteArrayInputStream definitionInput =
			new ByteArrayInputStream(definition.getBytes());
		try {
			// Build and return the Concordia object.
			return
				new Concordia(
					definitionInput,
					ValidationController.BASIC_CONTROLLER);
		}
		// If it was invalid JSON.
		catch(JsonParseException e) {
			throw
				new InvalidArgumentException(
					"The definition is not valid JSON.",
					e);
		}
		// If it was invalid Concordia.
		catch(ConcordiaException e) {
			throw
				new InvalidArgumentException(
					"The definition is invalid: " + e.getMessage(),
					e);
		}
		// If we couldn't read from our own input stream, which should never
		// happen.
		catch(IOException e) {
			throw
				new IllegalStateException(
					"Could not read from our own input stream.",
					e);
		}
		// Always be sure to close our own stream.
		finally {
			try {
				definitionInput.close();
			}
			catch(IOException e) {
				LOGGER.warn("Could not close our own input stream.",
						e);
			}
		}
	}
    /**
     * Returns a randomly generated ID.
     *
     * @return A randomly generated ID.
     */
    protected static String getRandomId() {
        return UUID.randomUUID().toString();
    }


}
