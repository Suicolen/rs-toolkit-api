package api.definition.skeletal.animation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Polynomial {
    private float[] coefficients;
    private int deg;
}