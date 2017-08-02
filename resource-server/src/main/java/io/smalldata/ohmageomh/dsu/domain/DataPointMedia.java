package io.smalldata.ohmageomh.dsu.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openmhealth.schema.domain.omh.DataPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * <p>
 * The internal representation of a generic media object.
 * </p>
 *
 * @author John Jenkins
 */
public class DataPointMedia {
    /**
     * The unique identifier for this media file.
     */
    @Id
    private final String id;

    /**
     * The identifier for this media file within the data point it belongs to.
     */
    private final String mediaId;

    /**
     * The identifier for this media file within the data point it belongs to.
     */
    private final String dataPointId;


    /**
     * The {@link InputStream} that is connected to the data.
     */
    @Transient // do not store this field
    @JsonIgnore // do not serialize this field
    private InputStream stream;
    /**
     * The size, in bytes, of the data.
     */
    private final long size;
    /**
     * The content-type of the data.
     */
    private final String contentType;
    private final String userId;

    @PersistenceConstructor
    public DataPointMedia(String id, String mediaId, String dataPointId, long size, String contentType, String userId){
        this.id = id;
        this.mediaId = mediaId;
        this.dataPointId = dataPointId;
        this.size = size;
        this.contentType = contentType;
        this.userId = userId;
    }
    /**
     * Creates a new Media object from a {@link org.springframework.web.multipart.MultipartFile} object.
     *
     * @param datapoint
     *        The data point this media belongs to
     *
     * @param media
     *        The {@link org.springframework.web.multipart.MultipartFile} to base this Media object off of.
     *
     * @throws IllegalArgumentException
     *         The ID or media file were null or could not be read.
     *
     * @see #generateUuid()
     */
    public DataPointMedia(final DataPoint datapoint, final MultipartFile media)
            throws IllegalArgumentException {

        // Validate the input.
        if(datapoint == null) {
            throw new IllegalArgumentException("The media is null.");
        }
        if(media == null) {
            throw new IllegalArgumentException("The media is null.");
        }

        // Save the object.
        this.id = generateUuid();
        this.dataPointId = datapoint.getHeader().getId();
        this.userId = datapoint.getHeader().getUserId();
        try {
            stream = media.getInputStream();
        }
        catch(IOException e) {
            throw
                    new IllegalArgumentException(
                            "Could not connect to the media.",
                            e);
        }
        mediaId = media.getOriginalFilename();
        size = media.getSize();
        contentType = media.getContentType();
    }



    /**
     * Returns the unique identifier for this point.
     *
     * @return The unique identifier for this point.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the input stream.
     *
     * @return The input stream.
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Returns the size.
     *
     * @return The size.
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the content-type.
     *
     * @return The content-type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Generates a new, universally unique identifier to be used for a new
     * Media object.
     *
     * @return A new, universally unique identifier.
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public String getDataPointId() {
        return dataPointId;
    }

    public String getMediaId() {
        return mediaId;
    }
}