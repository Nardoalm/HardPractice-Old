package us.alleypvp.practice.common.serializer;

import us.alleypvp.practice.common.logger.Logger;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class Serializer {

    public String serializeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return "null";
        }

        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
                ":" + location.getYaw() + ":" + location.getPitch();
    }

    public Location deserializeLocation(String source) {
        if (source == null || source.trim().isEmpty() || source.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            String[] split = source.split(":");
            if (split.length < 4) return null;

            World world = Bukkit.getServer().getWorld(split[0]);
            if (world == null && !Bukkit.getWorlds().isEmpty()) {
                world = Bukkit.getWorlds().get(0);
            }

            if (world == null) return null;

            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = split.length >= 6 ? Float.parseFloat(split[4]) : 0.0f;
            float pitch = split.length >= 6 ? Float.parseFloat(split[5]) : 0.0f;

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }

    public String serializeItemStack(ItemStack[] items) {
        try {
            ItemStack[] safeItems = items != null ? items : new ItemStack[0];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(safeItems.length);
            for (ItemStack item : safeItems) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception exception) {
            throw new RuntimeException("Unable to serialize item stacks.", exception);
        }
    }

    public ItemStack[] deserializeItemStack(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new ItemStack[0];
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception exception) {
            Logger.logException("Unable to deserialize item stacks.", exception);
            return new ItemStack[0];
        }
    }

    public List<String> serializePotionEffects(List<PotionEffect> potionEffects) {
        return potionEffects.stream()
                .map(effect -> effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier())
                .collect(Collectors.toList());
    }

    public List<PotionEffect> deserializePotionEffects(List<String> serializedEffects) {
        return serializedEffects.stream()
                .map(s -> {
                    String[] parts = s.split(":");
                    if (parts.length < 3) return null;
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    int duration = Integer.parseInt(parts[1]);
                    int amplifier = Integer.parseInt(parts[2]);
                    return type != null ? new PotionEffect(type, duration, amplifier) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}