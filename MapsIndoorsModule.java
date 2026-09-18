package com.mapsindoorsrn.core;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapsindoors.core.MPBaseMapCacheRegionListener;
import com.mapsindoors.core.MPBuilding;
import com.mapsindoors.core.MPBuildingCollection;
import com.mapsindoors.core.MPCategory;
import com.mapsindoors.core.MPCategoryCollection;
import com.mapsindoors.core.MPDataSetCache;
import com.mapsindoors.core.MPDataSetCacheManager;
import com.mapsindoors.core.MPDataSetCacheScope;
import com.mapsindoors.core.MPDataSetManagerStatus;
import com.mapsindoors.core.MPDisplayRule;
import com.mapsindoors.core.MPFilter;
import com.mapsindoors.core.MPLocation;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPQuery;
import com.mapsindoors.core.MPSolution;
import com.mapsindoors.core.MPUserRole;
import com.mapsindoors.core.MPUserRoleCollection;
import com.mapsindoors.core.MPVenue;
import com.mapsindoors.core.MPVenueCollection;
import com.mapsindoors.core.MapsIndoors;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.models.MPIMapProviderBaseMapCache;
import com.mapsindoors.core.models.MPMapStyle;
import com.mapsindoorsrn.core.models.Filter;
import com.mapsindoorsrn.core.models.MPError;
import com.mapsindoorsrn.core.models.PositionProvider;
import com.mapsindoorsrn.core.models.PositionResult;
import com.mapsindoorsrn.core.models.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ReactModule(name = MapsIndoorsModule.NAME)
public class MapsIndoorsModule extends ReactContextBaseJavaModule {
    public static final String NAME = "MapsIndoorsModule";

    private final ReactApplicationContext reactContext;
    private final Gson gson = RnGson.create();

    private PositionProvider positionProvider;

    public MapsIndoorsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        // Initialized here rather than lazily inside the caching methods, because MPTileProvider asks
        // MPDataSetCacheManager whether a solution has offline tiles when the map builds its floor-tile
        // overlay - and getInstance() throws outright when nothing has initialized it. sIsInitialized is
        // static, so it is false again on every cold start: an app that cached tiles in an earlier run
        // and then launches offline would build its tile overlay against the network URL and render no
        // MapsIndoors tiles at all. Doing it in the constructor puts it before any map can exist, and
        // costs a file-path setup - the datasets manifest is only read on first getInstance().
        if (!MPDataSetCacheManager.isInitialized()) {
            MPDataSetCacheManager.initialize(reactContext);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Rejects with the SDK's error, serialised.
     *
     * Most callers are SDK callbacks, off React Native's stack, where an unhandled throw takes the
     * host app down rather than failing the call. {@code MPError} carries {@code MIError.tag},
     * which is a bare {@code Object}, so this is the same reflection hazard the rest of the module
     * guards - and the one path where a throw would land when the call has already failed
     * (MS-3983). A failure to serialise the error still has to reach the caller, so it falls back
     * to the Gson-free payload rather than being dropped.
     */
    private void reject(Promise promise, MIError error) {
        final String errorString;
        try {
            errorString = gson.toJson(MPError.fromMIError(error));
        } catch (Exception | StackOverflowError t) {
            RnGson.rejectSerialisationFailure(promise, "MapsIndoorsError",
                    "Could not serialise error " + (error != null ? error.code : "null") + ": " + t);
            return;
        }
        promise.reject("MapsIndoorsError", errorString);
    }

    @ReactMethod
    public void loadMapsIndoors(String apiKey, ReadableArray optionalStrings, Promise promise) {
        try {
            if (optionalStrings != null) {
                List<String> venueList = new ArrayList<>();
                for (int i = 0; i < optionalStrings.size(); i++) {
                    venueList.add(optionalStrings.getString(i));
                }
                MapsIndoors.load(reactContext.getApplicationContext(), apiKey, venueList, miError -> {
                    if (miError == null) {
                        promise.resolve(null);
                    } else {
                        reject(promise, miError);
                    }
                });
            } else {
                MapsIndoors.load(reactContext.getApplicationContext(), apiKey, miError -> {
                    if (miError == null) {
                        promise.resolve(null);
                    } else {
                        reject(promise, miError);
                    }
                });
            }
        } catch (Exception e) {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, e.getMessage()));
        }
    }

    @ReactMethod
    public void getDefaultVenue(final Promise promise) {
        MPVenueCollection venues = MapsIndoors.getVenues();
        if (venues != null) {
            MPVenue venue = venues.getDefaultVenue();
            if (venue != null) {
                String venueString = gson.toJson(venue);
                promise.resolve(venueString);
            } else {
                promise.resolve(null);
            }
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch venues, try waiting until MapsIndoors is Ready"));
        }

    }

    @ReactMethod
    public void getVenues(final Promise promise) {
        MPVenueCollection venueCollection = MapsIndoors.getVenues();
        if (venueCollection != null) {
            List<MPVenue> venues = venueCollection.getVenues();
            String venuesString = gson.toJson(venues);
            promise.resolve(venuesString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch venues, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getBuildings(final Promise promise) {
        MPBuildingCollection buildingCollection = MapsIndoors.getBuildings();
        if (buildingCollection != null) {
            List<MPBuilding> buildings = buildingCollection.getBuildings();
            String buildingString = gson.toJson(buildings);
            promise.resolve(buildingString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch buildings, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getCategories(final Promise promise) {
        MPCategoryCollection categoryCollection = MapsIndoors.getCategories();
        if (categoryCollection != null) {
            List<MPCategory> categories = categoryCollection.getCategories();
            String categoriesString = gson.toJson(categories);
            promise.resolve(categoriesString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch categories, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getLocations(final Promise promise) {
        ArrayList<MPLocation> locations = new ArrayList<>(MapsIndoors.getLocations());
        if (!locations.isEmpty()) {
            String locationsString = gson.toJson(locations);
            promise.resolve(locationsString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch locations, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void disableEventLogging(boolean disable, final Promise promise) {
        MapsIndoors.disableEventLogging(disable);
        promise.resolve(null);
    }

    @ReactMethod
    public void getApiKey(final Promise promise) {
        promise.resolve(MapsIndoors.getAPIKey());
    }

    @ReactMethod
    public void getAvailableLanguages(final Promise promise) {
        List<String> languages = MapsIndoors.getAvailableLanguages();
        if (languages != null) {
            ReadableArray array = Arguments.fromList(languages);
            promise.resolve(array);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "No languages are available, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getDefaultLanguage(final Promise promise) {
        String defaultLanguage = MapsIndoors.getDefaultLanguage();
        if (defaultLanguage != null) {
            promise.resolve(defaultLanguage);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "No default language is available, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getLanguage(final Promise promise) {
        promise.resolve(MapsIndoors.getLanguage());
    }

    @ReactMethod
    public void getLocationById(String id, final Promise promise) {
        MPLocation location = MapsIndoors.getLocationById(id);
        if (location != null) {
            String locationString = gson.toJson(location);
            promise.resolve(locationString);
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void getLocationsByExternalIds(ReadableArray externalIdArray, final Promise promise) {
        List<String> externalIds = new ArrayList<>();
        for (int i = 0; i < externalIdArray.size(); i++) {
            externalIds.add(externalIdArray.getString(i));
        }
        List<MPLocation> locations = MapsIndoors.getLocationsByExternalIds(externalIds);
        String locationsString = gson.toJson(locations);
        promise.resolve(locationsString);
    }

    @ReactMethod
    public void getMapStyles(final Promise promise) {
        List<MPMapStyle> mapStyles = MapsIndoors.getMapStyles();
        String mapStylesString = gson.toJson(mapStyles);
        promise.resolve(mapStylesString);
    }

    @ReactMethod
    public void getSolution(final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            promise.resolve(gson.toJson(solution));
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "No solution is available, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void getLocationsAsync(String query, String filter, final Promise promise) {
        MPQuery mpQuery = gson.fromJson(query, Query.class).toMPQuery();
        MPFilter mpFilter = gson.fromJson(filter, Filter.class).toMPFilter();

        MapsIndoors.getLocationsAsync(mpQuery, mpFilter, (list, miError) -> {
            if (miError != null) {
                reject(promise, miError);
                return;
            }

            // Serialising here runs on the SDK's thread, outside React Native's try/catch, so an
            // unhandled throw takes the host app down instead of failing this call (MS-3983).
            // StackOverflowError is named because it is the failure being guarded and it is an
            // Error, not an Exception; OutOfMemoryError and the linkage Errors are left to
            // propagate, since nothing here can recover from them.
            //
            // Only the serialisation goes inside the guard. A throw from resolve - an already
            // settled promise, a torn-down bridge - would otherwise be answered by rejecting the
            // same promise from the catch, which throws again on this thread, uncaught.
            final String locationsString;
            try {
                locationsString = gson.toJson(list);
            } catch (Exception | StackOverflowError t) {
                RnGson.rejectSerialisationFailure(promise, "MapsIndoorsError",
                        "Could not serialise the locations result: " + t);
                return;
            }
            promise.resolve(locationsString);
        });
    }

    @ReactMethod
    public void locationDisplayRuleExists(String locationId, final Promise promise) {
        MPDisplayRule rule = MapsIndoors.getDisplayRule(MapsIndoors.getLocationById(locationId));
        promise.resolve(rule != null);
    }

    @ReactMethod
    public void displayRuleNameExists(String name, final Promise promise) {
        MPDisplayRule displayRule = MapsIndoors.getDisplayRule(name);
        promise.resolve(displayRule != null);

    }

    @ReactMethod
    public void setPositionProvider(String positionProviderName, final Promise promise) {
        positionProvider = new PositionProvider(positionProviderName);
        MapsIndoors.setPositionProvider(positionProvider);
        promise.resolve(null);
    }

    @ReactMethod
    public void removePositionProvider(final Promise promise) {
        positionProvider = null;
        MapsIndoors.setPositionProvider(null);
        promise.resolve(null);
    }

    @ReactMethod
    public void onPositionUpdate(String positionString) {
        PositionResult positionResult = gson.fromJson(positionString, PositionResult.class);
        if (positionProvider != null) {
            positionProvider.updatePosition(positionResult);
        }
    }

    @ReactMethod
    public void getUserRoles(final Promise promise) {
        MPUserRoleCollection userRoles = MapsIndoors.getUserRoles();
        if (userRoles != null) {
            String userRolesString = gson.toJson(userRoles.getUserRoles());
            promise.resolve(userRolesString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch userroles, try waiting until MapsIndoors is Ready"));
        }
    }

    @ReactMethod
    public void checkOfflineDataAvailability(final Promise promise) {
        promise.resolve(MapsIndoors.checkOfflineDataAvailability());
    }

    @ReactMethod
    public void destroy(final Promise promise) {
        MapsIndoors.destroy();
        promise.resolve(null);
    }

    @ReactMethod
    public void isApiKeyValid(final Promise promise) {
        promise.resolve(MapsIndoors.isAPIKeyValid());
    }

    @ReactMethod
    public void isInitialized(final Promise promise) {
        promise.resolve(MapsIndoors.isInitialized());
    }

    @ReactMethod
    public void isReady(final Promise promise) {
        promise.resolve(MapsIndoors.isReady());
    }

    @ReactMethod
    public void setLanguage(String lang, final Promise promise) {
        promise.resolve(MapsIndoors.setLanguage(lang));
    }

    @ReactMethod
    public void synchronizeContent(final Promise promise) {
        MapsIndoors.synchronizeContent(miError -> {
            if (miError != null) {
                reject(promise, miError);
            } else {
                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void applyUserRoles(String userRolesString, final Promise promise) {
        List<MPUserRole> userRoles = gson.fromJson(userRolesString, new TypeToken<List<MPUserRole>>() {}.getType());
        MapsIndoors.applyUserRoles(userRoles);
        promise.resolve(null);
    }

    @ReactMethod
    public void getAppliedUserRoles(final Promise promise) {
        List<MPUserRole> userRoles = MapsIndoors.getAppliedUserRoles();
        if (userRoles != null) {
            promise.resolve(gson.toJson(userRoles));
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void reverseGeoCode(String pointString, final Promise promise) {
        MPPoint point = gson.fromJson(pointString, MPPoint.class);
        MapsIndoors.reverseGeoCode(point, mpGeoCodeResult -> {
            // Not on React Native's stack either, and the same shape and Error handling - see
            // getLocationsAsync.
            final String geoCodeString;
            try {
                geoCodeString = gson.toJson(mpGeoCodeResult);
            } catch (Exception | StackOverflowError t) {
                RnGson.rejectSerialisationFailure(promise, "MapsIndoorsError",
                        "Could not serialise the reverse-geocode result: " + t);
                return;
            }
            promise.resolve(geoCodeString);
        });
    }

    @ReactMethod
    public void addVenuesToSync(ReadableArray venues, Promise promise) {
        try {
            List<String> venueList = new ArrayList<>();
            for (int i = 0; i < venues.size(); i++) {
                venueList.add(venues.getString(i));
            }
            MapsIndoors.addVenuesToSync(venueList);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }
    
    @ReactMethod
    public void removeVenuesToSync(ReadableArray venues, Promise promise) {
        try {
            List<String> venueList = new ArrayList<>();
            for (int i = 0; i < venues.size(); i++) {
                venueList.add(venues.getString(i));
            }
            MapsIndoors.removeVenuesToSync(venueList);
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getSyncedVenues(final Promise promise) {
        List<String> venues = MapsIndoors.getSyncedVenues();
        if (venues != null) {
            ReadableArray array = Arguments.fromList(venues);
            promise.resolve(array);
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void cacheData(String apiKey, Promise promise) {
        if (!MPDataSetCacheManager.isInitialized()) {
            MPDataSetCacheManager.initialize(reactContext);
        }
        MPDataSetCache cache = MPDataSetCacheManager.getInstance().getDataSetByID(apiKey);
        if (cache == null) {
            cache = MPDataSetCacheManager.getInstance().addDataSetWithCachingScope(apiKey, MPDataSetCacheScope.FULL);
        }else {
            cache.setScope(MPDataSetCacheScope.FULL);
        }
        MPDataSetCache finalCache = cache;
        MPDataSetCacheManager.getInstance().addMPDataSetCacheSyncListener(((mpDataSetCache, i) -> {
            if (i == MPDataSetManagerStatus.SYNC_FINISHED && finalCache.getSolutionId().equals(mpDataSetCache.getSolutionId())) {
                promise.resolve(true);
            } else if (i == MPDataSetManagerStatus.SYNC_FAILED && finalCache.getSolutionId().equals(mpDataSetCache.getSolutionId())) {
                promise.resolve(false);
            }
        }));
        MPDataSetCacheManager.getInstance().synchronizeDataSets(Collections.singletonList(cache));
    }

    @ReactMethod
    public void isBaseMapCachingSupported(Promise promise) {
        final MPIMapProviderBaseMapCache provider = BaseMapCacheProviders.ensureRegistered(reactContext);

        // Resolved rather than rejected when nothing is registered: "can this app cache base-map tiles"
        // has a true answer either way, and false is the useful one for a caller deciding whether to
        // offer offline base maps at all.
        promise.resolve(provider != null && provider.isBaseMapCachingSupported());
    }

    @ReactMethod
    public void setBaseMapTilesEnabled(boolean enabled, String apiKey, Promise promise) {
        // No ensureRegistered() here, unlike the two methods either side of it: flagging a dataset needs
        // no provider, and synchronizeBaseMapTiles registers one before it needs it. The manager itself
        // is already initialized by this module's constructor.
        final MPDataSetCacheManager manager = MPDataSetCacheManager.getInstance();
        final MPDataSetCache cache = manager.getDataSetByID(apiKey);

        if (cache == null) {
            // Managed from here on, with the same FULL scope cacheData() uses: there is nothing to flag
            // otherwise, and base-map caching needs the dataset's venues on disk to know what to cache
            // around.
            if (manager.addDataSetWithCachingScope(apiKey, MPDataSetCacheScope.FULL, enabled) == null) {
                // Reported rather than resolved: the flag is not set, so a later synchronizeBaseMapTiles
                // would fail as BASEMAP_CACHE_NOT_REGISTERED or simply cache nothing, displaced from the
                // cause. iOS rejects on the same call.
                reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Unable to manage dataset '" + apiKey + "'"));
                return;
            }
        } else {
            cache.setBaseMapTilesEnabled(enabled);
        }

        promise.resolve(null);
    }

    @ReactMethod
    public void synchronizeBaseMapTiles(@Nullable ReadableArray apiKeys, Promise promise) {
        BaseMapCacheProviders.ensureRegistered(reactContext);

        final MPDataSetCacheManager manager = MPDataSetCacheManager.getInstance();
        final List<MPDataSetCache> caches = new ArrayList<>();

        if (apiKeys != null) {
            for (int i = 0; i < apiKeys.size(); i++) {
                final String apiKey = apiKeys.getString(i);
                final MPDataSetCache cache = manager.getDataSetByID(apiKey);

                if (cache == null) {
                    // Rejected rather than skipped: an explicit list is an explicit instruction, and
                    // silently caching nothing for a key the caller named is the harder failure to spot.
                    reject(promise, new MIError(MIError.BASEMAP_CACHE_NOT_REGISTERED,
                            "No dataset is managed for '" + apiKey + "', so base-map tiles cannot be cached for it"));
                    return;
                }

                caches.add(cache);
            }
        }

        final MPBaseMapCacheRegionListener listener = new MPBaseMapCacheRegionListener() {
            @Override
            public void onProgress(double fraction) {
                // Guarded, unlike every other emitter in this module: those fire from map or UI
                // callbacks, so a live React context is a given. This one fires from a background
                // download owned by MPDataSetCacheManager - a static singleton - so it outlives a
                // context teardown, and a Metro reload part-way through a multi-minute sync would
                // otherwise call getJSModule against a bridge that is gone, once per tick.
                if (!reactContext.hasActiveReactInstance()) {
                    return;
                }

                final WritableMap params = Arguments.createMap();
                params.putDouble("progress", fraction);
                // Emitted unconditionally, where iOS gates on whether JavaScript is listening. Both are
                // right for their platform: RCTDeviceEventEmitter with nothing listening is harmless,
                // whereas iOS's RCTEventEmitter logs a warning per event.
                //
                // "onBaseMapCacheProgress" is core's EventNames.onBaseMapCacheProgress, also spelled out
                // in iOS's MapsIndoorsData.Event. A typo here delivers no progress rather than failing.
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("onBaseMapCacheProgress", params);
            }

            @Override
            public void onComplete(@Nullable MIError error) {
                if (error != null) {
                    reject(promise, error);
                } else {
                    promise.resolve(null);
                }
            }
        };

        if (apiKeys == null) {
            manager.synchronizeBaseMapTiles(listener);
        } else {
            manager.synchronizeBaseMapTiles(caches, listener);
        }
    }

    /**
     * Required by React Native's NativeEventEmitter, which MapsIndoors.synchronizeBaseMapTiles()
     * subscribes to for progress. The events themselves are emitted through RCTDeviceEventEmitter, so
     * there is nothing to do here.
     */
    @ReactMethod
    public void addListener(String eventName) {
    }

    /**
     * Counterpart to {@link #addListener(String)}. See its note.
     */
    @ReactMethod
    public void removeListeners(Integer count) {
    }
}
