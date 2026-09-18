package com.mapsindoorsrn.core.models;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPDirectionsRendererConfig;
import com.mapsindoors.core.MPDirectionsRendererOptions;
import com.mapsindoors.core.MPRouteAnimationType;
import com.mapsindoors.core.MPRouteArrowStyle;
import com.mapsindoors.core.MPRouteStampType;
import com.mapsindoors.core.MPStrokeStyle;

/**
 * The flat directions renderer options as they cross the React Native bridge.
 *
 * The Android SDK splits the same surface in two: the polyline styling and animation timing live on
 * {@link MPDirectionsRendererOptions}, everything else on {@link MPDirectionsRendererConfig}. Every
 * field here is nullable, so an option the app left out is inherited instead of reset - from the
 * previously applied options, or from the solution config.
 */
public class DirectionsRendererOptionsModel {
    /**
     * The stamp type value used for a custom icon. The Android SDK has no preset for it, a custom
     * stamp is expressed by {@link MPDirectionsRendererConfig.Stamp#iconUrl} alone.
     */
    private static final String STAMP_TYPE_CUSTOM = "custom";

    @Nullable @SerializedName("strokeColor") public String strokeColor;
    @Nullable @SerializedName("strokeOpacity") public Float strokeOpacity;
    @Nullable @SerializedName("strokeWeight") public Float strokeWeight;
    @Nullable @SerializedName("strokeStyle") public MPStrokeStyle strokeStyle;
    @Nullable @SerializedName("backgroundColorEnabled") public Boolean backgroundColorEnabled;
    @Nullable @SerializedName("backgroundColor") public String backgroundColor;
    @Nullable @SerializedName("backgroundColorOpacity") public Double backgroundColorOpacity;
    @Nullable @SerializedName("backgroundColorWeight") public Double backgroundColorWeight;
    @Nullable @SerializedName("animationType") public MPRouteAnimationType animationType;
    @Nullable @SerializedName("animationSpeed") public Double animationSpeed;
    @Nullable @SerializedName("animationMinDuration") public Double animationMinDuration;
    @Nullable @SerializedName("animationRepeating") public Boolean animationRepeating;
    @Nullable @SerializedName("forceAnimation") public Boolean forceAnimation;
    @Nullable @SerializedName("animatedOverlayColor") public String animatedOverlayColor;
    @Nullable @SerializedName("animatedOverlayOpacity") public Float animatedOverlayOpacity;
    @Nullable @SerializedName("animatedOverlayWeight") public Float animatedOverlayWeight;
    @Nullable @SerializedName("stampType") public String stampType;
    @Nullable @SerializedName("stampImageUrl") public String stampImageUrl;
    @Nullable @SerializedName("stampSpacing") public Double stampSpacing;
    @Nullable @SerializedName("stampScale") public Double stampScale;
    @Nullable @SerializedName("arrowStyle") public MPRouteArrowStyle arrowStyle;
    @Nullable @SerializedName("stampColor") public String stampColor;
    @Nullable @SerializedName("startDisplayRule") public RouteMarkerDisplayRule startDisplayRule;
    @Nullable @SerializedName("endDisplayRule") public RouteMarkerDisplayRule endDisplayRule;
    @Nullable @SerializedName("legBoundaryIcons") public LegBoundaryIcons legBoundaryIcons;
    @Nullable @SerializedName("elevated") public Boolean elevated;
    @Nullable @SerializedName("elevationHeight") public Double elevationHeight;
    @Nullable @SerializedName("fitBoundsMaxZoom") public Double fitBoundsMaxZoom;

    /**
     * The subset of a route terminus display rule that the Android SDK renders.
     *
     * The bridge keeps the label style flat, the way the iOS SDK exposes it, so it is nested back
     * into {@link MPDirectionsRendererConfig.TerminiLabelStyle} here.
     */
    public static class RouteMarkerDisplayRule {
        @Nullable @SerializedName("iconUrl") public String iconUrl;
        @Nullable @SerializedName("iconVisible") public Boolean iconVisible;
        @Nullable @SerializedName("iconSize") public IconSize iconSize;
        @Nullable @SerializedName("label") public String label;
        @Nullable @SerializedName("labelVisible") public Boolean labelVisible;
        @Nullable @SerializedName("labelTextSize") public Double labelTextSize;
        @Nullable @SerializedName("labelTextColor") public String labelTextColor;
        @Nullable @SerializedName("labelHaloColor") public String labelHaloColor;
        @Nullable @SerializedName("labelHaloWidth") public Double labelHaloWidth;
        @Nullable @SerializedName("zoomFrom") public Double zoomFrom;
        @Nullable @SerializedName("zoomTo") public Double zoomTo;

        MPDirectionsRendererConfig.TerminiDisplayRule toTerminiDisplayRule() {
            MPDirectionsRendererConfig.TerminiLabelStyle labelStyle = null;
            if (labelTextSize != null || labelTextColor != null || labelHaloColor != null || labelHaloWidth != null) {
                labelStyle = new MPDirectionsRendererConfig.TerminiLabelStyle(
                        labelTextSize, labelTextColor, labelHaloColor, labelHaloWidth);
            }
            return new MPDirectionsRendererConfig.TerminiDisplayRule(
                    iconUrl,
                    iconSize != null ? iconSize.toIconSize() : null,
                    iconVisible,
                    label,
                    labelVisible,
                    labelStyle,
                    zoomFrom,
                    zoomTo);
        }

        @Nullable
        static RouteMarkerDisplayRule fromTerminiDisplayRule(@Nullable MPDirectionsRendererConfig.TerminiDisplayRule rule) {
            if (rule == null) {
                return null;
            }
            RouteMarkerDisplayRule model = new RouteMarkerDisplayRule();
            model.iconUrl = rule.getIcon();
            model.iconVisible = rule.getIconVisible();
            model.iconSize = IconSize.fromIconSize(rule.getIconSize());
            model.label = rule.getLabel();
            model.labelVisible = rule.getLabelVisible();
            MPDirectionsRendererConfig.TerminiLabelStyle style = rule.getLabelStyle();
            if (style != null) {
                model.labelTextSize = style.getTextSize();
                model.labelTextColor = style.getTextColor();
                model.labelHaloColor = style.getHaloColor();
                model.labelHaloWidth = style.getHaloWidth();
            }
            model.zoomFrom = rule.getZoomFrom();
            model.zoomTo = rule.getZoomTo();
            return model;
        }
    }

    /**
     * Explicit icon dimensions in pixels.
     */
    public static class IconSize {
        @Nullable @SerializedName("width") public Double width;
        @Nullable @SerializedName("height") public Double height;

        MPDirectionsRendererConfig.IconSize toIconSize() {
            return new MPDirectionsRendererConfig.IconSize(width, height);
        }

        @Nullable
        static IconSize fromIconSize(@Nullable MPDirectionsRendererConfig.IconSize size) {
            if (size == null) {
                return null;
            }
            IconSize model = new IconSize();
            model.width = size.getWidth();
            model.height = size.getHeight();
            return model;
        }
    }

    /**
     * Per connector type leg boundary icons.
     *
     * The bridge uses camel cased connector names, the Android SDK's wire keys are lower cased.
     */
    public static class LegBoundaryIcons {
        @Nullable @SerializedName("defaultIcon") public String defaultIcon;
        @Nullable @SerializedName("elevator") public String elevator;
        @Nullable @SerializedName("escalator") public String escalator;
        @Nullable @SerializedName("stairs") public String stairs;
        @Nullable @SerializedName("ramp") public String ramp;
        @Nullable @SerializedName("wheelchairRamp") public String wheelchairRamp;
        @Nullable @SerializedName("wheelchairLift") public String wheelchairLift;
        @Nullable @SerializedName("ladder") public String ladder;
        @Nullable @SerializedName("entry") public String entry;
        @Nullable @SerializedName("scale") public Double scale;

        MPDirectionsRendererConfig.LegBoundaryIcons toLegBoundaryIcons() {
            return new MPDirectionsRendererConfig.LegBoundaryIcons(
                    defaultIcon, elevator, escalator, stairs, ramp,
                    wheelchairRamp, wheelchairLift, ladder, entry, scale);
        }

        @Nullable
        static LegBoundaryIcons fromLegBoundaryIcons(@Nullable MPDirectionsRendererConfig.LegBoundaryIcons icons) {
            if (icons == null) {
                return null;
            }
            LegBoundaryIcons model = new LegBoundaryIcons();
            model.defaultIcon = icons.getDefaultIcon();
            model.elevator = icons.getElevator();
            model.escalator = icons.getEscalator();
            model.stairs = icons.getStairs();
            model.ramp = icons.getRamp();
            model.wheelchairRamp = icons.getWheelchairramp();
            model.wheelchairLift = icons.getWheelchairlift();
            model.ladder = icons.getLadder();
            model.entry = icons.getEntry();
            model.scale = icons.getScale();
            return model;
        }
    }

    /**
     * Builds the polyline styling and animation timing the Android SDK takes as options.
     *
     * A set of options is declarative, so an option left out here falls back to the SDK's built-in
     * default rather than to whatever was applied before.
     */
    @NonNull
    public MPDirectionsRendererOptions toMPDirectionsRendererOptions() {
        MPDirectionsRendererOptions current = new MPDirectionsRendererOptions();
        Integer overlayColor = animatedOverlayColor != null
                ? Integer.valueOf(Color.parseColor(animatedOverlayColor))
                : current.getAnimatedOverlayColor();
        return new MPDirectionsRendererOptions(
                strokeColor != null ? Color.parseColor(strokeColor) : current.getStrokeColor(),
                strokeOpacity != null ? strokeOpacity : current.getStrokeOpacity(),
                strokeWeight != null ? strokeWeight : current.getStrokeWeight(),
                strokeStyle != null ? strokeStyle : current.getStrokeStyle(),
                animationSpeed != null ? animationSpeed : current.getAnimationSpeed(),
                animationMinDuration != null ? animationMinDuration : current.getAnimationMinDuration(),
                animationRepeating != null ? animationRepeating : current.getAnimationRepeating(),
                overlayColor,
                animatedOverlayOpacity != null ? animatedOverlayOpacity : current.getAnimatedOverlayOpacity(),
                animatedOverlayWeight != null ? animatedOverlayWeight : current.getAnimatedOverlayWeight(),
                fitBoundsMaxZoom != null ? fitBoundsMaxZoom : current.getFitBoundsMaxZoom(),
                forceAnimation != null ? forceAnimation : current.getForceAnimation());
    }

    /**
     * Builds the route styling the Android SDK takes as a config.
     *
     * The SDK merges a runtime config over the solution config whole-block for everything but the
     * line and the halo, so an app that overrides one field of a block would otherwise drop the rest
     * of the solution's block. Every block is therefore completed field by field from
     * {@code solutionConfig}, which keeps an option that was left out inherited rather than reset -
     * the resolution order the bridge documents on both platforms.
     *
     * A block neither the app nor the solution configured stays null, so the SDK keeps its built-in
     * appearance for it.
     *
     * @param solutionConfig the solution served config, as it was before any runtime override was
     *                       applied. Pass null when the solution serves none.
     */
    @NonNull
    public MPDirectionsRendererConfig toMPDirectionsRendererConfig(@Nullable MPDirectionsRendererConfig solutionConfig) {
        MPDirectionsRendererConfig.Line baseLine = solutionConfig != null ? solutionConfig.getLine() : null;
        MPDirectionsRendererConfig.Halo baseHalo = solutionConfig != null ? solutionConfig.getHalo() : null;
        MPDirectionsRendererConfig.Animation baseAnimation = solutionConfig != null ? solutionConfig.getAnimation() : null;
        MPDirectionsRendererConfig.Overlay baseOverlay = baseAnimation != null ? baseAnimation.getOverlay() : null;
        MPDirectionsRendererConfig.Stamp baseStamp = solutionConfig != null ? solutionConfig.getStamp() : null;
        MPDirectionsRendererConfig.Camera baseCamera = solutionConfig != null ? solutionConfig.getCamera() : null;
        MPDirectionsRendererConfig.Elevation baseElevation = solutionConfig != null ? solutionConfig.getElevation() : null;

        MPDirectionsRendererConfig.Line line = null;
        if (strokeColor != null || strokeOpacity != null || strokeWeight != null || strokeStyle != null || baseLine != null) {
            line = new MPDirectionsRendererConfig.Line(
                    strokeColor != null ? strokeColor : baseLine != null ? baseLine.getStrokeColor() : null,
                    strokeOpacity != null ? strokeOpacity.doubleValue() : baseLine != null ? baseLine.getStrokeOpacity() : null,
                    strokeWeight != null ? strokeWeight.doubleValue() : baseLine != null ? baseLine.getStrokeWeight() : null,
                    strokeStyle != null ? strokeStyle : baseLine != null ? baseLine.getStrokeStyle() : null);
        }

        MPDirectionsRendererConfig.Halo halo = null;
        if (backgroundColorEnabled != null || backgroundColor != null
                || backgroundColorOpacity != null || backgroundColorWeight != null || baseHalo != null) {
            halo = new MPDirectionsRendererConfig.Halo(
                    backgroundColorEnabled != null ? backgroundColorEnabled : baseHalo != null ? baseHalo.getEnabled() : null,
                    backgroundColor != null ? backgroundColor : baseHalo != null ? baseHalo.getColor() : null,
                    backgroundColorOpacity != null ? backgroundColorOpacity : baseHalo != null ? baseHalo.getOpacity() : null,
                    backgroundColorWeight != null ? backgroundColorWeight : baseHalo != null ? baseHalo.getWeight() : null);
        }

        MPDirectionsRendererConfig.Overlay overlay = null;
        if (animatedOverlayColor != null || animatedOverlayOpacity != null
                || animatedOverlayWeight != null || baseOverlay != null) {
            overlay = new MPDirectionsRendererConfig.Overlay(
                    animatedOverlayColor != null ? animatedOverlayColor : baseOverlay != null ? baseOverlay.getStrokeColor() : null,
                    animatedOverlayOpacity != null ? animatedOverlayOpacity.doubleValue() : baseOverlay != null ? baseOverlay.getStrokeOpacity() : null,
                    animatedOverlayWeight != null ? animatedOverlayWeight.doubleValue() : baseOverlay != null ? baseOverlay.getStrokeWeight() : null);
        }

        MPDirectionsRendererConfig.Animation animation = null;
        if (animationType != null || animationSpeed != null || animationMinDuration != null
                || forceAnimation != null || overlay != null || baseAnimation != null) {
            animation = new MPDirectionsRendererConfig.Animation(
                    animationType != null ? animationType : baseAnimation != null ? baseAnimation.getType() : null,
                    animationSpeed != null ? animationSpeed : baseAnimation != null ? baseAnimation.getSpeed() : null,
                    animationMinDuration != null ? animationMinDuration : baseAnimation != null ? baseAnimation.getMinAnimationTime() : null,
                    forceAnimation != null ? forceAnimation : baseAnimation != null ? baseAnimation.getForceAnimation() : null,
                    overlay);
        }

        MPDirectionsRendererConfig.Stamp stamp = null;
        if (stampType != null || stampImageUrl != null || stampSpacing != null
                || stampScale != null || arrowStyle != null || stampColor != null || baseStamp != null) {
            stamp = new MPDirectionsRendererConfig.Stamp(
                    stampPreset(baseStamp),
                    stampImageUrl != null ? stampImageUrl : baseStamp != null ? baseStamp.getIconUrl() : null,
                    stampSpacing != null ? stampSpacing : baseStamp != null ? baseStamp.getSpacingPx() : null,
                    stampScale != null ? stampScale : baseStamp != null ? baseStamp.getScale() : null,
                    arrowStyle != null ? arrowStyle : baseStamp != null ? baseStamp.getArrowStyle() : null,
                    stampColor != null ? stampColor : baseStamp != null ? baseStamp.getColor() : null);
        }

        MPDirectionsRendererConfig.Camera camera = null;
        if (fitBoundsMaxZoom != null || baseCamera != null) {
            camera = new MPDirectionsRendererConfig.Camera(
                    fitBoundsMaxZoom != null ? fitBoundsMaxZoom
                            : baseCamera != null ? baseCamera.getFitBoundsMaxZoom() : null);
        }

        MPDirectionsRendererConfig.Elevation elevation = null;
        if (elevated != null || elevationHeight != null || baseElevation != null) {
            elevation = new MPDirectionsRendererConfig.Elevation(
                    elevated != null ? elevated : baseElevation != null ? baseElevation.getElevated() : null,
                    elevationHeight != null ? elevationHeight : baseElevation != null ? baseElevation.getHeightMeters() : null);
        }

        return new MPDirectionsRendererConfig(
                line,
                halo,
                animation,
                stamp,
                solutionConfig != null ? solutionConfig.getStopIcons() : null,
                mergedLegBoundaryIcons(solutionConfig),
                mergedTerminiDisplayRule(startDisplayRule, solutionConfig != null ? solutionConfig.getStartDisplayRule() : null),
                mergedTerminiDisplayRule(endDisplayRule, solutionConfig != null ? solutionConfig.getEndDisplayRule() : null),
                camera,
                elevation);
    }

    @Nullable
    private MPDirectionsRendererConfig.LegBoundaryIcons mergedLegBoundaryIcons(@Nullable MPDirectionsRendererConfig solutionConfig) {
        MPDirectionsRendererConfig.LegBoundaryIcons base = solutionConfig != null ? solutionConfig.getLegBoundaryIcons() : null;
        if (legBoundaryIcons == null) {
            return base;
        }
        MPDirectionsRendererConfig.LegBoundaryIcons icons = legBoundaryIcons.toLegBoundaryIcons();
        if (base == null) {
            return icons;
        }
        return new MPDirectionsRendererConfig.LegBoundaryIcons(
                icons.getDefaultIcon() != null ? icons.getDefaultIcon() : base.getDefaultIcon(),
                icons.getElevator() != null ? icons.getElevator() : base.getElevator(),
                icons.getEscalator() != null ? icons.getEscalator() : base.getEscalator(),
                icons.getStairs() != null ? icons.getStairs() : base.getStairs(),
                icons.getRamp() != null ? icons.getRamp() : base.getRamp(),
                icons.getWheelchairramp() != null ? icons.getWheelchairramp() : base.getWheelchairramp(),
                icons.getWheelchairlift() != null ? icons.getWheelchairlift() : base.getWheelchairlift(),
                icons.getLadder() != null ? icons.getLadder() : base.getLadder(),
                icons.getEntry() != null ? icons.getEntry() : base.getEntry(),
                icons.getScale() != null ? icons.getScale() : base.getScale());
    }

    @Nullable
    private static MPDirectionsRendererConfig.TerminiDisplayRule mergedTerminiDisplayRule(
            @Nullable RouteMarkerDisplayRule override,
            @Nullable MPDirectionsRendererConfig.TerminiDisplayRule base) {
        if (override == null) {
            return base;
        }
        MPDirectionsRendererConfig.TerminiDisplayRule rule = override.toTerminiDisplayRule();
        if (base == null) {
            return rule;
        }
        MPDirectionsRendererConfig.TerminiLabelStyle baseStyle = base.getLabelStyle();
        MPDirectionsRendererConfig.TerminiLabelStyle style = rule.getLabelStyle();
        MPDirectionsRendererConfig.TerminiLabelStyle mergedStyle = style == null ? baseStyle
                : baseStyle == null ? style
                : new MPDirectionsRendererConfig.TerminiLabelStyle(
                        style.getTextSize() != null ? style.getTextSize() : baseStyle.getTextSize(),
                        style.getTextColor() != null ? style.getTextColor() : baseStyle.getTextColor(),
                        style.getHaloColor() != null ? style.getHaloColor() : baseStyle.getHaloColor(),
                        style.getHaloWidth() != null ? style.getHaloWidth() : baseStyle.getHaloWidth());

        return new MPDirectionsRendererConfig.TerminiDisplayRule(
                rule.getIcon() != null ? rule.getIcon() : base.getIcon(),
                rule.getIconSize() != null ? rule.getIconSize() : base.getIconSize(),
                rule.getIconVisible() != null ? rule.getIconVisible() : base.getIconVisible(),
                rule.getLabel() != null ? rule.getLabel() : base.getLabel(),
                rule.getLabelVisible() != null ? rule.getLabelVisible() : base.getLabelVisible(),
                mergedStyle,
                rule.getZoomFrom() != null ? rule.getZoomFrom() : base.getZoomFrom(),
                rule.getZoomTo() != null ? rule.getZoomTo() : base.getZoomTo());
    }

    /**
     * Flattens the options and the effective config the renderer currently holds back into the
     * bridge's shape.
     */
    @NonNull
    public static DirectionsRendererOptionsModel from(@Nullable MPDirectionsRendererOptions options,
                                                      @Nullable MPDirectionsRendererConfig config) {
        DirectionsRendererOptionsModel model = new DirectionsRendererOptionsModel();

        if (options != null) {
            model.strokeColor = colorToHexString(options.getStrokeColor());
            model.strokeOpacity = options.getStrokeOpacity();
            model.strokeWeight = options.getStrokeWeight();
            model.strokeStyle = options.getStrokeStyle();
            model.animationSpeed = options.getAnimationSpeed();
            model.animationMinDuration = options.getAnimationMinDuration();
            model.animationRepeating = options.getAnimationRepeating();
            Integer overlayColor = options.getAnimatedOverlayColor();
            model.animatedOverlayColor = overlayColor != null ? colorToHexString(overlayColor) : null;
            model.animatedOverlayOpacity = options.getAnimatedOverlayOpacity();
            model.animatedOverlayWeight = options.getAnimatedOverlayWeight();
            model.fitBoundsMaxZoom = options.getFitBoundsMaxZoom();
            model.forceAnimation = options.getForceAnimation();
        }

        if (config == null) {
            return model;
        }

        MPDirectionsRendererConfig.Line line = config.getLine();
        if (line != null) {
            if (model.strokeColor == null) model.strokeColor = line.getStrokeColor();
            if (model.strokeOpacity == null && line.getStrokeOpacity() != null) model.strokeOpacity = line.getStrokeOpacity().floatValue();
            if (model.strokeWeight == null && line.getStrokeWeight() != null) model.strokeWeight = line.getStrokeWeight().floatValue();
            if (model.strokeStyle == null) model.strokeStyle = line.getStrokeStyle();
        }

        MPDirectionsRendererConfig.Halo halo = config.getHalo();
        if (halo != null) {
            model.backgroundColorEnabled = halo.getEnabled();
            model.backgroundColor = halo.getColor();
            model.backgroundColorOpacity = halo.getOpacity();
            model.backgroundColorWeight = halo.getWeight();
        }

        MPDirectionsRendererConfig.Animation animation = config.getAnimation();
        if (animation != null) {
            model.animationType = animation.getType();
            if (model.animationSpeed == null) model.animationSpeed = animation.getSpeed();
            if (model.animationMinDuration == null) model.animationMinDuration = animation.getMinAnimationTime();
            if (model.forceAnimation == null) model.forceAnimation = animation.getForceAnimation();
            MPDirectionsRendererConfig.Overlay overlay = animation.getOverlay();
            if (overlay != null) {
                if (model.animatedOverlayColor == null) model.animatedOverlayColor = overlay.getStrokeColor();
                if (model.animatedOverlayOpacity == null && overlay.getStrokeOpacity() != null) model.animatedOverlayOpacity = overlay.getStrokeOpacity().floatValue();
                if (model.animatedOverlayWeight == null && overlay.getStrokeWeight() != null) model.animatedOverlayWeight = overlay.getStrokeWeight().floatValue();
            }
        }

        MPDirectionsRendererConfig.Stamp stamp = config.getStamp();
        if (stamp != null) {
            model.stampImageUrl = stamp.getIconUrl();
            model.stampType = model.stampImageUrl != null ? STAMP_TYPE_CUSTOM
                    : stamp.getPreset() != null ? stampTypeToString(stamp.getPreset()) : null;
            model.stampSpacing = stamp.getSpacingPx();
            model.stampScale = stamp.getScale();
            model.arrowStyle = stamp.getArrowStyle();
            model.stampColor = stamp.getColor();
        }

        model.startDisplayRule = RouteMarkerDisplayRule.fromTerminiDisplayRule(config.getStartDisplayRule());
        model.endDisplayRule = RouteMarkerDisplayRule.fromTerminiDisplayRule(config.getEndDisplayRule());
        model.legBoundaryIcons = LegBoundaryIcons.fromLegBoundaryIcons(config.getLegBoundaryIcons());

        MPDirectionsRendererConfig.Camera camera = config.getCamera();
        if (camera != null && model.fitBoundsMaxZoom == null) {
            model.fitBoundsMaxZoom = camera.getFitBoundsMaxZoom();
        }

        MPDirectionsRendererConfig.Elevation elevation = config.getElevation();
        if (elevation != null) {
            model.elevated = elevation.getElevated();
            model.elevationHeight = elevation.getHeightMeters();
        }

        return model;
    }

    /**
     * Resolves the stamp preset, inheriting from {@code base} where the app expressed no opinion.
     *
     * The Android SDK has no preset for a custom stamp, that case is carried by the icon URL alone,
     * so {@code custom} deliberately resolves to a null preset. An unrecognised value inherits
     * rather than doing the same: it is a typo, not a request for a custom stamp, and treating the
     * two alike would silently drop a preset the solution configured.
     */
    @Nullable
    private MPRouteStampType stampPreset(@Nullable MPDirectionsRendererConfig.Stamp base) {
        if (stampType == null) {
            return base != null ? base.getPreset() : null;
        }
        if ("none".equalsIgnoreCase(stampType)) {
            return MPRouteStampType.NONE;
        }
        if ("arrow".equalsIgnoreCase(stampType)) {
            return MPRouteStampType.ARROW;
        }
        if (STAMP_TYPE_CUSTOM.equalsIgnoreCase(stampType)) {
            return null;
        }
        return base != null ? base.getPreset() : null;
    }

    @NonNull
    private static String stampTypeToString(@NonNull MPRouteStampType type) {
        return type == MPRouteStampType.NONE ? "none" : "arrow";
    }

    /**
     * The bridge keeps colors opaque and carries transparency in the separate opacity options, so
     * that a color reads back the same on both platforms - the two SDKs disagree on where the alpha
     * component of an 8 digit hex string sits.
     */
    @NonNull
    private static String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }
}
