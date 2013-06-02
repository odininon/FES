package com.freyja.FES.RoutingSettings;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RoutingSettingsRegistry {
    public enum Type {
        ITEM, LIQUID, BOTH
    }

    private static RoutingSettingsRegistry instance = new RoutingSettingsRegistry();

    private LinkedList<IRoutingSetting> itemRoutingSettings = new LinkedList<IRoutingSetting>();
    private LinkedList<IRoutingSetting> liquidRoutingSettings = new LinkedList<IRoutingSetting>();

    private RoutingSettingsRegistry() {}

    public static RoutingSettingsRegistry Instance()
    {
        return instance;
    }

    public void registerRoutingSetting(IRoutingSetting setting, Type type)
    {
        switch (type) {
            case ITEM:
                if (!itemRoutingSettings.contains(setting)) itemRoutingSettings.add(setting);
                break;
            case LIQUID:
                if (!liquidRoutingSettings.contains(setting)) liquidRoutingSettings.add(setting);
                break;
            case BOTH:
                if (!itemRoutingSettings.contains(setting)) itemRoutingSettings.add(setting);
                if (!liquidRoutingSettings.contains(setting)) liquidRoutingSettings.add(setting);
                break;
        }
    }

    public IRoutingSetting getRoutingSetting(int index, Type type)
    {
        switch (type) {
            case ITEM:
                return itemRoutingSettings.get(index);
            case LIQUID:
                return liquidRoutingSettings.get(index);
        }
        return null;
    }

    public int indexOf(IRoutingSetting setting, Type type)
    {
        switch (type) {
            case ITEM:
                return itemRoutingSettings.indexOf(setting);
            case LIQUID:
                return liquidRoutingSettings.indexOf(setting);
        }
        return -1;
    }

    public int getSize(Type type)
    {
        switch (type) {
            case ITEM:
                return itemRoutingSettings.size();
            case LIQUID:
                return liquidRoutingSettings.size();
        }
        return -1;
    }

    public IRoutingSetting getModSetting(String modId, Type type)
    {
        switch (type) {
            case ITEM:
                for (IRoutingSetting setting : itemRoutingSettings) {
                    if (setting instanceof ModSortSettings && ((ModSortSettings) setting).getModId().equalsIgnoreCase(modId)) {
                        return setting;
                    }
                }
            case LIQUID:
                for (IRoutingSetting setting : liquidRoutingSettings) {
                    if (setting.getName().equalsIgnoreCase(modId)) {
                        return setting;
                    }
                }
        }
        return null;
    }

    public void sort(Type type)
    {
        switch (type) {
            case ITEM:
                Collections.sort(itemRoutingSettings.subList(2, itemRoutingSettings.size()), new Comparator<IRoutingSetting>() {
                    @Override
                    public int compare(IRoutingSetting o1, IRoutingSetting o2)
                    {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
            case LIQUID:
                Collections.sort(liquidRoutingSettings.subList(2, liquidRoutingSettings.size()), new Comparator<IRoutingSetting>() {
                    @Override
                    public int compare(IRoutingSetting o1, IRoutingSetting o2)
                    {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
        }
    }
}