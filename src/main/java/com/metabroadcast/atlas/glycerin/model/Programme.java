package com.metabroadcast.atlas.glycerin.model;

public abstract class Programme {

    public abstract boolean isEpisode();
    public abstract boolean isClip();
    public abstract boolean isSeries();
    public abstract boolean isBrand();
    
    public abstract Episode getAsEpisode();
    public abstract Clip getAsClip();
    public abstract Series getAsSeries();
    public abstract Brand getAsBrand();
    
    private static final class ProgrammeEpisode extends Programme {
        
        private final Episode value;
        
        public ProgrammeEpisode(Episode value) {
            this.value = value;
        }

        @Override public boolean isEpisode() { return true; }
        @Override public boolean isClip() { return false; }
        @Override public boolean isSeries() { return false; }
        @Override public boolean isBrand() { return false; }

        @Override public Episode getAsEpisode() { return value; }
        @Override public Clip getAsClip() { throw new IllegalStateException("value not a Clip"); }
        @Override public Series getAsSeries() { throw new IllegalStateException("value not a Series"); }
        @Override public Brand getAsBrand() { throw new IllegalStateException("value not a Brand"); }
        
    }

    private static final class ProgrammeClip extends Programme {
        
        private final Clip value;
        
        public ProgrammeClip(Clip value) {
            this.value = value;
        }
        
        @Override public boolean isEpisode() { return false; }
        @Override public boolean isClip() { return true; }
        @Override public boolean isSeries() { return false; }
        @Override public boolean isBrand() { return false; }
        
        @Override public Episode getAsEpisode() { throw new IllegalStateException("value not a Episode"); }
        @Override public Clip getAsClip() { return value; }
        @Override public Series getAsSeries() { throw new IllegalStateException("value not a Series"); }
        @Override public Brand getAsBrand() { throw new IllegalStateException("value not a Brand"); }
        
    }

    private static final class ProgrammeSeries extends Programme {
        
        private final Series value;
        
        public ProgrammeSeries(Series value) {
            this.value = value;
        }
        
        @Override public boolean isEpisode() { return false; }
        @Override public boolean isClip() { return false; }
        @Override public boolean isSeries() { return true; }
        @Override public boolean isBrand() { return false; }
        
        @Override public Episode getAsEpisode() { throw new IllegalStateException("value not a Episode"); }
        @Override public Clip getAsClip() { throw new IllegalStateException("value not a Clip"); }
        @Override public Series getAsSeries() { return value; }
        @Override public Brand getAsBrand() { throw new IllegalStateException("value not a Brand"); }
        
    }

    private static final class ProgrammeBrand extends Programme {
        
        private final Brand value;
        
        public ProgrammeBrand(Brand value) {
            this.value = value;
        }
        
        @Override public boolean isEpisode() { return false; }
        @Override public boolean isClip() { return false; }
        @Override public boolean isSeries() { return false; }
        @Override public boolean isBrand() { return true; }
        
        @Override public Episode getAsEpisode() { throw new IllegalStateException("value not a Episode"); }
        @Override public Clip getAsClip() { throw new IllegalStateException("value not a Clip"); }
        @Override public Series getAsSeries() { throw new IllegalStateException("value not a Series"); }
        @Override public Brand getAsBrand() { return value; }
        
    }

    public static Programme valueOf(Object programmeType) {
        if (programmeType instanceof Episode) {
            return new ProgrammeEpisode((Episode) programmeType);
        }
        if (programmeType instanceof Clip) {
            return new ProgrammeClip((Clip) programmeType);
        }
        if (programmeType instanceof Series) {
            return new ProgrammeSeries((Series) programmeType);
        }
        if (programmeType instanceof Brand) {
            return new ProgrammeBrand((Brand) programmeType);
        }
        throw new IllegalArgumentException("Can't create Programme from "
                + programmeType.getClass().getSimpleName());
    }
    
}
