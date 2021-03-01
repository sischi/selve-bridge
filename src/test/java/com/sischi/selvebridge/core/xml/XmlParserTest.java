package com.sischi.selvebridge.core.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sischi.selvebridge.gateway.models.message.SelveXmlError;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMessage;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodCall;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodResponse;
import com.sischi.selvebridge.gateway.models.message.SelveXmlMethodParameter.ParameterType;
import com.sischi.selvebridge.gateway.xml.MessageParser;
import com.sischi.selvebridge.util.HasLogger;

import org.junit.jupiter.api.Test;


public class XmlParserTest implements HasLogger {

    @Test
    public void deserializeMethodResponseError() throws JsonMappingException, JsonProcessingException {
        String xml = "<methodResponse>" +
            "<fault>" +
            "<array>" +
            "<string>Method not supported!</string>" +
            "<int>2</int>" +
            "</array>" +
            "</fault>" +
            "</methodResponse>";

        SelveXmlMessage actualMessage = MessageParser.getXmlMapper().readValue(xml, SelveXmlMessage.class);
        
        SelveXmlMessage expectedMessage = new SelveXmlMethodResponse()
                .withError(
                    new SelveXmlError()
                            .withDescription("Method not supported!")
                            .withCode(2)
                );

        assertEquals(expectedMessage.toString(), actualMessage.toString());
    }

    @Test
    public void deserializeMethodResponseSuccessNoArgs() throws JsonMappingException, JsonProcessingException {
        String xml = "<methodResponse>" +
            "<array>" +
            "<string>selve.GW.service.ping</string>" +
            "</array>" +
            "</methodResponse>";

        SelveXmlMessage actualMessage = MessageParser.getXmlMapper().readValue(xml, SelveXmlMessage.class);
        
        SelveXmlMessage expectedMessage = new SelveXmlMethodResponse()
                .withMethodName("selve.GW.service.ping");

        assertEquals(expectedMessage.toString(), actualMessage.toString());
    }


    @Test
    public void deserializeMethodResponseSuccessAllArgs() throws JsonMappingException, JsonProcessingException {
        String xml = "<methodResponse>" +
            "<array>" +
            "<string>selve.GW.service.methodname</string>" +
            "<string>Parameter 1</string>" +
            "<base64>11001100</base64>" +
            "<int>123</int>" +
            "</array>" +
            "</methodResponse>";

        SelveXmlMessage actualMessage = MessageParser.getXmlMapper().readValue(xml, SelveXmlMessage.class);
        
        SelveXmlMessage expectedMessage = new SelveXmlMethodResponse()
                .withMethodName("selve.GW.service.methodname")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.STRING, "Parameter 1"),
                    new SelveXmlMethodParameter(ParameterType.BASE64, "11001100"),
                    new SelveXmlMethodParameter(ParameterType.INT, 123)
                );

        assertEquals(expectedMessage.toString(), actualMessage.toString());
    }


    @Test
    public void deserializeMethodCallAllArgs() throws JsonMappingException, JsonProcessingException {
        String xml = "<methodCall>" +
            "<methodName>selve.GW.service.methodname</methodName>" +
            "<array>" +
            "<string>Parameter 1</string>" +
            "<base64>11001100</base64>" +
            "<int>123</int>" +
            "</array>" +
            "</methodCall>";

        SelveXmlMessage actualMessage = MessageParser.getXmlMapper().readValue(xml, SelveXmlMessage.class);
        
        SelveXmlMessage expectedMessage = new SelveXmlMethodCall()
                .withMethodName("selve.GW.service.methodname")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.STRING, "Parameter 1"),
                    new SelveXmlMethodParameter(ParameterType.BASE64, "11001100"),
                    new SelveXmlMethodParameter(ParameterType.INT, 123)
                );

        assertEquals(expectedMessage.toString(), actualMessage.toString());
    }



    @Test
    public void serializeMethodCallNoArgs() throws JsonMappingException, JsonProcessingException {
        
        SelveXmlMessage message = new SelveXmlMethodCall()
                .withMethodName("selve.GW.service.methodname");

        String actualXml = MessageParser.getXmlMapper().writeValueAsString(message);

        String expectedXml = "<methodCall>" +
            "<methodName>selve.GW.service.methodname</methodName>" +
            "</methodCall>";

        assertEquals(expectedXml, actualXml);
    }


    @Test
    public void serializeMethodCallAllArgs() throws JsonMappingException, JsonProcessingException {
        
        SelveXmlMessage message = new SelveXmlMethodCall()
                .withMethodName("selve.GW.service.methodname")
                .withParameter(
                    new SelveXmlMethodParameter(ParameterType.STRING, "Parameter 1"),
                    new SelveXmlMethodParameter(ParameterType.BASE64, "11001100"),
                    new SelveXmlMethodParameter(ParameterType.INT, 123)
                );

        String actualXml = MessageParser.getXmlMapper().writeValueAsString(message);

        String expectedXml = "<methodCall>" +
            "<methodName>selve.GW.service.methodname</methodName>" +
            "<array>" +
            "<string>Parameter 1</string>" +
            "<base64>11001100</base64>" +
            "<int>123</int>" +
            "</array>" +
            "</methodCall>";

        assertEquals(expectedXml, actualXml);
    }


}
