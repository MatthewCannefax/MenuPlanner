package com.matthewcannefax.menuplanner;

import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface MenuApplicationComponent {
    void inject(MainActivity activity);

    void inject(SplashFragment fragment);

    void inject(MenuListFragment fragment);
}
