package ru.hse.lorddux.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.hse.lorddux.structures.TaskItem;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XMLTaskParser {
    public static List<TaskItem> parse(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        Document document = builder.parse(is);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("QueueMessage");
        List<TaskItem> resList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            resList.add(getTaskItem(nodeList.item(i)));
        }
        return resList;

    }
    private static TaskItem getTaskItem(Node node) {
        TaskItem taskItem = new TaskItem();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            taskItem.setMessageId(getTagValue("MessageId", element));
            taskItem.setMessageText(getTagValue("MessageText", element));
            taskItem.setDequeueCount(Integer.valueOf(getTagValue("DequeueCount", element)));
            taskItem.setPopReceipt(getTagValue("PopReceipt", element));
        }
        return taskItem;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}
