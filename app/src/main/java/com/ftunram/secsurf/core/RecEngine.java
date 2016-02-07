package com.ftunram.secsurf.core;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by user on 1/12/2016.
 */
public class RecEngine {
    private dLDADis dLDA_Y, dLDA_Cb, dLDA_Cr;
    int nRHF=7;
    int nCHF=7;
    int scale=2;
    double thPorn= 0.6;
    //String root = Environment.getExternalStorageDirectory().toString();
    //File fileCascade= new File(root,"pornData/lbpcascade_frontalface.xml");
    //private CascadeClassifier faceDetector = new CascadeClassifier(fileCascade.getAbsolutePath());
    //private  CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");

    public int recPornFaceInfo(CascadeClassifier faceDetector, Mat image, Mat skinTone) {
        int res=0;
        int pNeg=0;
        int pPos=0;
        boolean face=false;

        //Face Detection
        Mat mRgba=new Mat();
        Mat mGrey=new Mat();
        //MatOfRect faces = new MatOfRect();
        image.copyTo(mRgba);
        image.copyTo(mGrey);
        Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist( mGrey, mGrey );

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mGrey, faceDetections);

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            int xF1,yF1,xF2,yF2, fW,fH;
            face=true;
            //Face Koordinat
            fW=rect.width;
            fH=rect.height;
            xF1=rect.x;
            yF1=rect.y;
            xF2=xF1+fW;
            yF2=yF1+fH;
            //Core.rectangle(mRgba, new Point(xF1,yF1),  new Point(xF2,yF2), new Scalar( 0, 0, 255 ), 2, 8, 0 );
            //Highgui.imwrite("detectedFace.png", mRgba);

            //Kordinat Body
            int x1,y1,x2,y2;

            x1=(int) (xF1-0.2*rect.width);
            y1=(int) ( yF2+0.6*rect.height);
            x2=(int) (xF2+0.2*rect.width);
            y2=(int) (yF2+ 5*rect.height);

            if (x1<0) x1=0;
            if (y1>mGrey.rows()-1) y1=mGrey.rows()-1;
            if (x2>mGrey.cols()-1) x2=mGrey.cols()-1;
            if (y2>mGrey.rows()-1) y2=mGrey.rows()-1;

            //real Kordinat Body
            Rect Body=new Rect();
            Body.x=x1;
            Body.y=y1;
            Body.width=x2-x1;
            Body.height=y2-y1;

            if (Body.height>0 && Body.width >0) {
                //define Porn
                double pSkin=0;
                Mat bodyROI = new Mat(skinTone, Body);
                //Highgui.imwrite("bodyRoi.png", bodyROI.mul(bodyROI,255));
                if (Body.height>1.10*fH){
                    //cout<<"sum rows matrix: \n"<<matSumRows(bodyROI)/float(faces[i].width)<<endl<<endl;
                    //cout<<"sum cols matrix: \n"<<matSumCols(bodyROI)/float(faces[i].height)<<endl<<endl;
                    Scalar tS= Core.sumElems(bodyROI);
                    pSkin=tS.val[0]/(float)(bodyROI.rows()*bodyROI.cols());
                }

                if (pSkin>thPorn) pNeg++;
                else pPos++;

            }
        }
        // Conclusion
        if (face){
            if (pNeg>pPos)
                res=1;
            else
                res=-1;
        }
        // Save the visualized detection.
        return res;
    }

    public void initRecEngine() {
        boolean res;
        dLDA_Y=new dLDADis();
        dLDA_Cr=new dLDADis();
        dLDA_Cb=new dLDADis();

        res=dLDA_Y.initial("Y");
        res=dLDA_Cb.initial("Cb");
        res=dLDA_Cr.initial("Cr");
        //if (!res) tranBatch("pornData/dtNeg");
    }

    public void tranBatch(String path)
    {
        //Get folder
        YCC fHF=new YCC();
        List<Mat> tF=new ArrayList<Mat>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                String dbFolder=path+"/"+listOfFiles[i].getName();
                File folder1 = new File(dbFolder);
                File[] listOfFiles1 = folder1.listFiles();
                for (int j = 0; j < listOfFiles1.length; j++) {
                    if (listOfFiles1[j].isFile()) {
                        YCC tmpfHF=new YCC();
                        String fName=dbFolder+"/"+listOfFiles1[j].getName();
                        if (j==0) {
                            fHF=getHFofImage(fName,nRHF,nCHF,null);
                        } else {
                            if (tF.size()>0) tF.clear();
                            tmpfHF=getHFofImage(fName,nRHF,nCHF,null);
                            if (tmpfHF.Y1!=null) {
                                tF.add(fHF.Y1);
                                tF.add(tmpfHF.Y1);
                                Core.hconcat(tF,fHF.Y1); tF.clear();

                                tF.add(fHF.Y2);
                                tF.add(tmpfHF.Y2);
                                Core.hconcat(tF,fHF.Y2); tF.clear();

                                tF.add(fHF.Y3);
                                tF.add(tmpfHF.Y3);
                                Core.hconcat(tF,fHF.Y3); tF.clear();
                                if (tmpfHF!=null) {
                                    tmpfHF.Y1.release();
                                    tmpfHF.Y2.release();
                                    tmpfHF.Y3.release();
                                }
                            }
                        }

                    }
                }//end for j
                String tID;
                if (i==0) tID="Neg";
                else tID="Pos";
                dLDA_Y.addData(fHF.Y1,tID);
                dLDA_Cb.addData(fHF.Y2,tID);
                dLDA_Cr.addData(fHF.Y3,tID);
                fHF.Y1.release(); fHF.Y2.release(); fHF.Y3.release();
            }
        }//end for i
        dLDA_Y.saveParams();
        dLDA_Cb.saveParams();
        dLDA_Cr.saveParams();
    }

    public boolean  recPorn2RecEngine (String fName, Mat frame) {
        boolean res=false;
        //Skin probability

        YCC hFQ=new YCC();
        Vector<myScore> mScY=new Vector<myScore>();
        Vector<myScore> mScCb=new Vector<myScore>();
        Vector<myScore> mScCr=new Vector<myScore>();

        if (frame!=null){
            hFQ=getHFofImage(null,nRHF,nCHF,frame);
        }else if (fName!=null) {
            //string imName="Data\\dtNeg\\Q3\\6.jpg";
            hFQ=getHFofImage(fName,nRHF,nCHF,null);
            //cout << "fY= " <<fYQ << endl<<endl;
        }
        if (hFQ!=null) {
            mScY=dLDA_Y.Matching(hFQ.Y1);
            mScCr=dLDA_Cb.Matching(hFQ.Y2);
            mScCb=dLDA_Cr.Matching(hFQ.Y3);
            //gabungkan
            myScore tS;
            for (int i=0; i<mScY.size();i++) {
                tS=new myScore();
                tS.ID=mScY.get(i).ID;
                //mScY[i].Score=(mScY[i].Score + 0.75*(mScCb[i].Score+mScCr[i].Score))/3;
                tS.sc=(mScY.get(i).sc + 0.75*(mScCb.get(i).sc+mScCr.get(i).sc))/3;
                mScY.set(i, tS);
            }
            Collections.sort(mScY);
            //no kNN
//			String recID=mScY.elementAt(0).ID;
//			if (recID.equals("Pos"))
//				res=true;
//			else
//				res=false;
            //with k-NN
            int tP=0;
            for (int i=0;i<9;i++){
                if (mScY.elementAt(i).ID.equals("Pos")) tP++;
            }

            if (tP>(9-tP))
                res=true;
            else
                res=false;
            //System.out.println( "Best Score : " + mScY.elementAt(0).sc+ "and Recognized as : "+res);
        }
        mScY.clear();
        mScCb.clear();
        mScCr.clear();
        return res;
    }

    //Method for getting skin based on YCbCr color space
    public Mat getSkinToneCbCr(Mat frame)
    {
        Mat im=frame.clone();
        Mat skinTone= new Mat(im.rows(), im.cols(), CvType.CV_64FC1);
        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);
        List<Mat>mv=new ArrayList<Mat>(3);
        Core.split(im,mv);

        Mat Cr=mv.get(1);
        Mat Cb=mv.get(2);


        for(int i=0;i<im.rows()-1;i++){
            for(int j=0;j<im.cols()-1;j++){
                double[] cb = Cb.get(i,j);
                double[] cr = Cr.get(i,j);

                if ((int)cb[0]>=77 && (int)cb[0]<=127 && (int)cr[0]>=133 && (int)cr[0]<=173 ){
                    skinTone.put(i,j,1);
                }else {
                    skinTone.put(i,j,0);
                }
            }
        }
        return skinTone;
    }

    //RecEngine Basedon Skin
    public boolean  recFramePorn2RecEngineMultiStage (Mat frame,CascadeClassifier faceDetector) {
        boolean res=false;
        Mat im=new Mat();

        double ratio=360.0/(double)Math.max(frame.cols(),frame.rows());
        int col=(int) Math.round(frame.cols()*ratio);
        int row=(int) Math.round(frame.rows()*ratio);
        Imgproc.resize(frame, im,new Size(col,row));

//        if (Math.min(frame.cols(),frame.rows())<320){
//        	Imgproc.resize(frame, im,new Size(frame.cols()*scale,frame.rows()*scale));
//        }
//        else if (Math.min(frame.cols(),frame.rows())<840) {
//        	Imgproc.resize(frame, im,new Size(frame.cols()/scale,frame.rows()/scale));
//        }else
//        	frame.copyTo(im);
        //System.out.println( "Cols : " + im.cols()+ "Row : "+im.rows());
        if (im.channels()==3){
            Mat skinTone=getSkinToneCbCr(im);
            //Highgui.imwrite("skin.jpg",skinTone.mul(skinTone, 255));
            Scalar ts=Core.sumElems(skinTone);
            double pSkin=(double)ts.val[0]/(skinTone.rows()*skinTone.cols());
            //System.out.println( "pSkin= " +pSkin );
            if(pSkin>0.15) {
                int res1=recPornFaceInfo(faceDetector,im,skinTone);
                if (res1==0) 		//Confuse
                    res=recPorn2RecEngine(null,frame);
                else if (res1==1) 	//Negative Image
                    res=true;
                else 			 	// Positive Image
                    res=false;
            }
        }
        return res;
    }

    //RecEngine Basedon Skin
    public boolean  recPorn2RecEngineMultiStage (String fName, CascadeClassifier faceDetector) {
        boolean res=false;
        int res1=0;
        Mat im= Highgui.imread(fName);
        double ratio=360.0/(double)Math.max(im.cols(),im.rows());
        int col=(int) Math.round(im.cols()*ratio);
        int row=(int) Math.round(im.rows()*ratio);
        Imgproc.resize(im, im,new Size(col,row));

        if (im.channels()==3){
            //convert to skin
            //System.out.println( "Cols : " + im.cols()+ "Row : "+im.rows());
            Mat skinTone=getSkinToneCbCr(im);
            //Highgui.imwrite("skin.jpg",skinTone.mul(skinTone, 255));
            Scalar ts=Core.sumElems(skinTone);
            double pSkin=(double)ts.val[0]/(skinTone.rows()*skinTone.cols());
            //System.out.println( "pSkin= " +pSkin );
            if(pSkin>0.15) {
                res1=recPornFaceInfo(faceDetector,im,skinTone);

                if (res1==0) 		//Confuse
                    res=recPorn2RecEngine(fName,null);
                else if (res1==1) 	//Negative Image
                    res=true;
                else 			 	// Positive Image
                    res=false;

            }
        }
        im.release();
        return res;
    }


    //Method for getting dct coef single channel Mat
    public Mat getDctFeatures(Mat imIn, int N, int M)
    {
        //Assume the Input size matrix
        Mat res = new Mat(imIn.size(), imIn.type());
        Core.dct(imIn,res);

        Mat tmp1=res.colRange(0,N).rowRange(0,M);
        //System.out.println( "mat Y= " +tmp1.dump() );

        Mat tmp=new Mat(N,M,CvType.CV_64FC1);
        tmp1.copyTo(tmp);
        tmp1.release();
        tmp=tmp.reshape(1,tmp.rows()*tmp.cols());

        double nT=Core.norm(tmp);
        Scalar t=new Scalar(1.0/nT);
        //System.out.println( "Norm = " +nT );
        Core.multiply(tmp, t, tmp);
        //System.out.println( "mat Y= " +tmp.dump() );
        return tmp;
    }

    //Method for getting hF coef of an image
    public YCC getHFofImage (String fName, int nRFitur, int nCFitur, Mat frame)
    {

        YCC tmp=new YCC();
        List<Mat>tF=new ArrayList<Mat>();
        Mat im=Mat();
        if (frame==null){
            im=Highgui.imread(fName);
        } else if (frame!=null) {
            im=frame.clone();
        };
        Size sz = new Size(128,128);
        if (im.rows()!=128 && im.cols()!=128) Imgproc.resize(im, im,sz);
        //get Skin Tone
        //Mat skin= getSkinToneCbCr( im);
        //Scalar ts=sum(skin);

        //if (double(ts[0])>10){
        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        //Split YcbCr
        List<Mat>mv=new ArrayList<Mat>(3);
        Core.split(im,mv);

        //=======Y Component=======
        Mat imIn=mv.get(0);

        Mat Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);

        double sc=1/255.0;
        Scalar alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        // Get Hu Moment
        Mat hu=new Mat();
        Moments m= Imgproc.moments(Y, false);
        Imgproc.HuMoments(m, hu);
        hu=hu.colRange(0,1).rowRange(0,4);

        // Get DCT features
        Core.normalize(Y,Y,0,1,Core.NORM_MINMAX);
        Mat hF=getDctFeatures(Y,nRFitur,nCFitur);
        tF.add(hF);
        tF.add(hu);
        Core.vconcat(tF,Y); tF.clear();

        //Normalize
        sc=Core.norm(Y);
        alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        tmp.Y1=Y.clone(); Y.release();
        //=======EOF Y Component=======

        //=======Cr Component=======
        imIn=mv.get(1);

        Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);

        sc=1/255.0;
        alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        // Get Hu Moment
        m= Imgproc.moments(Y, false);
        Imgproc.HuMoments(m, hu);
        hu=hu.colRange(0,1).rowRange(0,4);

        // Get DCT features
        Core.normalize(Y,Y,0,1,Core.NORM_MINMAX);
        hF=getDctFeatures(Y,nRFitur,nCFitur);
        tF.add(hF);
        tF.add(hu);

        Core.vconcat(tF,Y); tF.clear();

        //Normalize
        sc=Core.norm(Y);
        alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        tmp.Y2=Y.clone(); Y.release();
        //=======EOF Cr Component=======

        //=======Cb Component=======
        imIn=mv.get(2);

        Y = new Mat(imIn.size(), CvType.CV_64FC1);
        imIn.convertTo(Y, CvType.CV_64FC1);

        sc=1/255.0;
        alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        // Get Hu Moment
        m= Imgproc.moments(Y, false);
        Imgproc.HuMoments(m, hu);
        hu=hu.colRange(0,1).rowRange(0,4);

        // Get DCT features
        Core.normalize(Y,Y,0,1,Core.NORM_MINMAX);
        hF=getDctFeatures(Y,nRFitur,nCFitur);

        tF.add(hF);
        tF.add(hu);

        Core.vconcat(tF,Y); tF.clear();

        //Normalize
        sc=Core.norm(Y);
        alpha = new Scalar(sc);
        Core.multiply(Y, alpha, Y);

        tmp.Y3=Y.clone(); Y.release();
        //=======EOF Cb Component=======
        //}
        return tmp;
    }


    private Mat Mat() {
        // TODO Auto-generated method stub
        return null;
    }

}