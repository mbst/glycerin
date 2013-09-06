package com.metabroadcast.atlas.glycerin;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

final public class NitroJaxbContext {
    
    private final JAXBContext context;

    public NitroJaxbContext() {
        try {
            context = JAXBContext.newInstance("com.metabroadcast.atlas.glycerin.model");
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXB Context", e);
        }
    }
    
    public JAXBContext getContext() {
        return context;
    }
    
}
