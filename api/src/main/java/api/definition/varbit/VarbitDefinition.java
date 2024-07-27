package api.definition.varbit;

import api.definition.Definition;
import lombok.Data;

@Data
public class VarbitDefinition implements Definition {
    private int id;
    private int varpIndex;
    private int leastSignificantBit;
    private int mostSignificantBit;
}