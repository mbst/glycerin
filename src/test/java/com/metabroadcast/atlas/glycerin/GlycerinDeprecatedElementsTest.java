package com.metabroadcast.atlas.glycerin;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Feeds;
import com.metabroadcast.atlas.glycerin.queries.FeedsQuery;

public class GlycerinDeprecatedElementsTest {

    private GlycerinHttpClient glycerinClient;
    private HashMap<String, List<String>> ignorableDeprecations;

    @Parameters({ "nitro.host", "nitro.apikey" })
    @BeforeClass(groups = "integration")
    public void setup(String host, String apiKey) throws ParseException {
//        List<String> programmesList = ImmutableList.copyOf(getProgrammesDeprecatedElements());
//        List<String> availabilityList = ImmutableList.copyOf(getAvailabilityDeprecatedElements());
//        List<String> broadcastsList = ImmutableList.copyOf(getBroadcastsDeprecatedElements());
//        List<String> groupsList = ImmutableList.copyOf(getGroupsDeprecatedElements());
//        List<String> imagesList = ImmutableList.copyOf(getImagesDeprecatedElements());
//        List<String> masterbrandsList = ImmutableList.copyOf(getMasterbrandsDeprecatedElements());
//        List<String> peopleList = ImmutableList.copyOf(getPeopleDeprecatedElements());
//        List<String> pipsList = ImmutableList.copyOf(getPipsDeprecatedElements());
//        List<String> promotionsList = ImmutableList.copyOf(getPromotionsDeprecatedElements());
//        List<String> schedulesList = ImmutableList.copyOf(getSchedulesDeprecatedElements());
//        List<String> servicesList = ImmutableList.copyOf(getServicesDeprecatedElements());
//        List<String> versionsList = ImmutableList.copyOf(getVersionsDeprecatedElements());

        glycerinClient = new GlycerinHttpClient(HostSpecifier.from(host), apiKey, null,
                Optional.of("nitro/api"));
    }

    @Test(groups = "integration")
    public void testProgrammeForDeprecatedFields() throws GlycerinException {
        GlycerinQuery<Feeds, Feed> query = new FeedsQuery();
        GlycerinResponse<Feed> response = glycerinClient.get(query);

        response.getResults().stream().forEach(feed -> {
            feed.getName();
        });
    }
// TODO : Need to add all the deprecated elements for each feed type.
//    public List<String> getProgrammesDeprecatedElements() {
//        List<String> programmesDeprecatedElements = new ArrayList<>();
//        programmesDeprecatedElements.add();
//        programmesDeprecatedElements.add();
//        programmesDeprecatedElements.add();
//        programmesDeprecatedElements.add();
//        programmesDeprecatedElements.add();
//        programmesDeprecatedElements.add();
//    }
//
//    public List<String> getAvailabilityDeprecatedElements() {
//
//    }
//
//    public List<String> getBroadcastsDeprecatedElements() {
//
//    }
//
//    public List<String> getGroupsDeprecatedElements() {
//
//    }
//
//    public List<String> getImagesDeprecatedElements() {
//
//    }
//
//    public List<String> getMasterbrandsDeprecatedElements() {
//
//    }
//
//    public List<String> getPeopleDeprecatedElements() {
//
//    }
//
//    public List<String> getPipsDeprecatedElements() {
//
//    }
//
//    public List<String> getPromotionsDeprecatedElements() {
//
//    }
//
//    public List<String> getSchedulesDeprecatedElements() {
//
//    }
//
//    public List<String> getServicesDeprecatedElements() {
//
//    }
//
//    public List<String> getVersionsDeprecatedElements() {
//
//    }
}
