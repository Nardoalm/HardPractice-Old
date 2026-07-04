package live.pvphard.practice.feature.explosives.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.feature.explosives.ExplosiveService;
import live.pvphard.practice.feature.explosives.listener.ExplosiveListener;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Emmy
 * @project Hard
 * @since 11/06/2025
 */
@Setter
@Getter
@Service(provides = ExplosiveService.class, priority = 370)
public class ExplosiveServiceImpl implements ExplosiveService {
    private final HardPractice plugin;
    private final ConfigService configService;
    private final LocaleService localeService;

    private boolean enabled;

    private double horizontalFireballKnockback;
    private double verticalFireballKnockback;
    private double fireballExplosionRange;
    private double fireballThrowSpeed;

    private double tntExplosionRange;
    private int tntFuseTicks;

    /**
     * DI Constructor for the ExplosiveServiceImpl class.
     *
     * @param plugin        The main HardPractice instance.
     * @param configService The configuration service.
     * @param localeService The locale service.
     */
    public ExplosiveServiceImpl(HardPractice plugin, ConfigService configService, LocaleService localeService) {
        this.plugin = plugin;
        this.configService = configService;
        this.localeService = localeService;
    }

    @Override
    public void initialize(HardContext context) {
        this.enabled = this.localeService.getBoolean(SettingsLocaleImpl.EXPLOSIVE_ENABLED);

        if (this.enabled) {
            this.assignValues();
            this.registerListener();
        }
    }

    private void assignValues() {
        this.horizontalFireballKnockback = this.localeService.getDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_HORIZONTAL_KB_VALUE);
        this.verticalFireballKnockback = this.localeService.getDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_VERTICAL_KB_VALUE);
        this.fireballExplosionRange = this.localeService.getDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_EXPLOSION_RANGE_VALUE);
        this.fireballThrowSpeed = this.localeService.getDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_THROW_SPEED_VALUE);

        this.tntFuseTicks = this.localeService.getInt(SettingsLocaleImpl.EXPLOSIVE_TNT_FUSE_TICKS_VALUE);
        this.tntExplosionRange = this.localeService.getDouble(SettingsLocaleImpl.EXPLOSIVE_TNT_EXPLOSION_RANGE_VALUE);
    }

    @Override
    public void save() {
        this.localeService.setBoolean(SettingsLocaleImpl.EXPLOSIVE_ENABLED, this.enabled);

        this.localeService.setDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_HORIZONTAL_KB_VALUE, this.horizontalFireballKnockback);
        this.localeService.setDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_VERTICAL_KB_VALUE, this.verticalFireballKnockback);
        this.localeService.setDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_EXPLOSION_RANGE_VALUE, this.fireballExplosionRange);
        this.localeService.setDouble(SettingsLocaleImpl.EXPLOSIVE_FIREBALL_THROW_SPEED_VALUE, this.fireballThrowSpeed);

        this.localeService.setInt(SettingsLocaleImpl.EXPLOSIVE_TNT_FUSE_TICKS_VALUE, this.tntFuseTicks);
        this.localeService.setDouble(SettingsLocaleImpl.EXPLOSIVE_TNT_EXPLOSION_RANGE_VALUE, this.tntExplosionRange);
    }

    private void registerListener() {
        this.plugin.getServer().getPluginManager().registerEvents(new ExplosiveListener(), this.plugin);
    }
}
