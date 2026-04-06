import os
import requests
import json
import asyncio
from dotenv import load_dotenv
from pyproj import Proj, transform

# Load Environmental Tokens
load_dotenv(".env")
CDS_API_URL = os.getenv("CDS_API_URL")
CDS_API_KEY = os.getenv("CDS_API_KEY")
SH_CLIENT_ID = os.getenv("SH_CLIENT_ID")
SH_CLIENT_SECRET = os.getenv("SH_CLIENT_SECRET")

# Target Coordinates provided by User
utm_x = 752291.588786328095011
utm_y = 1153748.123352754861116

print("==========================================")
print("[*] 1. UTM -> LAT/LON COORDINATE TRANSLATION")
print("==========================================")
p_utm = Proj(proj='utm', zone=43, ellps='WGS84')
lon, lat = p_utm(utm_x, utm_y, inverse=True)
print(f"-> UTM: X: {utm_x}, Y: {utm_y}")
print(f"-> WGS84: Lat: {lat:.6f}, Lon: {lon:.6f}\n")

# Bounding box roughly 1x1km
bbox = (lon - 0.005, lat - 0.005, lon + 0.005, lat + 0.005)


print("==========================================")
print("[*] 2. COPERNICUS ERA5 (TEMPERATURE)")
print("==========================================")
def test_era5():
    try:
        import cdsapi
        print(f"Connecting to CDS: {CDS_API_URL}")
        c = cdsapi.Client(url=CDS_API_URL, key=CDS_API_KEY, quiet=True)
        # Using a tiny test payload to verify API Handshake
        res = c.retrieve('reanalysis-era5-single-levels', {
            'product_type': 'reanalysis', 'variable': '2m_temperature',
            'year': '2023', 'month': '01', 'day': '01', 'time': '12:00',
            'format': 'netcdf',
            'area': [lat + 0.1, lon - 0.1, lat - 0.1, lon + 0.1],
        }, 'test_era5_payload.nc')
        print(f"[SUCCESS] ERA5 Payload Downloaded! Target File: test_era5_payload.nc\n")
    except Exception as e:
        print(f"[FAILED] ERA5 Target Failed / Missing Data: {e}\n")

test_era5()


print("==========================================")
print("[*] 3. SATELLITE IMAGERY (SENTINEL HUB)")
print("==========================================")
def test_sentinel():
    try:
        # OAuth Handshake
        token_url = "https://services.sentinel-hub.com/oauth/token"
        resp = requests.post(token_url, data={"grant_type": "client_credentials"},
                             auth=(SH_CLIENT_ID, SH_CLIENT_SECRET))
        resp.raise_for_status()
        token = resp.json()["access_token"]
        print("[SUCCESS] Sentinel Hub Authenticated! Secured Access Token.")
        
        # Test Process API 
        process_url = "https://services.sentinel-hub.com/api/v1/process"
        headers = {"Authorization": f"Bearer {token}", "Accept": "image/jpeg"}
        
        json_payload = {
            "input": {
                "bounds": {"bbox": list(bbox), "properties": {"crs": "http://www.opengis.net/def/crs/EPSG/0/4326"}},
                "data": [{"type": "sentinel-2-l2a", "dataFilter": {"timeRange": {"from": "2023-10-01T00:00:00Z", "to": "2023-11-01T00:00:00Z"}}}]
            },
            "output": {"width": 512, "height": 512, "responses": [{"identifier": "default", "format": {"type": "image/jpeg"}}]},
            "evalscript": """
            //VERSION=3
            function setup() { return { inputs: ["B04", "B03", "B02"], outputs: { id: "default", bands: 3 } }; }
            function evaluatePixel(sample) { return [2.5 * sample.B04, 2.5 * sample.B03, 2.5 * sample.B02]; }
            """
        }
        res = requests.post(process_url, headers=headers, json=json_payload)
        res.raise_for_status()
        
        with open("test_sentinel_image.jpg", "wb") as f:
            f.write(res.content)
        print("[SUCCESS] Sentinel RGB Raster Extracted! Written to test_sentinel_image.jpg\n")
        
    except Exception as e:
        print(f"[FAILED] Sentinel Handshake Failed: {e}\n")

test_sentinel()


print("==========================================")
print("[*] 4. OPENSTREETMAP (OVERPASS API)")
print("==========================================")
def test_overpass():
    try:
        # Bbox order for overpass is South, West, North, East
        overpass_url = "https://overpass-api.de/api/interpreter"
        query = f"""
        [out:json][timeout:25];
        (
          way["highway"]({lat - 0.01}, {lon - 0.01}, {lat + 0.01}, {lon + 0.01});
          way["waterway"]({lat - 0.01}, {lon - 0.01}, {lat + 0.01}, {lon + 0.01});
        );
        out count;
        """
        response = requests.post(overpass_url, data={'data': query})
        data = response.json()
        print(f"[SUCCESS] OSM Overpass Query Hits: {json.dumps(data.get('elements', []), indent=2)}\n")
    except Exception as e:
        print(f"[FAILED] Overpass Connection Fault: {e}\n")

test_overpass()


print("==========================================")
print("[*] 5. GOOGLE EARTH ENGINE (SOIL, RAIN, LIGHTS)")
print("==========================================")
def test_gee():
    print("[WAIT] Initializing Google Earth Engine Vectors...")
    print("       Note: Make sure `earthengine authenticate` is mapped to `landroid-708c7` locally.")
    try:
        import ee
        ee.Initialize(project='landroid-708c7')
        print("[SUCCESS] GEE Handshake verified via python runtime.")
        
        point = ee.Geometry.Point([lon, lat])
        
        # 5.1 ISRIC SoilGrids
        soil_ph = ee.Image("projects/soilgrids-isric/phh2o_mean").reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=250
        ).getInfo()
        print(f"  -> ISRIC SoilGrids Extraction (pH): {soil_ph}")
        
        # 5.2 CHIRPS Rainfall
        rain = ee.ImageCollection("UCSB-CHG/CHIRPS/DAILY").filterDate('2023-01-01', '2023-12-31').sum().reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=5000
        ).getInfo()
        print(f"  -> CHIRPS Precipitation Sum (mm): {rain}")
        
        # 5.3 VIIRS Night Lights
        viirs = ee.ImageCollection("NOAA/VIIRS/DNB/MONTHLY_V1/VCMSLCFG").filterDate('2023-01-01', '2023-12-31').mean().reduceRegion(
            reducer=ee.Reducer.mean(), geometry=point, scale=500
        ).getInfo()
        print(f"  -> NOAA VIIRS Radiance Metrics: {viirs}")
        
    except Exception as e:
        print(f"[FAILED] GEE API Stack Fault: {e}")
        print("         Ensure `earthengine authenticate` has been run on this terminal and attached to proper IAM Roles.")

test_gee()
