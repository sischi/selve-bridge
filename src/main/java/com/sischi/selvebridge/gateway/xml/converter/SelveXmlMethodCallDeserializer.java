package com.sischi.selvebridge.gateway.xml.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.util.HasLogger;


/**
 * Custom deserializer to parse a method call xml string to a selve message object.
 * 
 * @author Simon Schiller
 */
public class SelveXmlMethodCallDeserializer extends StdDeserializer<SelveXmlMethodCall> implements HasLogger {

    private static final long serialVersionUID = 1L;

    public SelveXmlMethodCallDeserializer() {
        this(null);
    }

    protected SelveXmlMethodCallDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SelveXmlMethodCall deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        FromXmlParser xmlParser = (FromXmlParser) p;
        
        SelveXmlMethodCall message = new SelveXmlMethodCall();

        // the first token is the start object tag, so we have to advance the parser to the next token
        for(xmlParser.nextToken(); xmlParser.hasCurrentToken(); xmlParser.nextToken()) {
            String currentToken = xmlParser.getText();
            getLogger().debug("current token: '{}'", currentToken);

            if(currentToken.equals(SelveXmlMessage.XML_TAG_METHOD_NAME)) {
                // the next token should be the field value (here the method name)
                xmlParser.nextToken();
                message.setMethodName(xmlParser.getText());
            }
            else if(currentToken.equals(SelveXmlMessage.XML_TAG_METHOD_PARAMATER)) {
                // the next token indicates the start of the parameters array object ('{')
                xmlParser.nextToken();

                // the next token should be the fieldname of the first parameter
                xmlParser.nextToken();

                while(xmlParser.getCurrentToken() != JsonToken.END_OBJECT) {
                    SelveMethodParameter<?> param = SelveXmlMessageDeserializer.parseParameter(xmlParser);
                    message.addParamater(param);
                }
            }

        }

        return message;
    }
    
}
