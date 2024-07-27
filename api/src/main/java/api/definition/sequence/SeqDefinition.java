package api.definition.sequence;

import api.definition.Definition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static api.definition.sequence.SeqDefinition.Defaults.*;

@Data
public class SeqDefinition implements Definition {
    private int id;
    private int frameCount = DEFAULT_FRAME_COUNT;
    private int[] primaryFrameIds;
    private int[] secondaryFrameIds;
    private int[] delays;
    private int loopDelay = DEFAULT_LOOP_DELAY;
    private int[] masks;
    private boolean[] skeletalMasks;
    private boolean oneSquareAnimation = DEFAULT_ONE_SQUARE_ANIMATION;
    private int forcedPriority = DEFAULT_FORCED_PRIORITY;
    private int leftHandItem = DEFAULT_LEFT_HAND_ITEM;
    private int rightHandItem = DEFAULT_RIGHT_HAND_ITEM;
    private int frameStep = DEFAULT_FRAME_STEP;
    private int resetWhenWalk = DEFAULT_RESET_WHEN_WALK;
    private int priority = DEFAULT_PRIORITY;
    private int delayType = DEFAULT_DELAY_TYPE;
    private int[] chatFrameIds;
    private int skeletalId = DEFAULT_SKELETAL_ID;
    private int lastSkeletalKeyframeTick = DEFAULT_RANGE_BEGIN;
    private int firstSkeletalKeyframeTick = DEFAULT_RANGE_END;

    private Map<Integer, FrameSound> skeletalFrameSounds;
    private FrameSound[] frameSounds;

    /**
     * 718
     */

    private int[][] soundSettings;
    private boolean aBool5923;
    private boolean tweened;
    private boolean vorbisSound;
    private int[] soundDurations;
    private int[] anIntArray5927;
    private int[] anIntArray5919;

    private Map<Integer, Object> params;

    @JsonIgnore
    public int getSkeletalAnimDuration() {
        return this.firstSkeletalKeyframeTick - this.lastSkeletalKeyframeTick;
    }

    @JsonIgnore
    public boolean isSkeletalAnimation() {
        return skeletalId >= 0;
    }

    public SeqDefinition copy() {
        SeqDefinition copy = new SeqDefinition();
        copy.id = id;
        copy.frameCount = frameCount;
        copy.primaryFrameIds = primaryFrameIds;
        copy.secondaryFrameIds = secondaryFrameIds;
        copy.delays = delays;
        copy.loopDelay = loopDelay;
        copy.masks = masks;
        copy.skeletalMasks = skeletalMasks;
        copy.oneSquareAnimation = oneSquareAnimation;
        copy.forcedPriority = forcedPriority;
        copy.leftHandItem = leftHandItem;
        copy.rightHandItem = rightHandItem;
        copy.frameStep = frameStep;
        copy.resetWhenWalk = resetWhenWalk;
        copy.priority = priority;
        copy.delayType = delayType;
        copy.chatFrameIds = chatFrameIds;
        copy.skeletalId = skeletalId;
        copy.lastSkeletalKeyframeTick = lastSkeletalKeyframeTick;
        copy.firstSkeletalKeyframeTick = firstSkeletalKeyframeTick;
        copy.skeletalFrameSounds = skeletalFrameSounds;
        copy.frameSounds = frameSounds;
        copy.soundSettings = soundSettings;
        copy.aBool5923 = aBool5923;
        copy.tweened = tweened;
        copy.vorbisSound = vorbisSound;
        copy.soundDurations = soundDurations;
        copy.anIntArray5927 = anIntArray5927;
        copy.anIntArray5919 = anIntArray5919;
        copy.params = params;
        return copy;
    }

    public SeqDefinition deepCopy() {
        SeqDefinition copy = new SeqDefinition();
        copy.id = id;
        copy.frameCount = frameCount;
        if (primaryFrameIds != null) {
            copy.primaryFrameIds = Arrays.copyOf(primaryFrameIds, primaryFrameIds.length);
        }
        if (secondaryFrameIds != null) {
            copy.secondaryFrameIds = Arrays.copyOf(secondaryFrameIds, secondaryFrameIds.length);
        }
        if (delays != null) {
            copy.delays = Arrays.copyOf(delays, delays.length);
        }
        copy.loopDelay = loopDelay;
        if (masks != null) {
            copy.masks = Arrays.copyOf(masks, masks.length);
        }
        if (skeletalMasks != null) {
            copy.skeletalMasks = Arrays.copyOf(skeletalMasks, skeletalMasks.length);
        }
        copy.oneSquareAnimation = oneSquareAnimation;
        copy.forcedPriority = forcedPriority;
        copy.leftHandItem = leftHandItem;
        copy.rightHandItem = rightHandItem;
        copy.frameStep = frameStep;
        copy.resetWhenWalk = resetWhenWalk;
        copy.priority = priority;
        copy.delayType = delayType;
        if (chatFrameIds != null) {
            copy.chatFrameIds = Arrays.copyOf(chatFrameIds, chatFrameIds.length);
        }
        copy.skeletalId = skeletalId;
        copy.lastSkeletalKeyframeTick = lastSkeletalKeyframeTick;
        copy.firstSkeletalKeyframeTick = firstSkeletalKeyframeTick;
        if (skeletalFrameSounds != null) {
            copy.skeletalFrameSounds = new HashMap<>(skeletalFrameSounds);
        }
        if (frameSounds != null) {
            copy.frameSounds = Arrays.copyOf(frameSounds, frameSounds.length);
        }
        if (soundSettings != null) {
            copy.soundSettings = Arrays.copyOf(soundSettings, soundSettings.length);
        }
        copy.aBool5923 = aBool5923;
        copy.tweened = tweened;
        copy.vorbisSound = vorbisSound;
        if (soundDurations != null) {
            copy.soundDurations = Arrays.copyOf(soundDurations, soundDurations.length);
        }
        if (anIntArray5927 != null) {
            copy.anIntArray5927 = Arrays.copyOf(anIntArray5927, anIntArray5927.length);
        }
        if (anIntArray5919 != null) {
            copy.anIntArray5919 = Arrays.copyOf(anIntArray5919, anIntArray5919.length);
        }
        if (params != null) {
            copy.params = new HashMap<>(params);
        }
        return copy;
    }

    public static class Defaults {
        public static final int DEFAULT_FRAME_COUNT = 0;
        public static int DEFAULT_LOOP_DELAY = -1;
        public static boolean DEFAULT_ONE_SQUARE_ANIMATION;
        public static int DEFAULT_FORCED_PRIORITY = 5;
        public static int DEFAULT_LEFT_HAND_ITEM = -1;
        public static int DEFAULT_RIGHT_HAND_ITEM = -1;
        public static int DEFAULT_FRAME_STEP = 99;
        public static int DEFAULT_RESET_WHEN_WALK = -1;
        public static int DEFAULT_PRIORITY = -1;
        public static int DEFAULT_DELAY_TYPE = 2;
        public static int DEFAULT_SKELETAL_ID = -1;
        public static int DEFAULT_RANGE_BEGIN = 0;
        public static int DEFAULT_RANGE_END = 0;
    }

}

