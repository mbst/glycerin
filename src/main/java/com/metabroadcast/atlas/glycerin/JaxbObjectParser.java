package com.metabroadcast.atlas.glycerin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.google.api.client.util.ObjectParser;


class JaxbObjectParser implements ObjectParser {

    private final JAXBContext context;

    public JaxbObjectParser(JAXBContext context) {
        this.context = checkNotNull(context);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass)
            throws IOException {
        try {
            return (T) context.createUnmarshaller().unmarshal(in);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
        try {
            return context.createUnmarshaller().unmarshal(in);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
        try {
            return (T) context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Object parseAndClose(Reader reader, Type dataType) throws IOException {
        try {
            return context.createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

}
