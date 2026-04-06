import os
from dotenv import load_dotenv
from sentinelhub import SHConfig, SentinelHubRequest, DataCollection, MimeType, BBox, CRS
import numpy as np
import matplotlib.pyplot as plt

# Load credentials from .env file
load_dotenv()

def get_ndvi_image():
    # Setup configuration with Copernicus Data Space Ecosystem URL
    config = SHConfig()
    config.sh_client_id = os.getenv('SH_CLIENT_ID')
    config.sh_client_secret = os.getenv('SH_CLIENT_SECRET')
    config.sh_base_url = 'https://sh.dataspace.copernicus.eu'
    
    if config.sh_client_id == 'your_client_id_here' or not config.sh_client_secret:
        print("Error: Please update your .env file with your OAuth Client ID and Secret.")
        return

    # Define Area of Interest (Bounding Box) - currently set to a small area in Italy
    bbox = BBox([12.4, 41.8, 12.6, 42.0], crs=CRS.WGS84)

    # JavaScript Evalscript to calculate NDVI
    evalscript = """
    //VERSION=3
    function setup() {
      return {
        input: [{
          bands: ["B04", "B08", "dataMask"]
        }],
        output: {
          id: "default",
          bands: 1,
          sampleType: "FLOAT32"
        }
      };
    }

    function evaluatePixel(samples) {
      if (samples.dataMask === 0) {
          return [NaN];
      }
      let ndvi = (samples.B08 - samples.B04) / (samples.B08 + samples.B04);
      return [ndvi];
    }
    """

    print("Requesting Sentinel-2 data...")
    # Build the request
    request = SentinelHubRequest(
        evalscript=evalscript,
        input_data=[
            SentinelHubRequest.input_data(
                data_collection=DataCollection.SENTINEL2_L2A,
                time_interval=("2023-08-01", "2023-08-31")
            )
        ],
        responses=[
            SentinelHubRequest.output_response("default", response_format=MimeType.TIFF)
        ],
        bbox=bbox,
        size=[512, 512], # Size of the downloaded image
        config=config
    )

    try:
        # Fetch the data
        data = request.get_data()
        
        if not data:
            print("No data returned for this location/time period.")
            return

        # Data is a list of numpy arrays
        ndvi_array = data[0]
        print(f"Success! Downloaded NDVI array shape: {ndvi_array.shape}")
        
        # Display the image using matplotlib
        plt.figure(figsize=(10, 10))
        plt.imshow(ndvi_array, cmap='RdYlGn', vmin=-1, vmax=1)
        plt.colorbar(label='NDVI')
        plt.title('Sentinel-2 NDVI')
        plt.savefig('ndvi_output.png')
        print("Saved visualization as ndvi_output.png")
        plt.show()

    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    get_ndvi_image()
