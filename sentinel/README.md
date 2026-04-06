# Landroid Sentinel API Data Fetcher

This folder contains the **Plug-and-Play Sentinel Data Pipeline** for generating the 5 specific GeoTIFF bands required by the Landroid Android app (`1_TRUE_COLOR`, `3_NDVI`, `5_MOISTURE-INDEX`, `6_SWIR`, `7_NDWI`).

The script automatically downloads raw satellite data from the **Copernicus Data Space Ecosystem (Sentinel Hub API)**, performs the mathematical conversions for the bands, scales the indices properly into the `0 to 255` UInt8 range (so they can be read by the Android app's OpenCV implementation), and exports them.

## Setup Instructions 

1. **Install Python Dependencies:**
   Ensure you have Python installed, then run:
   ```bash
   pip install -r requirements.txt
   ```

2. **Set Up Credentials (`.env` file):**
   You need to create a hidden file named `.env` in this directory (it is ignored by git so your secrets remain safe).
   
   Create a `.env` file and paste the following inside it:
   ```env
   SH_CLIENT_ID=your_oauth_client_id_here
   SH_CLIENT_SECRET=your_oauth_client_secret_here
   ```
   *To get these credentials, log into the Copernicus Data Space Ecosystem Dashboard -> User Settings -> OAuth Clients -> Create New.*

## How to Fetch Data for a New Parcel

1. Open `landroid_pipeline.py` in your code editor.
2. At the top of the file, modify the **Configuration Section**:
   ```python
   # 1. Give your farm/parcel an ID (this will be the name of the output folder)
   PARCEL_ID = "MyAwesomeFarm" 
   
   # 2. Paste the longitude/latitude bounding box for the parcel 
   # Format: [min_lon, min_lat, max_lon, max_lat] -> you can get this from bboxfinder.com
   BBOX_COORDS = [12.4, 41.8, 12.6, 42.0]
   
   # 3. Specify the date range to pull from satellite history (YYYY-MM-DD)
   DATE_RANGE = ("2023-08-01", "2023-08-31")
   ```
3. Run the script:
   ```bash
   python landroid_pipeline.py
   ```

## Uploading to Supabase

Once the script finishes running, you will see a new folder named `supabase_storage/<PARCEL_ID>/`. 

Inside will be exactly 5 correctly formatted GeoTIFFs:
- `1_TRUE_COLOR.tif`
- `3_NDVI.tif`
- `5_MOISTURE-INDEX.tif`
- `6_SWIR.tif`
- `7_NDWI.tif`

You can take these files and upload them directly to your Supabase `parcels` bucket under the `/{parcel_id}/` directory to be consumed by the Landroid Android application!
