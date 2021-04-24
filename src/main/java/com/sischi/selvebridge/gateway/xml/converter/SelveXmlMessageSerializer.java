package com.sischi.selvebridge.gateway.xml.converter;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.sischi.selvebridge.gateway.models.message.SelveMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.util.HasLogger;



/**
 * Custom serializer to convert a selve message object to a xml string matching the selve specification.
 * 
 * @author Simon Schiller
 */
public class SelveXmlMessageSerializer extends StdSerializer<SelveXmlMessage> implements HasLogger {

    private static final long serialVersionUID = 1L;

    public SelveXmlMessageSerializer() {
        this(null);
    }

    protected SelveXmlMessageSerializer(Class<SelveXmlMessage> t) {
        super(t);
    }

    @Override
    public void serialize(SelveXmlMessage value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ToXmlGenerator xmlGen = (ToXmlGenerator) gen;

        // write outmost xml tag to be the message type
        xmlGen.setNextName(new QName(value.getType().getXmlTag()));
        xmlGen.writeStartObject();

        // write method name
        xmlGen.writeStringField(SelveXmlMessage.XML_TAG_METHOD_NAME, value.getMethodName());

        // write method parameters
        if(value.getParameters() != null && !value.getParameters().isEmpty()) {
            // write field name to be the name of the parameter collection
            xmlGen.writeFieldName(SelveXmlMessage.XML_TAG_METHOD_PARAMATER);
            xmlGen.writeStartObject();
            // write each existing parameter in the form: <type>value</type>
            for(SelveMethodParameter<?> param : value.getParameters()) {
                xmlGen.writeObjectField(param.getType().getXmlTag(), param.getValue());
            }
            xmlGen.writeEndObject();
        }
        
        xmlGen.writeEndObject();
    }
    
}
