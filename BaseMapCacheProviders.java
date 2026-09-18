package com.mapsindoorsrn.core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapsindoors.core.MPDataSetCacheManager;
import com.mapsindoors.core.models.MPIMapProviderBaseMapCache;

/**
 * The seam that lets the shared React Native core reach the map provider's base-map tile cache.
 *
 * <p>A {@link MPIMapProviderBaseMapCache} implementation is map-provider specific — {@code
 * MPMapboxBaseMapCacheProvider} caches into Mapbox's tile store, {@code MPGoogleBaseMapCacheProvider}
 * reports that Google Maps cannot cache at all — and this core module is compiled into both the Mapbox
 * and the Google Maps React Native package. It therefore cannot name either implementation. Each
 * package's {@code MapsIndoorsPackage} installs a factory here instead, and {@link
 * #ensureRegistered(Context)} builds from it on first use.</p>
 *
 * <p>Registration is deferred rather than done at app start for two reasons: it avoids initializing
 * {@link MPDataSetCacheManager} in apps that never cache anything, and the registration is held by the
 * manager instance, so it has to be re-done after {@code MapsIndoors.destroy()} tears that instance
 * down.</p>
 */
public final class BaseMapCacheProviders {

    /**
     * Builds the base-map tile cache for the map provider the hosting package renders with.
     */
    public interface Factory {
        /**
         * Creates a new base-map tile cache. Called at most once per {@link MPDataSetCacheManager}
         * instance, never with a map view in existence — an implementation must not need one.
         *
         * @return The map provider's base-map tile cache
         */
        @NonNull
        MPIMapProviderBaseMapCache create();
    }

    @Nullable
    private static volatile Factory sFactory;

    private BaseMapCacheProviders() { }

    /**
     * Installs the factory for the map provider in use. Called by each map package's
     * {@code MapsIndoorsPackage} as the package is constructed.
     *
     * @param factory The factory to build base-map tile caches from, or null to uninstall the current
     *         one
     */
    public static void setFactory(@Nullable Factory factory) {
        sFactory = factory;
    }

    /**
     * Makes sure a base-map tile cache is registered with the SDK, building one from the installed
     * factory if there isn't one already.
     *
     * <p>Idempotent, and safe to call on every base-map operation: an already-registered provider is
     * returned as-is rather than replaced, so caching already in flight is never orphaned.</p>
     *
     * @param context Context to initialize {@link MPDataSetCacheManager} with, if it isn't already
     * @return The registered base-map tile cache, or null if no factory has been installed. A null
     *         return is not an error to report on its own — the SDK reports
     *         {@code MIError.BASEMAP_CACHE_NOT_REGISTERED} for the operation that needed it
     */
    @Nullable
    public static synchronized MPIMapProviderBaseMapCache ensureRegistered(@NonNull Context context) {
        if (!MPDataSetCacheManager.isInitialized()) {
            MPDataSetCacheManager.initialize(context);
        }

        final MPDataSetCacheManager manager = MPDataSetCacheManager.getInstance();
        final MPIMapProviderBaseMapCache registered = manager.getBaseMapCacheProvider();

        if (registered != null) {
            return registered;
        }

        final Factory factory = sFactory;

        if (factory == null) {
            return null;
        }

        final MPIMapProviderBaseMapCache provider = factory.create();
        manager.setBaseMapCacheProvider(provider);

        return provider;
    }
}
