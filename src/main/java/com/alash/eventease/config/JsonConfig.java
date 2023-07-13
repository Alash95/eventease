//package com.alash.eventease.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.StringReader;
//
//public class JsonConfig {
//
//    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//    DocumentBuilder builder = factory.newDocumentBuilder();
//    InputSource is = new InputSource(new StringReader(xmlResponse));
//    Document doc = builder.parse(is);
//
//    ObjectMapper mapper = new ObjectMapper();
//    mapper.enable(SerializationFeature.INDENT_OUTPUT);
//    String json = mapper.writeValueAsString(doc);
//
//    public JsonConfig() throws ParserConfigurationException {
//    }
//}
