package api.definition.enums;

import api.definition.Definition;
import lombok.Data;

@Data
public class EnumDefinition implements Definition {
    private int id;
    private int[] intVals;
    private ScriptVarType keyType;
    private ScriptVarType valType;
    private String defaultString = "null";
    private int defaultInt;
    private int size;
    private int[] keys;
    private String[] stringVals;
}