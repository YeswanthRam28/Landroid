# Design System: Field Intelligence & The Organic Professional

## 1. Overview & Creative North Star: "The Digital Surveyor"
This design system is built to bridge the gap between high-utility geospatial data and the tactile, prestigious world of land management. Our Creative North Star is **The Digital Surveyor**: an experience that feels as authoritative as a leather-bound field journal and as precise as satellite telemetry.

We break the "SaaS template" look by rejecting rigid, boxy grids in favor of **Intentional Asymmetry** and **Tonal Depth**. By utilizing wide-aperture typography (mixing the editorial warmth of Newsreader/Fraunces with the technical precision of Plus Jakarta Sans/DM Sans), we create a "High-End Editorial" feel. Layouts should feel curated, with map-heavy bleeds that push content to the edges, interrupted only by floating, glass-like modules that suggest air and light.

---

### 2. Colors & Surface Philosophy
The palette avoids the "sterile blue" of tech, opting for a grounded, heritage-driven scheme that mimics the materials of the field: moss, loam, and amber sunlight.

*   **Primary (`#1A4731`):** The "Forest Green." Use this for brand authority and primary actions. 
*   **Tertiary/Accent (`#D4820A`):** The "Warm Amber." Use sparingly for high-value highlights, map pins, or specialized data points.
*   **Surface (`#FCF9F3`):** The "Warm Cream." This is our primary canvas, providing a softer eye-strain profile than pure white for long hours in the field.

#### The "No-Line" Rule
**Explicit Instruction:** Do not use 1px solid borders to define sections. We define boundaries through **Background Shifts**. A `surface-container-low` card sitting on a `surface` background provides all the separation needed. If a border feels "required," your contrast levels are likely incorrect.

#### Surface Hierarchy & Nesting
Treat the UI as physical layers of vellum or frosted glass.
1.  **Base:** `surface` (The map or main background).
2.  **Level 1:** `surface-container-low` (Secondary side panels).
3.  **Level 2:** `surface-container-lowest` (Active cards or floating modals).
4.  **Level 3:** `surface-bright` (High-intensity focus states).

#### The "Glass & Gradient" Rule
Floating map overlays must use **Glassmorphism**. Apply `surface-container-lowest` at 80% opacity with a `20px` backdrop-blur. To give CTAs "soul," use a subtle linear gradient from `primary` (#1A4731) to `primary-container` (#1A4731 at 90% opacity).

---

### 3. Typography: Editorial Authority
We use a high-contrast typographic scale to differentiate between "The Narrative" (Headlines) and "The Data" (UI).

*   **Display & Headlines (Newsreader/Fraunces):** These are our "Editorial" fonts. They should be set with tighter tracking and used for property names, large acreage numbers, and section headers. This conveys heritage and trust.
*   **Body, Labels & UI (Plus Jakarta Sans/DM Sans):** Our "Technical" fonts. These provide maximum legibility for coordinates, topographical data, and form inputs.

**Scale Highlight:**
*   **Display-LG (3.5rem):** For hero data points (e.g., total parcel value).
*   **Title-MD (1.125rem):** For card titles, using the technical font to maintain a "tool-first" feel.
*   **Label-SM (0.6875rem):** All-caps with 5% letter spacing for secondary data labels (e.g., "LAT/LONG").

---

### 4. Elevation & Depth
Depth is achieved through **Tonal Layering**, not structural shadows.

*   **The Layering Principle:** Instead of a shadow, place a `surface-container-lowest` (#FFFFFF) card on a `surface-container` (#F0EEE8) background. The 2-3% shift in luminosity creates a sophisticated, "quiet" lift.
*   **Ambient Shadows:** When an element must "float" over a map (e.g., a search bar), use a highly diffused shadow: `0 8px 32px rgba(28, 28, 24, 0.06)`. The shadow color is a tint of our `on-surface` color, never a neutral grey.
*   **Ghost Borders:** If accessibility requires a stroke, use `outline-variant` at 15% opacity. It should be felt, not seen.

---

### 5. Components

#### Buttons
*   **Primary:** Solid `primary` green. 12px rounded corners. No border. Text in `on-primary` (white).
*   **Secondary:** `surface-container-highest` background with `primary` text. Provides a soft, tactile feel.
*   **Tertiary:** Transparent background, `primary` text, underlined only on hover.

#### Floating Field Cards
*   **Style:** 16px corner radius (`xl`). 24px internal padding. 
*   **Interaction:** No dividers. Use 16px or 24px of vertical white space to separate the header from the data grid.
*   **Rule:** Use `surface-container-low` for the card body and `surface-container-lowest` for nested inner elements (like a "Details" chip).

#### Geospatial Chips
*   **Style:** 12px rounded (`md`). Background `secondary-container` (#D5E0F7).
*   **Content:** Pair a `label-md` technical font with a small icon for field-status (e.g., "Surveyed," "Pending").

#### Input Fields
*   **Style:** Minimalist. No bottom line or full box. Use a subtle `surface-container-high` background fill.
*   **States:** On focus, the background shifts to `surface-bright` with a 1px `tertiary` (Amber) "Ghost Border" to signal intelligence and activity.

#### Additional Component: The "Map Tray"
A specialized bottom-sheet or side-drawer that uses a heavy backdrop blur. It should feel like a transparent overlay resting on top of the physical land data below.

---

### 6. Do’s and Don’ts

#### Do:
*   **Do** use asymmetrical margins. A map can bleed to the left edge while text content is padded 40px from the right.
*   **Do** use "Warm Amber" for interactive map elements to draw the eye against the "Forest Green" UI.
*   **Do** prioritize whitespace. If a layout feels "crowded," remove a container background rather than shrinking the text.

#### Don’t:
*   **Don’t** use pure black (#000000) for text. Always use `on-surface` (#1C1C18) to maintain the organic, ink-on-paper feel.
*   **Don’t** use 1px dividers. If you need to separate content, use a 8px height block of `surface-container-low`.
*   **Don’t** use standard "Blue" for links. Use `primary` (Green) or `tertiary` (Amber) to stay within the field-intelligence aesthetic.