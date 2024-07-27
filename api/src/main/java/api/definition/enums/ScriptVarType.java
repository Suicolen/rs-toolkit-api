package api.definition.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum ScriptVarType {
    INTEGER(0, 'i', "integer"),
    BOOLEAN(1, '1', "boolean"),
    SEQ(6, 'A', "seq"),
    COLOUR(7, 'C', "colour"),
    /**
     * Also known as {@code Widget}.
     */
    COMPONENT(9, 'I', "component"),
    IDKIT(10, 'K', "idkit"),
    MIDI(11, 'M', "midi"),
    /**
     * Another version of {@code OBJ}, but means that on Jagex's side they used the internal name for an item.
     */
    NAMEDOBJ(13, 'O', "namedobj"),
    SYNTH(14, 'P', "synth"),
    STAT(17, 'S', "stat"),
    COORDGRID(22, 'c', "coordgrid"),
    GRAPHIC(23, 'd', "graphic"),
    FONTMETRICS(25, 'f', "fontmetrics"),
    ENUM(26, 'g', "enum"),
    JINGLE(28, 'j', "jingle"),
    /**
     * Also known as {@code Object}.
     */
    LOC(30, 'l', "loc"),
    MODEL(31, 'm', "model"),
    NPC(32, 'n', "npc"),
    /**
     * Also known as {@code Item}.
     */
    OBJ(33, 'o', "obj"),
    STRING(36, 's', "string"),
    SPOTANIM(37, 't', "spotanim"),
    INV(39, 'v', "inv"),
    TEXTURE(40, 'x', "texture"),
    CHAR(42, 'z', "char"),
    MAPSCENEICON(55, '£', "mapsceneicon"),
    MAPELEMENT(59, 'µ', "mapelement"),
    HITMARK(62, '×', "hitmark"),
    STRUCT(73, 'J', "struct"),
    DBROW(74, 'Ð', "dbrow"),
    ;

    private static final Map<Integer, ScriptVarType> idToTypeMap = new HashMap<>();
    private static final Map<Character, ScriptVarType> keyToTypeMap = new HashMap<>();

    static {
        for (ScriptVarType type : values()) {
            if (type.id != -1) {
                idToTypeMap.put(type.id, type);
            }
            keyToTypeMap.put(type.keyChar, type);
        }
    }

    public static ScriptVarType forId(int id) {
        return idToTypeMap.get(id);
    }

    public static ScriptVarType forCharKey(char key) {
        return keyToTypeMap.get(key);
    }

    /**
     * The type id when encoding or decoding types from some data structures.
     */
    private final int id;

    /**
     * The character used when encoding or decoding types.
     */
    private final char keyChar;

    /**
     * The full name of the var type.
     */
    private final String fullName;

}