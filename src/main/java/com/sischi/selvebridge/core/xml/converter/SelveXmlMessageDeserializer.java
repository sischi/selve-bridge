package com.sischi.selvebridge.core.xml.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.sischi.selvebridge.core.entities.message.SelveXmlMessage;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodCall;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.entities.message.MessageType;
import com.sischi.selvebridge.core.entities.message.SelveXmlMethodParameter.ParameterType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Custom desserializer to parse a selve message xml string to a selve message object
 * 
 * @author Simon Schiller
 */
public class SelveXmlMessageDeserializer extends StdDeserializer<SelveXmlMessage> {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(SelveXmlMessageDeserializer.class);

    public SelveXmlMessageDeserializer() {
        this(null);
    }

    protected SelveXmlMessageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SelveXmlMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        FromXmlParser xmlParser = (FromXmlParser) p;

        // get the root tag name
        // yea, this is not really self explaining but I found this here:
        // https://stackoverflow.com/questions/36317162/get-jackson-xmlmapper-to-read-root-element-name
        String rootName = xmlParser.getStaxReader().getLocalName();

        // parse message type
        MessageType messageType = null;
        try {
            messageType = MessageType.parse(rootName);
        } catch(IllegalArgumentException ex) {
            logger.error("unkown message type specified by root tag '{}'!", rootName, ex);
            return null;
        }

        // choose the proper deserializer according to the message type
        switch (messageType) {
            case METHOD_CALL:
                return p.getCodec().readValue(p, SelveXmlMethodCall.class);
            case METHOD_RESPONSE:
                return p.getCodec().readValue(p, SelveXmlMethodResponse.class);
            default:
                logger.error("unexpected selve xml message type '{}'!", messageType == null ? "null" : messageType);
                return null;
        }
    }


    /**
     * parse one single method parameter. This method expects the xml parser to point at the field name
     * of the parameter that should be parsed. At the end of the method the xmlParser will point to
     * the first token after the parameter parsed.<br>
     * <br>
     * The expected xml looks like this:<br>
     * <pre>
     * <type>value</type>
     * </pre>
     * 
     * @param xmlParser the @link{FromXmlParser} that has to point to the fieldname of the parameter to be parsed
     * @return the parsed @link{SelveXmlMethodParameter} object
     * @throws IOException the exception that may be throw by the xml parser
     */
    public static SelveXmlMethodParameter parseParameter(FromXmlParser xmlParser) throws IOException {
        SelveXmlMethodParameter param = new SelveXmlMethodParameter();
        
        // the current token should be the fieldname of the parameter

        String fieldName = xmlParser.getText();
        ParameterType type = ParameterType.parse(fieldName);
        param.setType(type);

        // the next token should be the value of the parameter
        xmlParser.nextToken();
        String fieldValue = xmlParser.getText();
        param.parseValue(fieldValue);

        logger.debug("successfully parsed xml field '{}' with value '{}' to '{}'", fieldName, fieldValue, param);

        // move to the next token after the current parameter
        xmlParser.nextToken();
        
        return param;
    }
    
}
