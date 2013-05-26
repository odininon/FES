package com.freyja.FES.RoutingSettings;

import java.util.LinkedList;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RoutingSettingsRegistry {
    private static RoutingSettingsRegistry instance = new RoutingSettingsRegistry();

    private LinkedList<IRoutingSetting> routingSettings = new LinkedList<IRoutingSetting>();

    private RoutingSettingsRegistry() {}

    public static RoutingSettingsRegistry Instance()
    {
        return instance;
    }

    public void registerRoutingSetting(IRoutingSetting setting)
    {
        if (!routingSettings.contains(setting)) routingSettings.add(setting);
    }

    public IRoutingSetting getRoutingSetting(int index)
    {
        return routingSettings.get(index);
    }

    public int indexOf(IRoutingSetting setting)
    {
        return routingSettings.indexOf(setting);
    }

    public int getSize()
    {
        return routingSettings.size();
    }
}
