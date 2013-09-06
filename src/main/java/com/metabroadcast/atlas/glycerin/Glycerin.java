package com.metabroadcast.atlas.glycerin;

import com.metabroadcast.atlas.glycerin.model.Availability;
import com.metabroadcast.atlas.glycerin.model.Broadcast;
import com.metabroadcast.atlas.glycerin.model.Programme;
import com.metabroadcast.atlas.glycerin.model.Service;
import com.metabroadcast.atlas.glycerin.model.Version;
import com.metabroadcast.atlas.glycerin.queries.AvailabilityQuery;
import com.metabroadcast.atlas.glycerin.queries.BroadcastsQuery;
import com.metabroadcast.atlas.glycerin.queries.ProgrammesQuery;
import com.metabroadcast.atlas.glycerin.queries.ServicesQuery;
import com.metabroadcast.atlas.glycerin.queries.VersionsQuery;

public interface Glycerin {

    GlycerinResponse<Programme> execute(ProgrammesQuery query) throws GlycerinException;

    GlycerinResponse<Availability> execute(AvailabilityQuery query) throws GlycerinException;

    GlycerinResponse<Broadcast> execute(BroadcastsQuery query) throws GlycerinException;

    GlycerinResponse<Service> execute(ServicesQuery query) throws GlycerinException;
    
    GlycerinResponse<Version> execute(VersionsQuery query) throws GlycerinException;

}
