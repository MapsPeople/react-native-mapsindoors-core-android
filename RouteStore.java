package com.mapsindoorsrn.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapsindoors.core.MPRoute;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps the routes {@link DirectionsServiceModule#getRoute} returned, so
 * {@link DirectionsRendererModule#setRoute} can render the very instance instead of parsing the copy
 * that comes back from JavaScript (SPEX-2782).
 *
 * Only that instance carries the endpoints the SDK reports with the directions Insights events, and
 * the SDK keeps them out of Gson, so a parsed copy reports none.
 *
 * Shared by the two modules, since neither can reach the other's instance. Holds at most
 * {@link #CAPACITY} routes and forgets the least recently used, since JavaScript never says when it is
 * done with one. A forgotten route still renders, from its JSON, just without endpoints. Rendering
 * a kept route again is safe: the SDK resolves its endpoints once and reads them on every event.
 */
final class RouteStore {
    static final int CAPACITY = 10;

    // Access order, so a lookup counts as use and a route the app keeps setting stays.
    private static final Map<String, MPRoute> sRoutes = new LinkedHashMap<String, MPRoute>(CAPACITY + 1, 1f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, MPRoute> eldest) {
            return size() > CAPACITY;
        }
    };

    private RouteStore() {}

    /** Keeps {@code route} and returns the key to look it up by. */
    @NonNull
    static synchronized String keep(@NonNull MPRoute route) {
        String key = UUID.randomUUID().toString();
        sRoutes.put(key, route);
        return key;
    }

    /** The route kept under {@code key}, if it is still kept. */
    @Nullable
    static synchronized MPRoute get(@Nullable String key) {
        return key == null ? null : sRoutes.get(key);
    }

    /**
     * Forgets every route, when MapsIndoors loads or is destroyed: a route belongs to the solution
     * that was loaded when it was requested.
     */
    static synchronized void clear() {
        sRoutes.clear();
    }
}
