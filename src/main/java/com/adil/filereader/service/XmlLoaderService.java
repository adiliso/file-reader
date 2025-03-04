package com.adil.filereader.service;

import com.adil.filereader.model.StockDataModel;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

public class XmlLoaderService implements LoaderService {

    @Override
    public void load(File file, Consumer<StockDataModel> processor) {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            XMLEventReader eventReader = factory.createXMLEventReader(fileInputStream);

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                // Look for the <value> element
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("value")) {
                        // Extract attributes
                        String date = getAttribute(startElement, "date");
                        double open = Double.parseDouble(getAttribute(startElement, "open"));
                        double high = Double.parseDouble(getAttribute(startElement, "high"));
                        double low = Double.parseDouble(getAttribute(startElement, "low"));
                        double close = Double.parseDouble(getAttribute(startElement, "close"));
                        long volume = Long.parseLong(getAttribute(startElement, "volume"));

                        // Create StockDataModel object
                        StockDataModel stock = new StockDataModel(date, open, high, low, close, volume);

                        // Process the stock data (send to consumer)
                        processor.accept(stock);
                    }
                }
            }
            eventReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to extract attribute values
    private String getAttribute(StartElement element, String attributeName) {
        Attribute attr = element.getAttributeByName(new javax.xml.namespace.QName(attributeName));
        return (attr != null) ? attr.getValue() : "";
    }
}
