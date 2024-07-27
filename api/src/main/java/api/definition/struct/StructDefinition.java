package api.definition.struct;

import api.definition.Definition;
import lombok.Data;

import java.util.Map;

@Data
public class StructDefinition implements Definition {
    private int id;
    private Map<Integer, Object> params = null;
}