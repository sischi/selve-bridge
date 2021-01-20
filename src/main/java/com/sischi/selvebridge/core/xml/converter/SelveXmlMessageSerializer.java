package com.sischi.selvebridge.core.xml.converter;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMessage;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodParameter;
import com.sischi.selvebridge.core.util.HasLogger;



/**
 * Custom serializer to convert a selve message object to a xml string matching the selve specification.
 * 
 * @author sischi
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
            for(SelveXmlMethodParameter param : value.getParameters()) {
                xmlGen.writeObjectField(param.getType().getXmlTag(), param.getValue());
            }
            xmlGen.writeEndObject();
        }
        
        xmlGen.writeEndObject();
    }
    
}
