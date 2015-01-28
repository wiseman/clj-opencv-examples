# clj-opencv-examples

Examples of using OpenCV from Clojure.

## Meanshift and camshift video tracking algorithms

```
$ lein run -m com.lemondronor.opencv.examples.camshift videos/slow_traffic_small.mp4
```

Output will be a series of frames of the form `frame-nnnn.png` showing
the ROI rect.

![Meanshift screenshot](/screenshots/meanshift.jpg?raw=true "Meanshift screenshot")

```
$ lein run -m com.lemondronor.opencv.examples.meanshift videos/slow_traffic_small.mp4
```

Output will be a series of frames of the form `frame-nnnn.png` showing
the ROI rotated rect.

![Camshift screenshot](/screenshots/camshift.jpg?raw=true "Camshift screenshot")
