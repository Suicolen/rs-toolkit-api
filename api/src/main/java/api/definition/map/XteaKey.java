package api.definition.map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record XteaKey(int archive, int group, @JsonProperty("name_hash") long nameHash, String name,
                      @JsonProperty("mapsquare") int mapSquare, int[] key) {
    public static int[] DEFAULT_KEY = {0, 0, 0, 0};
}
