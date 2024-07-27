package api.definition.bas;

import api.definition.Definition;
import lombok.Data;

@Data
public class EquipmentDefaults implements Definition {
    private int[] hidden;
    private int offhandSlot = -1;
    private int weaponSlot = -1;
    private int[] hiddenAnimationOffhandSlots;

    private int[] hiddenAnimationWeaponSlots;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id) {

    }
}
