package com.metabroadcast.atlas.glycerin;

import java.text.ParseException;
import java.util.List;

import com.metabroadcast.atlas.glycerin.model.Deprecated;
import com.metabroadcast.atlas.glycerin.model.Feed;
import com.metabroadcast.atlas.glycerin.model.Feeds;
import com.metabroadcast.atlas.glycerin.queries.FeedsQuery;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.net.HostSpecifier;
import com.google.common.util.concurrent.RateLimiter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.junit.Assert.fail;

public class GlycerinDeprecatedElementsTest {

    private GlycerinHttpClient glycerinClient;
    private HashMultimap<String, String> ignorableDeprecations;
    private Optional<RateLimiter> limiter = Optional.absent();
    private Optional<String> root = Optional.absent();

    @Test(groups = "integration")
    private void testProgrammeForDeprecatedFields() throws GlycerinException {
        GlycerinQuery<Feeds, Feed> query = new FeedsQuery();
        GlycerinResponse<Feed> response = glycerinClient.get(query);
        
        List<String> deprecatedFields = Lists.newArrayList();

        for (Feed feed : response.getResults()) {
            String feedName = feed.getName();
            if (feed.getDeprecations() != null && feed.getDeprecations().getDeprecated() != null) {
                for (Deprecated deprecated : feed.getDeprecations().getDeprecated()) {
                    if (!ignorableDeprecations.containsEntry(feedName, deprecated.getName())) {
                        deprecatedFields.add(String.format(
                                "Feed - %s : deprecated element - %s",
                                feedName,
                                deprecated.getName()
                        ));
                    }
                }
            }
        }

        if (!deprecatedFields.isEmpty()) {
            String fields = "Deprecated fields: \n";
            for (String field : deprecatedFields) {
                fields = fields + field + "\n";
            }
            fail(fields);
        }
    }

    @Parameters({ "nitro.host", "nitro.apikey" })
    @BeforeClass(groups = "integration")
    public void setUp(String host, String apiKey) throws ParseException {
        this.ignorableDeprecations = HashMultimap.create();
        addDeprecatedElementsToTheIgnorableMap();

        glycerinClient = new GlycerinHttpClient(HostSpecifier.from(host), apiKey, limiter, root);
    }

    private void addDeprecatedElementsToTheIgnorableMap() {
        addProgrammesDeprecatedElements();
        addBroadcastsDeprecatedElements();
        addGroupsDeprecatedElements();
        addItemsDeprecatedElements();
        addSchedulesDeprecatedElements();
        addVersionsDeprecatedElements();
        addAvailabilityDeprecatedElements();
        addImagesDeprecatedElements();
        addMasterbrandsDeprecatedElements();
        addPeopleDeprecatedElements();
        addPipsDeprecatedElements();
        addPromotionsDeprecatedElements();
        addServicesDeprecatedElements();
    }

    private void addProgrammesDeprecatedElements() {
        List<String> programmesDeprecatedElements = Lists.newArrayList();
        programmesDeprecatedElements.add("availability_from");
        programmesDeprecatedElements.add("availability_to");
        programmesDeprecatedElements.add("initial_letter_stop");
        programmesDeprecatedElements.add("most_popular");
        programmesDeprecatedElements.add("versions_availability");
        programmesDeprecatedElements.add("people");
        programmesDeprecatedElements.add("titles");
        programmesDeprecatedElements.add("genre_groups");
        programmesDeprecatedElements.add("n:formats");
        programmesDeprecatedElements.add("n:genre_groups");
        programmesDeprecatedElements.add("n:image");

        ignorableDeprecations.putAll("Programmes", programmesDeprecatedElements);
    }

    private void addBroadcastsDeprecatedElements() {
        List<String> broadcastDeprecatedElements = Lists.newArrayList();
        broadcastDeprecatedElements.add("n:image");

        ignorableDeprecations.putAll("Broadcasts", broadcastDeprecatedElements);
    }

    private void addGroupsDeprecatedElements() {
        List<String> groupsDeprecatedElements = Lists.newArrayList();
        groupsDeprecatedElements.add("n:image");

        ignorableDeprecations.putAll("Groups", groupsDeprecatedElements);
    }


    private void addItemsDeprecatedElements() {
        List<String> itemsDeprecatedElements = Lists.newArrayList();
        itemsDeprecatedElements.add("offset");
        itemsDeprecatedElements.add("n:image");

        ignorableDeprecations.putAll("Items", itemsDeprecatedElements);
    }

    private void addSchedulesDeprecatedElements() {
        List<String> schedulesDeprecatedElements = Lists.newArrayList();
        schedulesDeprecatedElements.add("titles");
        schedulesDeprecatedElements.add("n:image");

        ignorableDeprecations.putAll("Schedules", schedulesDeprecatedElements);
    }

    private void addVersionsDeprecatedElements() {
        List<String> versionsDeprecatedElements = Lists.newArrayList();
        versionsDeprecatedElements.add("n:version_types");

        ignorableDeprecations.putAll("Versions", versionsDeprecatedElements);
    }

    /*
    * The following methods adds an empty list to the MultiHashMap because at the moment
    * there are no ignorable deprecated fields for the specific feed types.
    */

    private void addAvailabilityDeprecatedElements() {
        List<String> availabilityDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Availability", availabilityDeprecatedElements);
    }

    private void addImagesDeprecatedElements() {
        List<String> imagesDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Images", imagesDeprecatedElements);
    }

    private void addMasterbrandsDeprecatedElements() {
        List<String> masterbrandDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Masterbrands", masterbrandDeprecatedElements);
    }

    private void addPeopleDeprecatedElements() {
        List<String> peopleDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("People", peopleDeprecatedElements);
    }

    private void addPipsDeprecatedElements() {
        List<String> pipsDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Pips", pipsDeprecatedElements);
    }

    private void addPromotionsDeprecatedElements() {
        List<String> promotionsDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Promotions", promotionsDeprecatedElements);
    }

    private void addServicesDeprecatedElements() {
        List<String> servicesDeprecatedElements = Lists.newArrayList();
        ignorableDeprecations.putAll("Services", servicesDeprecatedElements);
    }
}
