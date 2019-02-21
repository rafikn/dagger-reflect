package dagger.reflect;

import dagger.MembersInjector;
import dagger.android.AndroidInjector;

final class ReflectiveAndroidInjector<T> implements AndroidInjector<T> {
  private final MembersInjector<T> membersInjector;

  private ReflectiveAndroidInjector(MembersInjector<T> membersInjector) {
    this.membersInjector = membersInjector;
  }

  @Override public void inject(T instance) {
    membersInjector.injectMembers(instance);
  }

  static final class Factory<T> implements AndroidInjector.Factory<T> {
    private final Scope parent;
    private final Class<?>[] moduleClasses;
    private final Class<T> instanceClass;

    Factory(Scope parent, Class<?>[] moduleClasses, Class<T> instanceClass) {
      this.parent = parent;
      this.moduleClasses = moduleClasses;
      this.instanceClass = instanceClass;
    }

    @Override public AndroidInjector<T> create(T instance) {
      Scope.Builder scopeBuilder = new Scope.Builder(parent, null)
          .justInTimeLookupFactory(new ReflectiveJustInTimeLookupFactory())
          .addInstance(Key.of(null, instanceClass), instance);

      for (Class<?> moduleClass : moduleClasses) {
        scopeBuilder.addModule(moduleClass, null);
      }

      Scope scope = scopeBuilder.build();

      MembersInjector<T> membersInjector = ReflectiveMembersInjector.create(instanceClass, scope);
      return new ReflectiveAndroidInjector<>(membersInjector);
    }
  }
}
