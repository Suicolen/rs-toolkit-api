package api.definition.map;

import lombok.Data;

@Data
public class Tile {
    private int height;
    private int attrOpcode;
    private int settings;
    private int overlayId;
    private int overlayPath;
    private int overlayRotation;
    private int underlayId;
}
