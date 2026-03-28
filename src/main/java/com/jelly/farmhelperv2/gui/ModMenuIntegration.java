package com.jelly.farmhelperv2.gui;

import com.jelly.farmhelperv2.config.FarmHelperConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu integration to show FarmHelper config in the Mods list.
 * Converted from Forge 1.8.9 GuiModList to Fabric 1.21.1 ModMenu.
 *
 * Register as an entrypoint in fabric.mod.json:
 * "modmenu": ["com.jelly.farmhelperv2.gui.ModMenuIntegration"]
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FarmHelperConfigScreen::build;
    }
}
