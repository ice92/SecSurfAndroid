package com.ftunram.secsurf.core;

import android.support.v4.os.EnvironmentCompat;
import java.io.File;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.opencv.core.Mat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TaFileStorage {
    public static final int READ = 0;
    public static final int WRITE = 1;
    private Document doc;
    private File file;
    private boolean isWrite;
    private Element rootElement;

    public TaFileStorage() {
        this.file = null;
        this.isWrite = false;
        this.doc = null;
        this.rootElement = null;
    }

    public void open(String filePath, int flags) {
        if (flags == 0) {
            try {
                open(filePath);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        create(filePath);
    }

    public void open(String filePath) {
        try {
            this.file = new File(filePath);
            if (this.file == null || !this.file.isFile()) {
                System.err.println("Can not open file: " + filePath);
                return;
            }
            this.isWrite = false;
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.file);
            this.doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(String filePath) {
        try {
            this.file = new File(filePath);
            if (this.file == null) {
                System.err.println("Can not wrtie file: " + filePath);
                return;
            }
            this.isWrite = true;
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            this.rootElement = this.doc.createElement("opencv_storage");
            this.doc.appendChild(this.rootElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mat readMat(String tag) {
        if (this.isWrite) {
            System.err.println("Try read from file with write flags");
            return null;
        }
        NodeList nodelist = this.doc.getElementsByTagName(tag);
        Mat readMat = null;
        for (int i = READ; i < nodelist.getLength(); i += WRITE) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == WRITE) {
                Element element = (Element) node;
                if (!"opencv-matrix".equals(element.getAttribute("type_id"))) {
                    System.out.println("Fault type_id ");
                }
                String rowsStr = element.getElementsByTagName("rows").item(READ).getTextContent();
                String colsStr = element.getElementsByTagName("cols").item(READ).getTextContent();
                String dtStr = element.getElementsByTagName("dt").item(READ).getTextContent();
                String dataStr = element.getElementsByTagName("data").item(READ).getTextContent();
                int rows = Integer.parseInt(rowsStr);
                int cols = Integer.parseInt(colsStr);
                Scanner scanner = new Scanner(dataStr);
                Mat mat;
                int r;
                int c;
                if ("d".equals(dtStr)) {
                    mat = new Mat(rows, cols, 6);
                    double[] ds = new double[WRITE];
                    for (r = READ; r < rows; r += WRITE) {
                        for (c = READ; c < cols; c += WRITE) {
                            if (scanner.hasNextDouble()) {
                                ds[READ] = scanner.nextDouble();
                            } else {
                                ds[READ] = 0.0d;
                                System.err.println("Unmatched number of float value at rows=" + r + " cols=" + c);
                            }
                            mat.put(r, c, ds);
                        }
                    }
                } else {
                    if ("f".equals(dtStr)) {
                        mat = new Mat(rows, cols, 5);
                        float[] fs = new float[WRITE];
                        for (r = READ; r < rows; r += WRITE) {
                            for (c = READ; c < cols; c += WRITE) {
                                if (scanner.hasNextFloat()) {
                                    fs[READ] = scanner.nextFloat();
                                } else {
                                    fs[READ] = 0.0f;
                                    System.err.println("Unmatched number of float value at rows=" + r + " cols=" + c);
                                }
                                mat.put(r, c, fs);
                            }
                        }
                    } else {
                        if ("i".equals(dtStr)) {
                            mat = new Mat(rows, cols, 4);
                            int[] is = new int[WRITE];
                            for (r = READ; r < rows; r += WRITE) {
                                for (c = READ; c < cols; c += WRITE) {
                                    if (scanner.hasNextInt()) {
                                        is[READ] = scanner.nextInt();
                                    } else {
                                        is[READ] = READ;
                                        System.err.println("Unmatched number of int value at rows=" + r + " cols=" + c);
                                    }
                                    mat.put(r, c, is);
                                }
                            }
                        } else {
                            if ("s".equals(dtStr)) {
                                mat = new Mat(rows, cols, 3);
                                short[] ss = new short[WRITE];
                                for (r = READ; r < rows; r += WRITE) {
                                    for (c = READ; c < cols; c += WRITE) {
                                        if (scanner.hasNextShort()) {
                                            ss[READ] = scanner.nextShort();
                                        } else {
                                            ss[READ] = (short) 0;
                                            System.err.println("Unmatched number of int value at rows=" + r + " cols=" + c);
                                        }
                                        mat.put(r, c, ss);
                                    }
                                }
                            } else {
                                if ("b".equals(dtStr)) {
                                    mat = new Mat(rows, cols, READ);
                                    byte[] bs = new byte[WRITE];
                                    for (r = READ; r < rows; r += WRITE) {
                                        for (c = READ; c < cols; c += WRITE) {
                                            if (scanner.hasNextByte()) {
                                                bs[READ] = scanner.nextByte();
                                            } else {
                                                bs[READ] = (byte) 0;
                                                System.err.println("Unmatched number of byte value at rows=" + r + " cols=" + c);
                                            }
                                            mat.put(r, c, bs);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                scanner.close();
            }
        }
        return readMat;
    }

    public void writeMat(String tag, Mat mat) {
        try {
            if (this.isWrite) {
                String dtStr;
                Element matrix = this.doc.createElement(tag);
                matrix.setAttribute("type_id", "opencv-matrix");
                this.rootElement.appendChild(matrix);
                Element rows = this.doc.createElement("rows");
                rows.appendChild(this.doc.createTextNode(String.valueOf(mat.rows())));
                Element cols = this.doc.createElement("cols");
                cols.appendChild(this.doc.createTextNode(String.valueOf(mat.cols())));
                Element dt = this.doc.createElement("dt");
                int type = mat.type();
                if (type == 6) {
                    dtStr = "d";
                } else if (type == 5) {
                    dtStr = "f";
                } else if (type == 4) {
                    dtStr = "i";
                } else if (type == 3) {
                    dtStr = "s";
                } else if (type == 0) {
                    dtStr = "b";
                } else {
                    dtStr = EnvironmentCompat.MEDIA_UNKNOWN;
                }
                dt.appendChild(this.doc.createTextNode(dtStr));
                Element data = this.doc.createElement("data");
                data.appendChild(this.doc.createTextNode(dataStringBuilder(mat)));
                matrix.appendChild(rows);
                matrix.appendChild(cols);
                matrix.appendChild(dt);
                matrix.appendChild(data);
                return;
            }
            System.err.println("Try write to file with no write flags");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String dataStringBuilder(Mat mat) {
        StringBuilder sb = new StringBuilder();
        int rows = mat.rows();
        int cols = mat.cols();
        int type = mat.type();
        int r;
        int c;
        if (type == 6) {
            double[] ds = new double[WRITE];
            for (r = READ; r < rows; r += WRITE) {
                for (c = READ; c < cols; c += WRITE) {
                    mat.get(r, c, ds);
                    sb.append(String.valueOf(ds[READ]));
                    sb.append(' ');
                }
                sb.append('\n');
            }
        } else if (type == 5) {
            float[] fs = new float[WRITE];
            for (r = READ; r < rows; r += WRITE) {
                for (c = READ; c < cols; c += WRITE) {
                    mat.get(r, c, fs);
                    sb.append(String.valueOf(fs[READ]));
                    sb.append(' ');
                }
                sb.append('\n');
            }
        } else if (type == 4) {
            int[] is = new int[WRITE];
            for (r = READ; r < rows; r += WRITE) {
                for (c = READ; c < cols; c += WRITE) {
                    mat.get(r, c, is);
                    sb.append(String.valueOf(is[READ]));
                    sb.append(' ');
                }
                sb.append('\n');
            }
        } else if (type == 3) {
            short[] ss = new short[WRITE];
            for (r = READ; r < rows; r += WRITE) {
                for (c = READ; c < cols; c += WRITE) {
                    mat.get(r, c, ss);
                    sb.append(String.valueOf(ss[READ]));
                    sb.append(' ');
                }
                sb.append('\n');
            }
        } else if (type == 0) {
            byte[] bs = new byte[WRITE];
            for (r = READ; r < rows; r += WRITE) {
                for (c = READ; c < cols; c += WRITE) {
                    mat.get(r, c, bs);
                    sb.append(String.valueOf(bs[READ]));
                    sb.append(' ');
                }
                sb.append('\n');
            }
        } else {
            sb.append("unknown type\n");
        }
        return sb.toString();
    }

    public void release() {
        try {
            if (this.isWrite) {
                DOMSource source = new DOMSource(this.doc);
                StreamResult result = new StreamResult(this.file);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.transform(source, result);
                return;
            }
            System.err.println("Try release of file with no write flags");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
