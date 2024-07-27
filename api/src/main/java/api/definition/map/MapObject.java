package api.definition.map;

import api.model.Position;
import lombok.Data;

@Data
public class MapObject {
    private final int id;
    private final int type;
    private final int orientation;
    private final Position position;
}
