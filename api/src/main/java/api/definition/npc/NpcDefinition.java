package api.definition.npc;

import api.definition.Definition;
import api.definition.NamedDefinition;
import api.definition.object.ObjectDefinition;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static api.definition.npc.NpcDefinition.Defaults.*;

@Data
public class NpcDefinition implements Definition, NamedDefinition {

    private int id = DEFAULT_ID;
    private String name = DEFAULT_NAME;
    private int[] modelIds;
    private int[] headModelIds;
    private int standAnim = DEFAULT_ANIMATION_ID;
    private int walkAnim = DEFAULT_ANIMATION_ID;
    private int runAnim = DEFAULT_ANIMATION_ID;
    private int size = DEFAULT_SIZE;
    private int idleRotateLeftAnimation = DEFAULT_ANIMATION_ID;
    private int idleRotateRightAnimation = DEFAULT_ANIMATION_ID;
    private int rotate180Animation = DEFAULT_ANIMATION_ID;
    private int rotateLeftAnimation = DEFAULT_ANIMATION_ID;
    private int rotateRightAnimation = DEFAULT_ANIMATION_ID;
    private int runRotate180Animation = DEFAULT_ANIMATION_ID;
    private int runRotateLeftAnimation = DEFAULT_ANIMATION_ID;
    private int runRotateRightAnimation = DEFAULT_ANIMATION_ID;
    private int crawlAnimation = DEFAULT_ANIMATION_ID;
    private int crawlRotate180Animation = DEFAULT_ANIMATION_ID;
    private int crawlRotateLeftAnimation = DEFAULT_ANIMATION_ID;
    private int crawlRotateRightAnimation = DEFAULT_ANIMATION_ID;
    private int[] originalColors;
    private int[] targetColors;
    private int[] originalTextures;
    private int[] targetTextures;
    private String[] actions = ObjectDefinition.Defaults.DEFAULT_ACTIONS.clone();
    private boolean minimapVisible = DEFAULT_MINIMAP_VISIBLE;
    private int combatLevel = DEFAULT_COMBAT_LEVEL;
    private int widthScale = DEFAULT_WIDTH_SCALE;
    private int heightScale = DEFAULT_HEIGHT_SCALE;
    private boolean hasRenderPriority = DEFAULT_HAS_RENDER_PRIORITY;
    private int ambient = DEFAULT_AMBIENT;
    private int contrast = DEFAULT_CONTRAST;
    private int headIcon = DEFAULT_HEAD_ICON;
    private int rotationSpeed = DEFAULT_ROTATION_SPEED;
    private int[] configs;
    private int varbitId = DEFAULT_VARBIT_ID;
    private int varpIndex = DEFAULT_VARP_INDEX;
    private boolean isInteractable = DEFAULT_IS_INTERACTABLE;
    private boolean rotationFlag = DEFAULT_ROTATION_FLAG;
    private boolean follower = DEFAULT_FOLLOWER;
    private boolean lowPriorityFollowerOps = DEFAULT_LOW_PRIORITY_FOLLOWER_OPS;
    private Map<Integer, Object> params;
    private int category = DEFAULT_CATEGORY;

    private int[] iconGroups;
    private int[] iconIndices;

    private int overlayHeight = DEFAULT_OVERLAY_HEIGHT;
    private int[] npcStats = DEFAULT_NPC_STATS.clone();

    /**
     * 718
     */

    private int[] recolorPalette;
    private boolean aBool4904 = false;
    private boolean aBool4894 = true;
    private boolean aBool4912 = true;
    private int aShort4874;
    private int aShort4897;
    private int aByte4883 = -96;
    private int aByte4899 = -16;
    private int movementFlags;
    private int[][] modelTranslation;
    private int anInt4902 = -1;
    private int respawnDirection = -1;
    private int renderEmote = -1;
    private int moveSpeed = 0;
    private int specialByte;
    private int cursor1Op = -1;
    private int cursor1 = -1;
    private int cursor2Op = -1;
    private int cursor2 = -1;
    private int cursor = -1;
    private int soundVolume = 255;
    private boolean aBool4884 = false;
    private int mapElement = -1;
    private boolean aBool4890 = false;
    private int aByte4868;
    private int aByte4869;
    private int aByte4905;
    private int aByte4871;
    private int aByte4916 = -1;
    private int[] quests;
    private boolean aBool4872;
    private int anInt4917 = -1;
    private int anInt4911 = 256;
    private int anInt4919 = 256;
    private int anInt4913;
    private int anInt4908;
    private boolean aBool4920 = true;
    private int hitBarId = -1;
    private int anInt6178;

    /**
     * 377 and below
     */

    private String examine;

    public NpcDefinition copy() {
        NpcDefinition copy = new NpcDefinition();
        copy.id = id;
        copy.name = name;
        copy.size = size;
        copy.idleRotateLeftAnimation = idleRotateLeftAnimation;
        copy.idleRotateRightAnimation = idleRotateRightAnimation;
        copy.rotate180Animation = rotate180Animation;
        copy.rotateLeftAnimation = rotateLeftAnimation;
        copy.rotateRightAnimation = rotateRightAnimation;
        copy.runRotate180Animation = runRotate180Animation;
        copy.runRotateLeftAnimation = runRotateLeftAnimation;
        copy.runRotateRightAnimation = runRotateRightAnimation;
        copy.crawlAnimation = crawlAnimation;
        copy.crawlRotate180Animation = crawlRotate180Animation;
        copy.crawlRotateLeftAnimation = crawlRotateLeftAnimation;
        copy.crawlRotateRightAnimation = crawlRotateRightAnimation;
        copy.originalColors = originalColors;
        copy.targetColors = targetColors;
        copy.originalTextures = originalTextures;
        copy.targetTextures = targetTextures;
        copy.actions = actions;
        copy.minimapVisible = minimapVisible;
        copy.combatLevel = combatLevel;
        copy.widthScale = widthScale;
        copy.heightScale = heightScale;
        copy.hasRenderPriority = hasRenderPriority;
        copy.ambient = ambient;
        copy.contrast = contrast;
        copy.headIcon = headIcon;
        copy.rotationSpeed = rotationSpeed;
        copy.configs = configs;
        copy.varbitId = varbitId;
        copy.varpIndex = varpIndex;
        copy.isInteractable = isInteractable;
        copy.rotationFlag = rotationFlag;
        copy.follower = follower;
        copy.lowPriorityFollowerOps = lowPriorityFollowerOps;
        copy.params = params;
        copy.category = category;
        copy.modelIds = modelIds;
        copy.headModelIds = headModelIds;
        copy.standAnim = standAnim;
        copy.walkAnim = walkAnim;
        copy.runAnim = runAnim;
        copy.iconGroups = iconGroups;
        copy.iconIndices = iconIndices;
        copy.recolorPalette = recolorPalette;
        copy.aBool4904 = aBool4904;
        copy.aBool4894 = aBool4894;
        copy.aBool4912 = aBool4912;
        copy.aShort4874 = aShort4874;
        copy.aShort4897 = aShort4897;
        copy.aByte4883 = aByte4883;
        copy.aByte4899 = aByte4899;
        copy.movementFlags = movementFlags;
        copy.modelTranslation = modelTranslation;
        copy.anInt4902 = anInt4902;
        copy.respawnDirection = respawnDirection;
        copy.renderEmote = renderEmote;
        copy.moveSpeed = moveSpeed;
        copy.specialByte = specialByte;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        copy.cursor = cursor;
        copy.soundVolume = soundVolume;
        copy.aBool4884 = aBool4884;
        copy.mapElement = mapElement;
        copy.aBool4890 = aBool4890;
        copy.aByte4868 = aByte4868;
        copy.aByte4869 = aByte4869;
        copy.aByte4905 = aByte4905;
        copy.aByte4871 = aByte4871;
        copy.aByte4916 = aByte4916;
        copy.quests = quests;
        copy.aBool4872 = aBool4872;
        copy.anInt4917 = anInt4917;
        copy.anInt4911 = anInt4911;
        copy.anInt4919 = anInt4919;
        copy.anInt4913 = anInt4913;
        copy.anInt4908 = anInt4908;
        copy.aBool4920 = aBool4920;
        copy.hitBarId = hitBarId;
        copy.anInt6178 = anInt6178;
        copy.examine = examine;
        copy.overlayHeight = overlayHeight;
        copy.npcStats = npcStats;
        return copy;
    }

    public NpcDefinition deepCopy() {
        NpcDefinition copy = new NpcDefinition();
        copy.id = id;
        copy.name = name;
        copy.size = size;
        copy.idleRotateLeftAnimation = idleRotateLeftAnimation;
        copy.idleRotateRightAnimation = idleRotateRightAnimation;
        copy.rotate180Animation = rotate180Animation;
        copy.rotateLeftAnimation = rotateLeftAnimation;
        copy.rotateRightAnimation = rotateRightAnimation;
        copy.runRotate180Animation = runRotate180Animation;
        copy.runRotateLeftAnimation = runRotateLeftAnimation;
        copy.runRotateRightAnimation = runRotateRightAnimation;
        copy.crawlAnimation = crawlAnimation;
        copy.crawlRotate180Animation = crawlRotate180Animation;
        copy.crawlRotateLeftAnimation = crawlRotateLeftAnimation;
        copy.crawlRotateRightAnimation = crawlRotateRightAnimation;
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
        if (actions != null) {
            copy.actions = Arrays.copyOf(actions, actions.length);
        }
        copy.minimapVisible = minimapVisible;
        copy.combatLevel = combatLevel;
        copy.widthScale = widthScale;
        copy.heightScale = heightScale;
        copy.hasRenderPriority = hasRenderPriority;
        copy.ambient = ambient;
        copy.contrast = contrast;
        copy.headIcon = headIcon;
        copy.rotationSpeed = rotationSpeed;
        if (configs != null) {
            copy.configs = Arrays.copyOf(configs, configs.length);
        }
        copy.varbitId = varbitId;
        copy.varpIndex = varpIndex;
        copy.isInteractable = isInteractable;
        copy.rotationFlag = rotationFlag;
        copy.follower = follower;
        copy.lowPriorityFollowerOps = lowPriorityFollowerOps;
        if (params != null) {
            copy.params = new HashMap<>(params);
        }
        copy.category = category;
        if (modelIds != null) {
            copy.modelIds = Arrays.copyOf(modelIds, modelIds.length);
        }
        if (headModelIds != null) {
            copy.headModelIds = Arrays.copyOf(headModelIds, headModelIds.length);
        }
        copy.standAnim = standAnim;
        copy.walkAnim = walkAnim;
        copy.runAnim = runAnim;
        if (iconGroups != null) {
            copy.iconGroups = Arrays.copyOf(iconGroups, iconGroups.length);
        }
        if (iconIndices != null) {
            copy.iconIndices = Arrays.copyOf(iconIndices, iconIndices.length);
        }
        if (recolorPalette != null) {
            copy.recolorPalette = Arrays.copyOf(recolorPalette, recolorPalette.length);
        }
        copy.aBool4904 = aBool4904;
        copy.aBool4894 = aBool4894;
        copy.aBool4912 = aBool4912;
        copy.aShort4874 = aShort4874;
        copy.aShort4897 = aShort4897;
        copy.aByte4883 = aByte4883;
        copy.aByte4899 = aByte4899;
        copy.movementFlags = movementFlags;
        if (modelTranslation != null) {
            copy.modelTranslation = Arrays.copyOf(modelTranslation, modelTranslation.length);
        }
        copy.anInt4902 = anInt4902;
        copy.respawnDirection = respawnDirection;
        copy.renderEmote = renderEmote;
        copy.moveSpeed = moveSpeed;
        copy.specialByte = specialByte;
        copy.cursor1Op = cursor1Op;
        copy.cursor1 = cursor1;
        copy.cursor2Op = cursor2Op;
        copy.cursor2 = cursor2;
        copy.cursor = cursor;
        copy.soundVolume = soundVolume;
        copy.aBool4884 = aBool4884;
        copy.mapElement = mapElement;
        copy.aBool4890 = aBool4890;
        copy.aByte4868 = aByte4868;
        copy.aByte4869 = aByte4869;
        copy.aByte4905 = aByte4905;
        copy.aByte4871 = aByte4871;
        copy.aByte4916 = aByte4916;
        if (quests != null) {
            copy.quests = Arrays.copyOf(quests, quests.length);
        }
        copy.aBool4872 = aBool4872;
        copy.anInt4917 = anInt4917;
        copy.anInt4911 = anInt4911;
        copy.anInt4919 = anInt4919;
        copy.anInt4913 = anInt4913;
        copy.anInt4908 = anInt4908;
        copy.aBool4920 = aBool4920;
        copy.hitBarId = hitBarId;
        copy.anInt6178 = anInt6178;
        copy.examine = examine;
        copy.overlayHeight = overlayHeight;
        if (npcStats != null) {
            copy.npcStats = Arrays.copyOf(npcStats, npcStats.length);
        }
        return copy;
    }

    public static class Defaults {
        public static final int DEFAULT_ID = 0;
        public static final String DEFAULT_NAME = "null";
        public static final int DEFAULT_SIZE = 1;
        public static final int DEFAULT_ANIMATION_ID = -1;
        public static final String[] DEFAULT_ACTIONS = new String[5];
        public static final boolean DEFAULT_MINIMAP_VISIBLE = true;
        public static final int DEFAULT_COMBAT_LEVEL = -1;
        public static final int DEFAULT_WIDTH_SCALE = 128;
        public static final int DEFAULT_HEIGHT_SCALE = 128;
        public static final boolean DEFAULT_HAS_RENDER_PRIORITY = false;
        public static final int DEFAULT_AMBIENT = 0;
        public static final int DEFAULT_CONTRAST = 0;
        public static final int DEFAULT_HEAD_ICON = -1;
        public static final int DEFAULT_ROTATION_SPEED = 32;
        public static final int DEFAULT_VARBIT_ID = -1;
        public static final int DEFAULT_VARP_INDEX = -1;
        public static final boolean DEFAULT_IS_INTERACTABLE = true;
        public static final boolean DEFAULT_ROTATION_FLAG = true;
        public static final boolean DEFAULT_FOLLOWER = false;

        public static final boolean DEFAULT_LOW_PRIORITY_FOLLOWER_OPS = false;
        public static final int DEFAULT_CATEGORY = 0;

        public static final int DEFAULT_OVERLAY_HEIGHT = -1;
        public static final int[] DEFAULT_NPC_STATS = new int[]{1, 1, 1, 1, 1, 1};
    }

    @Override
    public String name() {
        return "null".equalsIgnoreCase(name) ? (name + ": " + id) : name + " - " + id;
    }
}
