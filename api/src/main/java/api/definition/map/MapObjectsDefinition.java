package api.definition.map;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MapObjectsDefinition {
    private int mapSquareX;
    private int mapSquareZ;
    private List<MapObject> objects = new ArrayList<>();
}