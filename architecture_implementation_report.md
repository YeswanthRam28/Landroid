# Landroid Architecture & Implemented Features Matrix

This document provides an exhaustive breakdown of the systems, API endpoints, pipelines, and frameworks successfully integrated into the Landroid ecosystem. From initial structure to final dynamic routing, the platform is entirely out of mock mode and operating completely live on physical geospatial metrics.

---

## 1. Core Architecture & Infrastructure
- **High-Performance Local Data Engine (`backend/main.py`):** Rebuilt the entire server interface on FastAPI. Operated locally (`localhost`/Network IPv4) to eliminate third-party hosting dependencies and guarantee speed.
- **`.env` Vault Isolation:** Stripped all hardcoded API tokens out of the codebase. Integrated a global `.env` file securing the `SUPABASE_URL`, `CDS_API_KEY`, and `SH_CLIENT_SECRET` out of the version control path.
- **Retrofit & Hilt DI (`com.landroid.core.network`):** Configured and stabilized Dependency Injection (`NetworkModule.kt`). Built the `RetrofitClient` async hooks to translate server-side JSON structures securely down to typed Kotlin classes.

---

## 2. Satellite Intelligence Pipelines (Earth Engine & Copernicus)
The core `/api/parcels/{id}/signals` backend execution loop simultaneously launches asynchronous Coroutine handlers hitting multi-gigabyte satellite databases in parallel.

- **Copernicus ERA5 (Climate Data Market):** Extracts the `.nc` (NetCDF) global grid and interpolates historic ground-level temperature timelines dynamically in `°F`.
- **ISRIC SoilGrids:** Hits Google Earth Engine via `projects/soilgrids-isric/phh2o_mean` natively pulling exact `pH` metrics based on localized acreage constraints. 
- **CHIRPS Daily Matrix:** Translates daily rainfall metrics natively dropping millimetric volumes dynamically into iOS/Android compatible outputs.
- **NOAA VIIRS (Night Lights):** Maps the `Development Index` extracting average surrounding radiance and lighting proxies representing structural/urban development progress near the plot.

---

## 3. High-Resolution Output (Sentinel Hub)
- **Sentinel Background Task Interceptor (`landroid_pipeline.py`):** Merged an external Python image generator directly into the FastAPI endpoint stack. 
- **Ghost Threading:** Rebuilt FastAPI to evaluate Sentinel-2 imagery perfectly asynchronously. When a parcel is minted, it secretly extracts 5 specialized android-friendly UInt8 (`0.0 - 255.0`) `.tif` array bundles:
  1. `1_TRUE_COLOR.tif`
  2. `3_NDVI.tif`
  3. `5_MOISTURE-INDEX.tif` (NDMI) 
  4. `6_SWIR.tif` (Soil/Water reflectivity) 
  5. `7_NDWI.tif` (Water Detection) 

---

## 4. Mobile Application Frontend Interfaces
Completely deleted dummy data parameters intercepting out the `generateSampleCanopies` loop. Mapped everything directly into the real Data Transfer Objects (DTOs) from the server.
- **GIS Map Rendering (`MapLibreView.kt`):** Constructed a full MapLibre layout running Esri Standard High-res base tiles globally injecting Overpass geometries mapped structurally via HTTP.
- **Landowner Dashboard Analytics (`DashboardScreen.kt`):** Constructed the structural 2-column display grid pulling dynamic colors, SVG icons (`Opacity`, `Thermostat`, `Star` via fixed Material mapping routines).
- **AI Plant Zones Segmenter (`PlantZonesScreen.kt`):** Stripped the mocked UI colors and mapped logic natively via the backend. Evaluates bare, sparse, healthy, and dense crop percentages cleanly relying purely on Sentinel-driven NDVI array extraction.
- **Canopy Counting Computer (`TreeCountScreen.kt`):** Built the density-multiplier framework linking structural `CanopyData(lat, lng, radius)` directly out from OpenCV approximations up into the client display parameters.
- **Valuation Pricing Logic (`ValuationScreen.kt`):** Evaluates exact pricing gradients dynamically. Factors `AreaAcres`, True Soil Health Multipliers, and vegetation algorithms natively into `Conservative`, `Estimated`, and `Optimistic` INR metrics natively formatted to the Rupee base!

---

## 5. Structural Geometric Endpoints (OSMNx)
- **Dynamic Driveable Routing (`/roads`):** Synthesizes local geometric Bounding Boxes hitting the open OSM APIs returning standard `GeoJson` lines representing vehicular pathways. Backwards compatible completely to `LineLayer` format.
- **Water Proximal Extractor (`/water`):** Injects Overpass queries hitting arrays mapping explicitly against `waterway` tags rendering physical boundary geometries dynamically over the physical map arrays.

---

## 6. Official Hackathon Mandatory Implementations (Phases 1–7)
Our codebase natively conforms entirely to the core constraints set forth inside the Hackathon Criteria matrix:

- **Phase 1: True OpenCV Processing:** Substituted out randomized "mock" canopies on the `backend/main.py`. Natively integrated `cv2.SimpleBlobDetector_create()` executing physical Matrix Thresholding and Circularity Detection processing over Drone Orthomosaic Raster Images.
- **Phase 1: Chronological Disparities (FR-28/31):** The AI Insights network endpoints structurally map explicitly defined `previousSurveyDate` and `previousTotalCount`. The UI `TreeCountScreen.kt` naturally executes comparison logic, printing explicitly out the Exact Trees Missing / Added since the last tracked survey operation.
- **Phase 2: Consultant Protocol UI (FR-12):** Created `ConsultantUploadScreen.kt`. It provides the Android standard `ActivityResultContracts` physical file picker interfaces designed explicitly to pull Drone Orthomosaics (.tif) and Digital Elevation Models (DEM) natively for the Land Consultant's operational flow.
- **Phase 3: Android Keystore Sanctity:** Fully implemented `EncryptedSharedPreferences` binding directly to `MasterKeys.AES256_GCM_SPEC` isolating Firebase standard `auth_token` access keys locally to OS levels securely mapping off root access.
- **Phase 3: Source Code Sanitization (M-06):** Injected absolute constraints inside `.gitignore` blocking `.env`, `google-services.json`, `keystore`, and dynamic `supabase_storage/` offline cache files guaranteeing GitHub repository compliance and avoiding M-06 penalties.
- **Phase 5: Geofencing Data Retention Limits:** Implemented precise timestamp cutoff configurations inside `AlertDao.kt`. Active query parameters completely wipe structural breaches occurring outside the mandatory `90-day` chronological threshold window dynamically tracked on initialization.
- **Phase 6: Strict 48-Hour Secure Document Vault Sharing:** Intercepted all mock responses on `/api/documents/upload`. The FastAPI system internally generates strict `PyJWT` tokens assigned explicitly with a precisely verified 48-hour signature expiration (`datetime.timedelta(hours=48)`). These tokens prevent illegal static access outside consultant domains.
- **Phase 7: Physical Localized Tamil Base Variants (D-02):** Implemented localized `res/values-ta/strings.xml` physically containing full statically built dictionaries covering every UI vector ranging from `app_tagline`, alerts matrices, onboarding constraints and valuation definitions directly supporting Tamil dialects out of the box dynamically via App Settings.
