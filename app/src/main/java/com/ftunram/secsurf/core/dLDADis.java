package com.ftunram.secsurf.core;

import android.os.Environment;
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
import org.opencv.BuildConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class dLDADis {
    private Vector<String> ID;
    private Mat f0U;
    private String idObj;
    private Mat mEC;
    private int nCls;
    private int nDT;
    private Vector<Integer> nEC;
    private Vector<Double> pC;
    private Mat pMuA;

    public dLDADis() {
        this.nEC = new Vector();
        this.pC = new Vector();
        this.ID = new Vector();
    }

    public void setParams() {
        if (this.nCls == 0) {
            int i;
            this.nCls = 5;
            this.nDT = 15;
            for (i = 0; i < this.nCls; i++) {
                this.nEC.addElement(Integer.valueOf(3));
            }
            for (i = 0; i < this.nCls; i++) {
                this.ID.addElement(new String("T"));
            }
        }
        this.mEC = Mat.eye(3, 3, CvType.CV_64FC1);
        this.pMuA = Mat.eye(4, 4, CvType.CV_64FC1);
    }

    public void saveParams() {
        String fName = "pornData/" + this.idObj + "_dLDADis.txt";
        try {
            int i;
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fName));
            bufferedWriter.write(String.valueOf(this.nCls));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(this.nDT));
            bufferedWriter.newLine();
            for (i = 0; i < this.nEC.size(); i++) {
                bufferedWriter.write(String.valueOf(this.nEC.elementAt(i)));
                bufferedWriter.newLine();
            }
            for (i = 0; i < this.ID.size(); i++) {
                bufferedWriter.write((String) this.ID.elementAt(i));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file '" + fName + "'");
        }
        fName = "pornData/" + this.idObj + "_dLDADis.xml";
        if (this.mEC != null) {
            TaFileStorage fs = new TaFileStorage();
            fs.create(fName);
            fs.writeMat("mEC", this.mEC);
            fs.writeMat("pMuA", this.pMuA);
            fs.release();
        }
    }

    public void readParams() {
        int i;
        String fName = "pornData/" + this.idObj + "_dLDADis.txt";
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, fName);
        this.nEC.clear();
        this.ID.clear();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            this.nCls = Integer.valueOf(bufferedReader.readLine()).intValue();
            this.nDT = Integer.valueOf(bufferedReader.readLine()).intValue();
            for (i = 0; i < this.nCls; i++) {
                this.nEC.addElement(Integer.valueOf(bufferedReader.readLine()));
            }
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                this.ID.addElement(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + fName + "'");
        } catch (IOException e2) {
            System.out.println("Error reading file '" + fName + "'");
        }
        if (this.mEC != null) {
            this.mEC.release();
            this.pMuA.release();
        }
        File file1 = new File(root, "pornData/" + this.idObj + "_dLDADis.xml");
        if (this.nCls > 0) {
            TaFileStorage fs = new TaFileStorage();
            fs.open(file1.getAbsolutePath());
            this.mEC = fs.readMat("mEC");
            this.pMuA = fs.readMat("pMuA");
        }
        for (i = 0; i < this.nEC.size(); i++) {
            this.pC.addElement(Double.valueOf(((double) ((Integer) this.nEC.elementAt(i)).intValue()) / ((double) this.nDT)));
        }
    }

    public void saveMat2Xml(String fName, Mat dt) {
        if (dt.cols() > 0) {
            TaFileStorage fs = new TaFileStorage();
            fs.create(fName);
            fs.writeMat("mat", dt);
            fs.release();
        }
    }

    public Mat readXml2Mat(String fName) {
        if (!new File(fName).exists()) {
            return null;
        }
        TaFileStorage fs = new TaFileStorage();
        fs.open(fName);
        return fs.readMat("mat");
    }

    public Boolean initial(String ObjName) {
        Boolean res = Boolean.valueOf(false);
        this.nCls = 0;
        this.nDT = 0;
        this.idObj = ObjName;
        String fName = "pornData/U_" + this.idObj + "_dLDA.xml";
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, fName);
        if (file.exists()) {
            this.f0U = readXml2Mat(file.getAbsolutePath());
        }
        file.delete();
        File f = new File(root, "pornData/" + this.idObj + "_dLDADis.xml");
        if (f.exists()) {
            readParams();
            res = Boolean.valueOf(true);
        }
        f.delete();
        return res;
    }

    public void free() {
        if (this.nCls > 0) {
            this.nCls = 0;
            if (this.mEC != null) {
                this.mEC.release();
            }
            if (this.pMuA != null) {
                this.pMuA.release();
            }
            if (this.nEC.size() > 0) {
                this.nEC.clear();
            }
            if (this.ID.size() > 0) {
                this.ID.clear();
            }
        }
        this.nDT = 0;
        this.idObj = BuildConfig.FLAVOR;
    }

    public Mat getSumRows(Mat dtIn) {
        int M = dtIn.rows();
        int N = dtIn.cols();
        Mat sumCols = Mat.zeros(M, 1, CvType.CV_64FC1);
        for (int j = 0; j < N; j++) {
            Core.add(sumCols, dtIn.col(j), sumCols);
        }
        return sumCols;
    }

    public Mat matTimeScalar(Mat dtIn, double sc) {
        Mat tmp = new Mat(dtIn.size(), dtIn.type());
        Core.multiply(dtIn, new Scalar(sc), tmp);
        return tmp;
    }

    public void addData(Mat dt, String iD) {
        List<Mat> tF = new ArrayList();
        if (this.nCls == 0) {
            this.nCls++;
            Mat t = getSumRows(dt);
            this.mEC = matTimeScalar(t, 1.0d / ((double) dt.cols()));
            this.nEC.addElement(new Integer(dt.cols()));
            this.nDT += dt.cols();
            this.pMuA = t.clone();
            t.release();
            this.ID.addElement(iD);
            Core.gemm(this.f0U.m6t(), dt, 1.0d, new Mat(), 0.0d, t);
            saveMat2Xml("pornData/dtIn/cls_" + this.idObj + String.valueOf(this.nCls) + ".xml", t);
            t.release();
            return;
        }
        this.nCls++;
        t = getSumRows(dt);
        tF.add(this.mEC);
        tF.add(matTimeScalar(t, 1.0d / ((double) dt.cols())));
        Core.hconcat(tF, this.mEC);
        this.nEC.addElement(new Integer(dt.cols()));
        this.nDT += dt.cols();
        Core.add(this.pMuA, t.clone(), this.pMuA);
        this.ID.addElement(iD);
        Core.gemm(this.f0U.m6t(), dt, 1.0d, new Mat(), 0.0d, t);
        saveMat2Xml("pornData/dtIn/cls_" + this.idObj + String.valueOf(this.nCls) + ".xml", t);
        t.release();
    }

    public Vector<myScore> Matching(Mat dQry) {
        Vector<myScore> tSc = new Vector();
        if (this.nDT > 0) {
            Mat fDQ = new Mat();
            Core.gemm(this.f0U.m6t(), dQry, 1.0d, new Mat(), 0.0d, fDQ);
            Mat tmp = Mat.zeros(this.f0U.rows(), 1, CvType.CV_64FC1);
            Mat d = new Mat(fDQ.size(), fDQ.type());
            String root = Environment.getExternalStorageDirectory().toString();
            for (int i = 0; i < this.nCls; i++) {
                File file = new File(root, "pornData/dtIn/cls_" + this.idObj + String.valueOf(i + 1) + ".xml");
                tmp = readXml2Mat(file.getAbsolutePath());
                for (int j = 0; j < tmp.cols(); j++) {
                    Core.subtract(tmp.col(j), fDQ, d);
                    double sc = Core.norm(d);
                    myScore tS = new myScore();
                    tS.sc = sc;
                    tS.ID = (String) this.ID.get(i);
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
