/*
 * Licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 Case Walker.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copyz
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.casewalker.narratechat.config;

import com.casewalker.modutils.config.AbstractConfig;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Configurations for the mod. Extends {@link AbstractConfig} and is intended to be used with
 * {@link com.casewalker.modutils.config.ConfigHandler}.
 *
 * @author Case Walker
 */
public class NarrateChatModConfig extends AbstractConfig {

    private static final String BASE_FILENAME = "narratechatmod";
    private static final Path DEFAULT_JSON_CONFIG = Path.of("config", BASE_FILENAME + ".json");
    private static final Path DEFAULT_YAML_CONFIG = Path.of("config", BASE_FILENAME + ".yml");
    private static final Path OTHER_DEFAULT_YAML_CONFIG = Path.of("config", BASE_FILENAME + ".yaml");

    private Boolean modEnabled;

    @Override
    public List<Path> getDefaultConfigPaths() {
        return List.of(DEFAULT_JSON_CONFIG, DEFAULT_YAML_CONFIG, OTHER_DEFAULT_YAML_CONFIG);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NarrateChatModConfig that = (NarrateChatModConfig) o;
        // Determine equality based on just the one config property
        return Objects.equals(modEnabled, that.modEnabled);
    }

    /**
     * Provide a string representation of the configuration which can be narrated to the player.
     *
     * @return The string representation of the config
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Narrate Chat Mod Configuration: The mod is ");
        if (isModEnabled()) {
            sb.append("enabled");
            if (modEnabled == null) {
                sb.append(" (by default)");
            }
        } else {
            sb.append("disabled");
        }
        sb.append(".");
        return sb.toString();
    }

    /**
     * Whether the mod is enabled. If the value is null, return true (enabled by default).
     *
     * @return Whether the mod is enabled
     */
    public boolean isModEnabled() {
        return modEnabled == null || modEnabled;
    }

    public void setModEnabled(final boolean modEnabled) {
        this.modEnabled = modEnabled;
    }
}
