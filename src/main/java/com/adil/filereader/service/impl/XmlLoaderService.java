package com.adil.filereader.service.impl;

import com.adil.filereader.model.StockDataModel;
import com.adil.filereader.service.LoaderService;

import javax.xml.namespace.QName;
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

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("value")) {
                        String date = getAttribute(startElement, "date");
                        double open = Double.parseDouble(getAttribute(startElement, "open"));
                        double high = Double.parseDouble(getAttribute(startElement, "high"));
                        double low = Double.parseDouble(getAttribute(startElement, "low"));
                        double close = Double.parseDouble(getAttribute(startElement, "close"));
                        long volume = Long.parseLong(getAttribute(startElement, "volume"));

                        StockDataModel stock = new StockDataModel(date, open, high, low, close, volume);

                        processor.accept(stock);
                    }
                }
            }
            eventReader.close();
        } catch (Exception e) {
//            info.appendText(e.getMessage());
        }
    }

    private String getAttribute(StartElement element, String attributeName) {
        Attribute attr = element.getAttributeByName(new QName(attributeName));
        return (attr != null) ? attr.getValue() : "";
    }
}
