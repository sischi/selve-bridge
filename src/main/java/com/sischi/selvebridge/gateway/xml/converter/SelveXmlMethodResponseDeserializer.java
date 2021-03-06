package com.sischi.selvebridge.gateway.xml.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterInt;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameterString;
import com.sischi.selvebridge.gateway.models.message.SelveXmlError;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.util.HasLogger;


/**
 * Custom deserializer to parse a method response xml string to a selve message object.
 * 
 * @author Simon Schiller
 */
public class SelveXmlMethodResponseDeserializer extends StdDeserializer<SelveXmlMethodResponse> implements HasLogger {

    private static final long serialVersionUID = 1L;

    public SelveXmlMethodResponseDeserializer() {
        this(null);
    }

    protected SelveXmlMethodResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SelveXmlMethodResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        FromXmlParser xmlParser = (FromXmlParser) p;
        
        SelveXmlMethodResponse message = new SelveXmlMethodResponse();

        // the first token is the start object tag, so we have to advance the parser to the next token
        for(xmlParser.nextToken(); xmlParser.hasCurrentToken(); xmlParser.nextToken()) {
            String currentToken = xmlParser.getText();
            getLogger().debug("current token: '{}'", currentToken);
            
            if(currentToken.equals(SelveXmlMessage.XML_TAG_ERROR)) {
                getLogger().debug("error token found! interpreting this message as error response.");
                message.setError(parseError(xmlParser));
            }
            else if(currentToken.equals(SelveXmlMessage.XML_TAG_METHOD_PARAMATER)) {
                // method parameter array tag found! interpreting the parameters as follows:
                // 1.: the method name ('string')
                // 2. - n.: method specific response parameter

                // the next token indicates the start of the parameters array object ('{')
                xmlParser.nextToken();

                // the next token should be the fieldname of the first parameter
                xmlParser.nextToken();

                SelveMethodParameter<?> methodNameParam = SelveXmlMessageDeserializer.parseParameter(xmlParser);
                message.setMethodName((String) methodNameParam.getValue());

                while(xmlParser.getCurrentToken() != JsonToken.END_OBJECT) {
                    SelveMethodParameter<?> param = SelveXmlMessageDeserializer.parseParameter(xmlParser);
                    message.addParamater(param);
                }
            }

            // the next token should be the end of the outer object ('}')
            xmlParser.nextToken();
        }
        return message;
    }

    

    /**
     * interprets the xml, starting where the xml parser points at, as an error.<br>
     * <br>
     * The expected xml looks like this:
     * <pre>
     * <fault>
     *     <array>
     *         <string>description</string>
     *         <int>code</int>
     *     </array>
     * </fault>
     * </pre>
     * 
     * 
     * @param xmlParser the @link{FromXmlParser} pointing at the start of the error object
     * @return the parsed @link{SelveXmlError} object
     * @throws IOException the exception that may be throw by the xml parser
     */
    public SelveXmlError parseError(FromXmlParser xmlParser) throws IOException {


        SelveXmlError error = new SelveXmlError();

        // the next token indicates the start of the error object ('{' or '<fault>')
        xmlParser.nextToken();
        // the next token should be the name of the parameter array ('array')
        xmlParser.nextToken();
        if(xmlParser.getText().equals(SelveXmlMessage.XML_TAG_METHOD_PARAMATER)) {
            // the next token indicates the start of the parameters array object ('{')
            xmlParser.nextToken();

            // the next token should be the fieldname of the first parameter (in this case: 'string')
            xmlParser.nextToken();

            SelveMethodParameter<?> descriptionParam = SelveXmlMessageDeserializer.parseParameter(xmlParser);

            // the current token should be the fieldname of the second parameter (in this case: 'int')
            SelveMethodParameter<?> codeParam = SelveXmlMessageDeserializer.parseParameter(xmlParser);

            error.withDescription(((SelveMethodParameterString) descriptionParam).getValue())
                    .withCode(((SelveMethodParameterInt) codeParam).getValue());
            
            // the current token should be the end of the parameters array ('}')
        }
        // the next token should be the end of the error object ('}')
        xmlParser.nextToken();

        getLogger().debug("xml error parsed: '{}'", error);

        return error;
    }
}
