package com.sischi.selvebridge.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sischi.selvebridge.core.xml.converter.SelveXmlMessageDeserializer;
import com.sischi.selvebridge.core.xml.converter.SelveXmlMessageSerializer;
import com.sischi.selvebridge.core.xml.converter.SelveXmlMethodCallDeserializer;
import com.sischi.selvebridge.core.xml.converter.SelveXmlMethodResponseDeserializer;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMessage;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodCall;
import com.sischi.selvebridge.core.xml.entity.SelveXmlMethodResponse;
import com.sischi.selvebridge.core.util.HasLogger;
import com.sischi.selvebridge.core.util.Utils;

import org.springframework.stereotype.Component;


/**
 * 
 * The @link{MessageParser} is responsible for converting the XML-Messages to corresponding Java POJOs and vice-versa.<br>
 * Due to the limitations of the Selve-XML-specification, that you don't know the size of the receiving messages,
 * this parser collects all incoming bytes in one single message buffer. Every time some bytes are received the
 * message buffer will be analyzed and if a complete message is found it gets converted and the resulting object will
 * be published to the registered callbacks.<br>
 * 
 * @author sischi
 */
@Component
public class MessageParser implements HasLogger {

    /** the actual instance of the xml object mapper */
    private static XmlMapper xmlMapper = null;

    /** the message buffer */
    private String messageBuffer = "";

    private List<IncomingXmlMessageHandler> selveXmlMessageHandlers = new ArrayList<>();

	public interface IncomingXmlMessageHandler {
		void onIncomingXmlMessage(SelveXmlMessage message);
    }
    
    public List<IncomingXmlMessageHandler> getIncomingXmlMessageHandlers() {
        return selveXmlMessageHandlers;
    }

    public void addIncomingXmlMessageHandler(IncomingXmlMessageHandler handler) {
        if(!getIncomingXmlMessageHandlers().contains(handler)) {
            getIncomingXmlMessageHandlers().add(handler);
        }
        else {
            getLogger().info("incoming xml message handler already registered!");
        }
    }

    private static final String XML_MESSAGE_DELIMITER = "\r\n\n";
    private static final String XML_LINE_SUFFIX = "\r\n";
    public static final String XML_MESSAGE_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String XML_METHOD_CALL_START_TAG = "<methodCall>";
    public static final String XML_METHOD_CALL_END_TAG = "</methodCall>";
    public static final String XML_METHOD_RESPONSE_START_TAG = "<methodResponse>";
    public static final String XML_METHOD_RESPONSE_END_TAG = "</methodResponse>";


    public static XmlMapper getXmlMapper() {
        if(xmlMapper == null) {
            initializeXmlMapper();
        }
        return xmlMapper;
    }

    protected static void initializeXmlMapper() {
        xmlMapper = new XmlMapper();
        // register custom (de-)serializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(SelveXmlMessage.class, new SelveXmlMessageSerializer());
        module.addDeserializer(SelveXmlMessage.class, new SelveXmlMessageDeserializer());
        module.addDeserializer(SelveXmlMethodCall.class, new SelveXmlMethodCallDeserializer());
        module.addDeserializer(SelveXmlMethodResponse.class, new SelveXmlMethodResponseDeserializer());
        xmlMapper.registerModule(module);
    }

    public void processRawData(String data) {
        getLogger().trace("parsing data '{}'", data);
        messageBuffer += data;
        parseMessages();
    }

    protected String cleanXmlMessage(String message) {
        String cleaned = "";
        getLogger().trace("removing unused xml header from message '{}'", message);
        cleaned = message.replace(XML_MESSAGE_HEADER + XML_LINE_SUFFIX, "");
        getLogger().trace("message result after removing unused xml header: '{}'", cleaned);
        return cleaned;
    }

    protected void parseMessages() {
        getLogger().trace("trying to parse messageBuffer: '{}'", messageBuffer);
        if (messageBuffer.contains(XML_MESSAGE_DELIMITER)) {
            getLogger().debug("messageBuffer contains end of message token '{}'",
                    Utils.escapeString(XML_MESSAGE_DELIMITER));
            String[] chunks = messageBuffer.split(XML_MESSAGE_DELIMITER);
            getLogger().debug("split message buffer into {} pieces", chunks.length);

            String tempMessageBuffer = "";
            for (int i = 0; i < chunks.length; i++) {
                String chunk = chunks[i];

                chunk = cleanXmlMessage(chunk);
                getLogger().debug("trying to parse piece: '{}'", chunk);
                SelveXmlMessage message = null;
                try {
                    message = getXmlMapper().readValue(chunk, SelveXmlMessage.class);
                    getLogger().debug("successfully parsed xml '{}' to message '{}'", chunk, message);
                    handleIncomingMessage(message);
                } catch (JsonProcessingException ex) {
                    getLogger().warn("xml '{}' could not be parsed to message! putting it back into message buffer.",
                            chunk, ex);
                    tempMessageBuffer = tempMessageBuffer.equals("") ? chunk
                            : tempMessageBuffer + XML_MESSAGE_DELIMITER + chunk;
                }
            }
            messageBuffer = tempMessageBuffer;
            getLogger().debug("message buffer after parsing: '{}'", messageBuffer);
        } else {
            getLogger().trace("no message delimiter found, so assuming that the message buffer does not contain any complete message.");
        }
    }

    
    protected void handleIncomingMessage(SelveXmlMessage message) {
        for(IncomingXmlMessageHandler handler : getIncomingXmlMessageHandlers()) {
            try {
                handler.onIncomingXmlMessage(message);
            } catch(Exception ex) {
                getLogger().warn("unhandled exception in message handler for message '{}'", message, ex);
            }
        }
    }
    

    public String messageToXml(SelveXmlMessage message) {
        try {
            String xml = getXmlMapper().writeValueAsString(message);
            getLogger().info("successfully serialized message '{}' to xml '{}'", message.toString(), xml);
            return xml;
        } catch (JsonProcessingException ex) {
            getLogger().error("could not serialize message '{}'!", message, ex);
            return "";
        }
    }

    public SelveXmlMessage xmlToMessage(String xml) {
        try {
            SelveXmlMessage message = getXmlMapper().readValue(xml, SelveXmlMessage.class);
            getLogger().info("successfully parsed xml '{}' to message '{}'", xml, message);
            return message;
        } catch (JsonProcessingException ex) {
            getLogger().error("could not parse message from xml '{}'!", xml, ex);
            return null;
        }
    }
}
