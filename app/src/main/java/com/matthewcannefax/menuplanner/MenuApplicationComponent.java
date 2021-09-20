package com.matthewcannefax.menuplanner;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface MenuApplicationComponent {
    void inject(MainActivity activity);

    void inject(SplashFragment fragment);

    void inject(MenuFragment fragment);
}
