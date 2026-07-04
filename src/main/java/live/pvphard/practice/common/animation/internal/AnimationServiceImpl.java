package live.pvphard.practice.common.animation.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.common.animation.AnimationService;
import live.pvphard.practice.common.animation.AnimationType;
import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.animation.internal.config.TextAnimation;
import live.pvphard.practice.common.animation.internal.types.Animation;
import live.pvphard.practice.common.logger.Logger;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the registration and retrieval of both internal and config-based animations.
 * Uses reflect to automatically register animations dynamically.
 *
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
@Getter
@Service(provides = AnimationService.class, priority = 310)
public class AnimationServiceImpl implements AnimationService {
    private final PluginConstant pluginConstant;

    private final Set<Animation> internalAnimations = new HashSet<>();
    private final Set<TextAnimation> configAnimations = new HashSet<>();

    /**
     * Constructor for DI.
     */
    public AnimationServiceImpl(PluginConstant pluginConstant) {
        this.pluginConstant = pluginConstant;
    }

    @Override
    public void initialize(HardContext context) {
        Reflections reflections = this.pluginConstant.getReflections();
        if (reflections == null) {
            Logger.error("AnimationServiceImpl nao pode ser inicializado: o objeto Reflections esta nulo.");
            return;
        }

        this.registerAnimations(reflections, Animation.class, this.internalAnimations);
        this.registerAnimations(reflections, TextAnimation.class, this.configAnimations);
    }

    @Override
    public <T> T getAnimation(Class<T> clazz, AnimationType type) {
        Set<?> sourceSet = (type == AnimationType.INTERNAL) ? this.internalAnimations : this.configAnimations;

        return sourceSet.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma animacao encontrada para a classe: " + clazz.getName() + " com o tipo: " + type));
    }

    @Override
    public Set<Animation> getInternalAnimations() {
        return Collections.unmodifiableSet(this.internalAnimations);
    }

    @Override
    public Set<TextAnimation> getConfigAnimations() {
        return Collections.unmodifiableSet(this.configAnimations);
    }

    /**
     * Scans and registers all non-abstract animation classes of the given type.
     *
     * @param <T>        The animation type.
     * @param superClass The base class of animations to register.
     * @param targetSet  The collection where instances should be stored.
     */
    private <T> void registerAnimations(Reflections reflections, Class<T> superClass, Set<T> targetSet) {
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(superClass).stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface())
                .collect(Collectors.toSet());

        for (Class<? extends T> clazz : classes) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                targetSet.add(instance);
            } catch (Exception e) {
                Logger.logException("Falha ao instanciar animacao: " + clazz.getName(), e);
            }
        }
    }
}
