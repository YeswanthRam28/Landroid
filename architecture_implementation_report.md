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
