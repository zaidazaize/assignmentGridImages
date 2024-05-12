## Installation

1. Clone the repository to your local machine:

   ```
   git clone https://github.com/zaidazaize/assignmentGridImages.git
   ```

2. Open the project in Android Studio.
3. Build and run the app on your Android device or emulator.
4. Good To have : Use Api level 32 emulator as I used this during development.(Optional)

# Grid Images Android App

This Android application is designed to showcase a grid of images downloaded from the internet. It
features image downloading, caching, and lazy loading functionalities.

## Features
- **API Integration**: The app integrates with an external API to fetch the details of the images.
- **Image Downloading**: Based on the API response, the app downloads the images from the provided
  URLs.
- **Image Caching**: Downloaded images are cached locally (on disc in cache folder) to improve performance and reduce data
  usage.
- **Grid Display**: Images are displayed in a grid layout for easy browsing and viewing.
- **Image Resizing**: Images are resized(both of network and local cached) to fit the grid layout size, ensuring smooth scrolling and
  optimal performance.
- **Lazy Loading**: Images are loaded lazily as the user scrolls through the grid, improving loading
  times and user experience.
- **Network Issue Handling**: The app gracefully handles network issues, such as no internet
  connection 
- **Error Handling**: The app handles errors gracefully and displays appropriate messages and placeholders.


## NO Third Party Libraries Used for fetching images
The app utilizes coroutines and the Android `HttpUrlConnection` class to fetch images from the internet. 

