package com.metabroadcast.atlas.glycerin;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;


class BaseGlycerinResponse<RAW, TRANSFORMED> implements GlycerinResponse<TRANSFORMED> {

    private final ImmutableList<TRANSFORMED> results;
    private final GlycerinHttpClient client;
    private final GlycerinQuery<RAW, TRANSFORMED> next;

    public BaseGlycerinResponse(Iterable<TRANSFORMED> results, GlycerinHttpClient client, @Nullable GlycerinQuery<RAW, TRANSFORMED> next) {
        this.client = client;
        this.next = next;
        this.results = ImmutableList.copyOf(results);
    }

    /* (non-Javadoc)
     * @see com.metabroadcast.atlas.glycerin.GlycerinResponse#getResults()
     */
    @Override
    public ImmutableList<TRANSFORMED> getResults() {
        return results;
    }
    
    /* (non-Javadoc)
     * @see com.metabroadcast.atlas.glycerin.GlycerinResponse#hasNext()
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    /* (non-Javadoc)
     * @see com.metabroadcast.atlas.glycerin.GlycerinResponse#getNext()
     */
    @Override
    public GlycerinResponse<TRANSFORMED> getNext() throws GlycerinException {
        checkState(hasNext());
        return client.get(next);
    }
    
}
