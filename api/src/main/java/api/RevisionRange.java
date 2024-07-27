package api;

public record RevisionRange(RevisionType type, int minimumRevision, int maximumRevision) {

    public RevisionRange(RevisionType type, int revision) {
        this(type, revision, revision);
    }

    public boolean includes(int revision) {
        return revision >= minimumRevision && revision <= maximumRevision;
    }

    public boolean greaterOrEqualTo(int revision) {
        return maximumRevision >= revision;
    }

    public boolean lowerOrEqualTo(int revision) {
        return minimumRevision <= revision;
    }

    public boolean isBetween(int min, int max) {
        return minimumRevision >= min && maximumRevision <= max;
    }

    public boolean isOSRS() {
        return type == RevisionType.OSRS;
    }

    public boolean isRS2() {
        return type == RevisionType.RS2;
    }

    public boolean isRS3() {
        return type == RevisionType.RS3;
    }



}
