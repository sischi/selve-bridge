package com.sischi.selvebridge.gateway.xml.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.sischi.selvebridge.gateway.models.message.MessageType;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterBase64;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterString;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterType;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;

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
    public static SelveMethodParameter<?> parseParameter(FromXmlParser xmlParser) throws IOException {
        // the current token should be the fieldname of the parameter

        String fieldName = xmlParser.getText();
        SelveMethodParameterType type = SelveMethodParameterType.parse(fieldName);

        // the next token should be the value of the parameter
        xmlParser.nextToken();
        String fieldValue = xmlParser.getText();
        
        SelveMethodParameter<?> param = null;
        try {
            switch (type) {
                case INT:
                    param = new SelveMethodParameterInt(Integer.parseInt(fieldValue));
                    break;
                case STRING:
                    param = new SelveMethodParameterString(fieldValue);
                    break;
                case BASE64:
                    param = SelveMethodParameterBase64.ofBase64(fieldValue);
                    break;
            }
            logger.debug("successfully parsed xml field '{}' with value '{}' to '{}'", fieldName, fieldValue, param);
        } catch(Exception e) {
            logger.warn("unexpected error while parsing parameter value '{}' of type '{}'! defaulting to type '{}'", fieldValue, type, SelveMethodParameterType.STRING);
            param = new SelveMethodParameterString(fieldValue);
        }
        
        // move to the next token after the current parameter
        xmlParser.nextToken();
        
        return param;
    }
    
}
