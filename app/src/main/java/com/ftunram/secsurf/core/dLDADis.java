package com.ftunram.secsurf.core;

import android.os.Environment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by user on 1/12/2016.
 */
public class dLDADis {
    private	int nCls, nDT;
    private Mat U;  //proj Matrix dLDA
    private	Mat mEC;
    private	Mat pMuA; //psesudo global mean
    private Vector<Integer> nEC=new Vector<Integer>();
    private	Vector<Double> pC=new Vector<Double>();
    private	Vector<String> ID=new Vector<String>();
    private	String idObj;

    public void setParams() {
        if (nCls==0) {

            nCls=5;
            nDT=15;

            //Vector NEC
            for (int i=0; i<nCls; i++)
                nEC.addElement(3);

            //Vector ID
            for (int i=0; i<nCls; i++)
                ID.addElement(new String("T"));
        }
        mEC=Mat.eye(3, 3, CvType.CV_64FC1);
        pMuA=Mat.eye(4, 4,CvType.CV_64FC1);

    }

    public void saveParams() {
        String fName="pornData/"+idObj+"_dLDADis.txt";
        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(fName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(String.valueOf(nCls));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(nDT));
            bufferedWriter.newLine();

            //Vector NEC
            for (int i=0; i<nEC.size(); i++){
                bufferedWriter.write(String.valueOf(nEC.elementAt(i)));
                bufferedWriter.newLine();
            }

            //Vector ID
            for (int i=0; i<ID.size(); i++){
                bufferedWriter.write(ID.elementAt(i));
                bufferedWriter.newLine();
            }

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        //Save xmlnya
        fName="pornData/"+idObj+"_dLDADis.xml";
        if (mEC!=null) {
            //Save into xml
            TaFileStorage fs=new TaFileStorage();
            fs.create(fName);
            fs.writeMat("mEC", mEC);
            fs.writeMat("pMuA", pMuA);
            fs.release();
        }

    }

    public void readParams() {
        String fName="pornData/"+idObj+"_dLDADis.txt";
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, fName);
        // This will reference one line at a time
        String line = null;
        nEC.clear();
        ID.clear();

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(file.getAbsolutePath());

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =  new BufferedReader(fileReader);

            //nCls
            line = bufferedReader.readLine();
            nCls=Integer.valueOf(line);

            //nDT
            line = bufferedReader.readLine();
            nDT=Integer.valueOf(line);

            // nEC
            int i=0;
            while(i<nCls) {
                line = bufferedReader.readLine();
                nEC.addElement(Integer.valueOf(line));
                i++;
            }

            //ID
            while((line = bufferedReader.readLine()) != null) {
                ID.addElement(line);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }

        //read xmlnya
        if (mEC!=null) {
            mEC.release(); pMuA.release();
        }
        fName="pornData/"+idObj+"_dLDADis.xml";
        File file1 = new File(root, fName);
        if (nCls>0) {
            //Save into xml
            TaFileStorage fs=new TaFileStorage();
            fs.open(file1.getAbsolutePath());
            mEC=fs.readMat("mEC");
            //System.out.println( "mat mEC= " +mEC.dump() );
            pMuA=fs.readMat("pMuA");
            //System.out.println( "mat pMuA= " +pMuA.dump() );
            //fs.release();
        }
        for (int i=0;i<nEC.size();i++) {
            pC.addElement((double)(nEC.elementAt(i))/nDT);
        }

    }



    public void saveMat2Xml(String fName,Mat dt ) {
        if (dt.cols()>0) {
            TaFileStorage fs=new TaFileStorage();
            fs.create(fName);
            fs.writeMat("mat", dt);
            fs.release();
        }
    }

    public Mat readXml2Mat(String fName) {
        File f = new File(fName);
        if (f.exists()) {
            TaFileStorage fs=new TaFileStorage();
            fs.open(fName);
            return fs.readMat("mat");
        } else return null;
    }

    public Boolean initial (String ObjName) {
        Boolean res=false;
        this.nCls= 0;
        this.nDT=0;
        this.idObj=ObjName;

        //load xml
        String fName="pornData/U_"+idObj+"_dLDA.xml";
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, fName);
        if (file.exists()) {
            //Load U xml
            U=readXml2Mat(file.getAbsolutePath());
            //System.out.println( "U  = " + U.dump() );
        }
        file.delete();

        //load trainned data
        fName="pornData/"+idObj+"_dLDADis.xml";
        File f = new File(root,fName);
        if (f.exists()) {

            //load2Mat();
            readParams();
            res=true;
        }
        f.delete();
        return res;
    }

    public void free () {
        if (this.nCls>0) {
            this.nCls= 0;
            //mEC
            if (mEC!=null) mEC.release();
            if (pMuA!=null) pMuA.release();
            if (nEC.size()>0) nEC.clear();
            if (ID.size()>0) ID.clear();
        }
        nDT=0;
        idObj="";
    }

    public Mat getSumRows(Mat dtIn) {
        int M=dtIn.rows();
        int N=dtIn.cols();

        Mat sumCols= Mat.zeros(M,1,CvType.CV_64FC1);
        for(int j=0;j<N;j++)
            Core.add(sumCols, dtIn.col(j), sumCols);
        //sumCols=sumCols+dtIn.col(j);
        return (sumCols);
    }

    public Mat matTimeScalar(Mat dtIn, double sc)
    {
        Mat tmp=new Mat(dtIn.size(), dtIn.type());
        Scalar alpha = new Scalar(sc);
        Core.multiply(dtIn, alpha, tmp);
        return tmp;
    }

    public void addData(Mat dt, String iD) {
        //String fName;
        List<Mat> tF=new ArrayList<Mat>();
        if (this.nCls==0) {
            nCls++;
            Mat t=getSumRows(dt);
            double sc=1.0/dt.cols();
            mEC=matTimeScalar(t, sc);
            nEC.addElement(new Integer(dt.cols()));
            nDT+=dt.cols();
            pMuA=t.clone(); t.release();
            ID.addElement(iD);
            //Projection
            //System.out.println( "dt : " + dt.dump());
            Core.gemm(U.t(),dt,1,new Mat(),0,t);
            //System.out.println( "t : " + t.dump());

            saveMat2Xml("pornData/dtIn/cls_"+idObj+String.valueOf(nCls)+".xml",t);
            t.release();
        } else {
            nCls++;
            Mat t=getSumRows(dt);

            //Cambine MEC
            tF.add(mEC);
            double sc=1.0/dt.cols();
            tF.add(matTimeScalar(t, sc));
            Core.hconcat(tF,mEC);

            nEC.addElement(new Integer(dt.cols()));
            nDT+=dt.cols();
            Core.add(pMuA, t.clone(), pMuA);
            //pMuA+=t.clone(); t.release();
            ID.addElement(iD);
            //Projection
            Core.gemm(U.t(),dt,1,new Mat(),0,t);
            saveMat2Xml("pornData/dtIn/cls_"+idObj+String.valueOf(nCls)+".xml",t);
            t.release();
        }
    }

    public  Vector<myScore> Matching( Mat dQry) {
        Vector<myScore> tSc=new Vector<myScore>();
        if (nDT>0) {
            //1. fQ extraction
            Mat fDQ= new Mat();
            //Core.multiply(U.t(), dQry, fDQ);
            Core.gemm(U.t(),dQry,1,new Mat(),0,fDQ);
            //System.out.println( "fQ = " + fDQ.dump() );

            //2. Match to FDs
            Mat tmp=Mat.zeros(U.rows(), 1, CvType.CV_64FC1);
            Mat d=new Mat(fDQ.size(),fDQ.type());
            myScore tS;

            String root = Environment.getExternalStorageDirectory().toString();
            //File file = new File(root);
            for (int i=0;i<nCls;i++) {
                double sc=10000 ;
                //load ldaFeatures
                String fName="pornData/dtIn/cls_"+idObj+String.valueOf(i+1)+".xml";
                File file=new File(root,fName);
                tmp=readXml2Mat(file.getAbsolutePath());
                //cout << "Omg= " << tmp << endl;
                for (int j=0;j<tmp.cols();j++) {
                    //System.out.println( "hF = " + tmp.col(j).dump() );
                    Core.subtract(tmp.col(j), fDQ, d);
                    sc=Core.norm(d);
                    tS=new myScore();
                    tS.sc=sc;
                    tS.ID=ID.get(i);
                    tSc.add(tS);
                }
                tmp.release();
                file.delete();
            }
            fDQ.release();
        }
        return tSc;
    }


}
