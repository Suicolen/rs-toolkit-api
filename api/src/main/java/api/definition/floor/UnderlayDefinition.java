package api.definition.floor;

import api.definition.Definition;
import lombok.Data;

@Data
public class UnderlayDefinition implements Definition {
    private int id;
    private int rgb;
}
