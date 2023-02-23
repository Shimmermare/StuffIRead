-dontobfuscate

-dontwarn com.shimmermare.stuffiread.ui.components.colorpicker.PopupColorPicker*
-dontwarn com.shimmermare.stuffiread.ui.components.tag.MultiTagSelector*
-dontwarn com.shimmermare.stuffiread.ui.components.tagcategory.TagCategorySelector*
-dontwarn org.jetbrains.skiko.GraphicsApi*
-dontwarn org.jetbrains.skiko.HardwareLayer*
-dontwarn org.jetbrains.skiko.redrawer.AbstractDirectSoftwareRedrawer*
-dontwarn org.jetbrains.skiko.redrawer.LinuxOpenGLRedrawer*
-dontwarn org.jetbrains.skiko.redrawer.SoftwareRedrawer*

# Otherwise logging will fail - accessing stacktrace with hardcoded index at io.github.aakira.napier.DebugAntilog.kt:81
-keep class io.github.aakira.napier.Napier* { *; }

# Kotlin serialization looks up the generated serializer classes through a function on companion
# objects. The companions are looked up reflectively so we need to explicitly keep these functions.
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
# If a companion has the serializer function, keep the companion field on the original type so that
# the reflective lookup succeeds.
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}