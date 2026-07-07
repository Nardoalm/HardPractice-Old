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
    @Getter
    private final List<ArmorStand> lines;
    private List<String> lastTextLines = new ArrayList<>();
    private static final double LINE_SPACING = 0.28;

    public Hologram(Location location) {
        this.location = location;
        this.lines = new ArrayList<>();
    }

    public void update(List<String> textLines) {
        this.lastTextLines = new ArrayList<>(textLines);

        if (this.location == null || this.location.getWorld() == null) {
            return;
        }

        int cx = this.location.getBlockX() >> 4;
        int cz = this.location.getBlockZ() >> 4;
        if (!this.location.getWorld().isChunkLoaded(cx, cz)) {
            this.location.getWorld().loadChunk(cx, cz);
        }

        this.cleanupStrayEntities();

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

    public void cleanupStrayEntities() {
        if (this.location == null || this.location.getWorld() == null) return;
        if (!this.location.getChunk().isLoaded()) return;

        double minY = this.location.getY() - (LINE_SPACING * 12);
        double maxY = this.location.getY() + 0.5;
        Location center = this.location.clone();
        center.setY((minY + maxY) / 2.0);
        
        double radiusY = (maxY - minY) / 2.0;
        
        try {
            for (Entity entity : this.location.getWorld().getNearbyEntities(center, 1.5, radiusY, 1.5)) {
                if (entity instanceof ArmorStand) {
                    ArmorStand as = (ArmorStand) entity;
                    if (!as.isVisible() && !as.hasGravity() && !this.lines.contains(as)) {
                        as.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanupEntitiesAtLocation() {
        cleanupStrayEntities();
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

    public void despawn() {
        lines.stream().filter(java.util.Objects::nonNull).forEach(ArmorStand::remove);
        lines.clear();
    }

    public void respawn() {
        despawn();
        if (lastTextLines != null && !lastTextLines.isEmpty()) {
            update(lastTextLines);
        }
    }

    public void destroy() {
        despawn();
    }
}