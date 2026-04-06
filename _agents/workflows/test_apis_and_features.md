---
description: Test Landroid Backend API and Features (User Guide)
---

# Landroid: High-Performance Geospatial Validation & User Guide
This workflow map outlines exactly how to validate all integrated geospatial APIs, evaluate concurrency, and operate the platform.

## 1. Validating Endpoints (Automated Checks)
The following commands hit your local Landroid FastAPI endpoints to ensure Copernicus, OSMNx, and Earth Engine layers are extracting raw data smoothly. 

// turbo-all
### View Live Parcels
```bash
curl -s http://127.0.0.1:8000/api/parcels
```

### Validate Real-time Geospatial Signals
This endpoint simultaneously routes geometry intersections via `asyncio.gather()` up to Google Earth Engine and the Copernicus CDS. 
```bash
# This will extract ERA5 Temp, OSMNx bounds, and Earth Engine layers (VIIRS, SoilGrids, CHIRPS)
curl -s http://127.0.0.1:8000/api/parcels/foo-demo/signals
```

### Validate OSMNx Structural Nodes (Roads)
Extracts geometric driveable networks inside a 1km bounding box proxying Overpass API.
```bash
curl -s http://127.0.0.1:8000/api/parcels/foo-demo/roads
```

### Validate OSMNx Proximal Bodies (Water)
```bash
curl -s http://127.0.0.1:8000/api/parcels/foo-demo/water
```

## 2. End-to-End User Guide (How to Use Landroid)

### Stage 1: Setup and Security Isolation
- Ensure your `.env` vault contains `SUPABASE_URL`, `SUPABASE_ANON_KEY`, `CDS_API_URL`, `CDS_API_KEY`, and `BACKEND_API_URL`.
- Start the engine by running `python main.py` in the `backend/` directory.

### Stage 2: Access Control
- Enter your mobile number into the Landroid App.
- An OTP request is natively mocked to `000000` via `/api/auth/verify-otp`. 
- Upon successful handshake, Hilt injects your user credentials navigating you straight into your `DashboardRepository`.

### Stage 3: The Consultant / Landowner Dispatch
- Navigating to a specific parcel extracts coordinate limits natively stored in `PARCELS_DB`. 
- The Android interface intercepts the centroid metadata (`10.4345, 77.2920`) and pushes an HTTP POST out to your local engine.
- You will see 5 metrics dynamically fill the grid UI: **NDVI**, **RAINFALL**, **TEMPERATURE**, **SOIL**, and **DEVELOPMENT (Night Lights)**.

### Stage 4: GIS Real-time Mapping 
- Tapping **"Map"** in your Navigation bar injects your coordinates into **MapLibre**. 
- It securely builds GeoJsonSource layers directly mapping over your `backend/main.py`.
- Roads will overlay the high-res Esri Satellite layers in **Light Gray**.
- Local structural water boundaries will render in **Deep Blue**.

## 3. Data Stack Reference
| Vector | Original Source | Python Pipeline | Output Result |
|--------|----------------|----------------|----------------|
| Temperature | Copernicus ECMWF | `cdsapi` -> NetCDF `.nc` | °F Historical Trend |
| Soil pH | ISRIC SoilGrids | `earthengine-api` | Mean pH Levels |
| Rainfall | CHIRPS Daily   | `earthengine-api` | Precip Inches |
| Night Lights | NOAA VIIRS | `earthengine-api` | Average Radiance (Development Index) |
| Roads | OpenStreetMap | `osmnx` bbox graph | `LineLayer` MapLibre Render |
| Water Prox | OpenStreetMap | `osmnx` | `FillLayer` MapLibre Render |
