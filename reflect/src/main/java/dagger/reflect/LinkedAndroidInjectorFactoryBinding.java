package dagger.reflect;

import dagger.android.AndroidInjector;
import dagger.reflect.Binding.LinkedBinding;

final class LinkedAndroidInjectorFactoryBinding<T>
    extends LinkedBinding<AndroidInjector.Factory<T>> {
  private final Scope scope;
  private final Class<?>[] moduleClasses;
  private final Class<T> instanceClass;

  LinkedAndroidInjectorFactoryBinding(Scope scope, Class<?>[] moduleClasses,
      Class<T> instanceClass) {
    this.scope = scope;
    this.moduleClasses = moduleClasses;
    this.instanceClass = instanceClass;
  }

  @Override public AndroidInjector.Factory<T> get() {
    return new ReflectiveAndroidInjector.Factory<>(scope, moduleClasses, instanceClass);
  }
}
