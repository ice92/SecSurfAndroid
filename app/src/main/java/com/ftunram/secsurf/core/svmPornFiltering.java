package com.ftunram.secsurf.core;

import android.support.v4.media.TransportMediator;
import java.util.ArrayList;
import java.util.List;
import org.opencv.BuildConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;

public class svmPornFiltering {
    CvSVM SVM;

    public svmPornFiltering() {
        this.SVM = new CvSVM();
    }

    public void initSVM(String fNameSvm) {
        this.SVM.load(fNameSvm);
    }

    public void finalize() {
        this.SVM.clear();
    }

    public boolean matchSVM(String fName, Mat frame) {
        Mat hFQ = new Mat();
        if (frame != null) {
            hFQ = getHFofImage4SVM(BuildConfig.FLAVOR, 1, 1, frame);
        } else if (fName != null) {
            hFQ = getHFofImage4SVM(fName, 1, 1, null);
        }
        float response = this.SVM.predict(hFQ);
        hFQ.release();
        if (response == -1.0f) {
            return true;
        }
        return false;
    }

    public YCC getSkinSegementedYCbCr(Mat frame) {
        YCC tmp = new YCC();
        Mat im = frame.clone();
        Imgproc.cvtColor(im, im, 36);
        List<Mat> mv = new ArrayList(3);
        Core.split(im, mv);
        Mat Y = (Mat) mv.get(0);
        Imgproc.equalizeHist(Y, Y);
        Mat Cr = (Mat) mv.get(1);
        Mat Cb = (Mat) mv.get(2);
        for (int i = 0; i < im.rows() - 1; i++) {
            for (int j = 0; j < im.cols() - 1; j++) {
                double[] y = Y.get(i, j);
                double[] cb = Cb.get(i, j);
                double[] cr = Cr.get(i, j);
                if (((int) cb[0]) < 77 || ((int) cb[0]) > TransportMediator.KEYCODE_MEDIA_PAUSE || ((int) cr[0]) < Imgproc.COLOR_RGBA2YUV_YV12 || ((int) cr[0]) > 173) {
                    y[0] = 0.0d;
                    cb[0] = 0.0d;
                    cr[0] = 0.0d;
                    Y.put(i, j, y);
                    Cr.put(i, j, cr);
                    Cb.put(i, j, cb);
                }
            }
        }
        tmp.Y1 = Y.clone();
        Y.release();
        tmp.Y2 = Cb.clone();
        Cb.release();
        tmp.Y3 = Cr.clone();
        Cr.release();
        mv.clear();
        return tmp;
    }

    public Mat getDctFeatures(Mat imIn, int N, int M) {
        Core.multiply(imIn, new Scalar(0.00392156862745098d), imIn);
        Mat res = new Mat(imIn.size(), imIn.type());
        Core.dct(imIn, res);
        Mat tmp1 = res.colRange(0, N).rowRange(0, M);
        Mat tmp = new Mat(N, M, CvType.CV_32FC1);
        tmp1.copyTo(tmp);
        tmp1.release();
        tmp = tmp.reshape(1, tmp.rows() * tmp.cols());
        res.release();
        return tmp;
    }

    public Mat getHFofImage4SVM(String fName, int nRFitur, int nCFitur, Mat frame) {
        Mat tHF = new Mat(1, 4, CvType.CV_32FC1);
        Mat im = new Mat();
        if (frame == null) {
            im = Highgui.imread(fName);
        } else if (frame != null) {
            im = frame.clone();
        }
        Size sz = new Size(128.0d, 128.0d);
        if (!(im.rows() == TransportMediator.FLAG_KEY_MEDIA_NEXT || im.cols() == TransportMediator.FLAG_KEY_MEDIA_NEXT)) {
            Imgproc.resize(im, im, sz);
        }
        YCC tmp = getSkinSegementedYCbCr(im);
        im.release();
        Mat Y = tmp.Y1.clone();
        tmp.Y1.release();
        tmp.Y1 = null;
        Y.convertTo(Y, CvType.CV_32FC1);
        Mat hF = getDctFeatures(Y, nRFitur, nCFitur);
        tHF.put(0, 1, hF.get(0, 0));
        hF.release();
        Mat hu = new Mat();
        Imgproc.HuMoments(Imgproc.moments(Y, false), hu);
        hu.convertTo(hu, CvType.CV_32FC1);
        tHF.put(0, 0, hu.get(0, 0));
        Y.release();
        hu.release();
        Y = tmp.Y2.clone();
        tmp.Y2.release();
        tmp.Y2 = null;
        Y.convertTo(Y, CvType.CV_32FC1);
        hF = getDctFeatures(Y, nRFitur, nCFitur);
        tHF.put(0, 2, hF.get(0, 0));
        Y.release();
        hF.release();
        Y = tmp.Y3.clone();
        tmp.Y3.release();
        tmp.Y3 = null;
        Y.convertTo(Y, CvType.CV_32FC1);
        hF = getDctFeatures(Y, nRFitur, nCFitur);
        tHF.put(0, 3, hF.get(0, 0));
        Y.release();
        hF.release();
        return tHF;
    }
}
