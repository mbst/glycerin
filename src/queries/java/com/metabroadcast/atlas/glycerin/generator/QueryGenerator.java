package com.metabroadcast.atlas.glycerin.generator;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;

import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Feeds;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;

public class QueryGenerator {

    private final String srcPkg;
    private final File feedDescriptor;
    private final String xmlModelPkg;

    public QueryGenerator(File feedDescriptor, String xmlModelPkg, String srcPkg) {
        this.feedDescriptor = feedDescriptor;
        this.xmlModelPkg = xmlModelPkg;
        this.srcPkg = srcPkg;
    }
    
    public static void main(String[] args) throws Exception {

        String outputDir = args[0];
        String feedDescriptorPath = args[1];
        String srcPkg = args[2];
        String xmlModelPkg = args[3];
        
        new QueryGenerator(new File(feedDescriptorPath), xmlModelPkg, srcPkg)
            .writeQueriesTo(create(outputDir));
        
    }

    private void writeQueriesTo(File outputDir) throws Exception {
        Feeds feeds = (Feeds) JAXBContext.newInstance(xmlModelPkg)
                .createUnmarshaller()
                .unmarshal(feedDescriptor);
        
        JCodeModel model = new JCodeModel();
        JPackage pkg = model._package(srcPkg);
        FeedQueryGenerator feedQueryGenerator = new FeedQueryGenerator(model, pkg);
        for (Feed feed : feeds.getFeed()) {
            feedQueryGenerator.generateQuery(feed);
        }
        
        model.build(outputDir);
    }

    private static File create(String outputDir) throws IOException {
        File file = new File(outputDir);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
