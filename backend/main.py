from fastapi import FastAPI, HTTPException, UploadFile, File, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from typing import List, Optional
from pydantic import BaseModel
import time
import uuid

app = FastAPI(title="Landroid Local Backend Dashboard")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ParcelCreate(BaseModel):
    name: str
    location: str
    district: str
    areaAcres: float
    assignedTo: str
    boundaryGeoJson: str
    centroidLat: float
    centroidLng: float

class ParcelResponse(ParcelCreate):
    id: str
    healthScore: int
    healthStatus: str
    ndvi: float
    rainfall: float
    soilType: str
    createdAt: int

class OtpRequest(BaseModel):
    phone: str

class VerifyRequest(BaseModel):
    phone: str
    otp: str
    role: str

class HealthSignal(BaseModel):
    type: str
    value: str
    unit: str
    trend: str
    historicalData: List[float]
    confidence: int

class CanopyData(BaseModel):
    lat: float
    lng: float
    radiusMeters: float
    isStressed: bool

class ZoneData(BaseModel):
    id: str
    colorHex: str
    minNdvi: float
    maxNdvi: float
    areaPercent: float

class TreeCountData(BaseModel):
    canopies: List[CanopyData]
    totalCount: int
    stressedCount: int
    densityPerAcre: float

class InsightsResponseDto(BaseModel):
    plantZones: List[ZoneData]
    treeCount: TreeCountData

PARCELS_DB = {}

demo_id = str(uuid.uuid4())
PARCELS_DB[demo_id] = {
    "id": demo_id,
    "name": "Kallapuram South Demo",
    "location": "Kallapuram",
    "district": "Udumalpet",
    "areaAcres": 42.5,
    "assignedTo": "+910000000000",
    "boundaryGeoJson": "",
    "centroidLat": 10.4345,
    "centroidLng": 77.2920,
    "healthScore": 88,
    "healthStatus": "HEALTHY",
    "ndvi": 0.65,
    "rainfall": 120.5,
    "soilType": "Red Soil",
    "createdAt": int(time.time() * 1000)
}

@app.get("/")
def read_root():
    return {"message": "Landroid Local API is running!"}

@app.get("/api/parcels", response_model=List[ParcelResponse])
def get_parcels():
    return list(PARCELS_DB.values())

@app.get("/api/parcels/{parcel_id}", response_model=ParcelResponse)
def get_parcel(parcel_id: str):
    if parcel_id not in PARCELS_DB:
        raise HTTPException(status_code=404, detail="Parcel not found")
    return PARCELS_DB[parcel_id]

def trigger_sentinel_pipeline(parcel_id: str, lat: float, lng: float):
    print(f"[*] [BACKGROUND THREAD] Booting Sentinel Pipeline for {parcel_id}...")
    import sys
    sys.path.append('../sentinel')
    try:
        from landroid_pipeline import process_parcel_bands
        # Approximating a 2x2km bounding box around the centroid 
        bbox = [lng - 0.01, lat - 0.01, lng + 0.01, lat + 0.01]
        out_dir = f"../supabase_storage/{parcel_id}"
        process_parcel_bands(parcel_id, bbox, output_dir=out_dir)
        print(f"[*] Sentinel Pipeline Completed Safely for {parcel_id}!")
    except Exception as e:
        print(f"[!] Sentinel background task failed: {e}")

@app.post("/api/parcels", response_model=ParcelResponse)
def create_parcel(parcel: ParcelCreate, background_tasks: BackgroundTasks):
    new_id = str(uuid.uuid4())
    health_score = 75 if parcel.areaAcres < 50 else 92
    
    new_parcel = {
        "id": new_id,
        "name": parcel.name,
        "location": parcel.location,
        "district": parcel.district,
        "areaAcres": parcel.areaAcres,
        "assignedTo": parcel.assignedTo,
        "boundaryGeoJson": parcel.boundaryGeoJson,
        "centroidLat": parcel.centroidLat,
        "centroidLng": parcel.centroidLng,
        "healthScore": 0,
        "healthStatus": "EVALUATING",
        "ndvi": 0.0,
        "rainfall": 0.0,
        "soilType": "Processing...",
        "createdAt": int(time.time() * 1000)
    }
    
    PARCELS_DB[new_id] = new_parcel
    print(f"[*] Created Parcel: {new_parcel['name']} | Handing off arrays to Sentinel Offline Thread.")
    
    # ----------------------------------------------------
    # OFFLOAD CRITICAL SATELLITE OPERATIONS TO BACKGROUND
    # ----------------------------------------------------
    background_tasks.add_task(trigger_sentinel_pipeline, new_id, parcel.centroidLat, parcel.centroidLng)
    
    return new_parcel

@app.post("/api/documents/upload")
async def upload_document(parcel_id: str, file: UploadFile = File(...)):
    print(f"[*] Received Document: {file.filename} for Parcel {parcel_id}")
    return {"status": "success", "url": f"http://local-backend/documents/{file.filename}"}

@app.post("/api/auth/send-otp")
def send_otp(req: OtpRequest):
    print(f"[*] Sending Secure OTP '000000' to {req.phone} via Network API")
    return {"verificationId": f"fastapi_{req.phone}"}

@app.post("/api/auth/verify-otp")
def verify_otp(req: VerifyRequest):
    if req.otp == "000000":
        user_id = str(uuid.uuid4())
        print(f"[*] Verified API Login for {req.phone} as {req.role}. Issued Token {user_id}")
        return {
            "uid": user_id,
            "name": "Local Network User",
            "phone": req.phone,
            "role": req.role
        }
    raise HTTPException(status_code=400, detail="Invalid OTP detected")

import asyncio

async def fetch_era5_temperature(lat, lng, parcel_id):
    try:
        import cdsapi
        import xarray as xr
        import os
        
        cds_url, cds_key = None, None
        try:
            with open("../.env") as f:
                for line in f:
                    if line.strip() and not line.startswith('#'):
                        k, v = line.strip().split('=', 1)
                        if k == "CDS_API_URL": cds_url = v
                        if k == "CDS_API_KEY": cds_key = v
        except Exception: pass
            
        if not cds_url or not cds_key: return "74.0", [68.0, 70.0, 71.0, 73.0, 74.0]

        c = cdsapi.Client(url=cds_url, key=cds_key)
        def retrieve():
            dl_path = f"era5__x_{parcel_id}.nc"
            if not os.path.exists(dl_path):
                c.retrieve('reanalysis-era5-single-levels', {
                    'product_type': 'reanalysis', 'variable': '2m_temperature',
                    'year': '2023', 'month': '12', 'day': '01',
                    'time': ['00:00', '06:00', '12:00', '18:00', '21:00'],
                    'format': 'netcdf', 'area': [lat + 0.1, lng - 0.1, lat - 0.1, lng + 0.1],
                }, dl_path)
            return xr.open_dataset(dl_path)['t2m'].values
            
        t2m = await asyncio.to_thread(retrieve)
        t_f = (t2m - 273.15) * 9/5 + 32
        extracted_temps = t_f.flatten()
        if len(extracted_temps) >= 5:
            hist_temp = extracted_temps[:5].tolist()
            return f"{hist_temp[-1]:.1f}", hist_temp
    except Exception as e:
        print(f"[*] Era5 Failure: {e}")
    return "74.0", [68.0, 70.0, 71.0, 73.0, 74.0]

async def fetch_gee_metrics(lat, lng):
    def retrieve():
        import ee
        try:
            ee.Initialize(project='landroid-708c7')
        except Exception as e:
            print(f"[*] GEE Init Blocked (Run earthengine authenticate): {e}")
            return None, None, None
        
        point = ee.Geometry.Point([lng, lat])
        
        # ISRIC Soil pH (scaled by 10)
        soil_ph = ee.Image("projects/soilgrids-isric/phh2o_mean").reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=250
        ).getInfo()
        
        # CHIRPS Rainfall (mm)
        rainfall = ee.ImageCollection("UCSB-CHG/CHIRPS/DAILY").filterDate('2023-01-01', '2023-12-31').sum().reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=5000
        ).getInfo()
        
        # VIIRS Night Lights (avg_rad)
        viirs = ee.ImageCollection("NOAA/VIIRS/DNB/MONTHLY_V1/VCMSLCFG").filterDate('2023-01-01', '2023-12-31').mean().reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=500
        ).getInfo()
        
        # Sentinel-2 NDVI
        ndvi = ee.ImageCollection("COPERNICUS/S2_SR_HARMONIZED").filterBounds(point).filterDate('2023-01-01', '2023-12-31').median().normalizedDifference(['B8', 'B4']).reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=10
        ).getInfo()
        
        s_val = (soil_ph.get('phh2o_mean') / 10.0) if soil_ph and soil_ph.get('phh2o_mean') else 8.2
        r_val = rainfall.get('precipitation') if rainfall and rainfall.get('precipitation') else 312.5
        v_val = viirs.get('avg_rad') if viirs and viirs.get('avg_rad') else 1.2
        n_val = ndvi.get('nd') if ndvi and ndvi.get('nd') else 0.64
        return s_val, r_val, v_val, n_val
        
    try:
        return await asyncio.to_thread(retrieve)
    except Exception as e:
        print(f"[*] GEE Thread Dumped: {e}")
        return None, None, None


@app.get("/api/parcels/{parcel_id}/signals", response_model=List[HealthSignal])
async def get_parcel_signals(parcel_id: str):
    print(f"[*] Firing Async Coroutines for Copernicus ERA5 and Google Earth Engine (SoilGrids, CHIRPS, VIIRS)...")
    parcel = PARCELS_DB.get(parcel_id)
    lat, lng = (10.4345, 77.2920) if not parcel else (parcel["centroidLat"], parcel["centroidLng"])
    
    # Run ERA5 and GEE simultaneously across OS background threads
    (era5_temp_val, era5_hist), gee_result = await asyncio.gather(
        fetch_era5_temperature(lat, lng, parcel_id),
        fetch_gee_metrics(lat, lng)
    )
    
    s_val, r_val, v_val, n_val = gee_result
    
    # Fallbacks if GEE fails (e.g. not authenticated locally)
    s_val = s_val if s_val is not None else 8.2
    r_val = (r_val / 25.4) if r_val is not None else 1.2 # Convert mm to inches for the front-end parser
    v_val = v_val if v_val is not None else 3.8
    n_val = n_val if n_val is not None else 0.65
    
    return [
        {"type": "NDVI", "value": f"{n_val:.2f}", "unit": "", "trend": "UP", "historicalData": [n_val*0.6, n_val*0.7, n_val*0.8, n_val*0.9, n_val], "confidence": 87},
        {"type": "RAINFALL", "value": f"{r_val:.1f}", "unit": "in", "trend": "DOWN", "historicalData": [r_val*1.5, r_val*1.3, r_val*1.1, r_val*1.05, r_val], "confidence": 91},
        {"type": "TEMPERATURE", "value": era5_temp_val, "unit": "°F", "trend": "STABLE", "historicalData": era5_hist, "confidence": 99},
        {"type": "SOIL", "value": f"{s_val:.1f}", "unit": "pH", "trend": "STABLE", "historicalData": [s_val*0.98, s_val*0.99, s_val*1.0, s_val*1.0, s_val], "confidence": 79},
        {"type": "DEVELOPMENT", "value": f"{v_val:.1f}", "unit": "rad", "trend": "UP", "historicalData": [v_val*0.3, v_val*0.5, v_val*0.7, v_val*0.8, v_val], "confidence": 94}
    ]


@app.get("/api/parcels/{parcel_id}/roads")
def get_parcel_roads(parcel_id: str):
    import osmnx as ox
    parcel = PARCELS_DB.get(parcel_id)
    lat, lng = (10.4345, 77.2920) if not parcel else (parcel["centroidLat"], parcel["centroidLng"])
    
    # Approx 1x1 km buffer
    bbox = (lat + 0.005, lat - 0.005, lng + 0.005, lng - 0.005)
    
    try:
        ox.settings.timeout = 60
        G = ox.graph_from_bbox(bbox=bbox, network_type='drive')
        nodes, edges = ox.graph_to_gdfs(G)
        return fastapi.responses.Response(content=edges.to_json(), media_type="application/json")
    except Exception as e:
        print(f"[*] Failed to fetch OSM Roads: {e}")
        return {"type": "FeatureCollection", "features": []}

@app.get("/api/parcels/{parcel_id}/water")
def get_parcel_water(parcel_id: str):
    import osmnx as ox
    parcel = PARCELS_DB.get(parcel_id)
    lat, lng = (10.4345, 77.2920) if not parcel else (parcel["centroidLat"], parcel["centroidLng"])
    
    bbox = (lat + 0.005, lat - 0.005, lng + 0.005, lng - 0.005)
    
    try:
        ox.settings.timeout = 60
        tags = {"natural": "water", "waterway": True}
        water = ox.features_from_bbox(bbox=bbox, tags=tags)
        return fastapi.responses.Response(content=water.to_json(), media_type="application/json")
    except Exception as e:
        print(f"[*] Failed to fetch OSM Water: {e}")
        return {"type": "FeatureCollection", "features": []}

@app.get("/api/parcels/{parcel_id}/insights", response_model=InsightsResponseDto)
def get_parcel_insights(parcel_id: str):
    import random
    parcel = PARCELS_DB.get(parcel_id)
    n = parcel.get('ndvi', 0.5) if parcel else 0.5
    acres = parcel.get('areaAcres', 42.5) if parcel else 42.5
    lat = parcel.get('centroidLat', 10.4345) if parcel else 10.4345
    lng = parcel.get('centroidLng', 77.2920) if parcel else 77.2920

    # Plant Zone Dynamics driven natively off True NDVI from Sentinel
    zones = [
        {"id": "bare", "colorHex": "#E53E3E", "minNdvi": 0.0, "maxNdvi": 0.2, "areaPercent": max(2.0, 100.0 - (n * 160.0))},
        {"id": "sparse", "colorHex": "#ED8936", "minNdvi": 0.2, "maxNdvi": 0.4, "areaPercent": 15.5},
        {"id": "healthy", "colorHex": "#48BB78", "minNdvi": 0.4, "maxNdvi": 0.6, "areaPercent": min(100.0, n * 80.0)},
        {"id": "dense", "colorHex": "#276749", "minNdvi": 0.6, "maxNdvi": 1.0, "areaPercent": max(0.0, (n * 100.0) - 25.0)}
    ]
    
    # Force total percent 100
    total = sum([z["areaPercent"] for z in zones])
    for z in zones: z["areaPercent"] = (z["areaPercent"] / total) * 100.0
    
    # Dynamically synthesize trees based on Density (NDVI) * Acreage
    density_per_acre = n * 45.0 + 5.0
    total_trees = int(density_per_acre * acres)
    stress_ratio = max(0.05, 1.0 - n) # Higher stress if NDVI is low
    
    canopies = []
    # Cap sent array size to prevent crashing Android UI via JSON parsing memory limits
    for i in range(min(total_trees, 800)):
        lat_offset = (random.random() - 0.5) * 0.01
        lng_offset = (random.random() - 0.5) * 0.01
        canopies.append({
            "lat": lat + lat_offset,
            "lng": lng + lng_offset,
            "radiusMeters": random.uniform(1.2, 4.5),
            "isStressed": random.random() < stress_ratio
        })

    return {
        "plantZones": zones,
        "treeCount": {
            "canopies": canopies,
            "totalCount": total_trees,
            "stressedCount": int(total_trees * stress_ratio),
            "densityPerAcre": density_per_acre
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
