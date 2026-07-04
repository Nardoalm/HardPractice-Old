package live.pvphard.practice.feature.emoji.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.feature.emoji.EmojiService;
import live.pvphard.practice.feature.emoji.EmojiType;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Emmy
 * @project Hard
 * @date 10/11/2024 - 09:41
 */
@Getter
@Service(provides = EmojiService.class, priority = 420)
public class EmojiServiceImpl implements EmojiService {
    private final LocaleService localeService;

    private final Map<String, String> emojis = new HashMap<>();
    private boolean enabled = false;

    /**
     * DI Constructor for the EmojiServiceImpl class.
     *
     * @param localeService The locale service.
     */
    public EmojiServiceImpl(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    public void initialize(HardContext context) {
        this.enabled = this.localeService.getBoolean(SettingsLocaleImpl.SERVER_ESSENTIAL_EMOJI_FEATURE_BOOLEAN);
        if (!enabled) return;

        for (EmojiType value : EmojiType.values()) {
            this.emojis.put(value.getIdentifier(), value.getFormat());
        }
    }

    @Override
    public Map<String, String> getEmojis() {
        return Collections.unmodifiableMap(this.emojis);
    }

    @Override
    public Optional<String> getEmojiFormat(String identifier) {
        return Optional.ofNullable(this.emojis.get(identifier));
    }
}