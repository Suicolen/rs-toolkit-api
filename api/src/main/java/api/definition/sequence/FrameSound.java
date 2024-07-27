package api.definition.sequence;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FrameSound {
    private int id;
    private int loops;
    private int location;
    private int retain;
}
