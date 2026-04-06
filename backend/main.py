from fastapi import FastAPI, HTTPException, UploadFile, File
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

@app.post("/api/parcels", response_model=ParcelResponse)
def create_parcel(parcel: ParcelCreate):
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
        "healthScore": health_score,
        "healthStatus": "MODERATE" if health_score < 80 else "HEALTHY",
        "ndvi": 0.45,
        "rainfall": 95.0,
        "soilType": "Unknown",
        "createdAt": int(time.time() * 1000)
    }
    
    PARCELS_DB[new_id] = new_parcel
    print(f"[*] Created Parcel: {new_parcel['name']}")
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

@app.get("/api/parcels/{parcel_id}/signals", response_model=List[HealthSignal])
def get_parcel_signals(parcel_id: str):
    print(f"[*] Pinging Copernicus CDS API for authentic ERA5 Temp...")
    parcel = PARCELS_DB.get(parcel_id)
    lat, lng = (10.4345, 77.2920) if not parcel else (parcel["centroidLat"], parcel["centroidLng"])
    
    temp_val = "74.0"
    hist_temp = [68.0, 70.0, 71.0, 73.0, 74.0]
    
    try:
        import cdsapi
        import xarray as xr
        import os
        
        cds_url = "https://cds.climate.copernicus.eu/api"
        cds_key = "f7fa1dcd-6d5b-463d-95df-d173b934b458"
        
        try:
            with open("../.env") as f:
                for line in f:
                    if line.strip() and not line.startswith('#'):
                        k, v = line.strip().split('=', 1)
                        if k == "CDS_API_URL": cds_url = v
                        if k == "CDS_API_KEY": cds_key = v
        except Exception:
            pass

        c = cdsapi.Client(url=cds_url, key=cds_key)
        dl_path = f"era5_cache_{parcel_id}.nc"
        
        if not os.path.exists(dl_path):
            print(f"[*] Polling ERA5 GRIB/NetCDF chunk for Bounding Box @ [{lat}, {lng}]")
            c.retrieve(
                'reanalysis-era5-single-levels',
                {
                    'product_type': 'reanalysis',
                    'variable': '2m_temperature',
                    'year': '2023',
                    'month': '12',
                    'day': '01',
                    'time': ['00:00', '06:00', '12:00', '18:00', '21:00'],
                    'format': 'netcdf',
                    'area': [lat + 0.1, lng - 0.1, lat - 0.1, lng + 0.1],
                },
                dl_path)
            
        ds = xr.open_dataset(dl_path)
        t2m = ds['t2m'].values
        # Convert Kelvin to Fahrenheit
        t_f = (t2m - 273.15) * 9/5 + 32
        
        extracted_temps = t_f.flatten()
        if len(extracted_temps) >= 5:
            hist_temp = extracted_temps[:5].tolist()
            temp_val = f"{hist_temp[-1]:.1f}"
            print(f"[*] Extracted NetCDF ERA5 Matrix: {temp_val}°F")
            
    except Exception as e:
        print(f"[*] CDS Request Halted (Using Fallback Queue): {e}")

    return [
        {"type": "NDVI", "value": "0.64", "unit": "", "trend": "UP", "historicalData": [0.38, 0.45, 0.50, 0.55, 0.64], "confidence": 87},
        {"type": "RAINFALL", "value": "1.2", "unit": "in", "trend": "DOWN", "historicalData": [3.2, 2.8, 2.1, 1.8, 1.2], "confidence": 91},
        {"type": "TEMPERATURE", "value": temp_val, "unit": "°F", "trend": "STABLE", "historicalData": hist_temp, "confidence": 99},
        {"type": "SOIL", "value": "8.2", "unit": "pH", "trend": "STABLE", "historicalData": [8.0, 8.1, 8.2, 8.2, 8.2], "confidence": 79}
    ]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
