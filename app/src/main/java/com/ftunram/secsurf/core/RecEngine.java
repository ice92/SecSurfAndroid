package com.ftunram.secsurf.core;

import android.support.v4.media.TransportMediator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class RecEngine {
    private dLDADis dLDA_Cb;
    private dLDADis dLDA_Cr;
    private dLDADis dLDA_Y;
    int nCHF;
    int nRHF;
    int scale;
    double thPorn;

    public RecEngine() {
        this.nRHF = 7;
        this.nCHF = 7;
        this.scale = 2;
        this.thPorn = 0.6d;
    }

    public int recPornFaceInfo(CascadeClassifier faceDetector, Mat image, Mat skinTone) {
        int pNeg = 0;
        int pPos = 0;
        boolean face = false;
        Mat mRgba = new Mat();
        Mat mGrey = new Mat();
        image.copyTo(mRgba);
        image.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, 6);
        Imgproc.equalizeHist(mGrey, mGrey);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mGrey, faceDetections);
        for (Rect rect : faceDetections.toArray()) {
            face = true;
            int fW = rect.width;
            int fH = rect.height;
            int xF1 = rect.f6x;
            int yF2 = rect.f7y + fH;
            int x1 = (int) (((double) xF1) - (0.2d * ((double) rect.width)));
            int y1 = (int) (((double) yF2) + (0.6d * ((double) rect.height)));
            int x2 = (int) (((double) (xF1 + fW)) + (0.2d * ((double) rect.width)));
            int y2 = yF2 + (rect.height * 5);
            if (x1 < 0) {
                x1 = 0;
            }
            if (y1 > mGrey.rows() - 1) {
                y1 = mGrey.rows() - 1;
            }
            if (x2 > mGrey.cols() - 1) {
                x2 = mGrey.cols() - 1;
            }
            if (y2 > mGrey.rows() - 1) {
                y2 = mGrey.rows() - 1;
            }
            Rect Body = new Rect();
            Body.f6x = x1;
            Body.f7y = y1;
            Body.width = x2 - x1;
            Body.height = y2 - y1;
            if (Body.height > 0 && Body.width > 0) {
                double pSkin = 0.0d;
                Mat bodyROI = new Mat(skinTone, Body);
                if (((double) Body.height) > 1.1d * ((double) fH)) {
                    pSkin = Core.sumElems(bodyROI).val[0] / ((double) ((float) (bodyROI.rows() * bodyROI.cols())));
                }
                if (pSkin > this.thPorn) {
                    pNeg++;
                } else {
                    pPos++;
                }
            }
        }
        if (!face) {
            return 0;
        }
        if (pNeg > pPos) {
            return 1;
        }
        return -1;
    }

    public void initRecEngine() {
        this.dLDA_Y = new dLDADis();
        this.dLDA_Cr = new dLDADis();
        this.dLDA_Cb = new dLDADis();
        boolean res = this.dLDA_Y.initial("Y").booleanValue();
        res = this.dLDA_Cb.initial("Cb").booleanValue();
        res = this.dLDA_Cr.initial("Cr").booleanValue();
    }

    public void tranBatch(String path) {
        YCC fHF = new YCC();
        List<Mat> tF = new ArrayList();
        File[] listOfFiles = new File(path).listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                String tID;
                String dbFolder = path + "/" + listOfFiles[i].getName();
                File[] listOfFiles1 = new File(dbFolder).listFiles();
                for (int j = 0; j < listOfFiles1.length; j++) {
                    if (listOfFiles1[j].isFile()) {
                        YCC tmpfHF = new YCC();
                        String fName = dbFolder + "/" + listOfFiles1[j].getName();
                        if (j == 0) {
                            fHF = getHFofImage(fName, this.nRHF, this.nCHF, null);
                        } else {
                            if (tF.size() > 0) {
                                tF.clear();
                            }
                            tmpfHF = getHFofImage(fName, this.nRHF, this.nCHF, null);
                            if (tmpfHF.Y1 != null) {
                                tF.add(fHF.Y1);
                                tF.add(tmpfHF.Y1);
                                Core.hconcat(tF, fHF.Y1);
                                tF.clear();
                                tF.add(fHF.Y2);
                                tF.add(tmpfHF.Y2);
                                Core.hconcat(tF, fHF.Y2);
                                tF.clear();
                                tF.add(fHF.Y3);
                                tF.add(tmpfHF.Y3);
                                Core.hconcat(tF, fHF.Y3);
                                tF.clear();
                                if (tmpfHF != null) {
                                    tmpfHF.Y1.release();
                                    tmpfHF.Y2.release();
                                    tmpfHF.Y3.release();
                                }
                            }
                        }
                    }
                }
                if (i == 0) {
                    tID = "Neg";
                } else {
                    tID = "Pos";
                }
                this.dLDA_Y.addData(fHF.Y1, tID);
                this.dLDA_Cb.addData(fHF.Y2, tID);
                this.dLDA_Cr.addData(fHF.Y3, tID);
                fHF.Y1.release();
                fHF.Y2.release();
                fHF.Y3.release();
            }
        }
        this.dLDA_Y.saveParams();
        this.dLDA_Cb.saveParams();
        this.dLDA_Cr.saveParams();
    }

    public boolean recPorn2RecEngine(String fName, Mat frame) {
        boolean res = false;
        YCC hFQ = new YCC();
        Vector<myScore> mScY = new Vector();
        Vector<myScore> mScCb = new Vector();
        Vector<myScore> mScCr = new Vector();
        if (frame != null) {
            hFQ = getHFofImage(null, this.nRHF, this.nCHF, frame);
        } else if (fName != null) {
            hFQ = getHFofImage(fName, this.nRHF, this.nCHF, null);
        }
        if (hFQ != null) {
            int i;
            mScY = this.dLDA_Y.Matching(hFQ.Y1);
            mScCr = this.dLDA_Cb.Matching(hFQ.Y2);
            mScCb = this.dLDA_Cr.Matching(hFQ.Y3);
            for (i = 0; i < mScY.size(); i++) {
                myScore tS = new myScore();
                tS.ID = ((myScore) mScY.get(i)).ID;
                tS.sc = (((((myScore) mScCr.get(i)).sc + ((myScore) mScCb.get(i)).sc) * 0.75d) + ((myScore) mScY.get(i)).sc) / 3.0d;
                mScY.set(i, tS);
            }
            Collections.sort(mScY);
            int tP = 0;
            for (i = 0; i < 9; i++) {
                if (((myScore) mScY.elementAt(i)).ID.equals("Pos")) {
                    tP++;
                }
            }
            if (tP > 9 - tP) {
                res = true;
            } else {
                res = false;
            }
        }
        mScY.clear();
        mScCb.clear();
        mScCr.clear();
        return res;
    }

    public Mat getSkinToneCbCr(Mat frame) {
        Mat im = frame.clone();
        Mat skinTone = new Mat(im.rows(), im.cols(), CvType.CV_64FC1);
        Imgproc.cvtColor(im, im, 36);
        List<Mat> mv = new ArrayList(3);
        Core.split(im, mv);
        Mat Cr = (Mat) mv.get(1);
        Mat Cb = (Mat) mv.get(2);
        for (int i = 0; i < im.rows() - 1; i++) {
            for (int j = 0; j < im.cols() - 1; j++) {
                double[] cb = Cb.get(i, j);
                double[] cr = Cr.get(i, j);
                if (((int) cb[0]) < 77 || ((int) cb[0]) > TransportMediator.KEYCODE_MEDIA_PAUSE || ((int) cr[0]) < Imgproc.COLOR_RGBA2YUV_YV12 || ((int) cr[0]) > 173) {
                    skinTone.put(i, j, 0.0d);
                } else {
                    skinTone.put(i, j, 1.0d);
                }
            }
        }
        return skinTone;
    }

    public boolean recFramePorn2RecEngineMultiStage(Mat frame, CascadeClassifier faceDetector) {
        Mat im = new Mat();
        double ratio = 360.0d / ((double) Math.max(frame.cols(), frame.rows()));
        double round = (double) ((int) Math.round(((double) frame.rows()) * ratio));
        Mat mat = frame;
        Imgproc.resize(mat, im, new Size((double) ((int) Math.round(((double) frame.cols()) * ratio)), r0));
        if (im.channels() != 3) {
            return false;
        }
        Mat skinTone = getSkinToneCbCr(im);
        if (Core.sumElems(skinTone).val[0] / ((double) (skinTone.rows() * skinTone.cols())) <= 0.15d) {
            return false;
        }
        int res1 = recPornFaceInfo(faceDetector, im, skinTone);
        if (res1 == 0) {
            return recPorn2RecEngine(null, frame);
        }
        if (res1 == 1) {
            return true;
        }
        return false;
    }

    public boolean recPorn2RecEngineMultiStage(String fName, CascadeClassifier faceDetector) {
        boolean res = false;
        Mat im = Highgui.imread(fName);
        double ratio = 360.0d / ((double) Math.max(im.cols(), im.rows()));
        Imgproc.resize(im, im, new Size((double) ((int) Math.round(((double) im.cols()) * ratio)), (double) ((int) Math.round(((double) im.rows()) * ratio))));
        if (im.channels() == 3) {
            Mat skinTone = getSkinToneCbCr(im);
            if (Core.sumElems(skinTone).val[0] / ((double) (skinTone.rows() * skinTone.cols())) > 0.15d) {
                int res1 = recPornFaceInfo(faceDetector, im, skinTone);
                res = res1 == 0 ? recPorn2RecEngine(fName, null) : res1 == 1;
            }
        }
        im.release();
        return res;
    }

    public Mat getDctFeatures(Mat imIn, int N, int M) {
        Mat res = new Mat(imIn.size(), imIn.type());
        Core.dct(imIn, res);
        Mat tmp1 = res.colRange(0, N).rowRange(0, M);
        Mat tmp = new Mat(N, M, CvType.CV_64FC1);
        tmp1.copyTo(tmp);
        tmp1.release();
        tmp = tmp.reshape(1, tmp.rows() * tmp.cols());
        Core.multiply(tmp, new Scalar(1.0d / Core.norm(tmp)), tmp);
        return tmp;
    }

    public YCC getHFofImage(String fName, int nRFitur, int nCFitur, Mat frame) {
        YCC tmp = new YCC();
        List<Mat> tF = new ArrayList();
        Mat im = Mat();
        if (frame == null) {
            im = Highgui.imread(fName);
        } else if (frame != null) {
            im = frame.clone();
        }
        Size size = new Size(128.0d, 128.0d);
        if (!(im.rows() == TransportMediator.FLAG_KEY_MEDIA_NEXT || im.cols() == TransportMediator.FLAG_KEY_MEDIA_NEXT)) {
            Imgproc.resize(im, im, size);
        }
        Imgproc.cvtColor(im, im, 36);
        List<Mat> arrayList = new ArrayList(3);
        Core.split(im, arrayList);
        Mat imIn = (Mat) arrayList.get(0);
        Mat Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);
        Core.multiply(Y, new Scalar(0.00392156862745098d), Y);
        Mat hu = new Mat();
        Imgproc.HuMoments(Imgproc.moments(Y, false), hu);
        hu = hu.colRange(0, 1).rowRange(0, 4);
        Core.normalize(Y, Y, 0.0d, 1.0d, 32);
        tF.add(getDctFeatures(Y, nRFitur, nCFitur));
        tF.add(hu);
        Core.vconcat(tF, Y);
        tF.clear();
        Core.multiply(Y, new Scalar(Core.norm(Y)), Y);
        tmp.Y1 = Y.clone();
        Y.release();
        imIn = (Mat) arrayList.get(1);
        Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);
        Core.multiply(Y, new Scalar(0.00392156862745098d), Y);
        Imgproc.HuMoments(Imgproc.moments(Y, false), hu);
        hu = hu.colRange(0, 1).rowRange(0, 4);
        Core.normalize(Y, Y, 0.0d, 1.0d, 32);
        tF.add(getDctFeatures(Y, nRFitur, nCFitur));
        tF.add(hu);
        Core.vconcat(tF, Y);
        tF.clear();
        Core.multiply(Y, new Scalar(Core.norm(Y)), Y);
        tmp.Y2 = Y.clone();
        Y.release();
        imIn = (Mat) arrayList.get(2);
        Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);
        Core.multiply(Y, new Scalar(0.00392156862745098d), Y);
        Imgproc.HuMoments(Imgproc.moments(Y, false), hu);
        hu = hu.colRange(0, 1).rowRange(0, 4);
        Core.normalize(Y, Y, 0.0d, 1.0d, 32);
        tF.add(getDctFeatures(Y, nRFitur, nCFitur));
        tF.add(hu);
        Core.vconcat(tF, Y);
        tF.clear();
        Core.multiply(Y, new Scalar(Core.norm(Y)), Y);
        tmp.Y3 = Y.clone();
        Y.release();
        return tmp;
    }

    private Mat Mat() {
        return null;
    }
}
