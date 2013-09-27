package com.metabroadcast.atlas.glycerin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.atlas.glycerin.model.Programme;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesQuery;

public class GlycerinIntegrationTest {

    private Glycerin glycerin;

    @Parameters({"nitro.host", "nitro.apikey"})
    @BeforeClass(groups = "integration")
    public void setup(String host, String apiKey) {
        glycerin = XmlGlycerin.builder(apiKey)
                .withHost(HostSpecifier.fromValid(host))
                .build();
    }
    
    @Test(groups = "integration")
    public void testGetsAProgramme() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withPid("b006m86d").build();
        GlycerinResponse<Programme> response = glycerin.execute(query);
        
        Programme eastEnders = Iterables.getOnlyElement(response.getResults());
        assertTrue(eastEnders.isBrand());
    }

    @Test(groups = "integration")
    public void testHasNextReturnsFalseWhenNoNext() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withPid("b006m86d").build();
        GlycerinResponse<Programme> response = glycerin.execute(query);
        
        assertFalse(response.hasNext());
    }

    @Test(groups = "integration")
    public void testHasNextReturnsTrueWhenNextPageAvailable() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b006m86d")
                .withPageSize(1)
                .build();
        GlycerinResponse<Programme> response = glycerin.execute(query);
        
        assertTrue(response.hasNext());
    }

    @Test(groups = "integration")
    public void testGetNextPage() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b006m86d")
                .withPageSize(1)
                .build();
        GlycerinResponse<Programme> response = glycerin.execute(query);
        
        response = response.getNext();
        
        assertFalse(response.getResults().isEmpty());
    }
    
}
