package com.metabroadcast.atlas.glycerin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.api.client.repackaged.com.google.common.base.Strings;
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
        GlycerinResponse<Programme> response = glycerin.execute(ProgrammesQuery.builder().withPid("b006m86d").build());
        
        Programme eastEnders = Iterables.getOnlyElement(response.getResults());
        assertTrue(eastEnders.isBrand());
    }
    
}
