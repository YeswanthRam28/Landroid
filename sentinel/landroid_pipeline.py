import os
import shutil
from dotenv import load_dotenv
from sentinelhub import SHConfig, SentinelHubRequest, DataCollection, MimeType, BBox, CRS

load_dotenv()

# Configuration Defaults
DEFAULT_OUTPUT_DIR = "supabase_storage"
DEFAULT_DATE_RANGE = ("2023-08-01", "2023-08-31")

def process_parcel_bands(parcel_id: str, bbox_coords: list, output_dir: str = None):
    config = SHConfig()
    config.sh_client_id = os.getenv('SH_CLIENT_ID')
    config.sh_client_secret = os.getenv('SH_CLIENT_SECRET')
    config.sh_base_url = 'https://sh.dataspace.copernicus.eu'
    
    if not config.sh_client_secret:
        print("Missing API credentials in .env file!")
        return

    target_dir = output_dir if output_dir else os.path.join(DEFAULT_OUTPUT_DIR, parcel_id)
    os.makedirs(target_dir, exist_ok=True)
    bbox = BBox(bbox_coords, crs=CRS.WGS84)
    print(f"Starting Landroid Pipeline processing for {parcel_id}...\n")

    # The Android app uses OpenCV which expects 8-bit grayscale (0-255).
    # Since index values (NDVI, NDWI) range from -1 to 1, we must transform 
    # them to 0.0 - 1.0 in JS. Sentinel Hub "AUTO" will scale that to 0 - 255!
    
    tasks = [
        {
            "name": "1_TRUE_COLOR.tif",
            "channels": 3,
            "evalscript": """
            //VERSION=3
            function setup() { return { input: [{ bands: ["B04", "B03", "B02"] }], output: { bands: 3, sampleType: "AUTO" } }; }
            function evaluatePixel(samples) {
                // Enhance brightness by 2.5x for better visual display
                return [samples.B04 * 2.5, samples.B03 * 2.5, samples.B02 * 2.5];
            }
            """
        },
        {
            "name": "3_NDVI.tif",
            "channels": 1,
            "evalscript": """
            //VERSION=3
            function setup() { return { input: [{ bands: ["B04", "B08", "dataMask"] }], output: { bands: 1, sampleType: "AUTO" } }; }
            function evaluatePixel(samples) {
                if (samples.dataMask === 0) return [0];
                let ndvi = (samples.B08 - samples.B04) / (samples.B08 + samples.B04);
                // Map -1.0 to 1.0  ---->  0.0 to 1.0 (so App gets 0 to 255)
                return [(ndvi + 1.0) / 2.0];
            }
            """
        },
        {
            "name": "5_MOISTURE-INDEX.tif",
            "channels": 1,
            "evalscript": """
            //VERSION=3
            function setup() { return { input: [{ bands: ["B8A", "B11", "dataMask"] }], output: { bands: 1, sampleType: "AUTO" } }; }
            function evaluatePixel(samples) {
                if (samples.dataMask === 0) return [0];
                let ndmi = (samples.B8A - samples.B11) / (samples.B8A + samples.B11);
                // Map -1.0 to 1.0  ---->  0.0 to 1.0
                return [(ndmi + 1.0) / 2.0];
            }
            """
        },
        {
            "name": "6_SWIR.tif",
            "channels": 1,
            "evalscript": """
            //VERSION=3
            function setup() { return { input: [{ bands: ["B12", "dataMask"] }], output: { bands: 1, sampleType: "AUTO" } }; }
            function evaluatePixel(samples) {
                if (samples.dataMask === 0) return [0];
                // SWIR is naturally 0.0 to 1.0 reflectance.
                return [samples.B12]; 
            }
            """
        },
        {
            "name": "7_NDWI.tif",
            "channels": 1,
            "evalscript": """
            //VERSION=3
            function setup() { return { input: [{ bands: ["B03", "B08", "dataMask"] }], output: { bands: 1, sampleType: "AUTO" } }; }
            function evaluatePixel(samples) {
                if (samples.dataMask === 0) return [0];
                let ndwi = (samples.B03 - samples.B08) / (samples.B03 + samples.B08);
                // Map -1.0 to 1.0  ---->  0.0 to 1.0
                return [(ndwi + 1.0) / 2.0];
            }
            """
        }
    ]

    for index, task in enumerate(tasks):
        print(f"[{index+1}/5] Processing {task['name']}...")
        
        request = SentinelHubRequest(
            evalscript=task["evalscript"],
            input_data=[SentinelHubRequest.input_data(
                data_collection=DataCollection.SENTINEL2_L2A,
                time_interval=DEFAULT_DATE_RANGE
            )],
            responses=[SentinelHubRequest.output_response("default", response_format=MimeType.TIFF)],
            bbox=bbox,
            size=[512, 512],
            config=config
        )
        
        # Download data
        try:
            image_array = request.get_data()
            if image_array:
                # The data is downloaded into the sentinelhub cache. 
                # We save it explicitly using tifffile to our target folder.
                import tifffile
                out_path = os.path.join(target_dir, task['name'])
                tifffile.imwrite(out_path, image_array[0])
                print(f"   Saved {out_path}")
            else:
                print(f"   No data found for this range.")
        except Exception as e:
            print(f"   Error fetching {task['name']}: {e}")

    print("\n✅ All bands generated! Ready for Landroid Supabase upload.")
    print(f"If you need to optimize them as COGs, run GDAL translation on {target_dir}.")

if __name__ == "__main__":
    process_parcel_bands("parcel_001", [12.4, 41.8, 12.6, 42.0])
