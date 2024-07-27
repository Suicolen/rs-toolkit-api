package api.definition.floor;

import api.definition.Definition;
import lombok.Data;

@Data
public class OverlayDefinition implements Definition {
    private int id;
    private int rgb;
    private int texture;
    private boolean hideUnderlay;
    private int secondaryRgb;
}
