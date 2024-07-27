package api.definition.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import api.definition.Definition;
import api.definition.NamedDefinition;
import lombok.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static api.definition.item.ItemDefinition.Defaults.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDefinition implements Definition, NamedDefinition {
    private int id;

    private String name = DEFAULT_NAME;

    private String examine = DEFAULT_EXAMINE;
    private String unknown1 = DEFAULT_UNKNOWN1;

    private int resizeX = DEFAULT_RESIZE_X;
    private int resizeY = DEFAULT_RESIZE_Y;
    private int resizeZ = DEFAULT_RESIZE_Z;

    private int pitch = DEFAULT_ICON_CAMERA_PITCH;
    private int yaw = DEFAULT_ICON_CAMERA_YAW;
    private int roll = DEFAULT_ICON_ROLL;

    private int cost = DEFAULT_COST;
    private boolean tradeable = DEFAULT_TRADEABLE;
    private int stackable = DEFAULT_STACKABLE;
    private int inventoryModelId = DEFAULT_INVENTORY_MODEL_ID;
    private boolean members = DEFAULT_MEMBERS;

    private int[] originalColors = DEFAULT_ORIGINAL_COLORS;
    private int[] targetColors = DEFAULT_TARGET_COLORS;
    private int[] originalTextures = DEFAULT_ORIGINAL_TEXTURES;
    private int[] targetTextures = DEFAULT_TARGET_TEXTURES;

    private int zoom = DEFAULT_ZOOM;
    private int iconXOffset = DEFAULT_ICON_X_OFFSET;
    private int iconYOffset = DEFAULT_ICON_Y_OFFSET;

    private int ambient = DEFAULT_AMBIENT;
    private int contrast = DEFAULT_CONTRAST;

    private int[] stackCount = DEFAULT_STACK_COUNT;
    private int[] stackId = DEFAULT_STACK_ID;

    private String[] groundOptions = DEFAULT_GROUND_OPTIONS.clone();
    private String[] options = DEFAULT_OPTIONS.clone();
    private int maleEquipModelId = DEFAULT_MALE_EQUIP_MODEL_ID;
    private int maleModel1 = DEFAULT_MALE_MODEL_1;
    private int maleModel2 = DEFAULT_MALE_MODEL_2;
    private int maleOffset = DEFAULT_MALE_OFFSET;
    private int maleHeadModel = DEFAULT_MALE_HEAD_MODEL;
    private int maleHeadModel2 = DEFAULT_MALE_HEAD_MODEL_2;

    private int femaleEquipModelId = DEFAULT_FEMALE_EQUIP_MODEL_ID;
    private int femaleModel1 = DEFAULT_FEMALE_MODEL_1;
    private int femaleModel2 = DEFAULT_FEMALE_MODEL_2;
    private int femaleOffset = DEFAULT_FEMALE_OFFSET;
    private int femaleHeadModel = DEFAULT_FEMALE_HEAD_MODEL;
    private int femaleHeadModel2 = DEFAULT_FEMALE_HEAD_MODEL_2;

    private int category = DEFAULT_CATEGORY;

    private int notedID = DEFAULT_NOTED_ID;
    private int notedTemplate = DEFAULT_NOTED_TEMPLATE;

    private int team = DEFAULT_TEAM;

    private int shiftClickDropIndex = DEFAULT_SHIFT_CLICK_DROP_INDEX;

    private int boughtId = DEFAULT_BOUGHT_ID;
    private int boughtTemplateId = DEFAULT_BOUGHT_TEMPLATE_ID;

    private int placeholderId = DEFAULT_PLACEHOLDER_ID;
    private int placeholderTemplateId = DEFAULT_PLACEHOLDER_TEMPLATE_ID;

    private int weight = DEFAULT_WEIGHT;
    private int wearPos1 = DEFAULT_WEAR_POS;
    private int wearPos2 = DEFAULT_WEAR_POS;
    private int wearPos3 = DEFAULT_WEAR_POS;

    private Map<Integer, Object> params = DEFAULT_PARAMS;

    /**
     * 718
     */

    private int multiStackSize = -1;
    private int[] spriteRecolorIndices = null;
    private int tooltipColor = 0;
    private boolean hasTooltipColor = false;
    private int unknownInt6 = 0;
    private int lendId = -1;
    private int lendTemplateId = -1;
    private int maleXWearOffset = 0;
    private int maleYWearOffset = 0;
    private int maleZWearOffset = 0;
    private int femaleXWearOffset = 0;
    private int femaleYWearOffset = 0;
    private int femaleZWearOffset = 0;
    private int cursor1Op = -1;
    private int cursor1 = -1;
    private int cursor2Op = -1;
    private int cursor2 = -1;
    private int[] quests;
    private int pickSizeShift = 0;

    @JsonIgnore
    public List<Integer> getModelIds() {
        return List.of(maleEquipModelId, femaleEquipModelId, maleModel1,
                maleModel2, femaleModel1, femaleModel2,
                inventoryModelId, maleHeadModel, maleHeadModel2,
                femaleHeadModel, femaleHeadModel2);
    }

    @JsonIgnore
    public List<Integer> getMaleModelIds() {
        return IntStream.of(maleEquipModelId, maleModel1, maleModel2)
                .filter(id -> id != -1)
                .boxed()
                .toList();
    }

    @JsonIgnore
    public List<Integer> getFemaleModelIds() {
        return IntStream.of(femaleEquipModelId, femaleModel1, femaleModel2)
                .filter(id -> id != -1)
                .boxed()
                .toList();
    }

    public ItemDefinition copy() {
        ItemDefinition copy = new ItemDefinition();
        copy.id = id;
        copy.name = name;
        copy.examine = examine;
        copy.unknown1 = unknown1;
        copy.resizeX = resizeX;
        copy.resizeY = resizeY;
        copy.resizeZ = resizeZ;
        copy.pitch = pitch;
        copy.yaw = yaw;
        copy.roll = roll;
        copy.cost = cost;
        copy.tradeable = tradeable;
        copy.stackable = stackable;
        copy.inventoryModelId = inventoryModelId;
        copy.members = members;
        copy.originalColors = originalColors;
        copy.targetColors = targetColors;
        copy.originalTextures = originalTextures;
        copy.targetTextures = targetTextures;
        copy.zoom = zoom;
        copy.iconXOffset = iconXOffset;
        copy.iconYOffset = iconYOffset;
        copy.ambient = ambient;
        copy.contrast = contrast;
        copy.stackCount = stackCount;
        copy.stackId = stackId;
        copy.groundOptions = groundOptions;
        copy.options = options;
        copy.maleEquipModelId = maleEquipModelId;
        copy.maleModel1 = maleModel1;
        copy.maleModel2 = maleModel2;
        copy.maleOffset = maleOffset;
        copy.maleHeadModel = maleHeadModel;
        copy.maleHeadModel2 = maleHeadModel2;
        copy.femaleEquipModelId = femaleEquipModelId;
        copy.femaleModel1 = femaleModel1;
        copy.femaleModel2 = femaleModel2;
        copy.femaleOffset = femaleOffset;
        copy.femaleHeadModel = femaleHeadModel;
        copy.femaleHeadModel2 = femaleHeadModel2;
        copy.category = category;
        copy.notedID = notedID;
        copy.notedTemplate = notedTemplate;
        copy.team = team;
        copy.shiftClickDropIndex = shiftClickDropIndex;
        copy.boughtId = boughtId;
        copy.boughtTemplateId = boughtTemplateId;
        copy.placeholderId = placeholderId;
        copy.placeholderTemplateId = placeholderTemplateId;
        copy.weight = weight;
        copy.wearPos1 = wearPos1;
        copy.wearPos2 = wearPos2;
        copy.wearPos3 = wearPos3;
        copy.params = params;
        copy.multiStackSize = multiStackSize;
        copy.spriteRecolorIndices = spriteRecolorIndices;
        copy.tooltipColor = tooltipColor;
        copy.hasTooltipColor = hasTooltipColor;
        copy.unknownInt6 = unknownInt6;
        copy.lendId = lendId;
        copy.lendTemplateId = lendTemplateId;
        copy.maleXWearOffset = maleXWearOffset;
        copy.maleYWearOffset = maleYWearOffset;
        copy.maleZWearOffset = maleZWearOffset;
        copy.femaleXWearOffset = femaleXWearOffset;
        copy.femaleYWearOffset = femaleYWearOffset;
        copy.femaleZWearOffset = femaleZWearOffset;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        copy.quests = quests;
        copy.pickSizeShift = pickSizeShift;
        return copy;
    }

    public ItemDefinition deepCopy() {
        ItemDefinition copy = new ItemDefinition();
        copy.id = id;
        copy.name = name;
        copy.examine = examine;
        copy.unknown1 = unknown1;
        copy.resizeX = resizeX;
        copy.resizeY = resizeY;
        copy.resizeZ = resizeZ;
        copy.pitch = pitch;
        copy.yaw = yaw;
        copy.roll = roll;
        copy.cost = cost;
        copy.tradeable = tradeable;
        copy.stackable = stackable;
        copy.inventoryModelId = inventoryModelId;
        copy.members = members;
        if (originalColors != null) {
            copy.originalColors = Arrays.copyOf(originalColors, originalColors.length);
        }
        if (targetColors != null) {
            copy.targetColors = Arrays.copyOf(targetColors, targetColors.length);
        }
        if (originalTextures != null) {
            copy.originalTextures = Arrays.copyOf(originalTextures, originalTextures.length);
        }
        if (targetTextures != null) {
            copy.targetTextures = Arrays.copyOf(targetTextures, targetTextures.length);
        }
        copy.zoom = zoom;
        copy.iconXOffset = iconXOffset;
        copy.iconYOffset = iconYOffset;
        copy.ambient = ambient;
        copy.contrast = contrast;
        if (stackCount != null) {
            copy.stackCount = Arrays.copyOf(stackCount, stackCount.length);
        }
        if (stackId != null) {
            copy.stackId = Arrays.copyOf(stackId, stackId.length);
        }
        if (groundOptions != null) {
            copy.groundOptions = Arrays.copyOf(groundOptions, groundOptions.length);
        }
        if (options != null) {
            copy.options = Arrays.copyOf(options, options.length);
        }
        copy.maleEquipModelId = maleEquipModelId;
        copy.maleModel1 = maleModel1;
        copy.maleModel2 = maleModel2;
        copy.maleOffset = maleOffset;
        copy.maleHeadModel = maleHeadModel;
        copy.maleHeadModel2 = maleHeadModel2;
        copy.femaleEquipModelId = femaleEquipModelId;
        copy.femaleModel1 = femaleModel1;
        copy.femaleModel2 = femaleModel2;
        copy.femaleOffset = femaleOffset;
        copy.femaleHeadModel = femaleHeadModel;
        copy.femaleHeadModel2 = femaleHeadModel2;
        copy.category = category;
        copy.notedID = notedID;
        copy.notedTemplate = notedTemplate;
        copy.team = team;
        copy.shiftClickDropIndex = shiftClickDropIndex;
        copy.boughtId = boughtId;
        copy.boughtTemplateId = boughtTemplateId;
        copy.placeholderId = placeholderId;
        copy.placeholderTemplateId = placeholderTemplateId;
        copy.weight = weight;
        copy.wearPos1 = wearPos1;
        copy.wearPos2 = wearPos2;
        copy.wearPos3 = wearPos3;
        copy.params = new HashMap<>(params);
        copy.multiStackSize = multiStackSize;
        if (spriteRecolorIndices != null) {
            copy.spriteRecolorIndices = Arrays.copyOf(spriteRecolorIndices, spriteRecolorIndices.length);
        }
        copy.tooltipColor = tooltipColor;
        copy.hasTooltipColor = hasTooltipColor;
        copy.unknownInt6 = unknownInt6;
        copy.lendId = lendId;
        copy.lendTemplateId = lendTemplateId;
        copy.maleXWearOffset = maleXWearOffset;
        copy.maleYWearOffset = maleYWearOffset;
        copy.maleZWearOffset = maleZWearOffset;
        copy.femaleXWearOffset = femaleXWearOffset;
        copy.femaleYWearOffset = femaleYWearOffset;
        copy.femaleZWearOffset = femaleZWearOffset;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        if (quests != null) {
            copy.quests = Arrays.copyOf(quests, quests.length);
        }
        copy.pickSizeShift = pickSizeShift;
        return copy;
    }

    public static class Defaults {
        public static final String DEFAULT_NAME = "null";

        public static final String DEFAULT_EXAMINE = null;
        public static final String DEFAULT_UNKNOWN1 = null;
        public static final int DEFAULT_RESIZE_X = 128;
        public static final int DEFAULT_RESIZE_Y = 128;
        public static final int DEFAULT_RESIZE_Z = 128;

        public static final int DEFAULT_ICON_CAMERA_PITCH = 0;
        public static final int DEFAULT_ICON_CAMERA_YAW = 0;
        public static final int DEFAULT_ICON_ROLL = 0;

        public static final int DEFAULT_COST = 1;
        public static final boolean DEFAULT_TRADEABLE = false;
        public static final int DEFAULT_STACKABLE = 0;
        public static final int DEFAULT_INVENTORY_MODEL_ID = 0;
        public static final boolean DEFAULT_MEMBERS = false;

        public static final int[] DEFAULT_ORIGINAL_COLORS = null;
        public static final int[] DEFAULT_TARGET_COLORS = null;
        public static final int[] DEFAULT_ORIGINAL_TEXTURES = null;
        public static final int[] DEFAULT_TARGET_TEXTURES = null;

        public static final int DEFAULT_ZOOM = 2000;
        public static final int DEFAULT_ICON_X_OFFSET = 0;
        public static final int DEFAULT_ICON_Y_OFFSET = 0;

        public static final int DEFAULT_AMBIENT = 0;
        public static final int DEFAULT_CONTRAST = 0;

        public static final int[] DEFAULT_STACK_COUNT = null;
        public static final int[] DEFAULT_STACK_ID = null;

        public static final String[] DEFAULT_GROUND_OPTIONS = new String[]{null, null, "Take", null, null};

        public static final String[] DEFAULT_OPTIONS = new String[]{null, null, null, null, "Drop"};

        public static final int DEFAULT_MALE_EQUIP_MODEL_ID = -1;
        public static final int DEFAULT_MALE_MODEL_1 = -1;
        public static final int DEFAULT_MALE_MODEL_2 = -1;
        public static final int DEFAULT_MALE_OFFSET = 0;
        public static final int DEFAULT_MALE_HEAD_MODEL = -1;
        public static final int DEFAULT_MALE_HEAD_MODEL_2 = -1;

        public static final int DEFAULT_FEMALE_EQUIP_MODEL_ID = -1;
        public static final int DEFAULT_FEMALE_MODEL_1 = -1;
        public static final int DEFAULT_FEMALE_MODEL_2 = -1;
        public static final int DEFAULT_FEMALE_OFFSET = 0;
        public static final int DEFAULT_FEMALE_HEAD_MODEL = -1;
        public static final int DEFAULT_FEMALE_HEAD_MODEL_2 = -1;

        public static final int DEFAULT_CATEGORY = 0;

        public static final int DEFAULT_NOTED_ID = -1;
        public static final int DEFAULT_NOTED_TEMPLATE = -1;

        public static final int DEFAULT_TEAM = 0;

        public static final int DEFAULT_SHIFT_CLICK_DROP_INDEX = -2;

        public static final int DEFAULT_BOUGHT_ID = -1;
        public static final int DEFAULT_BOUGHT_TEMPLATE_ID = -1;

        public static final int DEFAULT_PLACEHOLDER_ID = -1;
        public static final int DEFAULT_PLACEHOLDER_TEMPLATE_ID = -1;

        public static final Map<Integer, Object> DEFAULT_PARAMS = null;

        public static final int DEFAULT_WEIGHT = 0;
        public static final int DEFAULT_WEAR_POS = -1;

    }

    @Override
    public String name() {
        return "null".equalsIgnoreCase(name) ? (name + ": " + id) : name + " - " + id;
    }
}
