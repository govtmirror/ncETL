/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.data;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author jwalker
 */
public class ASCIIGridHeaderFile {
    
    private static Pattern headerPat = Pattern.compile("^GRIDid.*x-coord.*y-coord.*elev.*$");
    private static Pattern gridPat = Pattern.compile("^\\s*(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)$");
    
    private File underlyingFile;
    private int[][] gridIds;
    private float[] xVals;
    private float[] yVals;
    private float[][] zVals;
    
    public ASCIIGridHeaderFile(File infile) {
        this.underlyingFile = infile;
    }

    public void readExtents() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(underlyingFile));
        List<Integer> gridIdList = Lists.newArrayList();
        List<Float> xvalList = Lists.newArrayList();
        List<Float> yvalList = Lists.newArrayList();
        List<Float> zvalList = Lists.newArrayList();
        
        try {
            // do stuff
            String line = null;
            while ((line = reader.readLine()) != null) {
                // get to header line
                if (headerPat.matcher(line).matches()) {
                    // go through each line
                    float currentY = Float.NaN;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = gridPat.matcher(line);
                        if (matcher.matches()) {
                            int id = Integer.parseInt(matcher.group(1));
                            float xcoord = Float.parseFloat(matcher.group(2));
                            float ycoord = Float.parseFloat(matcher.group(3));
                            float zcoord = Float.parseFloat(matcher.group(4));
                            
                            // moving to next Y
                            if (ycoord != currentY) {
                                currentY = ycoord;
                                yvalList.add(ycoord);
                            }
                            // only populate xlist first time through
                            if (yvalList.size() == 1) {
                                xvalList.add(xcoord);
                            }
                            gridIdList.add(id);
                            zvalList.add(zcoord);
                        }
                    }
                }
            }
            
            xVals = new float[xvalList.size()];
            for (int i=0; i<xVals.length; i++) {
                xVals[i] = xvalList.get(i).floatValue();
            }
            yVals = new float[yvalList.size()];
            for (int i=0; i<yVals.length; i++) {
                yVals[i] = yvalList.get(i).floatValue();
            }
            zVals = new float[yVals.length][xVals.length];
            gridIds = new int[yVals.length][xVals.length];
            for (int y=0; y<yVals.length; y++) {
                for (int x=0; x<xVals.length; x++) {
                    zVals[y][x] = zvalList.get((y * xVals.length) + x);
                    gridIds[y][x] = gridIdList.get((y * xVals.length) + x);
                }
            }
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public int getYLength() {
        return yVals.length;
    }
    
    public int getXLength() {
        return xVals.length;
    }
    
    public int[][] getGridIds() {
        return gridIds;
    }
    
    public float[][] getZGrid() {
        return zVals;
    }
    
    public float[] getXDim() {
        return xVals;
    }
    
    public float[] getYDim() {
        return yVals;
    }
}
