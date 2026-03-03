package dev.celestial.silly;

import org.jetbrains.annotations.Nullable;

// utility class for booleans that have 3 states
// - set true
// - set false
// - unset
public class OverridableBoolean {
    private Boolean overridden = false;
    private Boolean value;
    public Boolean isOverridden() {
        return overridden;
    }

    @Nullable
    public Boolean getValue() {
        return value;
    }

    public void setValue(@Nullable Boolean v) {
        overridden = v != null;
        value = v;
    }
}
