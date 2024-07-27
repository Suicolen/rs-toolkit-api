package api.definition.openrs2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record OpenRS2CacheModel(int id, String scope, String game, CacheEnvironment environment,
                                String language,
                                List<CacheBuild> builds, Instant timestamp, List<String> sources,
                                @JsonProperty("valid_indexes") int validIndexes,
                                int indexes, @JsonProperty("valid_groups") int validGroups,
                                int groups, @JsonProperty("valid_keys") int validKeys, int keys,
                                long size,
                                int blocks,
                                @JsonProperty("disk_store_valid") boolean diskStoreValid) {

    public int getFirstBuildMajor() {
        return builds.getFirst().major();
    }

    public boolean hasBuilds() {
        return builds != null && !builds.isEmpty();
    }

    public boolean hasTimestamp() {
        return timestamp != null;
    }

    public boolean isOSRS() {
        return game.equalsIgnoreCase("oldschool");
    }

}
