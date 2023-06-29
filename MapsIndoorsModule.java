package com.reactlibrary.core;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapsindoors.core.MPBuilding;
import com.mapsindoors.core.MPBuildingCollection;
import com.mapsindoors.core.MPCategory;
import com.mapsindoors.core.MPCategoryCollection;
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
import com.mapsindoors.core.models.MPMapStyle;
import com.reactlibrary.core.models.Filter;
import com.reactlibrary.core.models.MPError;
import com.reactlibrary.core.models.PositionProvider;
import com.reactlibrary.core.models.PositionResult;
import com.reactlibrary.core.models.Query;

import java.util.ArrayList;
import java.util.List;

public class MapsIndoorsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final Gson gson = new Gson();

    private PositionProvider positionProvider;

    public MapsIndoorsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "MapsIndoorsModule";
    }

    private void reject(Promise promise, MIError error) {
        promise.reject("MapsIndoorsError", gson.toJson(MPError.fromMIError(error)));
    }

    @ReactMethod
    public void loadMapsIndoors(String apiKey, final Promise promise) {
        MapsIndoors.load(reactContext.getApplicationContext(), apiKey, miError -> {
            if (miError == null) {
                promise.resolve(null);
            } else {
                reject(promise, miError);
            }
        });
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
        List<MPLocation> locations = MapsIndoors.getLocations();
        if (!locations.isEmpty()) {
            String locationsString = gson.toJson(locations);
            promise.resolve(locationsString);
        } else {
            reject(promise, new MIError(MIError.UNKNOWN_ERROR, "Cannot fetch categories, try waiting until MapsIndoors is Ready"));
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
        promise.resolve(MapsIndoors.getDefaultLanguage());
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
        String solutionString = solution != null ? gson.toJson(solution): null;
        promise.resolve(solutionString);
    }

    @ReactMethod
    public void getLocationsAsync(String query, String filter, final Promise promise) {
        MPQuery mpQuery = gson.fromJson(query, Query.class).toMPQuery();
        MPFilter mpFilter = gson.fromJson(filter, Filter.class).toMPFilter();

        MapsIndoors.getLocationsAsync(mpQuery, mpFilter, (list, miError) -> {
            if (miError == null) {
                String locationsString = gson.toJson(list);
                promise.resolve(locationsString);
            } else {
                reject(promise, miError);
            }
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
        MapsIndoors.reverseGeoCode(point, mpGeoCodeResult -> promise.resolve(gson.toJson(mpGeoCodeResult)));
    }

}
