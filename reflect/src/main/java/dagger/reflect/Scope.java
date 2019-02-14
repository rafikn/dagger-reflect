package dagger.reflect;

import dagger.Lazy;
import dagger.reflect.Binding.LinkedBinding;
import dagger.reflect.Binding.UnlinkedBinding;
import javax.inject.Provider;
import org.jetbrains.annotations.Nullable;

final class Scope {
  private final BindingMap bindings;

  Scope(BindingMap bindings) {
    this.bindings = bindings;
  }

  <T> @Nullable T get(Class<T> cls) {
    return cls.cast(get(Key.of(null, cls)));
  }

  @Nullable Object get(Key key) {
    return getBinding(key).get();
  }

  <T> Provider<T> getProvider(Class<T> cls) {
    return new KeyProvider<>(this, Key.of(null, cls));
  }

  Provider<?> getProvider(Key key) {
    return new KeyProvider<>(this, key);
  }

  <T> Lazy<T> getLazy(Class<T> cls) {
    return new KeyLazy<>(this, Key.of(null, cls));
  }

  Lazy<?> getLazy(Key key) {
    return new KeyLazy<>(this, key);
  }

  LinkedBinding<?> getBinding(Key key) {
    Binding binding = bindings.get(key);
    if (binding instanceof LinkedBinding<?>) {
      return (LinkedBinding<?>) binding;
    }
    if (binding == null) {
      throw new IllegalArgumentException("No provider available for " + key);
    }
    return Linker.link(bindings, key, (UnlinkedBinding) binding);
  }

  private static final class KeyProvider<T> implements Provider<T> {
    private final Scope scope;
    private final Key key;

    KeyProvider(Scope scope, Key key) {
      this.scope = scope;
      this.key = key;
    }

    @Override public @Nullable T get() {
      return (T) scope.get(key);
    }
  }

  private static final class KeyLazy<T> implements Lazy<T> {
    private static final Object EMPTY = new Object();

    private final Object lock = new Object();
    private final Scope scope;
    private final Key key;
    private @Nullable Object value = EMPTY;

    KeyLazy(Scope scope, Key key) {
      this.scope = scope;
      this.key = key;
    }

    @Override public @Nullable T get() {
      Object value = this.value;
      if (value == EMPTY) {
        synchronized (lock) {
          value = this.value;
          if (value == EMPTY) {
            value = scope.get(key);
          }
        }
      }
      return (T) value;
    }
  }
}
