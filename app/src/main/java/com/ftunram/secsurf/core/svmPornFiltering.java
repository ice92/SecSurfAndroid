package com.ftunram.secsurf.core;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.ml.CvSVM;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 1/20/2016.
 */
public class svmPornFiltering {
    CvSVM SVM= new CvSVM();

    public void initSVM (String fNameSvm) {
        SVM.load(fNameSvm);
    }

    public void finalize(){
        SVM.clear();
    }

    public  boolean matchSVM (String fName, Mat frame){
        boolean res =false;
        //1.a Extract fitur
        Mat hFQ=new Mat();
        if (frame!=null){
            hFQ=getHFofImage4SVM("",1,1,frame);
        }else if (fName!=null) {
            //string imName="Data\\dtNeg\\Q3\\6.jpg";
            hFQ=getHFofImage4SVM(fName,1,1,null);
            //cout << "fY= " <<fYQ << endl<<endl;
        }

        //2. Recognize
        float response = SVM.predict(hFQ);
        hFQ.release();
        if (response==-1) res=true; //porn
        else res=false;	 //non Porn

        return res;
    }

    //Method for getting YCBCR with segmented skin
    public YCC getSkinSegementedYCbCr(Mat frame)
    {
        YCC tmp=new YCC();
        Mat im=frame.clone();
        //Mat skinTone= new Mat(im.rows(), im.cols(), CvType.CV_32FC1);
        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);
        List<Mat>mv=new ArrayList<Mat>(3);
        Core.split(im,mv);

        Mat Y=mv.get(0);  Imgproc.equalizeHist( Y, Y );
        Mat Cr=mv.get(1);
        Mat Cb=mv.get(2);


        for(int i=0;i<im.rows()-1;i++){
            for(int j=0;j<im.cols()-1;j++){
                double[] y = Y.get(i,j);
                double[] cb = Cb.get(i,j);
                double[] cr = Cr.get(i,j);

                if (!((int)cb[0]>=77 && (int)cb[0]<=127 && (int)cr[0]>=133 && (int)cr[0]<=173 )){
                    //skinTone.put(i,j,0);
                    y[0]=0; cb[0]=0; cr[0]=0;
                    Y.put(i, j, y);
                    Cr.put(i, j, cr);
                    Cb.put(i, j, cb);
                }
            }
        }
        tmp.Y1=Y.clone(); Y.release();
        Y =null;
        tmp.Y2=Cb.clone(); Cb.release();
        Cb=null;
        tmp.Y3=Cr.clone();
        Cr.release(); Cr=null;
        //skinTone.release();
        mv.clear();

        return tmp;
    }

    //Method for getting dct coef single channel Mat
    public Mat getDctFeatures(Mat imIn, int N, int M)
    {
        //Assume the Input size matrix
        Scalar t=new Scalar(1.0/255);
        //System.out.println( "Norm = " +nT );
        Core.multiply(imIn, t, imIn);
        Mat res = new Mat(imIn.size(), imIn.type());
        Core.dct(imIn,res);

        Mat tmp1=res.colRange(0,N).rowRange(0,M);
        //System.out.println( "mat Y= " +tmp1.dump() );

        Mat tmp=new Mat(N,M,CvType.CV_32FC1);
        tmp1.copyTo(tmp);
        tmp1.release();
        tmp=tmp.reshape(1,tmp.rows()*tmp.cols());
        res.release(); res=null;
        tmp1=null;

        return tmp;
    }

    //Method for getting hF coef of an image
    public Mat getHFofImage4SVM (String fName, int nRFitur, int nCFitur, Mat frame)
    {
        Mat tHF=new Mat(1,4,CvType.CV_32FC1);

        Mat im=new Mat();
        if (frame==null){
            im=Highgui.imread(fName);
        } else if (frame!=null) {
            im=frame.clone();
        };
        Size sz = new Size(128,128);
        if (im.rows()!=128 && im.cols()!=128) Imgproc.resize(im, im,sz);
        //get Skin Tone
        YCC tmp=getSkinSegementedYCbCr(im);
        im.release(); im=null;

        //if (double(ts[0])>10){
        //=======Y Component=======

        Mat Y = tmp.Y1.clone(); tmp.Y1.release();tmp.Y1=null;
        Y.convertTo(Y, CvType.CV_32FC1);

        Mat hF=getDctFeatures(Y,nRFitur,nCFitur);
        double[] y = hF.get(0,0);
        tHF.put(0, 1, y);
        hF.release();hF=null;

        //=======Moment Component=======

        // Get Hu Moment
        Mat hu=new Mat();
        Moments m= Imgproc.moments(Y, false);
        Imgproc.HuMoments(m, hu);
        hu.convertTo(hu, CvType.CV_32FC1);
        y = hu.get(0,0);
        tHF.put(0, 0, y);
        Y.release(); hu.release();
        Y=null; hu=null;

        //=======Cr Component=======
        // Get DCT features Cb and cr
        Y = tmp.Y2.clone(); tmp.Y2.release();tmp.Y2=null;
        //Highgui.imwrite("Cb.jpg",Y);
        Y.convertTo(Y, CvType.CV_32FC1);
        //Core.normalize(Y,Y,0,1,Core.NORM_MINMAX);
        hF=getDctFeatures(Y,nRFitur,nCFitur);
        y = hF.get(0,0);
        tHF.put(0, 2, y);
        Y.release();
        hF.release();
        Y=null; hF=null;

        //=======Cb Component=======
        // Get DCT features Cb and cr
        Y = tmp.Y3.clone(); tmp.Y3.release(); tmp.Y3=null;
        //Highgui.imwrite("Cr.jpg",Y);
        Y.convertTo(Y, CvType.CV_32FC1);
        //Core.normalize(Y,Y,0,1,Core.NORM_MINMAX);
        hF=getDctFeatures(Y,nRFitur,nCFitur);
        y = hF.get(0,0);
        tHF.put(0, 3, y);

        Y.release();
        hF.release();
        Y=null; hF=null;
        //=======EOF Cb Component=======
        tmp=null;
        return tHF;
    }
}
