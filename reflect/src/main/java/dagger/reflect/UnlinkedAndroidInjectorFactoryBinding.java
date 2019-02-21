package dagger.reflect;

final class UnlinkedAndroidInjectorFactoryBinding extends Binding.UnlinkedBinding {
  private final Class<?>[] moduleClasses;
  private final Class<?> instanceClass;

  UnlinkedAndroidInjectorFactoryBinding(Class<?>[] moduleClasses, Class<?> instanceClass) {
    this.moduleClasses = moduleClasses;
    this.instanceClass = instanceClass;
  }

  @Override public LinkedBinding<?> link(Linker linker, Scope scope) {
    return new LinkedAndroidInjectorFactoryBinding<>(scope, moduleClasses, instanceClass);
  }
}
