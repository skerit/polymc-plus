package rocks.blackblock.polymcplus.wizard;

import com.mojang.datafixers.util.Pair;
import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.util.math.EulerAngle;

import java.util.Collections;

public class VArmorStand extends AbstractVirtualEntity {


    @Override
    public EntityType<?> getEntityType() {
        return EntityType.ARMOR_STAND;
    }

    public void sendArmorStandFlags(PacketConsumer players, boolean small, boolean showArms, boolean hideBasePlate, boolean isMarker) {
        byte flag = 0;
        if (small)         flag += ArmorStandEntity.SMALL_FLAG;
        if (showArms)      flag += ArmorStandEntity.SHOW_ARMS_FLAG;
        if (hideBasePlate) flag += ArmorStandEntity.HIDE_BASE_PLATE_FLAG;
        if (isMarker)      flag += ArmorStandEntity.MARKER_FLAG;

        try {

            players.sendPacket(EntityUtil.createDataTrackerUpdate(
                    this.id,
                    ArmorStandEntity.ARMOR_STAND_FLAGS,
                    flag
            ));
        } catch (Exception e) {
            PolyMc.LOGGER.error("Error sending ArmorStand flags packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendSingleSlot(PacketConsumer players, EquipmentSlot slot, ItemStack stack) {
        try {
            players.sendPacket(new EntityEquipmentUpdateS2CPacket(this.id, Collections.singletonList(new Pair<>(slot, stack))));
        } catch (Exception e) {
            PolyMc.LOGGER.error("Error sending head single-slot packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendHeadRotation(PacketConsumer players, EulerAngle angle) {
        try {
            players.sendPacket(headRotationPacket(angle));
        } catch (Exception e) {
            PolyMc.LOGGER.error("Error sending head rotation packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendHeadRotation(PacketConsumer players, float pitch, float yaw, float roll) {
        sendHeadRotation(players, new EulerAngle(pitch, yaw, roll));
    }

    public EntityTrackerUpdateS2CPacket headRotationPacket(float pitch, float yaw, float roll) {
        return headRotationPacket(new EulerAngle(pitch, yaw, roll));
    }

    public void makeInvisible(PacketConsumer playerEntity) {
        this.sendFlags(playerEntity,
                false,
                false,
                false,
                false,
                true,
                false,
                false);
    }

    public EntityTrackerUpdateS2CPacket headRotationPacket(EulerAngle angle) {
        return EntityUtil.createDataTrackerUpdate(
                this.id,
                ArmorStandEntity.TRACKER_HEAD_ROTATION,
                angle
        );
    }
}
