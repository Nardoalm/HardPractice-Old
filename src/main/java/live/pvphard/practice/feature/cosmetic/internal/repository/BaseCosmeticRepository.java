package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.model.CosmeticRepository;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import lombok.Getter;

import java.util.*;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
@Getter
public abstract class BaseCosmeticRepository<T extends BaseCosmetic> implements CosmeticRepository<T> {
    private final Map<String, T> cosmeticsByName;

    public BaseCosmeticRepository() {
        this.cosmeticsByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Register a cosmetic class to the repository
     *
     * @param clazz The class to register
     */
    protected void registerCosmetic(Class<? extends T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            this.cosmeticsByName.put(instance.getName(), instance);
        } catch (Exception e) {
            Logger.error("Falha ao registrar a classe de cosmético " + clazz.getSimpleName() + ": " + e.getMessage());
        }
    }

    @Override
    public CosmeticType getRepositoryType() {
        if (cosmeticsByName.isEmpty()) {
            return null;
        }
        return cosmeticsByName.values().iterator().next().getType();
    }

    @Override
    public List<T> getCosmetics() {
        return new ArrayList<>(this.cosmeticsByName.values());
    }

    @Override
    public T getCosmetic(String name) {
        return this.cosmeticsByName.get(name);
    }
}
