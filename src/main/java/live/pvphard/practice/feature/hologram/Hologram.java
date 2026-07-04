package live.pvphard.practice.feature.hologram;

import lombok.Getter;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    @Getter
    private final Location location;
    private final List<ArmorStand> lines;
    private static final double LINE_SPACING = 0.28;

    public Hologram(Location location) {
        this.location = location;
        this.lines = new ArrayList<>();
    }

    public void update(List<String> textLines) {
        if (this.lines.isEmpty()) {
            this.cleanupEntitiesAtLocation();
        }

        while (lines.size() > textLines.size()) {
            ArmorStand removed = lines.remove(lines.size() - 1);
            if (removed != null) {
                removed.remove();
            }
        }

        Location currentLoc = location.clone();
        for (int i = 0; i < textLines.size(); i++) {
            String rawText = textLines.get(i);
            String text = CC.translate(rawText);
            boolean isBlank = rawText == null || rawText.trim().isEmpty();

            if (i < lines.size()) {
                ArmorStand as = lines.get(i);
                if (as != null && as.isValid()) {
                    as.setCustomName(text);
                    as.setCustomNameVisible(!isBlank);
                    as.teleport(currentLoc);
                } else {
                    ArmorStand newAs = createArmorStandElement(currentLoc, text, isBlank);
                    lines.set(i, newAs);
                }
            } else {
                ArmorStand as = createArmorStandElement(currentLoc, text, isBlank);
                lines.add(as);
            }
            currentLoc.subtract(0, LINE_SPACING, 0);
        }
    }

    private void cleanupEntitiesAtLocation() {
        if (this.location == null || this.location.getWorld() == null) return;
        double minY = this.location.getY() - (LINE_SPACING * 12);
        double maxY = this.location.getY() + 0.5;
        Location center = this.location.clone();
        center.setY((minY + maxY) / 2.0);
        
        double radiusY = (maxY - minY) / 2.0;
        
        try {
            for (Entity entity : this.location.getWorld().getNearbyEntities(center, 1.5, radiusY, 1.5)) {
                if (entity instanceof ArmorStand) {
                    ArmorStand as = (ArmorStand) entity;
                    if (!as.isVisible() && !as.hasGravity()) {
                        as.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArmorStand createArmorStandElement(Location loc, String text, boolean isBlank) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName(text);
        as.setCustomNameVisible(!isBlank);
        as.setBasePlate(false);
        as.setSmall(true);
        as.setArms(false);
        return as;
    }

    public void destroy() {
        lines.stream().filter(java.util.Objects::nonNull).forEach(ArmorStand::remove);
        lines.clear();
    }
}