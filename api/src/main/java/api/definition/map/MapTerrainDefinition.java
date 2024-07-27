package api.definition.map;


import lombok.Data;

@Data
public class MapTerrainDefinition {
    private int mapSquareX;
    private int mapSquareZ;
    private Tile[][][] tiles = new Tile[4][64][64];
}
