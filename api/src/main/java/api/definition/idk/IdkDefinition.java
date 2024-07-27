package api.definition.idk;

import api.definition.Definition;
import lombok.Data;

@Data
public class IdkDefinition implements Definition {
    private int id;
    private int[] originalColors;
    private int[] targetColors;
    private int[] originalTextures;
    private int[] targetTextures;
    private int bodyPartId = -1;
    private int[] modelIds;
    private int[] chatheadModelIds = {-1, -1, -1, -1, -1};
    private boolean nonSelectable = false;
}
