package com.metabroadcast.atlas.glycerin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import com.google.common.collect.Ordering;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.atlas.glycerin.model.MasterBrand;
import com.metabroadcast.atlas.glycerin.model.Programme;
import com.metabroadcast.atlas.glycerin.model.Service;
import com.metabroadcast.atlas.glycerin.model.Version;
import com.metabroadcast.atlas.glycerin.model.AvailableVersions;
import com.metabroadcast.atlas.glycerin.queries.MasterBrandsMixin;
import com.metabroadcast.atlas.glycerin.queries.MasterBrandsQuery;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesMixin;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesQuery;
import com.metabroadcast.atlas.glycerin.queries.ServiceTypeOption;
import com.metabroadcast.atlas.glycerin.queries.ServicesQuery;
import com.metabroadcast.atlas.glycerin.queries.VersionsQuery;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesSort;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesSortDirection;
import com.metabroadcast.atlas.glycerin.queries.AvailabilityOption;

import java.util.ArrayList;
import java.util.List;


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
        MasterBrandsQuery servicesQuery = MasterBrandsQuery.builder().withMixins(MasterBrandsMixin.IMAGES).build();
        GlycerinResponse<MasterBrand> response = glycerin.execute(servicesQuery);
        assertFalse(response.getResults().isEmpty());
    }

    @Test(groups = "integration")
    public void testServiceResultsWithServiceTypes() throws GlycerinException {
        ServicesQuery servicesQuery = ServicesQuery.builder()
                .withServiceType(ServiceTypeOption.LOCAL_RADIO,ServiceTypeOption.REGIONAL_RADIO,
                        ServiceTypeOption.NATIONAL_RADIO, ServiceTypeOption.TV)
                .build();
        GlycerinResponse<Service> response = glycerin.execute(servicesQuery);
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

    @Test(groups = "integration")
    public void testProgrammesQueryWithTitleAscendingSorting() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b039gr8y").
                        sortBy(ProgrammesSort.TITLE, ProgrammesSortDirection.ASCENDING).
                        build();
        GlycerinResponse<Programme> response = glycerin.execute(query);

        List<String> programsTitles = new ArrayList<>();
        for (Programme programme : response.getResults()) {
            programsTitles.add(programme.getAsClip().getTitle());
        }
        assertTrue(Ordering.natural().isOrdered(programsTitles));
    }

    @Test(groups = "integration")
    public void testProgrammesQueryWithScheduleDescendingSorting() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b007t575").
                        withAvailability(AvailabilityOption.AVAILABLE).
                        withMixins(ProgrammesMixin.AVAILABLE_VERSIONS).
                        withPageSize(25).
                        sortBy(ProgrammesSort.SCHEDULED_START, ProgrammesSortDirection.DESCENDING).
                        build();
        GlycerinResponse<Programme> response = glycerin.execute(query);

        List<Long> startSchedules = new ArrayList<>();
        for (Programme programme : response.getResults()) {
            if (programme.isSeries() || programme.isClip())  {
                continue;
            }
            AvailableVersions availableVersions = programme.getAsEpisode().getAvailableVersions();
            Long scheduledStartTime = obtainScheduledStartTime(availableVersions);
            if (scheduledStartTime == null) {
                continue;
            }
            startSchedules.add(scheduledStartTime);
        }
        assertTrue(Ordering.natural().reverse().isOrdered(startSchedules));
    }

    @Test(groups = "integration")
    public void testProgrammesQueryWithPidDefaultDescendingSorting() throws GlycerinException {
        ProgrammesQuery query = ProgrammesQuery.builder()
                .withDescendantsOf("b007t575").
                        sortBy(ProgrammesSort.PID).
                        build();
        GlycerinResponse<Programme> response = glycerin.execute(query);

        List<String> programmePids = new ArrayList<>();
        for (Programme programme : response.getResults()) {
            if (programme.isEpisode()) {
                programmePids.add(programme.getAsEpisode().getPid());
            } else if (programme.isSeries()) {
                programmePids.add(programme.getAsSeries().getPid());
            } else if (programme.isClip()) {
                programmePids.add(programme.getAsClip().getPid());
            }
            assertTrue(Ordering.natural().reverse().isOrdered(programmePids));
        }
    }

    private Long obtainScheduledStartTime(AvailableVersions availableVersions) {
        try {
            for( AvailableVersions.Version av: availableVersions.getVersion()){
                for (AvailableVersions.Version.Availabilities aa: av.getAvailabilities())   {
                    for(AvailableVersions.Version.Availabilities.Availability a: aa.getAvailableVersionsAvailability()) {
                        if ("future".equals(a.getStatus())) continue;
                        return a.getScheduledStart().toGregorianCalendar().getTimeInMillis();
                    }
                }
            }
        } catch (Exception e) {}
        return null;
    }
}
