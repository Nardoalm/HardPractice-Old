package live.pvphard.practice.bootstrap;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
@Getter
public final class HardContext {
    private static final String SERVICE_IMPL_PACKAGE = "live.pvphard.practice";

    private final HardPractice plugin;
    private final Map<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>, live.pvphard.practice.bootstrap.lifecycle.Service> serviceInstances = new ConcurrentHashMap<>();
    private final Map<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>, Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> serviceRegistry = new HashMap<>();
    private final Set<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> servicesBeingConstructed = new HashSet<>();

    private ScanResult scanResult;
    private List<live.pvphard.practice.bootstrap.lifecycle.Service> sortedServices = Collections.emptyList();

    public HardContext(HardPractice plugin) {
        this.plugin = Objects.requireNonNull(plugin, "A instância do plugin não pode ser nula");
    }

    /**
     * Discovers, instantiates, and initializes all services.
     */
    public void initialize() throws Exception {
        Logger.logPhaseStart("Service Initialization");

        try {
            this.scanResult = new ClassGraph().enableAllInfo().acceptPackages(SERVICE_IMPL_PACKAGE).scan();
        } catch (Exception e) {
            throw new IllegalStateException("A varredura do classpath falhou.", e);
        }

        List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> implementationClasses = discoverServices(this.scanResult);
        if (implementationClasses.isEmpty()) {
            throw new IllegalStateException("Nenhum serviço encontrado para carregar.");
        }

        for (Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> implClass : implementationClasses) {
            serviceRegistry.put(getProvidedInterface(implClass), implClass);
        }

        List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> sortedImplClasses = sortServicesByPriority(implementationClasses);
        Logger.info("Ordem de Inicialização dos Serviços: " +
                sortedImplClasses.stream().map(Class::getSimpleName).collect(Collectors.joining(" -> ")));

        for (Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> implClass : sortedImplClasses) {
            instantiateService(implClass);
        }

        this.sortedServices = sortedImplClasses.stream()
                .map(impl -> serviceInstances.get(getProvidedInterface(impl)))
                .collect(Collectors.toList());

        Logger.logPhaseStart("Service Setup Phase");
        for (live.pvphard.practice.bootstrap.lifecycle.Service service : sortedServices) {
            Logger.logTime(service.getClass().getSimpleName() + " Setup", () -> service.setup(this));
        }

        Logger.logPhaseStart("Service Initialization Phase");
        for (live.pvphard.practice.bootstrap.lifecycle.Service service : sortedServices) {
            Logger.logTime(service.getClass().getSimpleName() + " Initialization", () -> service.initialize(this));
        }

        Logger.logPhaseComplete("Service Initialization");
    }

    /**
     * Shuts down all managed services in reverse order.
     */
    public void shutdown() {
        Logger.info("--- Início do Encerramento dos Serviços ---");
        if (sortedServices == null) return;

        List<live.pvphard.practice.bootstrap.lifecycle.Service> reversedServices = new ArrayList<>(sortedServices);
        Collections.reverse(reversedServices);

        for (live.pvphard.practice.bootstrap.lifecycle.Service service : reversedServices) {
            try {
                service.shutdown(this);
            } catch (Exception e) {
                Logger.logException("Falha ao encerrar o serviço " + service.getClass().getSimpleName(), e);
            }
        }
        Logger.info("--- Encerramento dos Serviços Concluído ---");
    }

    /**
     * Retrieves a managed service by its interface.
     * @param serviceInterface The interface of the service to get.
     * @return An Optional containing the service instance if found.
     */
    public <T extends live.pvphard.practice.bootstrap.lifecycle.Service> Optional<T> getService(Class<T> serviceInterface) {
        return Optional.ofNullable(serviceInterface.cast(serviceInstances.get(serviceInterface)));
    }

    @SuppressWarnings("unchecked")
    private void instantiateService(Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> implClass) throws Exception {
        Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> providedInterface = getProvidedInterface(implClass);
        if (serviceInstances.containsKey(providedInterface)) {
            return;
        }

        if (servicesBeingConstructed.contains(implClass)) {
            throw new IllegalStateException("Dependência cíclica detectada! O ciclo envolve: " + implClass.getName());
        }
        servicesBeingConstructed.add(implClass);

        Constructor<?> constructor = Arrays.stream(implClass.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new NoSuchMethodException("Nenhum construtor adequado encontrado para " + implClass.getSimpleName()));

        List<Object> dependencies = new ArrayList<>();
        for (Class<?> paramType : constructor.getParameterTypes()) {
            if (HardPractice.class.isAssignableFrom(paramType)) {
                dependencies.add(this.plugin);
                continue;
            }
            if (live.pvphard.practice.bootstrap.lifecycle.Service.class.isAssignableFrom(paramType)) {
                Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> dependencyInterface = (Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>) paramType;
                Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> dependencyImpl = this.serviceRegistry.get(dependencyInterface);
                if (dependencyImpl == null) {
                    throw new ClassNotFoundException("Nenhuma implementação encontrada para a interface de serviço: " + dependencyInterface.getName());
                }

                instantiateService(dependencyImpl);
                dependencies.add(serviceInstances.get(dependencyInterface));
            } else {
                throw new IllegalStateException("Tipo de dependência não suportado em " + implClass.getSimpleName() + ": " + paramType.getSimpleName());
            }
        }

        live.pvphard.practice.bootstrap.lifecycle.Service serviceInstance = (live.pvphard.practice.bootstrap.lifecycle.Service) constructor.newInstance(dependencies.toArray());
        serviceInstances.put(providedInterface, serviceInstance);

        servicesBeingConstructed.remove(implClass);
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> discoverServices(ScanResult scanResult) {
        List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> services = new ArrayList<>();
        for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(Service.class.getName())) {
            if (!classInfo.isAbstract() && !classInfo.isInterface()) {
                services.add((Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>) classInfo.loadClass());
            }
        }
        return services;
    }

    private List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> sortServicesByPriority(List<Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service>> services) {
        return services.stream()
                .sorted(Comparator.comparingInt(c -> c.getAnnotation(Service.class).priority()))
                .collect(Collectors.toList());
    }

    private Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> getProvidedInterface(Class<? extends live.pvphard.practice.bootstrap.lifecycle.Service> implClass) {
        return implClass.getAnnotation(Service.class).provides();
    }
}
