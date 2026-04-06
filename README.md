# Landroid - Intelligent Farm Analytics & Management

**Landroid** is a cutting-edge Android application built specifically to revolutionize agricultural data processing, unifying **Land Consultants** and **Landowners** onto a heavily analytical, role-based platform. 

Built using modern **Jetpack Compose** architecture, the app scales rapidly, leveraging localized AI models like **OpenCV** alongside extensive GIS mapping vectors via **MapLibre** to serve real-time agronomic insights onto the palms of landowners.

---

## 🚀 Key Features by Role

### 🛡 Land Consultant (Admin Architecture)
The Command Center designed to create boundaries effectively. 
* **Create Parcels:** Digitize land blocks seamlessly.
* **Drone Integration:** Upload critical infrastructure maps (Orthomosaic, DEM, NDVI overlays).
* **Document Management Vault:** Upload regulatory framework files safely to AWS/Supabase structures (Patta, FMB, EC Files).
* **Landowner Assignment:** Securely lock and route bounded maps directly to farm operators.

### 🌱 Landowner (Read-Only Consumer Analytics)
The "Asset Portfolio" Dashboard customized for deep-dive analysis.
* **GIS Map Integration:** Native ESRI Satellite integration overlaid with projected boundary zones strictly for their assigned parcels.
* **Dynamic Land Health Score:** Custom tracking UI assessing soil/irrigation signals over targeted acreage.
* **OpenCV Tree Count:** Hardware-accelerated Canopy Detection parsing drone captures.
* **Algorithmic Land Valuation:** Instant calculations extracting the estimated market scope (₹).
* **Plant Zones:** Multi-spectral NDVI classification to track crop saturation.

---

## 🛠 Technology Stack

* **Frontend UI Engine:** Kotlin & Jetpack Compose (Material 3)
* **GIS / Mapping SDK:** MapLibre Native (GeoJSON EPSG:4326 Projection & ESRI World Imagery)
* **Computer Vision / AI Module:** OpenCV (Native C++ Bindings wrapped gracefully into Android via Kotlin Flow)
* **Authentication Network:** Firebase Authentication (OTP / Phone Number Protocols / Local Testing Bypass)
* **Database & Post-GIS Server:** Supabase 
* **State Management & Dependency Injection:** ViewModels, Coroutines, and Hilt/Dagger

---

## ⚙️ Initial Setup & Build Configuration

To run this application locally, you must assemble the encrypted configurations explicitly since they are tracked in `.gitignore`.

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/[Your-Link]/landroid.git
   ```

2. **Supply the `.env` configuration:**
   At the root `d:\Projects\Hackathon\Landroid\`, create a `.env` file containing your Supabase bindings:
   ```env
   SUPABASE_URL=your_project_url
   SUPABASE_ANON_KEY=your_secure_anon_key
   ```

3. **Provide Firebase Context:**
   Inside `app/`, create or insert a valid `google-services.json` tied to a project that shares the `SHA-1` debug key generated locally on your machine.
   * *Note:* Phone authentication billing walls can be completely bypassed in development locally by executing the hardcoded `0000000000` payload during the Auth OTP challenge sequence.

4. **Sync and Run via Android Studio**
   The `build.gradle.kts` structure uses KSP and Hilt processors. Simply Clean, Rebuild, and Deploy onto an emulator / physical device API 28+.
