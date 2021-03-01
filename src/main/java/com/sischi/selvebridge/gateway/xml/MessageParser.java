package com.sischi.selvebridge.gateway.xml;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.gateway.xml.converter.SelveXmlMessageDeserializer;
import com.sischi.selvebridge.gateway.xml.converter.SelveXmlMessageSerializer;
import com.sischi.selvebridge.gateway.xml.converter.SelveXmlMethodCallDeserializer;
import com.sischi.selvebridge.gateway.xml.converter.SelveXmlMethodResponseDeserializer;
import com.sischi.selvebridge.util.HasLogger;
import com.sischi.selvebridge.util.Utils;

import org.springframework.stereotype.Component;


/**
 * 
 * The {@link MessageParser} is responsible for converting the XML-Messages to corresponding Java POJOs and vice-versa.<br>
 * Due to the limitations of the Selve-XML-specification, that you don't know the size of the receiving messages,
 * this parser collects all incoming bytes in one single message buffer. Every time some bytes are received the
 * message buffer will be analyzed and if a complete message is found it gets converted and the resulting object will
 * be published to the registered callbacks.
 * 
 * @author Simon Schiller
 */
@Component
public class MessageParser implements HasLogger {

    /** the singleton instance of the xml object mapper */
    private static XmlMapper xmlMapper = null;

    /** the message buffer */
    private String messageBuffer = "";

    /** list of all currently registered message handlers */
    private List<IncomingXmlMessageHandler> selveXmlMessageHandlers = new ArrayList<>();

    /**
     * This interface represents a contract to be treated as a message handler. Every implementation can
     * be registered as such an hander and will be informed about new incoming messages.
     */
	public interface IncomingXmlMessageHandler {

        /**
         * handler method for incoming messages
         * @param message the incoming {@link SelveXmlMessage}
         */
		void onIncomingXmlMessage(SelveXmlMessage message);
    }
    
    /**
     * getter for the registered {@link IncomingXmlMessageHandler}s
     * @return a list of all currently registered {@link IncomingXmlMessageHandler}s
     */
    public List<IncomingXmlMessageHandler> getIncomingXmlMessageHandlers() {
        return selveXmlMessageHandlers;
    }

    /**
     * adds the given handler to the ist of registered handlers
     * @param handler the {@link IncomingXmlMessageHandler} that should be registered
     */
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


    /**
     * static method to obtain the instance of the xml mapper used by this parser to (de)serialize the xml messages
     * @return the singleton reference of the {@link XmlMapper} instance used to (de)serialize the xml messages
     */
    public static XmlMapper getXmlMapper() {
        if(xmlMapper == null) {
            initializeXmlMapper();
        }
        return xmlMapper;
    }

    /**
     * set up  static reference to the {@link XmlMapper} used to (de)serialize the xml messages
     */
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

    /**
     * adding the given data to the message buffer and tries to interprete the current message buffer
     * @param data the data that should be added to the message buffer
     */
    public void processRawData(String data) {
        getLogger().trace("parsing data '{}'", data);
        messageBuffer += data;
        parseMessages();
    }

    /**
     * cleanup the given xml message by removing the unused xml header
     * @param message the {@link String} representing the xml message
     * @return a clean xml message
     */
    protected String cleanXmlMessage(String message) {
        String cleaned = "";
        getLogger().trace("removing unused xml header from message '{}'", message);
        cleaned = message.replace(XML_MESSAGE_HEADER + XML_LINE_SUFFIX, "");
        getLogger().trace("message result after removing unused xml header: '{}'", cleaned);
        return cleaned;
    }

    /**
     * this method searches the message buffer for complete xml messages. If some are found, they
     * are tried to be parsed and properly handled.
     */
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
                    // post processing parsed xml message
                    postProcessParsedMessage(message);
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

    /**
     * handles any successfully parsed xml message by passing it to each of the registered handlers
     * @param message the successsfully parsed {@link SelveXmlMessage}
     */
    protected void handleIncomingMessage(SelveXmlMessage message) {
        for(IncomingXmlMessageHandler handler : getIncomingXmlMessageHandlers()) {
            try {
                handler.onIncomingXmlMessage(message);
            } catch(Exception ex) {
                getLogger().warn("unhandled exception in message handler for message '{}'", message, ex);
            }
        }
    }
    
    /**
     * serializing the given message to a string
     * @param message the {@link SelveXmlMessage} that should be serialized 
     * @return the {@link String} representing the serialized message
     */
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

    /**
     * deserializing the given xml string to the corresponding POJO
     * @param xml the string representation of a xml message that should be deserialized
     * @return the parsed {@link SelveXmlMessage}
     */
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

    protected void postProcessParsedMessage(SelveXmlMessage message) {
        // here we should add meta information based on the method name of the given message according to the specification
    }
}
