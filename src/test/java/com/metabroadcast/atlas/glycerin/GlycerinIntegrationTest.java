package com.metabroadcast.atlas.glycerin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.atlas.glycerin.model.MasterBrand;
import com.metabroadcast.atlas.glycerin.model.Programme;
import com.metabroadcast.atlas.glycerin.model.Service;
import com.metabroadcast.atlas.glycerin.model.Version;
import com.metabroadcast.atlas.glycerin.queries.MasterBrandsQuery;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesMixin;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesQuery;
import com.metabroadcast.atlas.glycerin.queries.ServicesQuery;
import com.metabroadcast.atlas.glycerin.queries.VersionsQuery;

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

    @Test(groups = "integration")
    public void testGetNextPageWhenParameterIsRepeated() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b006m86d", "b007t575")
                .withPageSize(1)
                .build();
        GlycerinResponse<Programme> response = glycerin.execute(query);
        
        response = response.getNext();
        
        assertFalse(response.getResults().isEmpty());
    }

    @Test(groups = "integration")
    public void testVersionResults() throws GlycerinException {
        VersionsQuery versionsQuery = VersionsQuery.builder().withPid("p0361000").build();
        GlycerinResponse<Version> response = glycerin.execute(versionsQuery);
        Version version = Iterables.getOnlyElement(response.getResults());
        assertEquals("p0361000", version.getPid());
    }

    @Test(groups = "integration")
    public void testServiceResults() throws GlycerinException {
        ServicesQuery servicesQuery = ServicesQuery.builder().build();
        GlycerinResponse<Service> response = glycerin.execute(servicesQuery);
        assertFalse(response.getResults().isEmpty());
    }

    @Test(groups = "integration")
    public void testMasterbrandResults() throws GlycerinException {
        MasterBrandsQuery servicesQuery = MasterBrandsQuery.builder().build();
        GlycerinResponse<MasterBrand> response = glycerin.execute(servicesQuery);
        assertFalse(response.getResults().isEmpty());
    }


    @Test(groups = "integration")
    public void testProgrammesQueryWithGenres() throws GlycerinException {
        ProgrammesQuery programmesQuery = ProgrammesQuery.builder().withPid("b039gr8y").withMixins(
                ProgrammesMixin.GENRE_GROUPINGS).build();
        GlycerinResponse<Programme> response = glycerin.execute(programmesQuery);
        Programme programme = Iterables.getOnlyElement(response.getResults());
        String value = programme.getAsEpisode().getGenreGroupings().getGenreGroup().get(0).getGenres().getGenre().get(0).getValue();
        assertEquals("Factual", value);
    }

    @Test(groups = "integration")
    public void testProgrammesQueryWithImages() throws GlycerinException {
        ProgrammesQuery programmesQuery = ProgrammesQuery.builder().withPid("b039gr8y").withMixins(
                ProgrammesMixin.IMAGES).build();
        GlycerinResponse<Programme> response = glycerin.execute(programmesQuery);
        Programme programme = Iterables.getOnlyElement(response.getResults());
        String value = programme.getAsEpisode().getImage().getTemplateUrl();
        assertEquals("http://ichef.bbci.co.uk/images/ic/$recipe/p01k0trp.jpg", value);

    }

    @Test(groups = "integration")
    public void testProgrammesQueryWithAncestorTitles() throws GlycerinException {
        ProgrammesQuery programmesQuery = ProgrammesQuery.builder().withPid("b039gr8y").withMixins(
                ProgrammesMixin.ANCESTOR_TITLES).build();
        GlycerinResponse<Programme> response = glycerin.execute(programmesQuery);
        Programme programme = Iterables.getOnlyElement(response.getResults());
        String value = programme.getAsEpisode().getAncestorTitles().getSeries().get(0).getPid();
        assertEquals("p01db7nj", value);

    }

    @Test(groups = "integration")
    public void testProgrammesQueryWithContributions() throws GlycerinException {
        ProgrammesQuery programmesQuery = ProgrammesQuery.builder().withPid("b039gr8y").withMixins(
                ProgrammesMixin.CONTRIBUTIONS).build();
        GlycerinResponse<Programme> response = glycerin.execute(programmesQuery);
        Programme programme = Iterables.getOnlyElement(response.getResults());
        String value = programme.getAsEpisode().getContributions().getContributionsMixinContribution().get(0).getContributor().getName().getGiven();
        assertEquals("Sue", value);
    }

}
