# Video Rank

Video Rank is an annotation tool for ranking videos. It displays video files from subdirectories and lets users assign ranking orders interactively. Annotations are saved in a CSV file for further analysis.

## Features

- **Multi-Video Display:**  
  Videos are loaded from subdirectories in the `./target` folder and displayed in groups of up to three side by side.

- **Interactive Annotation:**  
  - Click on a video (or press the corresponding digit key) to assign a ranking.  
  - Clicking pauses the video and applies a grayscale effect.
  
- **Keyboard Shortcuts:**  
  - Number keys (1, 2, 3, …) simulate clicks on the respective video.  
  - Press **Enter** or **N** to move to the next folder after annotation.

- **CSV Export:**  
  After annotation, results are saved to `results.csv` in the format:  
  `<folder_number>,<video_filename_without_extension_for_rank1>,<video_filename_for_rank2>,...`

- **Automatic Completion:**  
  The tool checks if all folders have been annotated. When all folders are finished, it alerts the user and exits.

- **Resizable UI:**  
  The interface is built using JavaFX and supports window resizing.

## Requirements

- Java JDK 8 or above (with JavaFX; for JDK 11+, ensure JavaFX is added to your classpath)

## Installation and Running

An executable JAR file named `app-all.jar` is provided. To run the application, execute:

```bash
java -jar app-all.jar

