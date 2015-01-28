(ns com.lemondronor.opencv.examples.camshift
  (:import [java.util ArrayList]
           [nu.pattern OpenCV]
           ;;[org.bytedeco.javacv OpenCVFrameGrabber]
           [org.opencv.core Core Mat MatOfInt MatOfFloat MatOfPoint Point Rect
            RotatedRect Scalar TermCriteria]
           [org.opencv.imgproc Imgproc]
           [org.opencv.highgui Highgui VideoCapture]
           [org.opencv.video Video])
  (:gen-class))

(set! *warn-on-reflection* true)


(defn mat-of-int [& args]
  (MatOfInt. (int-array args)))


(defn mat-of-float [& args]
  (MatOfFloat. (float-array args)))


(defn draw-rotated-rect [^RotatedRect rrect ^Mat frame]
  (let [points (make-array Point 4)]
    (.points rrect points)
    (Core/polylines
     frame
     (let [^MatOfPoint mp (MatOfPoint.)]
       (.fromList mp (seq points))
       (list mp))
     true
     (Scalar. 255 255 255)
     2)))


(defn -main [& args]
  (OpenCV/loadShared)
  (let [roi-rect (atom (Rect. 300 200 20 20))
        ^VideoCapture cap (VideoCapture. (first args))
        ^Mat frame (Mat.)
        ^Mat hsv-roi (Mat.)
        ^Mat mask (Mat.)]
    (.read cap frame)
    (let [^Mat roi (Mat. frame @roi-rect)
          ^Mat roi-hist (Mat.)]
      (Imgproc/cvtColor roi hsv-roi Imgproc/COLOR_BGR2HSV)
      (Core/inRange
       hsv-roi
       (Scalar. 0.0 30.0 32.0)
       (Scalar. 180.0 255.0 255.0)
       mask)
      (Imgproc/calcHist
       [hsv-roi] (mat-of-int 0) mask roi-hist (mat-of-int 180)
       (mat-of-float 0.0 180.0))
      (Core/normalize roi-hist roi-hist 0 255 Core/NORM_MINMAX)
      (loop [i 0
             more-frames? (.read cap frame)]
        (when more-frames?
          (let [^Mat hsv (Mat.)
                ^Mat dst (Mat.)]
            (Imgproc/cvtColor frame hsv Imgproc/COLOR_BGR2HSV)
            (Imgproc/calcBackProject
             [hsv] (mat-of-int 0) roi-hist dst (mat-of-float 0.0 180.0) 1.0)
            (let [rotrect (Video/CamShift
                           dst @roi-rect
                           (TermCriteria.
                            (+ TermCriteria/COUNT TermCriteria/EPS) 80 1.0))]
              (println i roi-rect rotrect)
              (reset! roi-rect (.boundingRect rotrect))
              ;;(Core/rectangle
              ;; frame (.tl @roi-rect) (.br @roi-rect) (Scalar. 255 255 255) 2)
              (draw-rotated-rect rotrect frame)
              (Highgui/imwrite (format "frame-%04d.png" i) frame)
              (recur (inc i) (.read cap frame)))))))))
