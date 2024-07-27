package api.definition.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapDefinition {
    private MapTerrainDefinition terrain;
    private MapObjectsDefinition objects;
}
