package api;

import api.cache.Cache;
import api.definition.enums.EnumDefinition;
import api.definition.floor.OverlayDefinition;
import api.definition.floor.UnderlayDefinition;
import api.definition.idk.IdkDefinition;
import api.definition.item.ItemDefinition;
import api.definition.model.RSModel;
import api.definition.npc.NpcDefinition;
import api.definition.object.ObjectDefinition;
import api.definition.bas.BASDefinition;
import api.definition.sequence.SeqBase;
import api.definition.sequence.SeqDefinition;
import api.definition.sequence.SeqFrame;
import api.definition.skeletal.animation.SkeletalAnimFrameset;
import api.definition.spotanim.SpotAnim;
import api.definition.struct.StructDefinition;
import api.definition.texture.RSTexture;
import api.definition.varbit.VarbitDefinition;

import java.util.List;

public abstract class CacheToolkit {

    protected final Cache cache;
    public final int revision;

    public CacheToolkit(Cache cache, int revision) {
        this.cache = cache;
        this.revision = revision;
    }

    public CacheToolkit(Cache cache) {
        this(cache, -1);
    }

    public abstract DefToolkit<RSModel, RSModel> models();

    public abstract DefToolkit<ItemDefinition, ItemDefinition> items();

    public abstract DefToolkit<NpcDefinition, NpcDefinition> npcs();

    public abstract DefToolkit<ObjectDefinition, ObjectDefinition> objects();

    public abstract DefToolkit<SeqDefinition, SeqDefinition> sequenceConfigs();

    public abstract DefToolkit<List<SeqFrame>, SeqFrame> frames();

    public abstract DefToolkit<SeqBase, SeqBase> bases();

    public abstract DefToolkit<SpotAnim, SpotAnim> graphics();

    public abstract DefToolkit<List<RSTexture>, List<RSTexture>> sprites();

    public abstract DefToolkit<RSTexture, RSTexture> textures();

    public abstract DefToolkit<IdkDefinition, IdkDefinition> kits();

    // yes, i could probably turn both legacy and skeletal anims into one toolkit but i prefer it this way for now
    public DefToolkit<SkeletalAnimFrameset, SkeletalAnimFrameset> skeletalFramesets() {
        throw new UnsupportedOperationException("This plugin doesn't support skeletal animations");
    }

    public DefToolkit<EnumDefinition, EnumDefinition> enums() {
        throw new UnsupportedOperationException("This plugin doesn't support enums");
    }

    public DefToolkit<StructDefinition, StructDefinition> structs() {
        throw new UnsupportedOperationException("This plugin doesn't support structs");
    }

    // pretty every revision we're planning to support has these, but cba implement them for all atm
    // TODO: change this later
    public DefToolkit<VarbitDefinition, VarbitDefinition> varbits() {
        throw new UnsupportedOperationException("This plugin doesn't support varbits");
    }

    // same as for varbits
    public DefToolkit<UnderlayDefinition, UnderlayDefinition> underlays() {
        throw new UnsupportedOperationException("This plugin doesn't support underlays");
    }

    // same as for varbits
    public DefToolkit<OverlayDefinition, OverlayDefinition> overlays() {
        throw new UnsupportedOperationException("This plugin doesn't support overlays");
    }

    public DefToolkit<BASDefinition, BASDefinition> baseAnimationSets() {
        throw new UnsupportedOperationException("This plugin doesn't support base animation sets (BAS)");
    }

    public abstract RevisionRange revision();

    // uh i don't really like the args part of this, but i can't think of a better way to do this right now
    public abstract <I, O> Codec<I, O> getCodecByType(DefinitionType type, Object... args);
}
