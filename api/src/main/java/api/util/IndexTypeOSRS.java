package api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IndexTypeOSRS {
    FRAMES(0),
    BASES(1),
    CONFIGS(2),
    INTERFACES(3),
    SOUND_EFFECTS(4),
    MAPS(5),
    MUSIC_TRACKS(6),
    MODELS(7),
    SPRITES(8),
    TEXTURES(9),
    BINARY(10),
    MUSIC_JINGLES(11),
    CLIENT_SCRIPT(12),
    FONTS(13),
    MUSIC_SAMPLES(14),
    MUSIC_PATCHES(15),
    WORLDMAP_OLD(16), // looks unused
    WORLDMAP_GEOGRAPHY(18),
    WORLDMAP(19),
    WORLDMAP_GROUND(20),
    DBTABLEINDEX(21);

    private final int id;
}
