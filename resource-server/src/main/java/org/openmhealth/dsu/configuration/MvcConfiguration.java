package org.openmhealth.dsu.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * The only purpose of this configuration is to make Jackson Deserialization obey the offset given in the datatime string.
 * Created by changun on 1/24/15.
 */

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(MvcConfiguration.class);
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Converters:" + converters.size());
        converters.add(converter());

    }

    /**
     * @return A new MappingJackson2HttpMessageConverter that will (de)serialize OffsetDateTime objects properly
     */
    @Bean
    MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        SimpleModule module =
                new SimpleModule("OffsetDateTime")
                .addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME)
                .addSerializer(OffsetDateTime.class, InstantSerializer.OFFSET_DATE_TIME);
        converter.getObjectMapper()
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setPropertyNamingStrategy(
                        PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
        .registerModule(module);

        return converter;
    }
}
